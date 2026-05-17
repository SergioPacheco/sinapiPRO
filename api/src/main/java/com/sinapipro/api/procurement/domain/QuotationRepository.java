package com.sinapipro.api.procurement.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface QuotationRepository extends JpaRepository<Quotation, UUID> {
    List<Quotation> findByPurchaseRequestId(UUID purchaseRequestId);
    Page<Quotation> findByPurchaseRequestBudgetId(UUID budgetId, Pageable pageable);

    @Query("SELECT q FROM Quotation q JOIN q.responses r " +
            "WHERE q.purchaseRequest.budget.id = :budgetId AND r.id = :quotationResponseId")
    Page<Quotation> findByBudgetIdAndResponseId(UUID budgetId, UUID quotationResponseId, Pageable pageable);
}
