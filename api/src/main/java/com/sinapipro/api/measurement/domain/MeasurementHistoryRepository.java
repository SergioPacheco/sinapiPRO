package com.sinapipro.api.measurement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MeasurementHistoryRepository extends JpaRepository<MeasurementHistory, UUID> {
    List<MeasurementHistory> findByMeasurementIdOrderByCreatedAtDesc(UUID measurementId);
}
