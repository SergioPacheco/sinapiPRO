package com.sinapipro.api.equipment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface EquipmentUsageRepository extends JpaRepository<EquipmentUsage, UUID> {
    List<EquipmentUsage> findByEquipmentIdOrderByUsageDateDesc(UUID equipmentId);
    List<EquipmentUsage> findByBudgetIdOrderByUsageDateDesc(UUID budgetId);

    @Query("SELECT COALESCE(SUM(u.hoursUsed), 0) FROM EquipmentUsage u WHERE u.equipment.id = :equipmentId")
    BigDecimal sumHoursByEquipment(UUID equipmentId);
}
