package com.sinapipro.api.procurement.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "receiving")
public class Receiving {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @Column(name = "quantity_received", nullable = false, precision = 14, scale = 4)
    private BigDecimal quantityReceived;

    @Column(name = "received_at", nullable = false)
    private LocalDate receivedAt;

    @Column(length = 300)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() { createdAt = Instant.now(); }

    protected Receiving() {}

    public Receiving(PurchaseOrder purchaseOrder, BigDecimal quantityReceived, LocalDate receivedAt, String notes) {
        this.purchaseOrder = purchaseOrder;
        this.quantityReceived = quantityReceived;
        this.receivedAt = receivedAt;
        this.notes = notes;
    }

    public UUID getId() { return id; }
    public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
    public BigDecimal getQuantityReceived() { return quantityReceived; }
    public LocalDate getReceivedAt() { return receivedAt; }
    public String getNotes() { return notes; }
}
