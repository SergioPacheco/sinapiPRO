package com.sinapipro.api.measurement.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "measurement_item")
public class MeasurementItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measurement_id", nullable = false)
    private Measurement measurement;

    @Column(name = "cost_code_id")
    private UUID costCodeId;

    @Column(nullable = false, length = 300)
    private String description;

    @Column(nullable = false, precision = 14, scale = 4)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false, precision = 14, scale = 4)
    private BigDecimal unitPrice;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() { createdAt = Instant.now(); }

    protected MeasurementItem() {}

    public MeasurementItem(Measurement measurement, UUID costCodeId, String description, BigDecimal quantity, BigDecimal unitPrice) {
        this.measurement = measurement;
        this.costCodeId = costCodeId;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public UUID getId() { return id; }
    public Measurement getMeasurement() { return measurement; }
    public UUID getCostCodeId() { return costCodeId; }
    public String getDescription() { return description; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getAmount() { return quantity.multiply(unitPrice).setScale(2, java.math.RoundingMode.HALF_UP); }
}
