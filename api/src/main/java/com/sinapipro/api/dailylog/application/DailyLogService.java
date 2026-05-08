package com.sinapipro.api.dailylog.application;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.dailylog.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class DailyLogService {

    private static final int MAX_EDIT_DAYS = 7;

    private final DailyLogRepository dailyLogRepository;
    private final BudgetRepository budgetRepository;

    public DailyLogService(DailyLogRepository dailyLogRepository, BudgetRepository budgetRepository) {
        this.dailyLogRepository = dailyLogRepository;
        this.budgetRepository = budgetRepository;
    }

    @Transactional
    public DailyLog create(UUID budgetId, LocalDate logDate, String weatherMorning, String weatherAfternoon,
                           String observations, List<LaborInput> labor, List<EquipmentInput> equipment,
                           List<OccurrenceInput> occurrences) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));

        validateLogDate(logDate);
        validateUniqueDate(budgetId, logDate);

        DailyLog log = new DailyLog(budget, logDate, weatherMorning, weatherAfternoon, observations);
        if (labor != null) {
            labor.forEach(l -> log.getLaborEntries().add(new DailyLogLabor(log, l.workerName(), l.role(), l.hours())));
        }
        if (equipment != null) {
            equipment.forEach(e -> log.getEquipmentEntries().add(new DailyLogEquipment(log, e.equipmentName(), e.hoursUsed(), e.hoursIdle())));
        }
        if (occurrences != null) {
            occurrences.forEach(o -> log.getOccurrences().add(new DailyLogOccurrence(log, o.type(), o.description())));
        }
        return dailyLogRepository.save(log);
    }

    public DailyLogSummary summary(UUID budgetId) {
        List<DailyLog> logs = dailyLogRepository.findByBudgetIdOrderByLogDateDesc(budgetId);

        BigDecimal totalLaborHours = BigDecimal.ZERO;
        BigDecimal totalEquipmentHours = BigDecimal.ZERO;
        int totalOccurrences = 0;

        for (DailyLog log : logs) {
            for (DailyLogLabor l : log.getLaborEntries()) {
                totalLaborHours = totalLaborHours.add(l.getHours());
            }
            for (DailyLogEquipment e : log.getEquipmentEntries()) {
                totalEquipmentHours = totalEquipmentHours.add(e.getHoursUsed());
            }
            totalOccurrences += log.getOccurrences().size();
        }

        return new DailyLogSummary(logs.size(), totalLaborHours, totalEquipmentHours, totalOccurrences);
    }

    public boolean canEdit(DailyLog log) {
        return !log.getLogDate().isBefore(LocalDate.now().minusDays(MAX_EDIT_DAYS));
    }

    private void validateLogDate(LocalDate logDate) {
        if (logDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Log date cannot be in the future");
        }
        if (logDate.isBefore(LocalDate.now().minusDays(MAX_EDIT_DAYS))) {
            throw new IllegalArgumentException("Cannot create log for dates older than " + MAX_EDIT_DAYS + " days");
        }
    }

    private void validateUniqueDate(UUID budgetId, LocalDate logDate) {
        List<DailyLog> existing = dailyLogRepository.findByBudgetIdOrderByLogDateDesc(budgetId);
        boolean duplicate = existing.stream().anyMatch(l -> l.getLogDate().equals(logDate));
        if (duplicate) {
            throw new IllegalArgumentException("A daily log already exists for date " + logDate + " in this budget");
        }
    }

    public record LaborInput(String workerName, String role, BigDecimal hours) {}
    public record EquipmentInput(String equipmentName, BigDecimal hoursUsed, BigDecimal hoursIdle) {}
    public record OccurrenceInput(String type, String description) {}
    public record DailyLogSummary(int totalLogs, BigDecimal totalLaborHours, BigDecimal totalEquipmentHours, int totalOccurrences) {}
}
