package com.sinapipro.api.timetracking.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TimesheetRepository extends JpaRepository<TimesheetEntry, UUID> {
    Page<TimesheetEntry> findByBudgetId(UUID budgetId, Pageable pageable);
    List<TimesheetEntry> findByBudgetIdAndWorkerNameOrderByWorkDateDesc(UUID budgetId, String workerName);

    @Query("SELECT COALESCE(SUM(t.regularHours + t.overtimeHours), 0) FROM TimesheetEntry t WHERE t.budgetId = :budgetId")
    BigDecimal sumTotalHoursByBudget(UUID budgetId);

    @Query("SELECT COALESCE(SUM(t.unitsProduced), 0) FROM TimesheetEntry t WHERE t.budgetId = :budgetId AND t.unitsProduced IS NOT NULL")
    BigDecimal sumUnitsProducedByBudget(UUID budgetId);
}
