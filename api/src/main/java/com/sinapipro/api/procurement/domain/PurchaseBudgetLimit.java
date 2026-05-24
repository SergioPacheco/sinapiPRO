package com.sinapipro.api.procurement.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "purchase_budget_limit")
public class PurchaseBudgetLimit extends TenantAwareEntity {
    @Column(name = "project_id", nullable = false) private UUID projectId;
    @Column(name = "period_start", nullable = false) private LocalDate periodStart;
    @Column(name = "period_end", nullable = false) private LocalDate periodEnd;
    @Column(name = "limit_amount", nullable = false, precision = 18, scale = 2) private BigDecimal limitAmount;
    @Column(name = "consumed_amount", precision = 18, scale = 2) private BigDecimal consumedAmount = BigDecimal.ZERO;
    @Column(name = "requires_auth_above", precision = 18, scale = 2) private BigDecimal requiresAuthAbove;

    protected PurchaseBudgetLimit() {}

    public PurchaseBudgetLimit(UUID projectId, LocalDate periodStart, LocalDate periodEnd,
                                BigDecimal limitAmount, BigDecimal requiresAuthAbove) {
        this.projectId = projectId;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.limitAmount = limitAmount;
        this.requiresAuthAbove = requiresAuthAbove;
    }

    public UUID getProjectId() { return projectId; }
    public LocalDate getPeriodStart() { return periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; }
    public BigDecimal getLimitAmount() { return limitAmount; }
    public BigDecimal getConsumedAmount() { return consumedAmount; }
    public BigDecimal getRequiresAuthAbove() { return requiresAuthAbove; }
    public BigDecimal getAvailableAmount() { return limitAmount.subtract(consumedAmount); }

    public boolean canConsume(BigDecimal amount) {
        return getAvailableAmount().compareTo(amount) >= 0;
    }

    public boolean requiresAuthorization(BigDecimal amount) {
        return requiresAuthAbove != null && amount.compareTo(requiresAuthAbove) > 0;
    }

    public void consume(BigDecimal amount) {
        if (!canConsume(amount)) throw new IllegalStateException("Budget limit exceeded");
        this.consumedAmount = this.consumedAmount.add(amount);
    }
}
