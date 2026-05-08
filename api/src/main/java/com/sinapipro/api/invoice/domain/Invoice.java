package com.sinapipro.api.invoice.domain;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.shared.domain.AuditableEntity;
import com.sinapipro.api.supplier.domain.Supplier;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoice")
public class Invoice extends AuditableEntity {

    @Column(nullable = false, unique = true, length = 40)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InvoiceStatus status;

    @Column(columnDefinition = "text")
    private String notes;

    protected Invoice() {}

    public Invoice(String number, Budget budget, Supplier supplier, BigDecimal amount,
                   LocalDate issueDate, LocalDate dueDate, InvoiceStatus status, String notes) {
        this.number = number;
        this.budget = budget;
        this.supplier = supplier;
        this.amount = amount;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = status;
        this.notes = notes;
    }

    public String getNumber() { return number; }
    public Budget getBudget() { return budget; }
    public Supplier getSupplier() { return supplier; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public InvoiceStatus getStatus() { return status; }
    public String getNotes() { return notes; }

    public void update(BigDecimal amount, LocalDate dueDate, InvoiceStatus status, String notes) {
        this.amount = amount;
        this.dueDate = dueDate;
        this.status = status;
        this.notes = notes;
    }
}
