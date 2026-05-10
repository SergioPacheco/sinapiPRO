package com.sinapipro.api.procurement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface QuotationEmailRepository extends JpaRepository<QuotationEmail, UUID> {
    List<QuotationEmail> findByQuotationId(UUID quotationId);
}
