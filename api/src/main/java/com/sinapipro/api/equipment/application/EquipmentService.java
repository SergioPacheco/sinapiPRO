package com.sinapipro.api.equipment.application;

import com.sinapipro.api.equipment.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentUsageRepository usageRepository;

    public EquipmentService(EquipmentRepository equipmentRepository, EquipmentUsageRepository usageRepository) {
        this.equipmentRepository = equipmentRepository;
        this.usageRepository = usageRepository;
    }

    @Transactional
    public Equipment create(String code, String name, String type, String brand, String model,
                            Integer year, String licensePlate, BigDecimal hourlyCost) {
        if (equipmentRepository.existsByCode(code)) {
            throw new IllegalArgumentException("Equipment code already exists: " + code);
        }
        return equipmentRepository.save(new Equipment(code, name, type, brand, model, year, licensePlate, hourlyCost));
    }

    @Transactional
    public EquipmentUsage recordUsage(UUID equipmentId, UUID budgetId, LocalDate usageDate,
                                      BigDecimal hoursUsed, BigDecimal kmUsed, String operator, String notes) {
        Equipment equipment = findOrThrow(equipmentId);
        equipment.addUsage(hoursUsed, kmUsed);
        equipmentRepository.save(equipment);

        EquipmentUsage usage = new EquipmentUsage(equipment, budgetId, usageDate, hoursUsed, kmUsed, operator, notes);
        return usageRepository.save(usage);
    }

    @Transactional
    public Equipment scheduleMaintenace(UUID equipmentId, BigDecimal nextHours, LocalDate nextDate) {
        Equipment equipment = findOrThrow(equipmentId);
        equipment.setMaintenanceSchedule(nextHours, nextDate);
        return equipmentRepository.save(equipment);
    }

    public List<MaintenanceAlert> getMaintenanceAlerts() {
        List<MaintenanceAlert> alerts = new ArrayList<>();
        for (Equipment e : equipmentRepository.findMaintenanceDueByHours()) {
            alerts.add(new MaintenanceAlert(e.getId(), e.getCode(), e.getName(), "HOURS",
                    "Current: " + e.getCurrentHours() + "h, Limit: " + e.getNextMaintenanceHours() + "h"));
        }
        for (Equipment e : equipmentRepository.findMaintenanceDueByDate()) {
            if (alerts.stream().noneMatch(a -> a.equipmentId().equals(e.getId()))) {
                alerts.add(new MaintenanceAlert(e.getId(), e.getCode(), e.getName(), "DATE",
                        "Due: " + e.getNextMaintenanceDate()));
            }
        }
        return alerts;
    }

    public Page<Equipment> list(Pageable pageable) {
        return equipmentRepository.findAll(pageable);
    }

    public EquipmentCostSummary costSummary(UUID budgetId) {
        List<EquipmentUsage> usages = usageRepository.findByBudgetIdOrderByUsageDateDesc(budgetId);
        BigDecimal totalHours = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;
        for (EquipmentUsage u : usages) {
            totalHours = totalHours.add(u.getHoursUsed());
            totalCost = totalCost.add(u.getCost());
        }
        return new EquipmentCostSummary(usages.size(), totalHours, totalCost);
    }

    private Equipment findOrThrow(UUID id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Equipment not found: " + id));
    }

    public record MaintenanceAlert(UUID equipmentId, String code, String name, String alertType, String message) {}
    public record EquipmentCostSummary(int totalUsages, BigDecimal totalHours, BigDecimal totalCost) {}
}
