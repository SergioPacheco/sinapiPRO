package com.sinapipro.api.equipment.api;

import com.sinapipro.api.equipment.application.EquipmentService;
import com.sinapipro.api.equipment.application.EquipmentService.*;
import com.sinapipro.api.equipment.application.FleetService;
import com.sinapipro.api.equipment.domain.Equipment;
import com.sinapipro.api.equipment.domain.EquipmentFueling;
import com.sinapipro.api.equipment.domain.EquipmentFuelingRepository;
import com.sinapipro.api.equipment.domain.EquipmentRepository;
import com.sinapipro.api.equipment.domain.EquipmentUsage;
import com.sinapipro.api.equipment.domain.EquipmentUsageRepository;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.shared.api.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Equipment", description = "Equipment management, usage tracking and maintenance alerts")
@RestController
@RequestMapping("/api/v1/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final FleetService fleetService;
    private final EquipmentUsageRepository usageRepository;
    private final EquipmentFuelingRepository fuelingRepository;
    private final EquipmentRepository equipmentRepository;

    public EquipmentController(EquipmentService equipmentService, FleetService fleetService,
                               EquipmentUsageRepository usageRepository,
                               EquipmentFuelingRepository fuelingRepository, EquipmentRepository equipmentRepository) {
        this.equipmentService = equipmentService;
        this.fleetService = fleetService;
        this.usageRepository = usageRepository;
        this.fuelingRepository = fuelingRepository;
        this.equipmentRepository = equipmentRepository;
    }

    @Operation(summary = "List all equipment")
    @GetMapping
    @PreAuthorize("@perm.check('budget.read')")
    PageResponse<EquipmentResponse> list(@PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(equipmentService.list(pageable).map(EquipmentResponse::from));
    }

    @Operation(summary = "Create equipment")
    @PostMapping
    @PreAuthorize("@perm.check('budget.write')")
    ResponseEntity<EquipmentResponse> create(@Valid @RequestBody CreateEquipmentRequest req) {
        Equipment e = equipmentService.create(req.code(), req.name(), req.type(), req.brand(),
                req.model(), req.year(), req.licensePlate(), req.hourlyCost());
        return ResponseEntity.created(URI.create("/api/v1/equipment/" + e.getId()))
                .body(EquipmentResponse.from(e));
    }

    @Operation(summary = "Record equipment usage")
    @PostMapping("/{equipmentId}/usage")
    @PreAuthorize("@perm.check('budget.write')")
    @ResponseStatus(HttpStatus.CREATED)
    UsageResponse recordUsage(@PathVariable UUID equipmentId, @Valid @RequestBody RecordUsageRequest req) {
        EquipmentUsage u = equipmentService.recordUsage(equipmentId, req.budgetId(), req.usageDate(),
                req.hoursUsed(), req.kmUsed() != null ? req.kmUsed() : BigDecimal.ZERO, req.operator(), req.notes());
        return UsageResponse.from(u);
    }

    @Operation(summary = "List usage history for equipment")
    @GetMapping("/{equipmentId}/usage")
    @PreAuthorize("@perm.check('budget.read')")
    List<UsageResponse> listUsage(@PathVariable UUID equipmentId) {
        return usageRepository.findByEquipmentIdOrderByUsageDateDesc(equipmentId).stream()
                .map(UsageResponse::from).toList();
    }

    @Operation(summary = "Schedule maintenance")
    @PostMapping("/{equipmentId}/maintenance-schedule")
    @PreAuthorize("@perm.check('budget.write')")
    EquipmentResponse scheduleMaintenance(@PathVariable UUID equipmentId,
                                          @Valid @RequestBody MaintenanceScheduleRequest req) {
        Equipment e = equipmentService.scheduleMaintenace(equipmentId, req.nextHours(), req.nextDate());
        return EquipmentResponse.from(e);
    }

    @Operation(summary = "Get maintenance alerts (equipment due for maintenance)")
    @GetMapping("/maintenance-alerts")
    @PreAuthorize("@perm.check('budget.read')")
    List<MaintenanceAlert> maintenanceAlerts() {
        return equipmentService.getMaintenanceAlerts();
    }

    @Operation(summary = "Equipment cost summary for a budget")
    @GetMapping("/cost-summary")
    @PreAuthorize("@perm.check('budget.read')")
    EquipmentCostSummary costSummary(@RequestParam UUID budgetId) {
        return equipmentService.costSummary(budgetId);
    }

    // --- Fueling ---

    @Operation(summary = "Record equipment fueling")
    @PostMapping("/{equipmentId}/fueling")
    @PreAuthorize("@perm.check('budget.write')")
    @ResponseStatus(HttpStatus.CREATED)
    FuelingResponse recordFueling(@PathVariable UUID equipmentId, @Valid @RequestBody RecordFuelingRequest req) {
        var equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new DomainNotFoundException("Equipment not found: " + equipmentId));
        var fueling = fuelingRepository.save(new EquipmentFueling(equipment, req.budgetId(), req.fuelingDate(),
                req.fuelType(), req.liters(), req.costPerLiter(), req.odometer(), req.notes()));
        return FuelingResponse.from(fueling);
    }

    @Operation(summary = "List fueling history for equipment")
    @GetMapping("/{equipmentId}/fueling")
    @PreAuthorize("@perm.check('budget.read')")
    List<FuelingResponse> listFueling(@PathVariable UUID equipmentId) {
        return fuelingRepository.findByEquipmentIdOrderByFuelingDateDesc(equipmentId).stream()
                .map(FuelingResponse::from).toList();
    }

    // --- DTOs ---
    record CreateEquipmentRequest(@NotBlank String code, @NotBlank String name, @NotBlank String type,
                                  String brand, String model, Integer year, String licensePlate,
                                  @NotNull @Positive BigDecimal hourlyCost) {}
    record RecordUsageRequest(@NotNull UUID budgetId, @NotNull LocalDate usageDate,
                              @NotNull @Positive BigDecimal hoursUsed, BigDecimal kmUsed,
                              String operator, String notes) {}
    record MaintenanceScheduleRequest(BigDecimal nextHours, LocalDate nextDate) {}

    record EquipmentResponse(UUID id, String code, String name, String type, String brand, String model,
                             Integer year, BigDecimal hourlyCost, String status,
                             BigDecimal currentHours, BigDecimal currentKm, boolean maintenanceDue) {
        static EquipmentResponse from(Equipment e) {
            return new EquipmentResponse(e.getId(), e.getCode(), e.getName(), e.getType(), e.getBrand(),
                    e.getModel(), e.getYear(), e.getHourlyCost(), e.getStatus(),
                    e.getCurrentHours(), e.getCurrentKm(), e.isMaintenanceDue());
        }
    }

    record UsageResponse(UUID id, LocalDate usageDate, BigDecimal hoursUsed, BigDecimal kmUsed,
                         String operator, BigDecimal cost) {
        static UsageResponse from(EquipmentUsage u) {
            return new UsageResponse(u.getId(), u.getUsageDate(), u.getHoursUsed(), u.getKmUsed(),
                    u.getOperator(), u.getCost());
        }
    }

    record RecordFuelingRequest(@NotNull UUID budgetId, @NotNull LocalDate fuelingDate, @NotBlank String fuelType,
                                @NotNull @Positive BigDecimal liters, @NotNull @Positive BigDecimal costPerLiter,
                                BigDecimal odometer, String notes) {}

    record FuelingResponse(UUID id, LocalDate fuelingDate, String fuelType, BigDecimal liters,
                           BigDecimal costPerLiter, BigDecimal totalCost, BigDecimal odometer) {
        static FuelingResponse from(EquipmentFueling f) {
            return new FuelingResponse(f.getId(), f.getFuelingDate(), f.getFuelType(), f.getLiters(),
                    f.getCostPerLiter(), f.getTotalCost(), f.getOdometer());
        }
    }
}
