# ADR-0003 — Hibernate Filter para Multi-Tenancy

## Status
Aceito

## Contexto
SinapiPRO é SaaS multi-tenant. Cada construtora (tenant) deve ver apenas seus dados. Precisamos de isolamento de dados sem complexidade de infraestrutura.

Estratégias possíveis: banco separado por tenant, schema separado, ou discriminator column (shared database).

## Decisão
Usar **shared database com discriminator column** (`tenant_id`) + **Hibernate `@Filter`** para aplicar automaticamente o filtro em todas as queries JPQL. Um `TenantInterceptor` extrai o tenant do JWT e ativa o filtro na Session.

## Alternativas consideradas
1. **Database per tenant** — isolamento total, mas: connection pool por tenant, migrations multiplicadas, custo de infra alto.
2. **Schema per tenant** — bom isolamento, mas: Flyway precisa rodar N vezes, connection switching complexo.
3. **Shared DB + Hibernate Filter (escolhido)** — simples, uma migration serve todos, filtro transparente.
4. **Row Level Security (PostgreSQL)** — nativo do banco, mas: acoplamento com PG, difícil de testar, não funciona com connection pooling padrão.

## Consequências
### Positivas
- Uma única migration serve todos os tenants
- Connection pool compartilhado (eficiente)
- Filtro transparente para o código de negócio
- Fácil de testar (basta setar TenantContext)

### Negativas
- Filtro NÃO funciona em native queries (precisa adicionar `AND tenant_id = ?` manualmente)
- Risco de data leak se o filtro não for ativado (mitigado pelo interceptor)
- Não há isolamento de performance entre tenants (noisy neighbor)

### Riscos
- Desenvolvedor esquece de ativar filtro em nova query nativa → data leak
- Tenant com muitos dados degrada performance de outros

## Como medir sucesso
- Zero data leak em testes de isolamento (`TenantIsolationTest`)
- Queries sem full table scan (tenant_id indexado)

## Plano de rollback
Migrar para Row Level Security do PostgreSQL se o volume de native queries crescer demais, ou para schema-per-tenant se noisy neighbor se tornar problema.
