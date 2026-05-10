package com.sinapipro.api.procurement.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {
    List<PurchaseOrder> findByBudgetIdOrderByCreatedAtDesc(UUID budgetId);
    Page<PurchaseOrder> findByBudgetId(UUID budgetId, Pageable pageable);

    @Query("SELECT po FROM PurchaseOrder po WHERE po.budget.id = :budgetId " +
           "AND po.expectedDeliveryDate < :today " +
           "AND po.status IN ('PENDING', 'APPROVED', 'PARTIAL')")
    List<PurchaseOrder> findOverdue(UUID budgetId, LocalDate today);
}
