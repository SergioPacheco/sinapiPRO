package com.sinapipro.api.schedule.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface HolidayRepository extends JpaRepository<Holiday, UUID> {
    List<Holiday> findByProjectIdOrderByHolidayDate(UUID projectId);
}
