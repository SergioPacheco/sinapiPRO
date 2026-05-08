package com.sinapipro.api.jobcosting.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CostCodeRepository extends JpaRepository<CostCode, UUID> {

    List<CostCode> findByBudgetIdOrderByCode(UUID budgetId);

    @Query("SELECT c FROM CostCode c WHERE c.budget.id = :budgetId AND c.parent IS NULL ORDER BY c.code")
    List<CostCode> findRootCodes(UUID budgetId);
}
