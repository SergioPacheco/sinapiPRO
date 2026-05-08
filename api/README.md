# SinapiPRO Showcase API

> API REST moderna para gestão de obras e orçamentos da construção civil, baseada na tabela SINAPI

[![Java 25](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/projects/jdk/25/)
[![Spring Boot 4](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL 17](https://img.shields.io/badge/PostgreSQL-17.5-blue.svg)](https://www.postgresql.org/)
[![Tests](https://img.shields.io/badge/Tests-68%20passing-brightgreen.svg)]()

---

## Quick Start

```bash
# Requer: Java 25 (SDKMAN), Docker (para PostgreSQL)
cd api

# Dev com Docker Compose automático (Spring Boot inicia o PG)
mvn spring-boot:run -s .mvn/settings.xml

# Ou full stack (app + PG + Prometheus + Grafana + OTel)
mvn package -s .mvn/settings.xml -DskipTests
docker compose -f compose.showcase.yaml up -d
```

**Acesso:**
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Actuator: http://localhost:8081/actuator
- Grafana: http://localhost:3000 (admin/admin)
- Prometheus: http://localhost:9090

---

## Autenticação

```bash
# Obter token JWT
curl -X POST http://localhost:8080/api/v1/auth/token \
  -H "Content-Type: application/json" \
  -d '{"grantType":"PASSWORD","username":"admin@sinapipro.dev","password":"SinapiPro#2026"}'

# Usar token
curl http://localhost:8080/api/v1/budgets \
  -H "Authorization: Bearer <access_token>"
```

---

## Endpoints (90+)

### Core — Orçamento & Custos
| Módulo | Base Path | Descrição |
|--------|-----------|-----------|
| Budgets | `/api/v1/budgets` | CRUD orçamentos + filtros + paginação |
| Budget Detail | `/api/v1/budgets/{id}` | Etapas, itens, BDI, Curva ABC, reajuste de preços |
| Compositions | `/api/v1/compositions` | Catálogo SINAPI, full-text search, cálculo de custo, importação CSV |
| Materials | `/api/v1/materials` | Materiais e preços por estado/mês |
| Job Costing | `/api/v1/budgets/{id}/cost-codes` | Códigos de custo, variância, WIP Report (over/under billing) |

### Execução — Controle da Obra
| Módulo | Base Path | Descrição |
|--------|-----------|-----------|
| Schedule | `/api/v1/budgets/{id}/schedule` | Atividades, Curva S, Caminho Crítico (CPM), dependências |
| Measurements | `/api/v1/budgets/{id}/measurements` | Medições com workflow DRAFT→SUBMITTED→APPROVED→PAID + Progress Billing |
| Contracts | `/api/v1/budgets/{id}/contracts` | Contratos, aditivos (change orders), financial summary |
| Daily Log | `/api/v1/budgets/{id}/daily-logs` | Diário de obra (MO, equipamentos, clima, ocorrências) |
| Time Tracking | `/api/v1/budgets/{id}/timesheets` | Timesheets, horas extras, produtividade por função |

### Suprimentos & Equipamentos
| Módulo | Base Path | Descrição |
|--------|-----------|-----------|
| Procurement | `/api/v1/budgets/{id}/procurement` | Cotação → análise comparativa → pedido → recebimento |
| Equipment | `/api/v1/equipment` | Cadastro, uso por obra, alertas de manutenção (horas/data) |
| Suppliers | `/api/v1/suppliers` | Gestão de fornecedores |
| Invoices | `/api/v1/invoices` | Faturas (payable + receivable auto-geradas) |

### Qualidade & Segurança
| Módulo | Base Path | Descrição |
|--------|-----------|-----------|
| RFI | `/api/v1/budgets/{id}/rfis` | Requests for Information com prazo, overdue tracking |
| Submittals | `/api/v1/budgets/{id}/submittals` | Aprovação de documentos técnicos (6 status AIA) |
| Punch List | `/api/v1/budgets/{id}/punch-list` | Pendências de entrega (OPEN→IN_PROGRESS→COMPLETED→VERIFIED) |
| Safety | `/api/v1/safety/templates`, `/budgets/{id}/safety/*` | Checklists, inspeções, incidentes |
| Documents | `/api/v1/budgets/{id}/documents` | Upload com versionamento e validação OWASP |

### Inteligência & Visibilidade
| Módulo | Base Path | Descrição |
|--------|-----------|-----------|
| Analytics | `/api/v1/analytics` | Portfolio, EVM (CPI/SPI/EAC/VAC), Cash Flow, AI Delay Forecast |
| Weather Delays | `/api/v1/budgets/{id}/weather-delays` | Registro de dias perdidos + impacto no cronograma |
| Notifications | `/api/v1/notifications` | Alertas cross-module (RFI overdue, manutenção, contratos) |
| Events | `/api/v1/events` | SSE (Server-Sent Events) em tempo real |

---

## Arquitetura

```
src/main/java/com/sinapipro/api/
├── budget/          ← Orçamentos (stages, items, BDI, ABC, reajuste)
├── sinapi/          ← Composições SINAPI (cálculo, importação CSV)
├── jobcosting/      ← Cost codes, transactions, variância, WIP Report
├── schedule/        ← Cronograma, Curva S, CPM (caminho crítico)
├── measurement/     ← Medições + workflow + Progress Billing + integração JC
├── contract/        ← Contratos + change orders + financial summary
├── procurement/     ← Cotação → pedido → recebimento + integração JC
├── equipment/       ← Equipamentos + manutenção + custo por hora
├── dailylog/        ← Diário de obra + validações
├── timetracking/    ← Timesheets + labor productivity
├── analytics/       ← EVM, cash flow, portfolio, AI delay forecast
├── forecast/        ← Predição de atrasos (SPI + weather patterns)
├── rfi/             ← Requests for Information + overdue
├── submittal/       ← Aprovação de documentos técnicos (AIA)
├── punchlist/       ← Punch list (4-state workflow)
├── safety/          ← Checklists, inspeções, incidentes
├── document/        ← GED (upload, versionamento, OWASP)
├── weather/         ← Weather delay tracking
├── notification/    ← Alertas cross-module (idempotentes)
├── invoice/         ← Faturas
├── supplier/        ← Fornecedores
├── security/        ← JWT + OAuth2 + refresh tokens
├── shared/          ← Error handler (RFC 9457), events (SSE), observability
└── config/          ← Security, OpenAPI, health check, rate limiting
```

Cada módulo segue: `domain/` (entities + repos) → `application/` (services) → `api/` (controllers + DTOs)

---

## Funcionalidades Diferenciais (150% do mercado)

| Feature | Descrição |
|---------|-----------|
| **AI Delay Forecast** | Predição de atrasos baseada em SPI + padrão histórico de weather delays |
| **Progress Billing** | Medição aprovada gera invoice automaticamente (receivable) |
| **EVM Completo** | PV, EV, AC, CPI, SPI, EAC, VAC com BAC real dos cost codes |
| **WIP Report** | Over/under billing com billing ratio |
| **Labor Productivity** | Hours/unit e cost by role a partir de timesheets |
| **Notification Engine** | Alertas idempotentes cross-module (RFI, equipment, contracts) |
| **Submittals (AIA)** | 6 status: DRAFT→SUBMITTED→APPROVED/AS_NOTED/REJECTED/REVISE |
| **Critical Path (CPM)** | Topological sort + forward/backward pass + float calculation |
| **SINAPI Import** | Importação de materiais e composições via CSV |
| **Curva ABC** | Classificação 80/15/5 de insumos por custo |

---

## Testes

```bash
# Unit tests (68 testes, sem Docker)
mvn test -s .mvn/settings.xml -Dtest='!*IntegrationTest,!*IntegrationTest$*'

# Integration tests (requer Docker para Testcontainers)
mvn test -s .mvn/settings.xml -Dtest='*IntegrationTest'

# Todos
mvn test -s .mvn/settings.xml
```

---

## Stack

| Camada | Tecnologia |
|--------|-----------|
| Runtime | Java 25 + Virtual Threads |
| Framework | Spring Boot 4.0.5, Spring Security 7 (OAuth2 JWT) |
| Persistência | Spring Data JPA, Hibernate 7, Flyway (V1–V14) |
| Banco | PostgreSQL 17.5 (UUID PKs, JSONB, tsvector full-text) |
| Observabilidade | Micrometer + Prometheus + Grafana + OpenTelemetry |
| Docs | SpringDoc OpenAPI 3 (Swagger UI com 21 tags) |
| Testes | JUnit 5, Mockito, AssertJ, Testcontainers |
| Container | Docker multi-stage, ZGC, non-root, health check |
| CI/CD | GitHub Actions (build → test → docker) |
| Segurança | Rate limiting (120 req/min), file type validation, JWT stateless |

---

## Variáveis de Ambiente

| Variável | Default | Descrição |
|----------|---------|-----------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/sinapipro` | URL do banco |
| `SPRING_DATASOURCE_USERNAME` | `sinapipro` | Usuário DB |
| `SPRING_DATASOURCE_PASSWORD` | `sinapipro` | Senha DB |
| `SINAPIPRO_SECURITY_SECRET` | (em application.yaml) | Secret para assinatura JWT |
| `MANAGEMENT_OTLP_TRACING_ENDPOINT` | `http://localhost:4318/v1/traces` | OpenTelemetry collector |
| `SINAPIPRO_STORAGE_PATH` | `./uploads` | Diretório de armazenamento de documentos |

---

## Métricas do Projeto

| Métrica | Valor |
|---------|-------|
| Módulos de negócio | 21 |
| Arquivos Java | ~180 |
| Migrations Flyway | V1–V14 |
| Testes unitários | 68 |
| Testes integração | 4 |
| Endpoints REST | 90+ |
| Score vs mercado | 150% |
