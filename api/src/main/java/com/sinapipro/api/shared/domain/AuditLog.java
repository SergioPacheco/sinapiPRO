package com.sinapipro.api.shared.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "tenant_id") private UUID tenantId;
    @Column(name = "entity_type", nullable = false, length = 60) private String entityType;
    @Column(name = "entity_id", nullable = false) private UUID entityId;
    @Column(nullable = false, length = 20) private String action;
    @Column(name = "changed_by", length = 200) private String changedBy;
    @Column(name = "changed_at", nullable = false) private Instant changedAt = Instant.now();
    @Column(columnDefinition = "jsonb") private String changes;

    protected AuditLog() {}

    public AuditLog(UUID tenantId, String entityType, UUID entityId, String action, String changedBy, String changes) {
        this.tenantId = tenantId; this.entityType = entityType; this.entityId = entityId;
        this.action = action; this.changedBy = changedBy; this.changes = changes;
    }

    public UUID getId() { return id; }
    public UUID getTenantId() { return tenantId; }
    public String getEntityType() { return entityType; }
    public UUID getEntityId() { return entityId; }
    public String getAction() { return action; }
    public String getChangedBy() { return changedBy; }
    public Instant getChangedAt() { return changedAt; }
    public String getChanges() { return changes; }
}
