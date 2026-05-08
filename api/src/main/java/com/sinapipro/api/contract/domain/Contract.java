package com.sinapipro.api.contract.domain;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.shared.domain.AuditableEntity;
import com.sinapipro.api.supplier.domain.Supplier;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contract")
public class Contract extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(nullable = false, length = 40)
    private String number;

    @Column(nullable = false, length = 300)
    private String description;

    @Column(name = "original_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal originalValue;

    @Column(name = "retention_pct", nullable = false, precision = 5, scale = 4)
    private BigDecimal retentionPct;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContractStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChangeOrder> changeOrders = new ArrayList<>();

    protected Contract() {}

    public Contract(Budget budget, Supplier supplier, String number, String description,
                    BigDecimal originalValue, BigDecimal retentionPct, LocalDate startDate, LocalDate endDate) {
        this.budget = budget;
        this.supplier = supplier;
        this.number = number;
        this.description = description;
        this.originalValue = originalValue;
        this.retentionPct = retentionPct;
        this.status = ContractStatus.DRAFT;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Budget getBudget() { return budget; }
    public Supplier getSupplier() { return supplier; }
    public String getNumber() { return number; }
    public String getDescription() { return description; }
    public BigDecimal getOriginalValue() { return originalValue; }
    public BigDecimal getRetentionPct() { return retentionPct; }
    public ContractStatus getStatus() { return status; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public List<ChangeOrder> getChangeOrders() { return changeOrders; }

    public BigDecimal getUpdatedValue() {
        BigDecimal additions = changeOrders.stream()
                .filter(co -> co.getStatus() == ChangeOrderStatus.APPROVED)
                .map(ChangeOrder::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return originalValue.add(additions);
    }

    public void activate() {
        if (status != ContractStatus.DRAFT) throw new IllegalStateException("Can only activate DRAFT contracts");
        this.status = ContractStatus.ACTIVE;
    }

    public void complete() {
        if (status != ContractStatus.ACTIVE) throw new IllegalStateException("Can only complete ACTIVE contracts");
        this.status = ContractStatus.COMPLETED;
    }

    public void cancel() {
        if (status == ContractStatus.COMPLETED) throw new IllegalStateException("Cannot cancel COMPLETED contracts");
        this.status = ContractStatus.CANCELLED;
    }
}
