package com.sinapipro.api.shared.application;

import com.sinapipro.api.shared.domain.AuditLog;
import com.sinapipro.api.shared.domain.AuditLogRepository;
import com.sinapipro.api.tenant.domain.TenantContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String entityType, UUID entityId, String action, String changes) {
        var user = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName() : "system";
        auditLogRepository.save(new AuditLog(TenantContext.get(), entityType, entityId, action, user, changes));
    }

    public Page<AuditLog> getHistory(String entityType, UUID entityId, Pageable pageable) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByChangedAtDesc(entityType, entityId, pageable);
    }
}
