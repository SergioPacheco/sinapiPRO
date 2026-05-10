package com.sinapipro.api.measurement.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "measurement_history")
public class MeasurementHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "measurement_id", nullable = false)
    private UUID measurementId;

    @Column(nullable = false, length = 20)
    private String action;

    @Column(name = "from_status", length = 20)
    private String fromStatus;

    @Column(name = "to_status", nullable = false, length = 20)
    private String toStatus;

    @Column(name = "performed_by", length = 140)
    private String performedBy;

    @Column(length = 500)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public MeasurementHistory() {}

    public MeasurementHistory(UUID measurementId, String action, String fromStatus, String toStatus, String performedBy, String reason) {
        this.measurementId = measurementId;
        this.action = action;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.performedBy = performedBy;
        this.reason = reason;
    }

    public UUID getId() { return id; }
    public UUID getMeasurementId() { return measurementId; }
    public String getAction() { return action; }
    public String getFromStatus() { return fromStatus; }
    public String getToStatus() { return toStatus; }
    public String getPerformedBy() { return performedBy; }
    public String getReason() { return reason; }
    public Instant getCreatedAt() { return createdAt; }
}
