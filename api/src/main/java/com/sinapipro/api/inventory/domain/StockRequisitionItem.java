package com.sinapipro.api.inventory.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "stock_requisition_item")
public class StockRequisitionItem {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "requisition_id", nullable = false) private StockRequisition requisition;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "stock_item_id", nullable = false) private StockItem stockItem;
    @Column(nullable = false, precision = 14, scale = 4) private BigDecimal quantity;
    @Column(name = "delivered_quantity", nullable = false, precision = 14, scale = 4) private BigDecimal deliveredQuantity;

    protected StockRequisitionItem() {}
    public StockRequisitionItem(StockRequisition requisition, StockItem stockItem, BigDecimal quantity) {
        this.requisition = requisition; this.stockItem = stockItem; this.quantity = quantity; this.deliveredQuantity = BigDecimal.ZERO;
    }

    public UUID getId() { return id; }
    public StockItem getStockItem() { return stockItem; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getDeliveredQuantity() { return deliveredQuantity; }
    public void deliver(BigDecimal qty) { this.deliveredQuantity = this.deliveredQuantity.add(qty); }
}
