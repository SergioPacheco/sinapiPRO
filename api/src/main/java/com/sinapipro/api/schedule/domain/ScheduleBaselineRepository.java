package com.sinapipro.api.schedule.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ScheduleBaselineRepository extends JpaRepository<ScheduleBaseline, UUID> {
    List<ScheduleBaseline> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
