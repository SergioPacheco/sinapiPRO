# State — SinapiPRO

## Sessão atual: 2026-05-08

### O que mudou
- **Refatoração completa**: projeto migrado de Java 11 + Spring Boot 2.7 + MySQL + Thymeleaf para **Java 25 + Spring Boot 4.0.5 + PostgreSQL 17.5 + API REST**
- Módulo legado (raiz) mantido como referência de domínio
- Novo módulo `api/` é onde todo desenvolvimento acontece

### Módulos implementados no api
| Módulo | Status | Descrição |
|--------|--------|-----------|
| `budget/` | ✅ Completo | CRUD + filtros + paginação |
| `supplier/` | ✅ Completo | CRUD + filtros |
| `invoice/` | ✅ Completo | CRUD + relacionamentos |
| `sinapi/` | ✅ Completo | Composições + insumos + preços + cálculo de custo |
| `security/` | ✅ Completo | JWT + OAuth2 + refresh tokens |
| `shared/` | ✅ Completo | ProblemDetail, SSE events, observabilidade |
| `config/` | ✅ Completo | Security, OpenAPI |

### Infraestrutura
- CI/CD: `.github/workflows/showcase-ci.yml` (build + test + docker + OWASP)
- Docker: `compose.showcase.yaml` (app + PG + pgAdmin + Prometheus + Grafana + OTel)
- Testes: WebTestClient + Testcontainers + JUnit 5 + Mockito + JaCoCo

### Specs pendentes (em `.kiro/docs/specs-and-tasks.md`)
- SPEC 1: Job Costing & Cost Codes (P0)
- SPEC 2: Cronograma & Planejamento Físico (P0)
- SPEC 3: Medições de Obra (P0)
- SPEC 5: Contratos & Aditivos (P1)
- SPEC 6: Suprimentos (P1)
- SPEC 7: Orçamento Detalhado — refatoração do budget (P1)
- SPEC 8: Dashboard & Relatórios (P2)
- SPEC 9: Diário de Obra (P2)

### Próximo passo
SPEC 7 (Orçamento Detalhado) — refatorar budget para suportar etapas hierárquicas + composições SINAPI + BDI

---

## Histórico

### 2026-05-08 — Refatoração api
- Criado módulo `api/` com Spring Boot 4.0.5 + Java 25
- Implementados 4 módulos de domínio + security + shared
- CI/CD com GitHub Actions
- Specs de mercado pesquisadas e documentadas (9 specs, 3 fases)
- SPEC 4 (Composições SINAPI) implementada com full-text search PostgreSQL
