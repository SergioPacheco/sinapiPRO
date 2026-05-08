package com.sinapipro.api.punchlist.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PunchListRepository extends JpaRepository<PunchListItem, UUID> {
    Page<PunchListItem> findByBudgetId(UUID budgetId, Pageable pageable);
    Page<PunchListItem> findByBudgetIdAndStatus(UUID budgetId, PunchListStatus status, Pageable pageable);
    long countByBudgetIdAndStatus(UUID budgetId, PunchListStatus status);
}
