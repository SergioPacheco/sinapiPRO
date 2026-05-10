package com.sinapipro.api.budget.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import com.sinapipro.api.sinapi.domain.Composition;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "budget_item")
public class BudgetItem extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id", nullable = false)
    private BudgetStage stage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "composition_id", nullable = false)
    private Composition composition;

    @Column(nullable = false, precision = 14, scale = 4)
    private BigDecimal quantity;

    @Column(name = "unit_cost", nullable = false, precision = 14, scale = 4)
    private BigDecimal unitCost;

    @Column(name = "bdi_pct", nullable = false, precision = 6, scale = 4)
    private BigDecimal bdiPct;

    @Column(name = "custom_code", length = 30)
    private String customCode;

    @Column(name = "price_source", length = 20)
    private String priceSource = "SINAPI";

    protected BudgetItem() {}

    public BudgetItem(BudgetStage stage, Composition composition, BigDecimal quantity, BigDecimal unitCost, BigDecimal bdiPct) {
        this.stage = stage;
        this.composition = composition;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.bdiPct = bdiPct;
    }

    public BudgetStage getStage() { return stage; }
    public Composition getComposition() { return composition; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getUnitCost() { return unitCost; }
    public BigDecimal getBdiPct() { return bdiPct; }
    public String getCustomCode() { return customCode; }
    public void setCustomCode(String customCode) { this.customCode = customCode; }
    public String getPriceSource() { return priceSource; }
    public void setPriceSource(String priceSource) { this.priceSource = priceSource; }

    public BigDecimal getDirectCost() {
        return quantity.multiply(unitCost);
    }

    public BigDecimal getTotalWithBdi() {
        return getDirectCost().multiply(BigDecimal.ONE.add(bdiPct));
    }

    public void update(BigDecimal quantity, BigDecimal unitCost, BigDecimal bdiPct) {
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.bdiPct = bdiPct;
    }
}
