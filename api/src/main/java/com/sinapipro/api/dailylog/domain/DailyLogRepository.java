package com.sinapipro.api.dailylog.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DailyLogRepository extends JpaRepository<DailyLog, UUID> {
    List<DailyLog> findByBudgetIdOrderByLogDateDesc(UUID budgetId);
    Page<DailyLog> findByBudgetId(UUID budgetId, Pageable pageable);
}
