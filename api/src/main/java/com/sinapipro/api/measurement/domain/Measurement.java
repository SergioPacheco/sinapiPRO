package com.sinapipro.api.measurement.domain;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "measurement", uniqueConstraints = @UniqueConstraint(columnNames = {"budget_id", "number"}))
public class Measurement extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @Column(nullable = false)
    private Integer number;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MeasurementStatus status;

    @Column(name = "retention_pct", nullable = false, precision = 5, scale = 4)
    private BigDecimal retentionPct;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "extra_item")
    private boolean extraItem = false;

    @OneToMany(mappedBy = "measurement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeasurementItem> items = new ArrayList<>();

    protected Measurement() {}

    public Measurement(Budget budget, Integer number, LocalDate periodStart, LocalDate periodEnd, BigDecimal retentionPct) {
        this.budget = budget;
        this.number = number;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.retentionPct = retentionPct;
        this.status = MeasurementStatus.DRAFT;
    }

    public Budget getBudget() { return budget; }
    public Integer getNumber() { return number; }
    public LocalDate getPeriodStart() { return periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; }
    public MeasurementStatus getStatus() { return status; }
    public BigDecimal getRetentionPct() { return retentionPct; }
    public String getNotes() { return notes; }
    public List<MeasurementItem> getItems() { return items; }

    public BigDecimal getGrossAmount() {
        return items.stream().map(MeasurementItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getNetAmount() {
        BigDecimal gross = getGrossAmount();
        return gross.subtract(gross.multiply(retentionPct)).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public void submit() {
        if (status != MeasurementStatus.DRAFT) throw new IllegalStateException("Can only submit DRAFT measurements");
        this.status = MeasurementStatus.SUBMITTED;
    }

    public void approve() {
        if (status != MeasurementStatus.SUBMITTED) throw new IllegalStateException("Can only approve SUBMITTED measurements");
        this.status = MeasurementStatus.APPROVED;
    }

    public void pay() {
        if (status != MeasurementStatus.APPROVED) throw new IllegalStateException("Can only pay APPROVED measurements");
        this.status = MeasurementStatus.PAID;
    }

    public void reject(String reason) {
        if (status != MeasurementStatus.SUBMITTED) throw new IllegalStateException("Can only reject SUBMITTED measurements");
        this.status = MeasurementStatus.DRAFT;
        this.rejectionReason = reason;
    }

    public String getRejectionReason() { return rejectionReason; }
    public boolean isExtraItem() { return extraItem; }
}
