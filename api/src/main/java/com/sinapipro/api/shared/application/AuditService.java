package com.sinapipro.api.shared.application;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// --- Entity ---
@Entity @Table(name = "audit_log")
class AuditLog extends TenantAwareEntity {
    @Column(name = "entity_type", nullable = false, length = 60) private String entityType;
    @Column(name = "entity_id", nullable = false) private UUID entityId;
    @Column(nullable = false, length = 20) private String action; // CREATE, UPDATE, DELETE
    @Column(name = "changed_by", length = 200) private String changedBy;
    @Column(name = "changed_at", nullable = false) private Instant changedAt = Instant.now();
    @Column(name = "old_values", columnDefinition = "jsonb") @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON) private Map<String, Object> oldValues;
    @Column(name = "new_values", columnDefinition = "jsonb") @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON) private Map<String, Object> newValues;
    protected AuditLog() {}
    public AuditLog(String entityType, UUID entityId, String action, String changedBy, Map<String, Object> oldValues, Map<String, Object> newValues) {
        this.entityType = entityType; this.entityId = entityId; this.action = action;
        this.changedBy = changedBy; this.oldValues = oldValues; this.newValues = newValues;
    }
    public String getEntityType() { return entityType; } public UUID getEntityId() { return entityId; }
    public String getAction() { return action; } public String getChangedBy() { return changedBy; }
    public Instant getChangedAt() { return changedAt; }
    public Map<String, Object> getOldValues() { return oldValues; } public Map<String, Object> getNewValues() { return newValues; }
}

// --- Repository ---
interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findByEntityTypeAndEntityIdOrderByChangedAtDesc(String entityType, UUID entityId);
    List<AuditLog> findByEntityTypeOrderByChangedAtDesc(String entityType);
}

// --- Service ---
@Service @Transactional
public class AuditService {
    private final AuditLogRepository auditRepo;
    public AuditService(AuditLogRepository auditRepo) { this.auditRepo = auditRepo; }

    /** 16.8 — Registrar alteração */
    public void log(String entityType, UUID entityId, String action, String changedBy, Map<String, Object> oldValues, Map<String, Object> newValues) {
        auditRepo.save(new AuditLog(entityType, entityId, action, changedBy, oldValues, newValues));
    }

    /** 16.8 — Histórico de alterações de uma entidade */
    public List<AuditLog> history(String entityType, UUID entityId) {
        return auditRepo.findByEntityTypeAndEntityIdOrderByChangedAtDesc(entityType, entityId);
    }

    /** 16.8 — Histórico por tipo */
    public List<AuditLog> historyByType(String entityType) {
        return auditRepo.findByEntityTypeOrderByChangedAtDesc(entityType);
    }
}
