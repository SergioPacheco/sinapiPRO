package com.sinapipro.api.inventory.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockItemRepository extends JpaRepository<StockItem, UUID> {
    List<StockItem> findByBudgetIdOrderByDescription(UUID budgetId);
    Optional<StockItem> findByBudgetIdAndDescription(UUID budgetId, String description);
}
