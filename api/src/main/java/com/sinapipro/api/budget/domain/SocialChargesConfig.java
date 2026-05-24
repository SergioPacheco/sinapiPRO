package com.sinapipro.api.budget.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Configuração de encargos sociais — horista vs mensalista, Simples Nacional.
 */
@Entity
@Table(name = "social_charges_config")
public class SocialChargesConfig extends TenantAwareEntity {


    @Column(name = "budget_id", nullable = false)
    private UUID budgetId;

    @Column(nullable = false, length = 30)
    private String workerType; // HOURLY, MONTHLY

    @Column(name = "tax_regime", nullable = false, length = 30)
    private String taxRegime = "NORMAL"; // NORMAL, SIMPLES_NACIONAL

    @Column(name = "inss_pct", precision = 6, scale = 4)
    private BigDecimal inssPct = BigDecimal.ZERO;

    @Column(name = "fgts_pct", precision = 6, scale = 4)
    private BigDecimal fgtsPct = BigDecimal.ZERO;

    @Column(name = "vacation_pct", precision = 6, scale = 4)
    private BigDecimal vacationPct = BigDecimal.ZERO;

    @Column(name = "thirteenth_pct", precision = 6, scale = 4)
    private BigDecimal thirteenthPct = BigDecimal.ZERO;

    @Column(name = "other_pct", precision = 6, scale = 4)
    private BigDecimal otherPct = BigDecimal.ZERO;

    public SocialChargesConfig() {}

    public UUID getBudgetId() { return budgetId; }
    public String getWorkerType() { return workerType; }
    public String getTaxRegime() { return taxRegime; }
    public BigDecimal getInssPct() { return inssPct; }
    public BigDecimal getFgtsPct() { return fgtsPct; }
    public BigDecimal getVacationPct() { return vacationPct; }
    public BigDecimal getThirteenthPct() { return thirteenthPct; }
    public BigDecimal getOtherPct() { return otherPct; }

    public BigDecimal getTotalPct() {
        return inssPct.add(fgtsPct).add(vacationPct).add(thirteenthPct).add(otherPct);
    }

    public void setBudgetId(UUID id) { this.budgetId = id; }
    public void setWorkerType(String t) { this.workerType = t; }
    public void setTaxRegime(String r) { this.taxRegime = r; }
    public void setInssPct(BigDecimal v) { this.inssPct = v; }
    public void setFgtsPct(BigDecimal v) { this.fgtsPct = v; }
    public void setVacationPct(BigDecimal v) { this.vacationPct = v; }
    public void setThirteenthPct(BigDecimal v) { this.thirteenthPct = v; }
    public void setOtherPct(BigDecimal v) { this.otherPct = v; }
}
