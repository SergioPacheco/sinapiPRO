package com.sinapipro.api.schedule.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScheduleActivityRepository extends JpaRepository<ScheduleActivity, UUID> {
    List<ScheduleActivity> findByBudgetIdOrderBySortOrder(UUID budgetId);
}
