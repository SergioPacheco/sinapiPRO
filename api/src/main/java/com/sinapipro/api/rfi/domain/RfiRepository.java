package com.sinapipro.api.rfi.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface RfiRepository extends JpaRepository<Rfi, UUID> {
    Page<Rfi> findByBudgetId(UUID budgetId, Pageable pageable);
    List<Rfi> findByBudgetIdAndStatus(UUID budgetId, RfiStatus status);
    int countByBudgetId(UUID budgetId);
}
