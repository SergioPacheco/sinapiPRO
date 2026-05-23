package com.sinapipro.api.finance.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "receivable")
public class Receivable extends AuditableEntity {

    @Column(name = "budget_id", nullable = false)
    private UUID budgetId;

    @Column(name = "project_id")
    private UUID projectId;

    @Column(name = "measurement_id")
    private UUID measurementId;

    @Column(name = "invoice_id")
    private UUID invoiceId;

    @Column(nullable = false, length = 300)
    private String description;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "received_date")
    private LocalDate receivedDate;

    @Column(name = "received_amount", precision = 18, scale = 2)
    private BigDecimal receivedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(length = 60)
    private String category;

    @Column(length = 500)
    private String notes;

    protected Receivable() {}

    public Receivable(UUID budgetId, String description, BigDecimal amount, LocalDate dueDate, String category) {
        this.budgetId = budgetId;
        this.description = description;
        this.amount = amount;
        this.dueDate = dueDate;
        this.category = category;
        this.status = PaymentStatus.PENDING;
    }

    public UUID getBudgetId() { return budgetId; }
    public UUID getProjectId() { return projectId; }
    public void setProjectId(UUID projectId) { this.projectId = projectId; }
    public UUID getMeasurementId() { return measurementId; }
    public UUID getInvoiceId() { return invoiceId; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReceivedDate() { return receivedDate; }
    public BigDecimal getReceivedAmount() { return receivedAmount; }
    public PaymentStatus getStatus() { return status; }
    public String getCategory() { return category; }
    public String getNotes() { return notes; }

    public void setMeasurementId(UUID measurementId) { this.measurementId = measurementId; }
    public void setInvoiceId(UUID invoiceId) { this.invoiceId = invoiceId; }
    public void setNotes(String notes) { this.notes = notes; }

    public void receive(BigDecimal receivedAmount, LocalDate receivedDate) {
        this.receivedAmount = receivedAmount;
        this.receivedDate = receivedDate;
        this.status = PaymentStatus.PAID;
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELLED;
    }
}
