package com.sinapipro.api.procurement.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "quotation_email")
public class QuotationEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "quotation_id", nullable = false)
    private UUID quotationId;

    @Column(name = "supplier_id", nullable = false)
    private UUID supplierId;

    @Column(name = "supplier_email", nullable = false, length = 200)
    private String supplierEmail;

    @Column(nullable = false, length = 20)
    private String status = "PENDING"; // PENDING, SENT, FAILED

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public QuotationEmail() {}

    public QuotationEmail(UUID quotationId, UUID supplierId, String supplierEmail) {
        this.quotationId = quotationId;
        this.supplierId = supplierId;
        this.supplierEmail = supplierEmail;
    }

    public UUID getId() { return id; }
    public UUID getQuotationId() { return quotationId; }
    public UUID getSupplierId() { return supplierId; }
    public String getSupplierEmail() { return supplierEmail; }
    public String getStatus() { return status; }
    public Instant getSentAt() { return sentAt; }

    public void markSent() { this.status = "SENT"; this.sentAt = Instant.now(); }
    public void markFailed(String error) { this.status = "FAILED"; this.errorMessage = error; }
}
