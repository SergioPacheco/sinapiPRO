package com.sinapipro.api.budget.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BdiConfigRepository extends JpaRepository<BdiConfig, UUID> {
    Optional<BdiConfig> findByBudgetId(UUID budgetId);
    Optional<BdiConfig> findByBudgetIdAndItemType(UUID budgetId, String itemType);
    List<BdiConfig> findAllByBudgetId(UUID budgetId);
}
