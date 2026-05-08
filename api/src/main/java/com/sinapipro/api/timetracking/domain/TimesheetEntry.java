package com.sinapipro.api.timetracking.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "timesheet_entry")
public class TimesheetEntry {

    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "budget_id", nullable = false) private UUID budgetId;
    @Column(name = "cost_code_id") private UUID costCodeId;
    @Column(name = "worker_name", nullable = false, length = 140) private String workerName;
    @Column(nullable = false, length = 80) private String role;
    @Column(name = "work_date", nullable = false) private LocalDate workDate;
    @Column(name = "regular_hours", nullable = false, precision = 4, scale = 2) private BigDecimal regularHours;
    @Column(name = "overtime_hours", nullable = false, precision = 4, scale = 2) private BigDecimal overtimeHours;
    @Column(name = "hourly_rate", nullable = false, precision = 10, scale = 2) private BigDecimal hourlyRate;
    @Column(name = "units_produced", precision = 10, scale = 2) private BigDecimal unitsProduced;
    @Column(name = "unit_type", length = 30) private String unitType;
    @Column(length = 300) private String notes;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;

    @PrePersist void prePersist() { createdAt = Instant.now(); }
    protected TimesheetEntry() {}

    public TimesheetEntry(UUID budgetId, UUID costCodeId, String workerName, String role, LocalDate workDate,
                          BigDecimal regularHours, BigDecimal overtimeHours, BigDecimal hourlyRate,
                          BigDecimal unitsProduced, String unitType, String notes) {
        this.budgetId = budgetId; this.costCodeId = costCodeId; this.workerName = workerName;
        this.role = role; this.workDate = workDate; this.regularHours = regularHours;
        this.overtimeHours = overtimeHours; this.hourlyRate = hourlyRate;
        this.unitsProduced = unitsProduced; this.unitType = unitType; this.notes = notes;
    }

    public UUID getId() { return id; }
    public UUID getBudgetId() { return budgetId; }
    public UUID getCostCodeId() { return costCodeId; }
    public String getWorkerName() { return workerName; }
    public String getRole() { return role; }
    public LocalDate getWorkDate() { return workDate; }
    public BigDecimal getRegularHours() { return regularHours; }
    public BigDecimal getOvertimeHours() { return overtimeHours; }
    public BigDecimal getHourlyRate() { return hourlyRate; }
    public BigDecimal getUnitsProduced() { return unitsProduced; }
    public String getUnitType() { return unitType; }
    public BigDecimal getTotalHours() { return regularHours.add(overtimeHours); }
    public BigDecimal getLaborCost() {
        BigDecimal regular = regularHours.multiply(hourlyRate);
        BigDecimal overtime = overtimeHours.multiply(hourlyRate.multiply(new BigDecimal("1.5")));
        return regular.add(overtime);
    }
}
