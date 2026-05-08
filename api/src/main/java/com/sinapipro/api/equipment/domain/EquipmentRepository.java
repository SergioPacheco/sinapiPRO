package com.sinapipro.api.equipment.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {
    Optional<Equipment> findByCode(String code);
    boolean existsByCode(String code);
    Page<Equipment> findByStatus(String status, Pageable pageable);

    @Query("SELECT e FROM Equipment e WHERE e.nextMaintenanceHours IS NOT NULL AND e.currentHours >= e.nextMaintenanceHours")
    List<Equipment> findMaintenanceDueByHours();

    @Query("SELECT e FROM Equipment e WHERE e.nextMaintenanceDate IS NOT NULL AND e.nextMaintenanceDate <= CURRENT_DATE")
    List<Equipment> findMaintenanceDueByDate();
}
