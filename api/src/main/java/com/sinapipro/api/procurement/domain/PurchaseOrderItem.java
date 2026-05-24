package com.sinapipro.api.procurement.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "purchase_order_item")
public class PurchaseOrderItem {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "order_id", nullable = false) private UUID orderId;
    @Column(name = "material_id") private UUID materialId;
    @Column(nullable = false, length = 300) private String description;
    @Column(nullable = false, length = 20) private String unit;
    @Column(nullable = false, precision = 14, scale = 4) private BigDecimal quantity;
    @Column(name = "unit_price", nullable = false, precision = 14, scale = 4) private BigDecimal unitPrice;
    @Column(name = "total_price", nullable = false, precision = 18, scale = 2) private BigDecimal totalPrice;
    @Column(name = "received_quantity", precision = 14, scale = 4) private BigDecimal receivedQuantity = BigDecimal.ZERO;
    @Column(name = "budget_item_id") private UUID budgetItemId;
    @Column(name = "cost_code_id") private UUID costCodeId;
    @Column(length = 300) private String notes;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;

    @PrePersist void prePersist() { createdAt = Instant.now(); }
    protected PurchaseOrderItem() {}

    public PurchaseOrderItem(UUID orderId, UUID materialId, String description, String unit,
                              BigDecimal quantity, BigDecimal unitPrice) {
        this.orderId = orderId;
        this.materialId = materialId;
        this.description = description;
        this.unit = unit;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = quantity.multiply(unitPrice).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public UUID getMaterialId() { return materialId; }
    public String getDescription() { return description; }
    public String getUnit() { return unit; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public BigDecimal getReceivedQuantity() { return receivedQuantity; }
    public UUID getBudgetItemId() { return budgetItemId; }
    public UUID getCostCodeId() { return costCodeId; }
    public String getNotes() { return notes; }

    public void setBudgetItemId(UUID budgetItemId) { this.budgetItemId = budgetItemId; }
    public void setCostCodeId(UUID costCodeId) { this.costCodeId = costCodeId; }

    public void receiveQuantity(BigDecimal qty) {
        this.receivedQuantity = this.receivedQuantity.add(qty);
    }

    public BigDecimal getPendingQuantity() {
        return quantity.subtract(receivedQuantity);
    }

    public boolean isFullyReceived() {
        return receivedQuantity.compareTo(quantity) >= 0;
    }
}
