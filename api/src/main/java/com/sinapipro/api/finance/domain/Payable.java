package com.sinapipro.api.finance.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "payable")
public class Payable extends AuditableEntity {

    @Column(name = "budget_id", nullable = false)
    private UUID budgetId;

    @Column(name = "supplier_id")
    private UUID supplierId;

    @Column(name = "purchase_order_id")
    private UUID purchaseOrderId;

    @Column(name = "measurement_id")
    private UUID measurementId;

    @Column(nullable = false, length = 300)
    private String description;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @Column(name = "paid_amount", precision = 18, scale = 2)
    private BigDecimal paidAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(length = 60)
    private String category;

    @Column(length = 500)
    private String notes;

    protected Payable() {}

    public Payable(UUID budgetId, UUID supplierId, String description, BigDecimal amount,
                   LocalDate dueDate, String category) {
        this.budgetId = budgetId;
        this.supplierId = supplierId;
        this.description = description;
        this.amount = amount;
        this.dueDate = dueDate;
        this.category = category;
        this.status = PaymentStatus.PENDING;
    }

    public UUID getBudgetId() { return budgetId; }
    public UUID getSupplierId() { return supplierId; }
    public UUID getPurchaseOrderId() { return purchaseOrderId; }
    public UUID getMeasurementId() { return measurementId; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getPaidDate() { return paidDate; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public PaymentStatus getStatus() { return status; }
    public String getCategory() { return category; }
    public String getNotes() { return notes; }

    public void setPurchaseOrderId(UUID purchaseOrderId) { this.purchaseOrderId = purchaseOrderId; }
    public void setMeasurementId(UUID measurementId) { this.measurementId = measurementId; }
    public void setNotes(String notes) { this.notes = notes; }

    public void pay(BigDecimal paidAmount, LocalDate paidDate) {
        this.paidAmount = paidAmount;
        this.paidDate = paidDate;
        this.status = PaymentStatus.PAID;
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELLED;
    }
}
