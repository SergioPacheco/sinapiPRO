package com.sinapipro.api.safety.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SafetyIncidentRepository extends JpaRepository<SafetyIncident, UUID> {
    Page<SafetyIncident> findByBudgetId(UUID budgetId, Pageable pageable);
    java.util.List<SafetyIncident> findByBudgetIdOrderByIncidentDateDesc(UUID budgetId);
    long countByBudgetIdAndStatus(UUID budgetId, String status);
}
