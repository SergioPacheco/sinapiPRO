package com.sinapipro.api.equipment.application;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

// --- Entities ---

@Entity @Table(name = "vehicle")
class Vehicle extends TenantAwareEntity {
    @Column(nullable = false, length = 10) private String plate;
    @Column(length = 50) private String brand;
    @Column(length = 80) private String model;
    @Column(name = "year_manufacture") private Integer yearManufacture;
    @Column(length = 20) private String renavam;
    @Column(name = "fuel_type", length = 20) private String fuelType;
    @Column(precision = 12, scale = 1) private BigDecimal odometer = BigDecimal.ZERO;
    @Column(name = "insurance_expiry") private LocalDate insuranceExpiry;
    @Column(name = "project_id") private UUID projectId;
    @Column(name = "driver_id") private UUID driverId;
    @Column(nullable = false, length = 20) private String status = "ACTIVE";
    protected Vehicle() {}
    public Vehicle(String plate, String brand, String model, String fuelType) { this.plate = plate; this.brand = brand; this.model = model; this.fuelType = fuelType; }
    public String getPlate() { return plate; } public String getBrand() { return brand; }
    public String getModel() { return model; } public BigDecimal getOdometer() { return odometer; }
    public String getStatus() { return status; } public String getFuelType() { return fuelType; }
    public void updateOdometer(BigDecimal km) { if (km.compareTo(odometer) > 0) this.odometer = km; }
}

@Entity @Table(name = "vehicle_fueling")
class VehicleFueling extends TenantAwareEntity {
    @Column(name = "vehicle_id", nullable = false) private UUID vehicleId;
    @Column(name = "fueling_date", nullable = false) private LocalDate fuelingDate;
    @Column(nullable = false, precision = 12, scale = 1) private BigDecimal odometer;
    @Column(nullable = false, precision = 8, scale = 2) private BigDecimal liters;
    @Column(name = "unit_price", nullable = false, precision = 8, scale = 4) private BigDecimal unitPrice;
    @Column(name = "total_cost", nullable = false, precision = 12, scale = 2) private BigDecimal totalCost;
    protected VehicleFueling() {}
    public VehicleFueling(UUID vehicleId, LocalDate date, BigDecimal odometer, BigDecimal liters, BigDecimal unitPrice) {
        this.vehicleId = vehicleId; this.fuelingDate = date; this.odometer = odometer;
        this.liters = liters; this.unitPrice = unitPrice; this.totalCost = liters.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
    }
    public UUID getVehicleId() { return vehicleId; } public BigDecimal getLiters() { return liters; }
    public BigDecimal getTotalCost() { return totalCost; } public BigDecimal getOdometer() { return odometer; }
}

@Entity @Table(name = "vehicle_maintenance")
class VehicleMaintenance extends TenantAwareEntity {
    @Column(name = "vehicle_id", nullable = false) private UUID vehicleId;
    @Column(nullable = false, length = 20) private String type; // PREVENTIVE, CORRECTIVE
    @Column(nullable = false, length = 300) private String description;
    @Column(name = "scheduled_date") private LocalDate scheduledDate;
    @Column(name = "executed_date") private LocalDate executedDate;
    @Column(precision = 12, scale = 2) private BigDecimal cost;
    @Column(name = "next_km", precision = 12, scale = 1) private BigDecimal nextKm;
    @Column(name = "next_date") private LocalDate nextDate;
    @Column(nullable = false, length = 20) private String status = "SCHEDULED";
    protected VehicleMaintenance() {}
    public VehicleMaintenance(UUID vehicleId, String type, String description, LocalDate scheduledDate, BigDecimal nextKm, LocalDate nextDate) {
        this.vehicleId = vehicleId; this.type = type; this.description = description;
        this.scheduledDate = scheduledDate; this.nextKm = nextKm; this.nextDate = nextDate;
    }
    public String getStatus() { return status; } public BigDecimal getCost() { return cost; }
    public void execute(LocalDate date, BigDecimal cost) { this.executedDate = date; this.cost = cost; this.status = "EXECUTED"; }
}

// --- Repositories ---
interface VehicleRepository extends JpaRepository<Vehicle, UUID> { List<Vehicle> findByStatusAndProjectId(String status, UUID projectId); }
interface VehicleFuelingRepository extends JpaRepository<VehicleFueling, UUID> { List<VehicleFueling> findByVehicleIdOrderByFuelingDateDesc(UUID vehicleId); }
interface VehicleMaintenanceRepository extends JpaRepository<VehicleMaintenance, UUID> {
    List<VehicleMaintenance> findByVehicleIdAndStatus(UUID vehicleId, String status);
    List<VehicleMaintenance> findByStatusAndScheduledDateBefore(String status, LocalDate date);
}

// --- Service ---
@Service @Transactional
public class FleetService {
    private final VehicleRepository vehicleRepo;
    private final VehicleFuelingRepository fuelingRepo;
    private final VehicleMaintenanceRepository maintenanceRepo;
    private final VehicleIncidentRepository incidentRepo;
    private final EquipmentRentalRepository rentalRepo;

    public FleetService(VehicleRepository vehicleRepo, VehicleFuelingRepository fuelingRepo,
                        VehicleMaintenanceRepository maintenanceRepo, VehicleIncidentRepository incidentRepo,
                        EquipmentRentalRepository rentalRepo) {
        this.vehicleRepo = vehicleRepo; this.fuelingRepo = fuelingRepo;
        this.maintenanceRepo = maintenanceRepo; this.incidentRepo = incidentRepo;
        this.rentalRepo = rentalRepo;
    }

    /** 12.1 — Cadastro veículo */
    public Vehicle createVehicle(String plate, String brand, String model, String fuelType) {
        return vehicleRepo.save(new Vehicle(plate, brand, model, fuelType));
    }

    /** 12.2 — Abastecimento com km/litro */
    public VehicleFueling registerFueling(UUID vehicleId, LocalDate date, BigDecimal odometer, BigDecimal liters, BigDecimal unitPrice) {
        var vehicle = vehicleRepo.findById(vehicleId).orElseThrow();
        vehicle.updateOdometer(odometer);
        vehicleRepo.save(vehicle);
        return fuelingRepo.save(new VehicleFueling(vehicleId, date, odometer, liters, unitPrice));
    }

    /** 12.3 — Manutenção preventiva */
    public VehicleMaintenance scheduleMaintenance(UUID vehicleId, String type, String description, LocalDate scheduledDate, BigDecimal nextKm, LocalDate nextDate) {
        return maintenanceRepo.save(new VehicleMaintenance(vehicleId, type, description, scheduledDate, nextKm, nextDate));
    }

    /** 12.3 — Executar manutenção */
    public VehicleMaintenance executeMaintenance(UUID maintenanceId, LocalDate date, BigDecimal cost) {
        var m = maintenanceRepo.findById(maintenanceId).orElseThrow();
        m.execute(date, cost);
        return maintenanceRepo.save(m);
    }

    /** 12.3 — Manutenções vencidas */
    public List<VehicleMaintenance> overdueMaintenance() {
        return maintenanceRepo.findByStatusAndScheduledDateBefore("SCHEDULED", LocalDate.now());
    }

    /** 12.5 — TCO (Total Cost of Ownership) por veículo */
    public VehicleTCO calculateTCO(UUID vehicleId) {
        var fuelings = fuelingRepo.findByVehicleIdOrderByFuelingDateDesc(vehicleId);
        var maintenances = maintenanceRepo.findByVehicleIdAndStatus(vehicleId, "EXECUTED");

        var fuelCost = fuelings.stream().map(VehicleFueling::getTotalCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        var maintenanceCost = maintenances.stream().map(VehicleMaintenance::getCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        var totalCost = fuelCost.add(maintenanceCost);

        var totalLiters = fuelings.stream().map(VehicleFueling::getLiters).reduce(BigDecimal.ZERO, BigDecimal::add);
        var kmPerLiter = BigDecimal.ZERO;
        if (fuelings.size() >= 2) {
            var first = fuelings.getLast().getOdometer();
            var last = fuelings.getFirst().getOdometer();
            var totalKm = last.subtract(first);
            if (totalLiters.signum() > 0) kmPerLiter = totalKm.divide(totalLiters, 2, RoundingMode.HALF_UP);
        }

        return new VehicleTCO(vehicleId, fuelCost, maintenanceCost, totalCost, kmPerLiter, fuelings.size(), maintenances.size());
    }

    public record VehicleTCO(UUID vehicleId, BigDecimal fuelCost, BigDecimal maintenanceCost, BigDecimal totalCost, BigDecimal kmPerLiter, int fuelingCount, int maintenanceCount) {}

    /** 12.4 — Registrar multa ou sinistro */
    public VehicleIncident registerIncident(UUID vehicleId, String type, LocalDate date, String description,
                                             BigDecimal cost, UUID driverId) {
        return incidentRepo.save(new VehicleIncident(vehicleId, type, date, description, cost, driverId));
    }

    /** 12.4 — Listar multas/sinistros de um veículo */
    public List<VehicleIncident> listIncidents(UUID vehicleId) {
        return incidentRepo.findByVehicleIdOrderByIncidentDateDesc(vehicleId);
    }

    /** 12.6 — Registrar locação de equipamento */
    public EquipmentRental createRental(UUID equipmentId, UUID projectId, LocalDate startDate,
                                         LocalDate expectedEndDate, BigDecimal dailyRate) {
        return rentalRepo.save(new EquipmentRental(equipmentId, projectId, startDate, expectedEndDate, dailyRate));
    }

    /** 12.6 — Devolver equipamento */
    public EquipmentRental returnRental(UUID rentalId, LocalDate returnDate) {
        var rental = rentalRepo.findById(rentalId).orElseThrow();
        rental.returnEquipment(returnDate);
        return rentalRepo.save(rental);
    }

    /** 12.6 — Locações ativas */
    public List<EquipmentRental> activeRentals() {
        return rentalRepo.findByStatus("ACTIVE");
    }
}

// --- 12.4 Multas e sinistros ---

@Entity @Table(name = "vehicle_incident")
class VehicleIncident extends TenantAwareEntity {
    @Column(name = "vehicle_id", nullable = false) private UUID vehicleId;
    @Column(nullable = false, length = 20) private String type; // FINE, ACCIDENT, THEFT
    @Column(name = "incident_date", nullable = false) private LocalDate incidentDate;
    @Column(nullable = false, length = 500) private String description;
    @Column(precision = 12, scale = 2) private BigDecimal cost;
    @Column(name = "driver_id") private UUID driverId;
    @Column(nullable = false, length = 20) private String status = "OPEN";

    protected VehicleIncident() {}
    public VehicleIncident(UUID vehicleId, String type, LocalDate date, String description, BigDecimal cost, UUID driverId) {
        this.vehicleId = vehicleId; this.type = type; this.incidentDate = date;
        this.description = description; this.cost = cost; this.driverId = driverId;
    }
    public UUID getVehicleId() { return vehicleId; }
    public String getType() { return type; }
    public LocalDate getIncidentDate() { return incidentDate; }
    public String getDescription() { return description; }
    public BigDecimal getCost() { return cost; }
    public UUID getDriverId() { return driverId; }
    public String getStatus() { return status; }
}

// --- 12.6 Locação de equipamentos ---

@Entity @Table(name = "equipment_rental")
class EquipmentRental extends TenantAwareEntity {
    @Column(name = "equipment_id", nullable = false) private UUID equipmentId;
    @Column(name = "project_id", nullable = false) private UUID projectId;
    @Column(name = "start_date", nullable = false) private LocalDate startDate;
    @Column(name = "expected_end_date") private LocalDate expectedEndDate;
    @Column(name = "actual_end_date") private LocalDate actualEndDate;
    @Column(name = "daily_rate", nullable = false, precision = 12, scale = 2) private BigDecimal dailyRate;
    @Column(name = "total_cost", precision = 12, scale = 2) private BigDecimal totalCost;
    @Column(nullable = false, length = 20) private String status = "ACTIVE";

    protected EquipmentRental() {}
    public EquipmentRental(UUID equipmentId, UUID projectId, LocalDate startDate, LocalDate expectedEndDate, BigDecimal dailyRate) {
        this.equipmentId = equipmentId; this.projectId = projectId; this.startDate = startDate;
        this.expectedEndDate = expectedEndDate; this.dailyRate = dailyRate;
    }
    public UUID getEquipmentId() { return equipmentId; }
    public UUID getProjectId() { return projectId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getExpectedEndDate() { return expectedEndDate; }
    public LocalDate getActualEndDate() { return actualEndDate; }
    public BigDecimal getDailyRate() { return dailyRate; }
    public BigDecimal getTotalCost() { return totalCost; }
    public String getStatus() { return status; }

    public void returnEquipment(LocalDate returnDate) {
        this.actualEndDate = returnDate;
        this.status = "RETURNED";
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, returnDate);
        this.totalCost = dailyRate.multiply(BigDecimal.valueOf(Math.max(days, 1)));
    }
}

// --- Repositories for new entities ---
interface VehicleIncidentRepository extends JpaRepository<VehicleIncident, UUID> {
    List<VehicleIncident> findByVehicleIdOrderByIncidentDateDesc(UUID vehicleId);
}

interface EquipmentRentalRepository extends JpaRepository<EquipmentRental, UUID> {
    List<EquipmentRental> findByStatus(String status);
    List<EquipmentRental> findByEquipmentId(UUID equipmentId);
}
