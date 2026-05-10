package com.sinapipro.api.procurement.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "purchase_order_cost_distribution")
public class PurchaseOrderCostDistribution {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "purchase_order_id", nullable = false) private PurchaseOrder purchaseOrder;
    @Column(name = "cost_code_id", nullable = false) private UUID costCodeId;
    @Column(nullable = false, precision = 5, scale = 4) private BigDecimal percentage;
    @Column(nullable = false, precision = 18, scale = 2) private BigDecimal amount;

    protected PurchaseOrderCostDistribution() {}
    public PurchaseOrderCostDistribution(PurchaseOrder purchaseOrder, UUID costCodeId, BigDecimal percentage, BigDecimal amount) {
        this.purchaseOrder = purchaseOrder; this.costCodeId = costCodeId; this.percentage = percentage; this.amount = amount;
    }

    public UUID getId() { return id; }
    public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
    public UUID getCostCodeId() { return costCodeId; }
    public BigDecimal getPercentage() { return percentage; }
    public BigDecimal getAmount() { return amount; }
}
