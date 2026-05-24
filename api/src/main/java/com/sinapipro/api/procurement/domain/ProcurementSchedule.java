package com.sinapipro.api.procurement.domain;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "procurement_schedule")
public class ProcurementSchedule extends TenantAwareEntity {
    @Column(name = "project_id", nullable = false) private UUID projectId;
    @Column(name = "budget_item_id") private UUID budgetItemId;
    @Column(name = "material_description", nullable = false, length = 300) private String materialDescription;
    @Column(name = "planned_date", nullable = false) private LocalDate plannedDate;
    @Column(nullable = false, precision = 14, scale = 4) private BigDecimal quantity;
    @Column(name = "estimated_cost", precision = 18, scale = 2) private BigDecimal estimatedCost;
    @Column(nullable = false, length = 20) private String status = "PLANNED";
    @Column(name = "purchase_order_id") private UUID purchaseOrderId;

    protected ProcurementSchedule() {}

    public ProcurementSchedule(UUID projectId, String materialDescription, LocalDate plannedDate,
                                BigDecimal quantity, BigDecimal estimatedCost) {
        this.projectId = projectId;
        this.materialDescription = materialDescription;
        this.plannedDate = plannedDate;
        this.quantity = quantity;
        this.estimatedCost = estimatedCost;
    }

    public UUID getProjectId() { return projectId; }
    public UUID getBudgetItemId() { return budgetItemId; }
    public String getMaterialDescription() { return materialDescription; }
    public LocalDate getPlannedDate() { return plannedDate; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public String getStatus() { return status; }
    public UUID getPurchaseOrderId() { return purchaseOrderId; }

    public void setBudgetItemId(UUID budgetItemId) { this.budgetItemId = budgetItemId; }

    public void linkOrder(UUID purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
        this.status = "ORDERED";
    }

    public void markReceived() { this.status = "RECEIVED"; }
    public void cancel() { this.status = "CANCELLED"; }
}
