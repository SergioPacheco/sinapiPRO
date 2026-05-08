package com.sinapipro.api.measurement.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MeasurementRepository extends JpaRepository<Measurement, UUID> {
    List<Measurement> findByBudgetIdOrderByNumberDesc(UUID budgetId);
    Page<Measurement> findByBudgetId(UUID budgetId, Pageable pageable);
}
