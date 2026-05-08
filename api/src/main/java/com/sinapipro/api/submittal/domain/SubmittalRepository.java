package com.sinapipro.api.submittal.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SubmittalRepository extends JpaRepository<Submittal, UUID> {
    Page<Submittal> findByBudgetId(UUID budgetId, Pageable pageable);
    int countByBudgetId(UUID budgetId);
}
