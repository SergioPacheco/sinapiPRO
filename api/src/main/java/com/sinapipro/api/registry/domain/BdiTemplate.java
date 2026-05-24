package com.sinapipro.api.registry.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "bdi_template")
public class BdiTemplate extends TenantAwareEntity {
    @Column(nullable = false, unique = true, length = 100) private String name;
    @Column(nullable = false) private BigDecimal administration = BigDecimal.ZERO;
    @Column(nullable = false) private BigDecimal profit = BigDecimal.ZERO;
    @Column(name = "financial_cost", nullable = false) private BigDecimal financialCost = BigDecimal.ZERO;
    @Column(nullable = false) private BigDecimal taxes = BigDecimal.ZERO;
    @Column(nullable = false) private BigDecimal total = BigDecimal.ZERO;
    protected BdiTemplate() {}
    public BdiTemplate(String name, BigDecimal administration, BigDecimal profit, BigDecimal financialCost, BigDecimal taxes, BigDecimal total) {
        this.name = name; this.administration = administration; this.profit = profit;
        this.financialCost = financialCost; this.taxes = taxes; this.total = total;
    }
    public String getName() { return name; }
    public BigDecimal getAdministration() { return administration; }
    public BigDecimal getProfit() { return profit; }
    public BigDecimal getFinancialCost() { return financialCost; }
    public BigDecimal getTaxes() { return taxes; }
    public BigDecimal getTotal() { return total; }
    public void update(String name, BigDecimal administration, BigDecimal profit, BigDecimal financialCost, BigDecimal taxes, BigDecimal total) {
        this.name = name; this.administration = administration; this.profit = profit;
        this.financialCost = financialCost; this.taxes = taxes; this.total = total;
    }
}
