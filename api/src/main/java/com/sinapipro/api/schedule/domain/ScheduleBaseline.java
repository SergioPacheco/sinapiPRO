package com.sinapipro.api.schedule.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "schedule_baseline")
public class ScheduleBaseline {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(nullable = false, length = 100)
    private String name;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<ActivitySnapshot> snapshot;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected ScheduleBaseline() {}

    public ScheduleBaseline(UUID projectId, String name, List<ActivitySnapshot> snapshot) {
        this.projectId = projectId;
        this.name = name;
        this.snapshot = snapshot;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getProjectId() { return projectId; }
    public String getName() { return name; }
    public List<ActivitySnapshot> getSnapshot() { return snapshot; }
    public Instant getCreatedAt() { return createdAt; }

    public record ActivitySnapshot(UUID activityId, String name, String plannedStart, String plannedEnd,
                                   String weight, String progressPct, int sortOrder) {}
}
