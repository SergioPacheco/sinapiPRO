package com.sinapipro.api.forecast.application;

import com.sinapipro.api.schedule.domain.ScheduleActivity;
import com.sinapipro.api.schedule.domain.ScheduleActivityRepository;
import com.sinapipro.api.weather.domain.WeatherDelay;
import com.sinapipro.api.weather.domain.WeatherDelayRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * AI Delay Forecast — predicts project delays based on:
 * 1. Historical weather delay patterns (frequency, avg hours lost)
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
        List<ScheduleActivity> activities = scheduleRepository.findByBudgetIdOrderBySortOrder(budgetId);
        List<WeatherDelay> weatherDelays = weatherRepository.findByBudgetIdOrderByDelayDateDesc(budgetId);

        if (activities.isEmpty()) {
            return new DelayForecast(0, 0, BigDecimal.ZERO, "LOW", List.of(), "No activities scheduled");
        }

        // Calculate schedule metrics
        LocalDate plannedEnd = activities.stream().map(ScheduleActivity::getPlannedEnd).max(LocalDate::compareTo).orElse(LocalDate.now());
        LocalDate today = LocalDate.now();
        long remainingDays = Math.max(0, ChronoUnit.DAYS.between(today, plannedEnd));

        // Calculate SPI (Schedule Performance Index)
        BigDecimal totalWeight = activities.stream().map(ScheduleActivity::getWeight).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal earnedProgress = BigDecimal.ZERO;
        for (ScheduleActivity a : activities) {
            if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal weightFraction = a.getWeight().divide(totalWeight, 6, RoundingMode.HALF_UP);
                earnedProgress = earnedProgress.add(weightFraction.multiply(a.getProgressPct()).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
            }
        }
        BigDecimal spi = earnedProgress.compareTo(BigDecimal.ZERO) > 0 ? earnedProgress : new BigDecimal("0.01");

        // Weather delay analysis
        int totalWeatherDelays = weatherDelays.size();
        BigDecimal avgHoursLostPerDelay = totalWeatherDelays > 0
                ? weatherDelays.stream().map(WeatherDelay::getHoursLost).reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(totalWeatherDelays), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Calculate delay frequency (delays per 30 days)
        long projectDurationSoFar = ChronoUnit.DAYS.between(
                activities.stream().map(ScheduleActivity::getPlannedStart).min(LocalDate::compareTo).orElse(today), today);
        double delayFrequency = projectDurationSoFar > 0
                ? (double) totalWeatherDelays / projectDurationSoFar * 30 : 0;

        // Predict future weather delays based on historical frequency
        int predictedWeatherDelays = (int) Math.round(delayFrequency * remainingDays / 30);
        int weatherDelayDays = (int) Math.round(predictedWeatherDelays * avgHoursLostPerDelay.doubleValue() / 8);

        // Schedule delay based on SPI
        int scheduleDelayDays = 0;
        if (spi.compareTo(BigDecimal.ONE) < 0 && remainingDays > 0) {
            // If SPI < 1, project is behind. Estimate delay = remaining / SPI - remaining
            double adjustedRemaining = remainingDays / spi.doubleValue();
            scheduleDelayDays = (int) Math.round(adjustedRemaining - remainingDays);
        }

        int totalPredictedDelay = scheduleDelayDays + weatherDelayDays;

        // Risk level
        String riskLevel;
        if (totalPredictedDelay <= 5) riskLevel = "LOW";
        else if (totalPredictedDelay <= 15) riskLevel = "MEDIUM";
        else if (totalPredictedDelay <= 30) riskLevel = "HIGH";
        else riskLevel = "CRITICAL";

        // Risk factors
        List<RiskFactor> factors = new ArrayList<>();
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

        String summary = totalPredictedDelay == 0
                ? "Project is on track. No significant delays predicted."
                : "Predicted total delay: " + totalPredictedDelay + " days (" + scheduleDelayDays + " schedule + " + weatherDelayDays + " weather)";

        return new DelayForecast(totalPredictedDelay, (int) remainingDays, spi, riskLevel, factors, summary);
    }

    public record DelayForecast(int predictedDelayDays, int remainingDays, BigDecimal spi,
                                String riskLevel, List<RiskFactor> riskFactors, String summary) {}
    public record RiskFactor(String type, String description, String impact) {}
}
