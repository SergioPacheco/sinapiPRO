package com.sinapipro.api.notification.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notification")
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "budget_id") private UUID budgetId;
    @Column(nullable = false, length = 40) private String type;
    @Column(nullable = false, length = 20) private String severity; // INFO, WARNING, CRITICAL
    @Column(nullable = false, length = 300) private String title;
    @Column(nullable = false, columnDefinition = "text") private String message;
    @Column(name = "entity_type", length = 40) private String entityType;
    @Column(name = "entity_id") private UUID entityId;
    @Column(length = 140) private String recipient;
    @Column(nullable = false) private Boolean read;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;

    @PrePersist void prePersist() { createdAt = Instant.now(); }
    protected Notification() {}

    public Notification(UUID budgetId, String type, String severity, String title, String message,
                        String entityType, UUID entityId, String recipient) {
        this.budgetId = budgetId; this.type = type; this.severity = severity;
        this.title = title; this.message = message; this.entityType = entityType;
        this.entityId = entityId; this.recipient = recipient; this.read = false;
    }

    public UUID getId() { return id; }
    public UUID getBudgetId() { return budgetId; }
    public String getType() { return type; }
    public String getSeverity() { return severity; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getEntityType() { return entityType; }
    public UUID getEntityId() { return entityId; }
    public String getRecipient() { return recipient; }
    public Boolean getRead() { return read; }
    public Instant getCreatedAt() { return createdAt; }

    public void markRead() { this.read = true; }
}
