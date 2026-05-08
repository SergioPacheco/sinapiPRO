package com.sinapipro.api.budget.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface BudgetStageRepository extends JpaRepository<BudgetStage, UUID> {

    @Query("SELECT s FROM BudgetStage s WHERE s.budget.id = :budgetId AND s.parent IS NULL ORDER BY s.sortOrder")
    List<BudgetStage> findRootStages(UUID budgetId);

    List<BudgetStage> findByBudgetIdOrderBySortOrder(UUID budgetId);
}
