package com.sinapipro.api.tenant.application;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import com.sinapipro.api.tenant.domain.TenantContext;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * Garante que tenant_id é setado automaticamente em toda operação de escrita.
 * Previne que uma empresa crie dados sem tenant ou com tenant de outra empresa.
 */
public class TenantEntityListener {

    @PrePersist
    public void prePersist(Object entity) {
        if (entity instanceof TenantAwareEntity tenantEntity) {
            var currentTenant = TenantContext.get();
            if (currentTenant != null && tenantEntity.getTenantId() == null) {
                tenantEntity.setTenantId(currentTenant);
            }
        }
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        if (entity instanceof TenantAwareEntity tenantEntity) {
            var currentTenant = TenantContext.get();
            // Previne alteração de tenant_id (segurança)
            if (currentTenant != null && tenantEntity.getTenantId() != null
                    && !tenantEntity.getTenantId().equals(currentTenant)) {
                throw new SecurityException("Cannot modify entity belonging to another tenant");
            }
        }
    }
}
