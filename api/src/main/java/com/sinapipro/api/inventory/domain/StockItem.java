package com.sinapipro.api.inventory.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "stock_item")
public class StockItem extends AuditableEntity {
    @Column(name = "budget_id", nullable = false) private UUID budgetId;
    @Column(nullable = false, length = 300) private String description;
    @Column(nullable = false, length = 20) private String unit;
    @Column(name = "current_quantity", nullable = false, precision = 14, scale = 4) private BigDecimal currentQuantity;
    @Column(name = "min_quantity", nullable = false, precision = 14, scale = 4) private BigDecimal minQuantity;
    @Column(length = 100) private String location;

    protected StockItem() {}
    public StockItem(UUID budgetId, String description, String unit, BigDecimal minQuantity, String location) {
        this.budgetId = budgetId; this.description = description; this.unit = unit;
        this.currentQuantity = BigDecimal.ZERO; this.minQuantity = minQuantity; this.location = location;
    }

    public UUID getBudgetId() { return budgetId; }
    public String getDescription() { return description; }
    public String getUnit() { return unit; }
    public BigDecimal getCurrentQuantity() { return currentQuantity; }
    public BigDecimal getMinQuantity() { return minQuantity; }
    public String getLocation() { return location; }
    public boolean isBelowMinimum() { return currentQuantity.compareTo(minQuantity) < 0; }

    public void addQuantity(BigDecimal qty) { this.currentQuantity = this.currentQuantity.add(qty); }
    public void removeQuantity(BigDecimal qty) {
        if (this.currentQuantity.compareTo(qty) < 0) throw new IllegalStateException("Insufficient stock");
        this.currentQuantity = this.currentQuantity.subtract(qty);
    }
}
