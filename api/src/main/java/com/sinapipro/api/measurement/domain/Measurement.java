package com.sinapipro.api.measurement.domain;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Root — Measurement (Medição de Obra).
 *
 * Invariantes protegidas:
 * - State machine: DRAFT → SUBMITTED → APPROVED → PAID (ou SUBMITTED → DRAFT via reject)
 * - Período deve ser válido (start <= end)
 * - Retenção entre 0 e 1
 *
 * Domain Events publicados nas transições de estado.
 */
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

    @Column(name = "change_order_id")
    private UUID changeOrderId;

    @Column(name = "imported_from", length = 100)
    private String importedFrom;

    @OneToMany(mappedBy = "measurement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MeasurementItem> items = new ArrayList<>();

    /** Domain events pendentes — coletados pelo service após save */
    @Transient
    private final List<MeasurementEvent> domainEvents = new ArrayList<>();

    protected Measurement() {}

    public Measurement(Budget budget, Integer number, LocalDate periodStart, LocalDate periodEnd, BigDecimal retentionPct) {
        if (periodStart.isAfter(periodEnd)) throw new IllegalArgumentException("periodStart must be <= periodEnd");
        if (retentionPct.compareTo(BigDecimal.ZERO) < 0 || retentionPct.compareTo(BigDecimal.ONE) > 0)
            throw new IllegalArgumentException("retentionPct must be between 0 and 1");
        this.budget = budget;
        this.number = number;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.retentionPct = retentionPct;
        this.status = MeasurementStatus.DRAFT;
    }

    // === State Machine (transições protegidas) ===

    public void submit() {
        requireStatus(MeasurementStatus.DRAFT, "submit");
        this.status = MeasurementStatus.SUBMITTED;
        registerEvent(new MeasurementEvent.Submitted(getId(), budget.getId(), number, getGrossAmount(), Instant.now()));
    }

    public void approve(String approvedBy) {
        requireStatus(MeasurementStatus.SUBMITTED, "approve");
        this.status = MeasurementStatus.APPROVED;
        registerEvent(new MeasurementEvent.Approved(getId(), budget.getId(), number, getNetAmount(), approvedBy, Instant.now()));
    }

    public void pay() {
        requireStatus(MeasurementStatus.APPROVED, "pay");
        this.status = MeasurementStatus.PAID;
        registerEvent(new MeasurementEvent.Paid(getId(), budget.getId(), number, getNetAmount(), Instant.now()));
    }

    public void reject(String reason) {
        requireStatus(MeasurementStatus.SUBMITTED, "reject");
        this.status = MeasurementStatus.DRAFT;
        this.rejectionReason = reason;
        registerEvent(new MeasurementEvent.Rejected(getId(), budget.getId(), number, reason, Instant.now()));
    }

    // === Calculated Values ===

    public BigDecimal getGrossAmount() {
        return items.stream().map(MeasurementItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getNetAmount() {
        BigDecimal gross = getGrossAmount();
        return gross.subtract(gross.multiply(retentionPct)).setScale(2, RoundingMode.HALF_UP);
    }

    // === Domain Event support ===

    private void registerEvent(MeasurementEvent event) {
        domainEvents.add(event);
    }

    /** Retorna e limpa eventos pendentes (chamado pelo service após save) */
    public List<MeasurementEvent> drainEvents() {
        var events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    // === Guards ===

    private void requireStatus(MeasurementStatus required, String action) {
        if (status != required)
            throw new IllegalStateException("Cannot " + action + " measurement in status " + status + " (requires " + required + ")");
    }

    // === Getters ===

    public Budget getBudget() { return budget; }
    public Integer getNumber() { return number; }
    public LocalDate getPeriodStart() { return periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; }
    public MeasurementStatus getStatus() { return status; }
    public BigDecimal getRetentionPct() { return retentionPct; }
    public String getNotes() { return notes; }
    public List<MeasurementItem> getItems() { return items; }
    public String getRejectionReason() { return rejectionReason; }
    public boolean isExtraItem() { return extraItem; }
    public UUID getChangeOrderId() { return changeOrderId; }
    public void setChangeOrderId(UUID changeOrderId) { this.changeOrderId = changeOrderId; }
    public String getImportedFrom() { return importedFrom; }
    public void setImportedFrom(String importedFrom) { this.importedFrom = importedFrom; }
}
