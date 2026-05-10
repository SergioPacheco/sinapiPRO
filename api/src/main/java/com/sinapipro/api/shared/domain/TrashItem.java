package com.sinapipro.api.shared.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Lixeira genérica — armazena entidades excluídas para recuperação.
 */
@Entity
@Table(name = "trash_item")
public class TrashItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "entity_type", nullable = false, length = 40)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "entity_name", length = 200)
    private String entityName;

    @Column(name = "deleted_by", length = 140)
    private String deletedBy;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> snapshot;

    @Column(name = "deleted_at", nullable = false)
    private Instant deletedAt = Instant.now();

    @Column(name = "expires_at")
    private Instant expiresAt;

    public TrashItem() {}

    public TrashItem(String entityType, UUID entityId, String entityName, String deletedBy, Map<String, Object> snapshot) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityName = entityName;
        this.deletedBy = deletedBy;
        this.snapshot = snapshot;
        this.expiresAt = Instant.now().plusSeconds(30L * 24 * 60 * 60); // 30 days
    }

    public UUID getId() { return id; }
    public String getEntityType() { return entityType; }
    public UUID getEntityId() { return entityId; }
    public String getEntityName() { return entityName; }
    public String getDeletedBy() { return deletedBy; }
    public Map<String, Object> getSnapshot() { return snapshot; }
    public Instant getDeletedAt() { return deletedAt; }
    public Instant getExpiresAt() { return expiresAt; }
}
