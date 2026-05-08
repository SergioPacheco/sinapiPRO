package com.sinapipro.api.budget.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface BudgetItemRepository extends JpaRepository<BudgetItem, UUID> {

    List<BudgetItem> findByStageId(UUID stageId);

    @Query("SELECT i FROM BudgetItem i JOIN FETCH i.composition WHERE i.stage.budget.id = :budgetId")
    List<BudgetItem> findAllByBudgetId(UUID budgetId);

    @Query("SELECT COALESCE(SUM(i.quantity * i.unitCost), 0) FROM BudgetItem i WHERE i.stage.budget.id = :budgetId")
    BigDecimal sumDirectCostByBudget(UUID budgetId);

    @Query("SELECT COALESCE(SUM(i.quantity * i.unitCost * (1 + i.bdiPct)), 0) FROM BudgetItem i WHERE i.stage.id = :stageId")
    BigDecimal sumTotalByStage(UUID stageId);
}
