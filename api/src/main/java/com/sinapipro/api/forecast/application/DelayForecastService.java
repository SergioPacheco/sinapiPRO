package com.sinapipro.api.forecast.application;

import module java.base;

import com.sinapipro.api.schedule.domain.ScheduleActivity;
import com.sinapipro.api.schedule.domain.ScheduleActivityRepository;
import com.sinapipro.api.weather.domain.WeatherDelay;
import com.sinapipro.api.weather.domain.WeatherDelayRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.StructuredTaskScope;

/**
 * AI Delay Forecast — predicts project delays based on:
 * 1. Historical weather delay patterns
 * 2. Current schedule progress vs planned (SPI-based)
 * 3. Remaining duration and seasonal weather risk
 */
@Service
@Transactional(readOnly = true)
public class DelayForecastService {

    private final ScheduleActivityRepository scheduleRepository;
    private final WeatherDelayRepository weatherRepository;

    public DelayForecastService(ScheduleActivityRepository scheduleRepository,
                                WeatherDelayRepository weatherRepository) {
        this.scheduleRepository = scheduleRepository;
        this.weatherRepository = weatherRepository;
    }

    public DelayForecast predict(UUID budgetId) {
        // Structured Concurrency: fetch schedule and weather data in parallel
        List<ScheduleActivity> activities;
        List<WeatherDelay> weatherDelays;

        try (var scope = StructuredTaskScope.open()) {
            var activitiesTask = scope.fork(() -> scheduleRepository.findByBudgetIdOrderBySortOrder(budgetId));
            var weatherTask = scope.fork(() -> weatherRepository.findByBudgetIdOrderByDelayDateDesc(budgetId));
            scope.join();
            activities = activitiesTask.get();
            weatherDelays = weatherTask.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Forecast calculation interrupted", e);
        }

        if (activities.isEmpty()) {
            return new DelayForecast(0, 0, BigDecimal.ZERO, "LOW", List.of(), "No activities scheduled");
        }

        var plannedEnd = activities.stream().map(ScheduleActivity::getPlannedEnd).max(LocalDate::compareTo).orElse(LocalDate.now());
        var today = LocalDate.now();
        var remainingDays = Math.max(0, ChronoUnit.DAYS.between(today, plannedEnd));

        // Calculate SPI
        var totalWeight = activities.stream().map(ScheduleActivity::getWeight).reduce(BigDecimal.ZERO, BigDecimal::add);
        var earnedProgress = BigDecimal.ZERO;
        for (var a : activities) {
            if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
                var weightFraction = a.getWeight().divide(totalWeight, 6, RoundingMode.HALF_UP);
                earnedProgress = earnedProgress.add(weightFraction.multiply(a.getProgressPct()).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
            }
        }
        var spi = earnedProgress.compareTo(BigDecimal.ZERO) > 0 ? earnedProgress : new BigDecimal("0.01");

        // Weather delay analysis
        var totalWeatherDelays = weatherDelays.size();
        var avgHoursLostPerDelay = totalWeatherDelays > 0
                ? weatherDelays.stream().map(WeatherDelay::getHoursLost).reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(totalWeatherDelays), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        var projectDurationSoFar = ChronoUnit.DAYS.between(
                activities.stream().map(ScheduleActivity::getPlannedStart).min(LocalDate::compareTo).orElse(today), today);
        var delayFrequency = projectDurationSoFar > 0
                ? (double) totalWeatherDelays / projectDurationSoFar * 30 : 0.0;

        var predictedWeatherDelays = (int) Math.round(delayFrequency * remainingDays / 30);
        var weatherDelayDays = (int) Math.round(predictedWeatherDelays * avgHoursLostPerDelay.doubleValue() / 8);

        // Schedule delay based on SPI
        var scheduleDelayDays = 0;
        if (spi.compareTo(BigDecimal.ONE) < 0 && remainingDays > 0) {
            var adjustedRemaining = remainingDays / spi.doubleValue();
            scheduleDelayDays = (int) Math.round(adjustedRemaining - remainingDays);
        }

        var totalPredictedDelay = scheduleDelayDays + weatherDelayDays;

        var riskLevel = switch (totalPredictedDelay) {
            case int d when d <= 5 -> "LOW";
            case int d when d <= 15 -> "MEDIUM";
            case int d when d <= 30 -> "HIGH";
            default -> "CRITICAL";
        };

        var factors = new ArrayList<RiskFactor>();
        if (scheduleDelayDays > 0) {
            factors.add(new RiskFactor("SCHEDULE_BEHIND", "Schedule is behind (SPI=" + spi.setScale(2, RoundingMode.HALF_UP) + ")",
                    scheduleDelayDays + " days predicted delay"));
        }
        if (weatherDelayDays > 0) {
            factors.add(new RiskFactor("WEATHER_PATTERN", "Historical weather delays (" + String.format("%.1f", delayFrequency) + "/month)",
                    weatherDelayDays + " days predicted from weather"));
        }
        if (remainingDays > 180) {
            factors.add(new RiskFactor("LONG_DURATION", "Long remaining duration increases uncertainty",
                    remainingDays + " days remaining"));
        }

        var summary = totalPredictedDelay == 0
                ? "Project is on track. No significant delays predicted."
                : "Predicted total delay: " + totalPredictedDelay + " days (" + scheduleDelayDays + " schedule + " + weatherDelayDays + " weather)";

        return new DelayForecast(totalPredictedDelay, (int) remainingDays, spi, riskLevel, factors, summary);
    }

    public record DelayForecast(int predictedDelayDays, int remainingDays, BigDecimal spi,
                                String riskLevel, List<RiskFactor> riskFactors, String summary) {}
    public record RiskFactor(String type, String description, String impact) {}
}
