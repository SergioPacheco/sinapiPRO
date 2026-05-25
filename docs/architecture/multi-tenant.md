# Multi-Tenancy — SinapiPRO

> **Status: ✅ Implementado** | Estratégia: Hibernate Filter + JWT claim

## Arquitetura

```
Request → JWT (tenant_id claim) → TenantInterceptor → Hibernate Filter → Queries filtradas
```

### Componentes

| Classe | Responsabilidade |
|--------|-----------------|
| `TenantContext` | ThreadLocal com UUID do tenant atual |
| `TenantInterceptor` | HandlerInterceptor que extrai `tenant_id` do JWT e ativa o Hibernate Filter |
| `TenantAwareEntity` | Superclasse com `@FilterDef` + `@Filter` + campo `tenant_id` |
| `TenantEntityListener` | `@PrePersist` que seta `tenant_id` automaticamente em novas entidades |
| `Tenant` | Entidade com plano, limites (max_users, max_projects), status |

### Fluxo

1. **Request chega** com JWT contendo claim `tenant_id`
2. **TenantInterceptor.preHandle()** extrai o UUID e:
   - Seta no `TenantContext` (ThreadLocal)
   - Ativa `session.enableFilter("tenantFilter").setParameter("tenantId", id)`
3. **Todas as queries** em entidades que estendem `TenantAwareEntity` são filtradas automaticamente
4. **TenantEntityListener** garante que novos registros recebem o `tenant_id` correto
5. **afterCompletion()** limpa o ThreadLocal

### Código-chave

```java
// TenantAwareEntity — superclasse para entidades com isolamento
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = UUID.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@EntityListeners(TenantEntityListener.class)
@MappedSuperclass
public abstract class TenantAwareEntity extends AuditableEntity {
    @Column(name = "tenant_id")
    private UUID tenantId;
}
```

```java
// TenantInterceptor — ativa filtro por request
Session session = entityManager.unwrap(Session.class);
session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
```

## Tabelas com Isolamento

### Isoladas diretamente (têm `tenant_id`)

- `project` — obras
- `supplier` — fornecedores
- `client` — clientes
- `employee` — funcionários
- `bank_account` — contas bancárias
- `equipment` — equipamentos
- `development` — empreendimentos
- `notification` — notificações

### Isoladas por cascata (via `project_id` → `tenant_id`)

Todas as tabelas vinculadas a `project` herdam o isolamento:
budget, measurement, contract, schedule_activity, daily_log, purchase_order, payable, receivable, cost_code, safety_inspection, rfi, punch_list_item, submittal, document, weather_delay, etc.

### Tabelas globais (compartilhadas entre tenants)

- `tenant` — cadastro de empresas
- `city` — cidades/estados
- `monetary_index` / `monetary_index_value` — índices econômicos
- `hour_type` — tipos de hora
- `unit_of_measure` — unidades de medida
- `material` / `composition` — catálogo SINAPI (referência pública)
- `app_settings` — configurações globais

## Planos e Limites

```java
public class Tenant {
    private String plan;          // FREE, STARTER, PRO, ENTERPRISE
    private LocalDate planExpiresAt;
    private int maxUsers;         // default: 5
    private int maxProjects;      // default: 3
}
```

## Queries Nativas

Para queries nativas (não passam pelo Hibernate Filter), adicionar manualmente:

```java
@Query(value = "SELECT * FROM project WHERE status = :status AND tenant_id = :tenantId", nativeQuery = true)
List<Project> findByStatusNative(@Param("status") String status, @Param("tenantId") UUID tenantId);
```

## Planejado (ainda não implementado)

| Feature | Descrição |
|---------|-----------|
| Tenant provisioning API | Endpoint para criar tenant + admin user automaticamente |
| Billing integration | Webhook de pagamento → atualiza plano/limites |
| Data export per tenant | GDPR-compliant export de todos os dados |
| Tenant-specific branding | Logo, cores, domínio customizado |
