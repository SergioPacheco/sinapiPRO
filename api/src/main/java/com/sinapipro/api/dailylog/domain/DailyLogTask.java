package com.sinapipro.api.dailylog.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "daily_log_task")
public class DailyLogTask {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_log_id", nullable = false)
    private DailyLog dailyLog;

    @Column(name = "activity_id")
    private UUID activityId;

    @Column(nullable = false, length = 300)
    private String description;

    @Column(name = "progress_pct", precision = 5, scale = 2)
    private BigDecimal progressPct;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public DailyLogTask() {}

    public DailyLogTask(DailyLog dailyLog, UUID activityId, String description, BigDecimal progressPct) {
        this.dailyLog = dailyLog;
        this.activityId = activityId;
        this.description = description;
        this.progressPct = progressPct;
    }

    public UUID getId() { return id; }
    public DailyLog getDailyLog() { return dailyLog; }
    public UUID getActivityId() { return activityId; }
    public String getDescription() { return description; }
    public BigDecimal getProgressPct() { return progressPct; }
}
