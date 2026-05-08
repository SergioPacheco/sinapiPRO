package com.sinapipro.api.procurement.domain;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.shared.domain.AuditableEntity;
import com.sinapipro.api.supplier.domain.Supplier;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "purchase_order")
public class PurchaseOrder extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "quotation_response_id")
    private UUID quotationResponseId;

    @Column(name = "cost_code_id")
    private UUID costCodeId;

    @Column(nullable = false, length = 40, unique = true)
    private String number;

    @Column(nullable = false, length = 300)
    private String description;

    @Column(nullable = false, precision = 14, scale = 4)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false, precision = 14, scale = 4)
    private BigDecimal unitPrice;

    @Column(nullable = false, length = 20)
    private String status;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private List<Receiving> receivings = new ArrayList<>();

    protected PurchaseOrder() {}

    public PurchaseOrder(Budget budget, Supplier supplier, UUID quotationResponseId,
                         String number, String description, BigDecimal quantity, BigDecimal unitPrice, UUID costCodeId) {
        this.budget = budget;
        this.supplier = supplier;
        this.quotationResponseId = quotationResponseId;
        this.costCodeId = costCodeId;
        this.number = number;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.status = "PENDING";
    }

    public UUID getId() { return super.getId(); }
    public Budget getBudget() { return budget; }
    public Supplier getSupplier() { return supplier; }
    public UUID getQuotationResponseId() { return quotationResponseId; }
    public UUID getCostCodeId() { return costCodeId; }
    public String getNumber() { return number; }
    public String getDescription() { return description; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getTotalAmount() { return quantity.multiply(unitPrice); }
    public String getStatus() { return status; }
    public List<Receiving> getReceivings() { return receivings; }

    public BigDecimal getReceivedQuantity() {
        return receivings.stream().map(Receiving::getQuantityReceived).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void markPartiallyReceived() { this.status = "PARTIAL"; }
    public void markFullyReceived() { this.status = "RECEIVED"; }
}
