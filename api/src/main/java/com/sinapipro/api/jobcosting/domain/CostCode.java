package com.sinapipro.api.jobcosting.domain;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cost_code", uniqueConstraints = @UniqueConstraint(columnNames = {"budget_id", "code"}))
public class CostCode extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CostCode parent;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "budgeted_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal budgetedAmount;

    protected CostCode() {}

    public CostCode(Budget budget, CostCode parent, String code, String name, BigDecimal budgetedAmount) {
        this.budget = budget;
        this.parent = parent;
        this.code = code;
        this.name = name;
        this.budgetedAmount = budgetedAmount;
    }

    public Budget getBudget() { return budget; }
    public CostCode getParent() { return parent; }
    public UUID getParentId() { return parent != null ? parent.getId() : null; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public BigDecimal getBudgetedAmount() { return budgetedAmount; }

    public void update(String name, BigDecimal budgetedAmount) {
        this.name = name;
        this.budgetedAmount = budgetedAmount;
    }
}
