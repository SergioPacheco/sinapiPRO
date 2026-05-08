package com.sinapipro.api.procurement.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {
    List<PurchaseOrder> findByBudgetIdOrderByCreatedAtDesc(UUID budgetId);
    Page<PurchaseOrder> findByBudgetId(UUID budgetId, Pageable pageable);
}
