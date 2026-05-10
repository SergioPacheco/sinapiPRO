package com.sinapipro.api.budget.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface BudgetItemMemoRepository extends JpaRepository<BudgetItemMemo, UUID> {
    Optional<BudgetItemMemo> findByBudgetItemId(UUID budgetItemId);
}
