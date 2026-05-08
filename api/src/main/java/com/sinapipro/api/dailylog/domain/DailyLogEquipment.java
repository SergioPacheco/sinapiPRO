package com.sinapipro.api.dailylog.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "daily_log_equipment")
public class DailyLogEquipment {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "daily_log_id", nullable = false) private DailyLog dailyLog;
    @Column(name = "equipment_name", nullable = false, length = 140) private String equipmentName;
    @Column(name = "hours_used", nullable = false, precision = 4, scale = 2) private BigDecimal hoursUsed;
    @Column(name = "hours_idle", nullable = false, precision = 4, scale = 2) private BigDecimal hoursIdle;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @PrePersist void prePersist() { createdAt = Instant.now(); }
    protected DailyLogEquipment() {}
    public DailyLogEquipment(DailyLog dailyLog, String equipmentName, BigDecimal hoursUsed, BigDecimal hoursIdle) {
        this.dailyLog = dailyLog; this.equipmentName = equipmentName; this.hoursUsed = hoursUsed; this.hoursIdle = hoursIdle;
    }
    public UUID getId() { return id; }
    public String getEquipmentName() { return equipmentName; }
    public BigDecimal getHoursUsed() { return hoursUsed; }
    public BigDecimal getHoursIdle() { return hoursIdle; }
}
