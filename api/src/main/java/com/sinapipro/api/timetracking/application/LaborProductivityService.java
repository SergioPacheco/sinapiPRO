package com.sinapipro.api.timetracking.application;

import module java.base;

import com.sinapipro.api.timetracking.domain.TimesheetEntry;
import com.sinapipro.api.timetracking.domain.TimesheetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LaborProductivityService {

    private final TimesheetRepository repository;

    public LaborProductivityService(TimesheetRepository repository) {
        this.repository = repository;
    }

    public ProductivityReport calculate(UUID budgetId) {
        var totalHours = repository.sumTotalHoursByBudget(budgetId);
        var totalUnits = repository.sumUnitsProducedByBudget(budgetId);

        var hoursPerUnit = totalUnits.compareTo(BigDecimal.ZERO) > 0
                ? totalHours.divide(totalUnits, 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        var unitsPerHour = totalHours.compareTo(BigDecimal.ZERO) > 0
                ? totalUnits.divide(totalHours, 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        var all = repository.findByBudgetId(budgetId, org.springframework.data.domain.Pageable.unpaged()).getContent();
        var byRole = new LinkedHashMap<String, RoleStats>();
        for (var e : all) {
            byRole.computeIfAbsent(e.getRole(), _ -> new RoleStats()).add(e);
        }

        var roleMetrics = byRole.entrySet().stream()
                .map(entry -> {
                    var stats = entry.getValue();
                    var roleHoursPerUnit = stats.units.compareTo(BigDecimal.ZERO) > 0
                            ? stats.hours.divide(stats.units, 4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                    return new RoleProductivity(entry.getKey(), stats.hours, stats.units, stats.cost, roleHoursPerUnit);
                })
                .sorted(Comparator.comparing(RoleProductivity::totalCost).reversed())
                .toList();

        return new ProductivityReport(totalHours, totalUnits, hoursPerUnit, unitsPerHour, roleMetrics);
    }

    private static class RoleStats {
        BigDecimal hours = BigDecimal.ZERO;
        BigDecimal units = BigDecimal.ZERO;
        BigDecimal cost = BigDecimal.ZERO;
        void add(TimesheetEntry e) {
            hours = hours.add(e.getTotalHours());
            if (e.getUnitsProduced() != null) units = units.add(e.getUnitsProduced());
            cost = cost.add(e.getLaborCost());
        }
    }

    public record ProductivityReport(BigDecimal totalHours, BigDecimal totalUnits,
                                     BigDecimal hoursPerUnit, BigDecimal unitsPerHour,
                                     List<RoleProductivity> byRole) {}
    public record RoleProductivity(String role, BigDecimal totalHours, BigDecimal totalUnits,
                                   BigDecimal totalCost, BigDecimal hoursPerUnit) {}
}
