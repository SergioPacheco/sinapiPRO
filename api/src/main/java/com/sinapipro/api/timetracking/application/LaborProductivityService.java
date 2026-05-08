package com.sinapipro.api.timetracking.application;

import com.sinapipro.api.timetracking.domain.TimesheetEntry;
import com.sinapipro.api.timetracking.domain.TimesheetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LaborProductivityService {

    private final TimesheetRepository repository;

    public LaborProductivityService(TimesheetRepository repository) {
        this.repository = repository;
    }

    public ProductivityReport calculate(UUID budgetId) {
        BigDecimal totalHours = repository.sumTotalHoursByBudget(budgetId);
        BigDecimal totalUnits = repository.sumUnitsProducedByBudget(budgetId);

        BigDecimal hoursPerUnit = totalUnits.compareTo(BigDecimal.ZERO) > 0
                ? totalHours.divide(totalUnits, 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal unitsPerHour = totalHours.compareTo(BigDecimal.ZERO) > 0
                ? totalUnits.divide(totalHours, 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Productivity by role
        List<TimesheetEntry> all = repository.findByBudgetId(budgetId, org.springframework.data.domain.Pageable.unpaged()).getContent();
        Map<String, RoleStats> byRole = new LinkedHashMap<>();
        for (TimesheetEntry e : all) {
            byRole.computeIfAbsent(e.getRole(), k -> new RoleStats()).add(e);
        }

        List<RoleProductivity> roleMetrics = byRole.entrySet().stream()
                .map(entry -> {
                    RoleStats stats = entry.getValue();
                    BigDecimal roleHoursPerUnit = stats.units.compareTo(BigDecimal.ZERO) > 0
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
