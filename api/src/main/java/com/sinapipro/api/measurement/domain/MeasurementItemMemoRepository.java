package com.sinapipro.api.measurement.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MeasurementItemMemoRepository extends JpaRepository<MeasurementItemMemo, UUID> {
    Optional<MeasurementItemMemo> findByMeasurementItemId(UUID measurementItemId);
}
