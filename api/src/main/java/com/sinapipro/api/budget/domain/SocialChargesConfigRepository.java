package com.sinapipro.api.budget.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SocialChargesConfigRepository extends JpaRepository<SocialChargesConfig, UUID> {
    List<SocialChargesConfig> findByBudgetId(UUID budgetId);
    Optional<SocialChargesConfig> findByBudgetIdAndWorkerType(UUID budgetId, String workerType);
}
