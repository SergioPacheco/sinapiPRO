package com.sinapipro.api.equipment.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "equipment_fueling")
public class EquipmentFueling {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "equipment_id", nullable = false) private Equipment equipment;
    @Column(name = "budget_id", nullable = false) private UUID budgetId;
    @Column(name = "fueling_date", nullable = false) private LocalDate fuelingDate;
    @Column(name = "fuel_type", nullable = false, length = 30) private String fuelType;
    @Column(nullable = false, precision = 8, scale = 2) private BigDecimal liters;
    @Column(name = "cost_per_liter", nullable = false, precision = 8, scale = 4) private BigDecimal costPerLiter;
    @Column(name = "total_cost", nullable = false, precision = 14, scale = 2) private BigDecimal totalCost;
    @Column(precision = 10, scale = 2) private BigDecimal odometer;
    @Column(length = 300) private String notes;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @PrePersist void prePersist() { createdAt = Instant.now(); }

    protected EquipmentFueling() {}
    public EquipmentFueling(Equipment equipment, UUID budgetId, LocalDate fuelingDate, String fuelType,
                            BigDecimal liters, BigDecimal costPerLiter, BigDecimal odometer, String notes) {
        this.equipment = equipment; this.budgetId = budgetId; this.fuelingDate = fuelingDate;
        this.fuelType = fuelType; this.liters = liters; this.costPerLiter = costPerLiter;
        this.totalCost = liters.multiply(costPerLiter).setScale(2, java.math.RoundingMode.HALF_UP);
        this.odometer = odometer; this.notes = notes;
    }

    public UUID getId() { return id; }
    public Equipment getEquipment() { return equipment; }
    public UUID getBudgetId() { return budgetId; }
    public LocalDate getFuelingDate() { return fuelingDate; }
    public String getFuelType() { return fuelType; }
    public BigDecimal getLiters() { return liters; }
    public BigDecimal getCostPerLiter() { return costPerLiter; }
    public BigDecimal getTotalCost() { return totalCost; }
    public BigDecimal getOdometer() { return odometer; }
    public String getNotes() { return notes; }
}
