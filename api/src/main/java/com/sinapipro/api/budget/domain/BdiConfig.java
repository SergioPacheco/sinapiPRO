package com.sinapipro.api.budget.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "bdi_config")
public class BdiConfig extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false, unique = true)
    private Budget budget;

    @Column(nullable = false, precision = 6, scale = 4)
    private BigDecimal administration;

    @Column(nullable = false, precision = 6, scale = 4)
    private BigDecimal profit;

    @Column(nullable = false, precision = 6, scale = 4)
    private BigDecimal taxes;

    @Column(name = "social_charges", nullable = false, precision = 6, scale = 4)
    private BigDecimal socialCharges;

    @Column(name = "financial_expenses", nullable = false, precision = 6, scale = 4)
    private BigDecimal financialExpenses;

    @Column(nullable = false, precision = 6, scale = 4)
    private BigDecimal risks;

    protected BdiConfig() {}

    public BdiConfig(Budget budget, BigDecimal administration, BigDecimal profit, BigDecimal taxes,
                     BigDecimal socialCharges, BigDecimal financialExpenses, BigDecimal risks) {
        this.budget = budget;
        this.administration = administration;
        this.profit = profit;
        this.taxes = taxes;
        this.socialCharges = socialCharges;
        this.financialExpenses = financialExpenses;
        this.risks = risks;
    }

    public Budget getBudget() { return budget; }
    public BigDecimal getAdministration() { return administration; }
    public BigDecimal getProfit() { return profit; }
    public BigDecimal getTaxes() { return taxes; }
    public BigDecimal getSocialCharges() { return socialCharges; }
    public BigDecimal getFinancialExpenses() { return financialExpenses; }
    public BigDecimal getRisks() { return risks; }

    /** BDI total = soma de todos os componentes */
    public BigDecimal getTotalBdi() {
        return administration.add(profit).add(taxes).add(socialCharges).add(financialExpenses).add(risks);
    }

    public void update(BigDecimal administration, BigDecimal profit, BigDecimal taxes,
                       BigDecimal socialCharges, BigDecimal financialExpenses, BigDecimal risks) {
        this.administration = administration;
        this.profit = profit;
        this.taxes = taxes;
        this.socialCharges = socialCharges;
        this.financialExpenses = financialExpenses;
        this.risks = risks;
    }
}
