package com.sinapipro.api.schedule.application;

import module java.base;

import com.sinapipro.api.schedule.domain.ScheduleActivity;
import com.sinapipro.api.schedule.domain.ScheduleActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SCurveService {

    private final ScheduleActivityRepository repository;

    public SCurveService(ScheduleActivityRepository repository) {
        this.repository = repository;
    }

    public SCurveData calculate(UUID budgetId) {
        var activities = repository.findByBudgetIdOrderBySortOrder(budgetId);
        if (activities.isEmpty()) return new SCurveData(List.of());

        var minDate = activities.stream().map(ScheduleActivity::getPlannedStart).min(LocalDate::compareTo).orElse(LocalDate.now());
        var maxDate = activities.stream().map(ScheduleActivity::getPlannedEnd).max(LocalDate::compareTo).orElse(LocalDate.now());

        var points = new ArrayList<SCurvePoint>();
        var cumulativePlanned = BigDecimal.ZERO;
        var cumulativeActual = BigDecimal.ZERO;

        var current = minDate.withDayOfMonth(1);
        while (!current.isAfter(maxDate)) {
            var periodEnd = current.plusMonths(1).minusDays(1);

            for (var a : activities) {
                var weight = a.getWeight();
                var totalDays = ChronoUnit.DAYS.between(a.getPlannedStart(), a.getPlannedEnd()) + 1;
                if (totalDays <= 0) continue;

                var overlapStart = current.isBefore(a.getPlannedStart()) ? a.getPlannedStart() : current;
                var overlapEnd = periodEnd.isAfter(a.getPlannedEnd()) ? a.getPlannedEnd() : periodEnd;
                if (!overlapStart.isAfter(overlapEnd)) {
                    var daysInPeriod = ChronoUnit.DAYS.between(overlapStart, overlapEnd) + 1;
                    var fraction = BigDecimal.valueOf(daysInPeriod).divide(BigDecimal.valueOf(totalDays), 6, RoundingMode.HALF_UP);
                    cumulativePlanned = cumulativePlanned.add(weight.multiply(fraction));
                }
            }

            var actualSnapshot = BigDecimal.ZERO;
            for (var a : activities) {
                if (a.getActualStart() != null && !a.getActualStart().isAfter(periodEnd)) {
                    actualSnapshot = actualSnapshot.add(a.getWeight().multiply(a.getProgressPct()).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
                }
            }
            cumulativeActual = actualSnapshot;

            points.add(new SCurvePoint(current, cumulativePlanned.setScale(4, RoundingMode.HALF_UP),
                    cumulativeActual.setScale(4, RoundingMode.HALF_UP)));
            current = current.plusMonths(1);
        }
        return new SCurveData(points);
    }

    public record SCurveData(List<SCurvePoint> points) {}
    public record SCurvePoint(LocalDate period, BigDecimal plannedCumulative, BigDecimal actualCumulative) {}
}
