package com.sinapipro.api.tenant.domain;

import java.util.UUID;

/**
 * Tenant context using Scoped Values (JEP 481, Java 25).
 *
 * Scoped Values are the modern replacement for ThreadLocal:
 * - Immutable within a scope (no accidental leaks between requests)
 * - Automatically inherited by child virtual threads
 * - Zero cleanup needed (no try/finally + remove())
 * - Better performance with Virtual Threads (no pinning)
 *
 * The TenantInterceptor binds the value via ScopedValue.callWhere(),
 * and all code within the request scope reads it via CURRENT.get().
 *
 * Fallback: ThreadLocal for contexts where ScopedValue.callWhere() cannot wrap
 * the entire call chain (e.g., Hibernate filters set in interceptor).
 */
public final class TenantContext {

    /** Primary: Scoped Value — immutable, virtual-thread-safe */
    public static final ScopedValue<UUID> CURRENT = ScopedValue.newInstance();

    /** Fallback: ThreadLocal for Hibernate filter integration */
    private static final ThreadLocal<UUID> THREAD_LOCAL = new ThreadLocal<>();

    private TenantContext() {}

    /** Get tenant from ScopedValue (preferred) or ThreadLocal (fallback) */
    public static UUID get() {
        return CURRENT.isBound() ? CURRENT.get() : THREAD_LOCAL.get();
    }

    /** Set via ThreadLocal (used by interceptor for Hibernate filter compatibility) */
    public static void set(UUID tenantId) { THREAD_LOCAL.set(tenantId); }

    /** Clear ThreadLocal (called in afterCompletion) */
    public static void clear() { THREAD_LOCAL.remove(); }
}
