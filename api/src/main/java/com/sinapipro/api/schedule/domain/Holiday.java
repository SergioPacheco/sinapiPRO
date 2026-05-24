package com.sinapipro.api.schedule.domain;
import com.sinapipro.api.shared.domain.TenantAwareEntity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "schedule_holiday", uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "holiday_date"}))
public class Holiday extends TenantAwareEntity {


    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "holiday_date", nullable = false)
    private LocalDate holidayDate;

    @Column(length = 100)
    private String description;

    @Column(nullable = false)
    private boolean recurring = false;

    public Holiday() {}

    public Holiday(UUID projectId, LocalDate holidayDate, String description, boolean recurring) {
        this.projectId = projectId;
        this.holidayDate = holidayDate;
        this.description = description;
        this.recurring = recurring;
    }

    public UUID getProjectId() { return projectId; }
    public LocalDate getHolidayDate() { return holidayDate; }
    public String getDescription() { return description; }
    public boolean isRecurring() { return recurring; }
}
