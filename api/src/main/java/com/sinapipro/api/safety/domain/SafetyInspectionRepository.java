package com.sinapipro.api.safety.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SafetyInspectionRepository extends JpaRepository<SafetyInspection, UUID> {
    Page<SafetyInspection> findByBudgetId(UUID budgetId, Pageable pageable);
}
