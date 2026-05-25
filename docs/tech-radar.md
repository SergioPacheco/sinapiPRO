# SinapiPRO — Tech Radar

> Atualizado: 2026-05-25 | Revisão: trimestral

## ADOPT (usar em produção, padrão do projeto)

| Tecnologia | Justificativa |
|-----------|---------------|
| Java 25 + Virtual Threads | Runtime principal, throughput alto sem complexidade reativa |
| Spring Boot 4 | Framework web, DI, configuração, actuator |
| PostgreSQL 17 | Banco principal — JSONB, tsvector, UUID nativo |
| Flyway | Schema migrations versionadas e reversíveis |
| Spring Data JPA / Hibernate 7 | ORM com Hibernate Filter para multi-tenancy |
| Spring Security + JWT (HMAC) | Autenticação stateless, scopes granulares |
| Micrometer + Prometheus | Métricas de aplicação e negócio |
| OpenTelemetry | Distributed tracing (mesmo em monolito, para preparação) |
| Docker + Compose | Ambiente de dev reproduzível |
| Testcontainers | Testes de integração com banco real |
| JaCoCo | Cobertura de código |
| ArchUnit | Fitness functions — boundaries automatizadas |
| Structured Logging (ECS) | Logs parseáveis, correlação por trace-id |

## TRIAL (usando em escopo limitado, avaliando para adoção ampla)

| Tecnologia | Onde está sendo usado | Próximo passo |
|-----------|----------------------|---------------|
| Resilience4j | Email service (CB + Retry) | Expandir para todas integrações externas |
| FastExcel | Exportação de dados | Substituir Apache POI completamente |
| JTE (templates compilados) | Relatórios PDF | Avaliar performance vs Thymeleaf |
| OpenHTMLtoPDF | PDF tabular | Validar com relatórios complexos |
| Sealed Interfaces (Domain Events) | Measurement module | Expandir para todos os aggregates |
| Spring ApplicationEventPublisher | Eventos intra-processo | Avaliar se precisa de broker externo |

## ASSESS (investigando, sem uso em produção)

| Tecnologia | Por que avaliar | Risco |
|-----------|----------------|-------|
| GraalVM Native Image | Startup < 1s, menor footprint | Reflection-heavy (JPA), build time longo |
| Structured Concurrency (JEP 480) | Substituir try-with-executor pattern | Preview feature, API pode mudar |
| ScopedValues (JEP 481) | Substituir ThreadLocal para tenant context | Preview, Spring ainda não suporta nativamente |
| Playwright (PDF complexo) | Relatórios com gráficos ECharts | Dependência pesada, cold start |
| Gotenberg (sidecar) | PDF em produção K8s | Complexidade operacional |
| Row Level Security (PG) | Alternativa ao Hibernate Filter | Acoplamento com PG, difícil de testar |
| Keycloak / Auth0 | IdP externo para RBAC real | Custo, complexidade de integração |

## HOLD (não usar em novos desenvolvimentos)

| Tecnologia | Motivo | Alternativa |
|-----------|--------|-------------|
| `ddl-auto: update` em produção | Risco de perda de dados, irreversível | Flyway + `validate` |
| Apache POI | Pesado em memória, API verbosa | FastExcel (streaming) |
| InMemoryUserDetailsManager | Não escala, sem persistência | UserRepository + banco |
| Secrets hardcoded | Risco de segurança | Environment variables / Vault |
| `connection-timeout: 300s` | Threads presas por 5 min | 30s máximo |
| Spring WebFlux (para este projeto) | Complexidade desnecessária com Virtual Threads | Spring MVC + VT |
| FreeMarker + Flying Saucer (legado) | Stack antiga de PDF | JTE + OpenHTMLtoPDF |

---

## Processo de atualização

1. **Trimestral**: revisar cada item, mover entre quadrantes se necessário
2. **Ao adicionar dependência**: registrar aqui com justificativa
3. **Ao deprecar**: mover para HOLD com alternativa documentada
4. **ADR obrigatório**: para mover item de ASSESS → TRIAL ou TRIAL → ADOPT
