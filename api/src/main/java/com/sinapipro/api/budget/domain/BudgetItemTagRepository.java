package com.sinapipro.api.budget.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BudgetItemTagRepository extends JpaRepository<BudgetItemTag, UUID> {
    List<BudgetItemTag> findByBudgetItemId(UUID budgetItemId);
    void deleteByBudgetItemIdAndTag(UUID budgetItemId, String tag);
}
