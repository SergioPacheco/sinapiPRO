package com.sinapipro.api.procurement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ProcurementScheduleRepository extends JpaRepository<ProcurementSchedule, UUID> {
    List<ProcurementSchedule> findByProjectIdAndStatusOrderByPlannedDate(UUID projectId, String status);
    List<ProcurementSchedule> findByProjectIdOrderByPlannedDate(UUID projectId);
}
