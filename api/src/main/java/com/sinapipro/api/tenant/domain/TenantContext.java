package com.sinapipro.api.tenant.domain;

import java.util.UUID;

/**
 * Armazena o tenantId da request atual (ThreadLocal).
 * Usado pelo TenantInterceptor e pelo Hibernate Filter.
 */
public final class TenantContext {

    private static final ThreadLocal<UUID> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {}

    public static void set(UUID tenantId) { CURRENT_TENANT.set(tenantId); }
    public static UUID get() { return CURRENT_TENANT.get(); }
    public static void clear() { CURRENT_TENANT.remove(); }
}
