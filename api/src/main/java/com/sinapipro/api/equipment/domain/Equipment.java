package com.sinapipro.api.equipment.domain;

import com.sinapipro.api.shared.domain.AuditableEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "equipment")
public class Equipment extends AuditableEntity {

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 60)
    private String type;

    @Column(length = 100)
    private String brand;

    @Column(length = 100)
    private String model;

    private Integer year;

    @Column(name = "license_plate", length = 20)
    private String licensePlate;

    @Column(name = "hourly_cost", nullable = false, precision = 14, scale = 4)
    private BigDecimal hourlyCost;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "current_hours", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentHours;

    @Column(name = "current_km", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentKm;

    @Column(name = "next_maintenance_hours", precision = 10, scale = 2)
    private BigDecimal nextMaintenanceHours;

    @Column(name = "next_maintenance_date")
    private LocalDate nextMaintenanceDate;

    protected Equipment() {}

    public Equipment(String code, String name, String type, String brand, String model,
                     Integer year, String licensePlate, BigDecimal hourlyCost) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.licensePlate = licensePlate;
        this.hourlyCost = hourlyCost;
        this.status = "AVAILABLE";
        this.currentHours = BigDecimal.ZERO;
        this.currentKm = BigDecimal.ZERO;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public Integer getYear() { return year; }
    public String getLicensePlate() { return licensePlate; }
    public BigDecimal getHourlyCost() { return hourlyCost; }
    public String getStatus() { return status; }
    public BigDecimal getCurrentHours() { return currentHours; }
    public BigDecimal getCurrentKm() { return currentKm; }
    public BigDecimal getNextMaintenanceHours() { return nextMaintenanceHours; }
    public LocalDate getNextMaintenanceDate() { return nextMaintenanceDate; }

    public void addUsage(BigDecimal hours, BigDecimal km) {
        this.currentHours = this.currentHours.add(hours);
        this.currentKm = this.currentKm.add(km);
    }

    public void setMaintenanceSchedule(BigDecimal nextHours, LocalDate nextDate) {
        this.nextMaintenanceHours = nextHours;
        this.nextMaintenanceDate = nextDate;
    }

    public void setStatus(String status) { this.status = status; }

    public boolean isMaintenanceDue() {
        if (nextMaintenanceHours != null && currentHours.compareTo(nextMaintenanceHours) >= 0) return true;
        if (nextMaintenanceDate != null && !LocalDate.now().isBefore(nextMaintenanceDate)) return true;
        return false;
    }
}
