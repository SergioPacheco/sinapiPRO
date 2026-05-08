package com.sinapipro.api.procurement.domain;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "purchase_request")
public class PurchaseRequest extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @Column(name = "cost_code_id")
    private UUID costCodeId;

    @Column(nullable = false, length = 300)
    private String description;

    @Column(nullable = false, precision = 14, scale = 4)
    private BigDecimal quantity;

    @Column(nullable = false, length = 20)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PurchaseRequestStatus status;

    @Column(name = "requested_by", length = 140)
    private String requestedBy;

    protected PurchaseRequest() {}

    public PurchaseRequest(Budget budget, UUID costCodeId, String description, BigDecimal quantity, String unit, String requestedBy) {
        this.budget = budget;
        this.costCodeId = costCodeId;
        this.description = description;
        this.quantity = quantity;
        this.unit = unit;
        this.requestedBy = requestedBy;
        this.status = PurchaseRequestStatus.OPEN;
    }

    public Budget getBudget() { return budget; }
    public UUID getCostCodeId() { return costCodeId; }
    public String getDescription() { return description; }
    public BigDecimal getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public PurchaseRequestStatus getStatus() { return status; }
    public String getRequestedBy() { return requestedBy; }

    public void close() { this.status = PurchaseRequestStatus.CLOSED; }
}
