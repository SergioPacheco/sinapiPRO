package com.sinapipro.api.equipment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface EquipmentFuelingRepository extends JpaRepository<EquipmentFueling, UUID> {
    List<EquipmentFueling> findByEquipmentIdOrderByFuelingDateDesc(UUID equipmentId);
}
