package com.sinapipro.api.jobcosting.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CostTransactionRepository extends JpaRepository<CostTransaction, UUID> {

    List<CostTransaction> findByCostCodeIdOrderByTransactionDateDesc(UUID costCodeId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM CostTransaction t WHERE t.costCode.id = :costCodeId AND t.type = :type")
    BigDecimal sumByCodeAndType(UUID costCodeId, CostTransactionType type);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM CostTransaction t WHERE t.costCode.budget.id = :budgetId AND t.type = :type")
    BigDecimal sumByBudgetAndType(UUID budgetId, CostTransactionType type);

    boolean existsByReferenceIdAndType(UUID referenceId, CostTransactionType type);
}
