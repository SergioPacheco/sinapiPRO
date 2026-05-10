package com.sinapipro.api.inventory.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "stock_requisition")
public class StockRequisition {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "budget_id", nullable = false) private UUID budgetId;
    @Column(name = "requested_by", nullable = false, length = 140) private String requestedBy;
    @Column(nullable = false, length = 20) private String status;
    @Column(length = 300) private String notes;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @PrePersist void prePersist() { createdAt = Instant.now(); }

    @OneToMany(mappedBy = "requisition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockRequisitionItem> items = new ArrayList<>();

    protected StockRequisition() {}
    public StockRequisition(UUID budgetId, String requestedBy, String notes) {
        this.budgetId = budgetId; this.requestedBy = requestedBy; this.notes = notes; this.status = "PENDING";
    }

    public UUID getId() { return id; }
    public UUID getBudgetId() { return budgetId; }
    public String getRequestedBy() { return requestedBy; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }
    public List<StockRequisitionItem> getItems() { return items; }
    public void approve() { this.status = "APPROVED"; }
    public void deliver() { this.status = "DELIVERED"; }
    public void cancel() { this.status = "CANCELLED"; }
}
