package com.sinapipro.api.equipment.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "equipment_usage")
public class EquipmentUsage extends TenantAwareEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(name = "budget_id", nullable = false)
    private UUID budgetId;

    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;

    @Column(name = "hours_used", nullable = false, precision = 6, scale = 2)
    private BigDecimal hoursUsed;

    @Column(name = "km_used", nullable = false, precision = 8, scale = 2)
    private BigDecimal kmUsed;

    @Column(length = 140)
    private String operator;

    @Column(length = 300)
    private String notes;



    protected EquipmentUsage() {}

    public EquipmentUsage(Equipment equipment, UUID budgetId, LocalDate usageDate,
                          BigDecimal hoursUsed, BigDecimal kmUsed, String operator, String notes) {
        this.equipment = equipment;
        this.budgetId = budgetId;
        this.usageDate = usageDate;
        this.hoursUsed = hoursUsed;
        this.kmUsed = kmUsed;
        this.operator = operator;
        this.notes = notes;
    }

    public Equipment getEquipment() { return equipment; }
    public UUID getBudgetId() { return budgetId; }
    public LocalDate getUsageDate() { return usageDate; }
    public BigDecimal getHoursUsed() { return hoursUsed; }
    public BigDecimal getKmUsed() { return kmUsed; }
    public String getOperator() { return operator; }
    public String getNotes() { return notes; }
    public BigDecimal getCost() { return hoursUsed.multiply(equipment.getHourlyCost()); }
}
