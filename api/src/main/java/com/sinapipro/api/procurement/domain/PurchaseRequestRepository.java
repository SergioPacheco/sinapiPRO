package com.sinapipro.api.procurement.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, UUID> {
    List<PurchaseRequest> findByBudgetIdOrderByCreatedAtDesc(UUID budgetId);
    Page<PurchaseRequest> findByBudgetId(UUID budgetId, Pageable pageable);
}
