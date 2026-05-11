package com.sinapipro.api.shared.domain;

import com.sinapipro.api.tenant.domain.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

/**
 * Interface marker para repositories que precisam de filtro tenant em queries nativas.
 * 
 * Para queries JPQL: o Hibernate Filter já funciona automaticamente.
 * Para queries nativas: adicionar manualmente "AND tenant_id = :tenantId"
 * e usar TenantContext.get() para obter o valor.
 * 
 * Exemplo de uso em query nativa:
 * <pre>
 * @Query(value = "SELECT * FROM project WHERE status = :status AND tenant_id = :tenantId", nativeQuery = true)
 * List<Project> findByStatusNative(@Param("status") String status, @Param("tenantId") UUID tenantId);
 * </pre>
 * 
 * Chamar com: repository.findByStatusNative("ACTIVE", TenantContext.get());
 */
@NoRepositoryBean
public interface TenantAwareRepository<T extends TenantAwareEntity, ID> extends JpaRepository<T, ID> {

    /**
     * Retorna o tenant_id da request atual.
     * Usar em queries nativas como parâmetro.
     */
    default UUID currentTenantId() {
        return TenantContext.get();
    }
}
