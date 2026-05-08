package com.sinapipro.api.dailylog.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "daily_log_occurrence")
public class DailyLogOccurrence {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "daily_log_id", nullable = false) private DailyLog dailyLog;
    @Column(nullable = false, length = 40) private String type;
    @Column(nullable = false, columnDefinition = "text") private String description;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @PrePersist void prePersist() { createdAt = Instant.now(); }
    protected DailyLogOccurrence() {}
    public DailyLogOccurrence(DailyLog dailyLog, String type, String description) {
        this.dailyLog = dailyLog; this.type = type; this.description = description;
    }
    public UUID getId() { return id; }
    public String getType() { return type; }
    public String getDescription() { return description; }
}
