package com.sinapipro.api.measurement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MeasurementApproverRepository extends JpaRepository<MeasurementApprover, UUID> {
    List<MeasurementApprover> findByProjectIdAndActiveTrue(UUID projectId);
}
