package com.sinapipro.api.dailylog.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "daily_log_labor")
public class DailyLogLabor {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "daily_log_id", nullable = false) private DailyLog dailyLog;
    @Column(name = "worker_name", nullable = false, length = 140) private String workerName;
    @Column(nullable = false, length = 80) private String role;
    @Column(nullable = false, precision = 4, scale = 2) private BigDecimal hours;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @PrePersist void prePersist() { createdAt = Instant.now(); }
    protected DailyLogLabor() {}
    public DailyLogLabor(DailyLog dailyLog, String workerName, String role, BigDecimal hours) {
        this.dailyLog = dailyLog; this.workerName = workerName; this.role = role; this.hours = hours;
    }
    public UUID getId() { return id; }
    public String getWorkerName() { return workerName; }
    public String getRole() { return role; }
    public BigDecimal getHours() { return hours; }
}
