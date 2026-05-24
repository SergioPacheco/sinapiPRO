package com.sinapipro.api.commercial.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "price_table_item")
public class PriceTableItem {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "price_table_id", nullable = false) private UUID priceTableId;
    @Column(name = "unit_id", nullable = false) private UUID unitId;
    @Column(nullable = false, precision = 18, scale = 2) private BigDecimal price;
    @Column(name = "down_payment_pct", precision = 5, scale = 2) private BigDecimal downPaymentPct;
    @Column(name = "max_installments") private Integer maxInstallments;

    protected PriceTableItem() {}
    public PriceTableItem(UUID priceTableId, UUID unitId, BigDecimal price, BigDecimal downPaymentPct, Integer maxInstallments) {
        this.priceTableId = priceTableId; this.unitId = unitId; this.price = price;
        this.downPaymentPct = downPaymentPct; this.maxInstallments = maxInstallments;
    }

    public UUID getId() { return id; }
    public UUID getPriceTableId() { return priceTableId; }
    public UUID getUnitId() { return unitId; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getDownPaymentPct() { return downPaymentPct; }
    public Integer getMaxInstallments() { return maxInstallments; }
}
