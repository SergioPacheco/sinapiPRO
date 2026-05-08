package com.sinapipro.api.procurement.domain;

import com.sinapipro.api.supplier.domain.Supplier;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "quotation_response")
public class QuotationResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id", nullable = false)
    private Quotation quotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "unit_price", nullable = false, precision = 14, scale = 4)
    private BigDecimal unitPrice;

    @Column(name = "delivery_days")
    private Integer deliveryDays;

    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() { createdAt = Instant.now(); }

    protected QuotationResponse() {}

    public QuotationResponse(Quotation quotation, Supplier supplier, BigDecimal unitPrice, Integer deliveryDays, String notes) {
        this.quotation = quotation;
        this.supplier = supplier;
        this.unitPrice = unitPrice;
        this.deliveryDays = deliveryDays;
        this.notes = notes;
    }

    public UUID getId() { return id; }
    public Quotation getQuotation() { return quotation; }
    public Supplier getSupplier() { return supplier; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public Integer getDeliveryDays() { return deliveryDays; }
    public String getNotes() { return notes; }
}
