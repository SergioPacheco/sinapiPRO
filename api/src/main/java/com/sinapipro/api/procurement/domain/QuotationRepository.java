package com.sinapipro.api.procurement.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuotationRepository extends JpaRepository<Quotation, UUID> {
    List<Quotation> findByPurchaseRequestId(UUID purchaseRequestId);
}
