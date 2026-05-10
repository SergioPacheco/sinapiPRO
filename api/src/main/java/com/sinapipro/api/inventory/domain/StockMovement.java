package com.sinapipro.api.inventory.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "stock_movement")
public class StockMovement {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "stock_item_id", nullable = false) private StockItem stockItem;
    @Column(nullable = false, length = 20) private String type; // IN, OUT
    @Column(nullable = false, precision = 14, scale = 4) private BigDecimal quantity;
    @Column(name = "reference_id") private UUID referenceId;
    @Column(name = "reference_type", length = 40) private String referenceType;
    @Column(length = 300) private String notes;
    @Column(name = "moved_at", nullable = false, updatable = false) private Instant movedAt;
    @PrePersist void prePersist() { movedAt = Instant.now(); }

    protected StockMovement() {}
    public StockMovement(StockItem stockItem, String type, BigDecimal quantity, UUID referenceId, String referenceType, String notes) {
        this.stockItem = stockItem; this.type = type; this.quantity = quantity;
        this.referenceId = referenceId; this.referenceType = referenceType; this.notes = notes;
    }

    public UUID getId() { return id; }
    public StockItem getStockItem() { return stockItem; }
    public String getType() { return type; }
    public BigDecimal getQuantity() { return quantity; }
    public UUID getReferenceId() { return referenceId; }
    public String getReferenceType() { return referenceType; }
    public String getNotes() { return notes; }
    public Instant getMovedAt() { return movedAt; }
}
