package com.sinapipro.api.budget.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Gerador de propostas para pregão — aplica descontos sobre o orçamento
 * para simular cenários de preço em licitações.
 */
@Entity
@Table(name = "budget_proposal")
public class BudgetProposal extends TenantAwareEntity {


    @Column(name = "budget_id", nullable = false)
    private UUID budgetId;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(name = "discount_pct", nullable = false, precision = 6, scale = 4)
    private BigDecimal discountPct;

    @Column(name = "original_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal originalValue;

    @Column(name = "proposed_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal proposedValue;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public BudgetProposal() {}

    public BudgetProposal(UUID budgetId, String description, BigDecimal discountPct, BigDecimal originalValue) {
        this.budgetId = budgetId;
        this.description = description;
        this.discountPct = discountPct;
        this.originalValue = originalValue;
        this.proposedValue = originalValue.multiply(BigDecimal.ONE.subtract(discountPct))
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public UUID getBudgetId() { return budgetId; }
    public String getDescription() { return description; }
    public BigDecimal getDiscountPct() { return discountPct; }
    public BigDecimal getOriginalValue() { return originalValue; }
    public BigDecimal getProposedValue() { return proposedValue; }
}
