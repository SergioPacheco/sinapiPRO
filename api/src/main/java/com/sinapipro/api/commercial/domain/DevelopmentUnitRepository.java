package com.sinapipro.api.commercial.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface DevelopmentUnitRepository extends JpaRepository<DevelopmentUnit, UUID> {
    List<DevelopmentUnit> findByDevelopmentIdOrderByCode(UUID developmentId);
    List<DevelopmentUnit> findByDevelopmentIdAndStatus(UUID developmentId, String status);
}
