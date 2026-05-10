package com.sinapipro.api.dailylog.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "daily_log_photo")
public class DailyLogPhoto {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "daily_log_id", nullable = false) private DailyLog dailyLog;
    @Column(name = "file_path", nullable = false, length = 500) private String filePath;
    @Column(length = 300) private String caption;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @PrePersist void prePersist() { createdAt = Instant.now(); }
    protected DailyLogPhoto() {}
    public DailyLogPhoto(DailyLog dailyLog, String filePath, String caption) {
        this.dailyLog = dailyLog; this.filePath = filePath; this.caption = caption;
    }
    public UUID getId() { return id; }
    public String getFilePath() { return filePath; }
    public String getCaption() { return caption; }
}
