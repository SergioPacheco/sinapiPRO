<p align="center">
  <img src="docs/logo.png" alt="SinapiPRO" width="200"/>
</p>

<h1 align="center">SinapiPRO</h1>

<p align="center">
  <strong>ERP open source para gestão completa de obras da construção civil</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-25-orange?logo=openjdk" alt="Java 25"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen?logo=springboot" alt="Spring Boot 4"/>
  <img src="https://img.shields.io/badge/Angular-19-red?logo=angular" alt="Angular 19"/>
  <img src="https://img.shields.io/badge/PrimeNG-19-blue?logo=angular" alt="PrimeNG 19"/>
  <img src="https://img.shields.io/badge/PostgreSQL-17-blue?logo=postgresql" alt="PostgreSQL"/>
  <img src="https://img.shields.io/badge/Docker-Ready-blue?logo=docker" alt="Docker"/>
  <img src="https://img.shields.io/badge/License-MIT-green" alt="License"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Virtual%20Threads-Loom-purple" alt="Virtual Threads"/>
  <img src="https://img.shields.io/badge/Structured%20Concurrency-JEP%20480-purple" alt="Structured Concurrency"/>
  <img src="https://img.shields.io/badge/GraalVM-Native%20Image-orange" alt="GraalVM"/>
  <img src="https://img.shields.io/badge/Observability-OTel%20%2B%20Prometheus-yellow" alt="Observability"/>
</p>

---

## 🏗️ Architecture

```
+-----------------------------------------------------------------------------+
|                          Frontend (Angular 19)                                |
|   PrimeNG 19  ·  ECharts 6  ·  Signals  ·  Standalone  ·  i18n (pt/en/es)  |
+--------------------------------------+--------------------------------------+
                                       |
                              REST + WebSocket (STOMP)
                                       |
+--------------------------------------v--------------------------------------+
|                       API (Spring Boot 4 + Java 25)                          |
|                                                                              |
|   +--------+  +-----------+  +--------+  +-----------+  +-------+           |
|   | Budget |  |Measurement|  |Schedule|  |Procurement|  |Finance|           |
|   +---+----+  +-----+-----+  +---+----+  +-----+-----+  +---+---+          |
|       |              |            |             |             |               |
|   +---v--------------v------------v-------------v-------------v-----------+  |
|   |            Shared (Events · Observability · Security)                 |  |
|   +-----------------------------------------------------------------------+  |
+--------+--------------+--------------+---------------+-----------------------+
         |              |              |               |
   +-----v-----+  +----v------+  +----v------+  +----v-------+
   | PostgreSQL |  | Elastic   |  | Keycloak  |  | Prometheus |
   |     17     |  | Search 8  |  |   26.x    |  | + Grafana  |
   +------------+  +-----------+  +-----------+  +------------+
```

## ✨ Key Features

### Backend
| Feature | Technology |
|---------|-----------|
| Virtual Threads | Project Loom (Java 25) |
| Structured Concurrency | JEP 480 — parallel report generation |
| Cache with Metrics | Caffeine + Micrometer |
| Real-time Notifications | WebSocket + STOMP |
| Full-text Search | Elasticsearch 8 |
| PDF Reports | JTE + OpenHTMLtoPDF |
| Excel Export | Apache POI (streaming) |
| Circuit Breaker | Resilience4j |
| Multi-tenant | Row-level isolation |
| RBAC | Fine-grained permissions |
| API Documentation | SpringDoc OpenAPI 3 |
| Observability | Micrometer + Prometheus + OpenTelemetry |
| Architecture Tests | ArchUnit |
| Integration Tests | Testcontainers |
| Object Storage | AWS S3 (via SDK v2) |
| Native Image | GraalVM (profile `native`) |

### Frontend
| Feature | Technology |
|---------|-----------|
| UI Components | PrimeNG 19 |
| Charts | Apache ECharts 6 |
| State Management | Angular Signals |
| Standalone Components | No NgModules |
| Dark Mode | CSS variables + theme toggle |
| Responsive | PrimeFlex 4 |
| i18n | ngx-translate (pt-BR, en, es) |
| E2E Tests | Cypress 14 |

### Backend Modules
| Module | Description |
|--------|-------------|
| `budget` | Orçamentos e composições |
| `measurement` | Medições de obra |
| `schedule` | Cronograma (CPM, Curva S) |
| `procurement` | Suprimentos e cotações |
| `contract` | Contratos e aditivos |
| `finance` | Financeiro (contas a pagar/receber) |
| `invoice` | Faturas |
| `jobcosting` | Custo real vs. orçado |
| `equipment` | Equipamentos e frota |
| `safety` | Segurança do trabalho |
| `dailylog` | Diário de obra |
| `document` | Gestão de documentos |
| `rfi` | Request for Information |
| `punchlist` | Punch List |
| `submittal` | Submittals |
| `timetracking` | Apontamento de horas |
| `notification` | Notificações (WebSocket) |
| `commercial` | Comercial / Propostas |
| `aftersales` | Pós-venda / Garantias |
| `delivery` | Entrega de obra |
| `analytics` | Dashboards e indicadores |
| `sinapi` | Catálogo SINAPI + Elasticsearch |
| `weather` | Condições climáticas |
| `supplier` | Fornecedores |
| `project` | Projetos / Obras |
| `tenant` | Multi-tenancy |
| `security` | JWT + OAuth2 + RBAC |
| `inventory` | Estoque |
| `registry` | Cadastros gerais |
| `team` | Equipes |
| `forecast` | Previsões |
| `report` | Geração de relatórios |

### Frontend Pages
Dashboard • Projetos • Workspace (Resumo, Cronograma, Contratos, Gantt) • Orçamentos (Planilha, Curva ABC, BDI) • Medições • Diário de Obra • Suprimentos (Compras, Cotações, Estoque) • Financeiro (Contas, Faturas, Fluxo de Caixa) • Job Costing • Equipamentos • Segurança • RFI • Punch List • Submittals • Documentos • Apontamento • Comercial • SINAPI • Cadastros (Clientes, Fornecedores, Colaboradores, Tabelas) • Analytics • Pós-Venda • Entrega • Portal do Fornecedor • Relatórios • Ordens de Serviço • Notificações • Configurações • Perfil

## 🚀 Quick Start

### Prerequisites
- Java 25 (Temurin)
- Node.js 22+
- Docker & Docker Compose

### One Command Startup (Docker)
```bash
docker compose up --build
# Frontend:     http://localhost:4200
# API/Swagger:  http://localhost:8080/swagger-ui.html
# Login:        admin@sinapipro.dev / SinapiPro#2026
```

### Development (API only)
```bash
cd api
mvn spring-boot:run -s .mvn/settings.xml
# API: http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
# Actuator: http://localhost:8081/actuator
```

### Full Stack with Observability
```bash
docker compose -f compose.dev.yaml up
# Frontend:     http://localhost:4200
# API:          http://localhost:8080
# Swagger:      http://localhost:8080/swagger-ui.html
# Keycloak:     http://localhost:9080 (admin/admin)
# Grafana:      http://localhost:3000 (admin/admin)
# Prometheus:   http://localhost:9090
# Elasticsearch: http://localhost:9200
# SonarQube:    http://localhost:9000 (admin/admin)
# MailHog:      http://localhost:8025
```

### With Observability Stack (optional)
```bash
docker compose --profile observability up
# Adds: Prometheus (:9090), Grafana (:3000), OpenTelemetry Collector (:4318)
```

### Run SonarQube Analysis
```bash
cd api
mvn verify sonar:sonar -Dsonar.host.url=http://localhost:9000 -s .mvn/settings.xml
```

### Native Image (GraalVM)
```bash
cd api
mvn -Pnative native:compile -s .mvn/settings.xml
```

## 📊 Observability

| Tool | URL | Purpose |
|------|-----|---------|
| Prometheus | :9090 | Metrics collection |
| Grafana | :3000 | Dashboards & alerts |
| OpenTelemetry | :4318 | Distributed tracing |
| Actuator | :8081 | Health, metrics, caches |

### Exposed Metrics
- JVM (heap, GC, threads, virtual threads)
- HTTP (latency p50/p95/p99, throughput)
- Cache (hit ratio, evictions — Caffeine)
- Database (connection pool, query time — HikariCP)
- Business (measurements approved, budgets created)

## 🧪 Testing Strategy

```bash
# Frontend unit tests (Jest — fast, no browser needed)
cd web && npm test

# Frontend unit tests with coverage (CI mode)
cd web && npm run test:ci

# Backend unit + integration tests (requires Docker for Testcontainers)
cd api && mvn test -s .mvn/settings.xml

# Architecture boundary tests (ArchUnit — included in mvn test)

# E2E tests (Cypress — requires running app)
cd web && npx cypress run

# Accessibility tests (cypress-axe — included in E2E suite)
# Runs axe-core WCAG 2.1 AA checks on dashboard and budget pages

# Coverage report (backend)
cd api && mvn verify -s .mvn/settings.xml
# Report: api/target/site/jacoco/index.html
```

### Test Pyramid

| Layer | Tool | Count | Speed |
|-------|------|:-----:|:-----:|
| Unit (frontend) | Jest + jest-preset-angular | 38+ | ⚡ ~3s |
| Unit (backend) | JUnit 5 + Mockito | 46 | ⚡ ~8s |
| Integration (backend) | Testcontainers + @SpringBootTest | 12 | 🐢 ~30s |
| E2E | Cypress 14 + cypress-axe | 8 | 🐢 ~45s |
| Architecture | ArchUnit | 1 | ⚡ ~2s |

## 🏛️ Project Structure

```
sinapiPRO/
├── api/                          # Spring Boot 4 API (Java 25)
│   ├── pom.xml
│   ├── Dockerfile
│   ├── compose.yaml              # Dev PostgreSQL (Spring Boot Docker Compose)
│   ├── compose.showcase.yaml     # Showcase: API + PG + Prometheus + Grafana
│   ├── .mvn/settings.xml         # Maven Central (bypasses corporate Nexus)
│   └── src/
│       ├── main/java/.../api/
│       │   ├── budget/           # Vertical slice: api/application/domain
│       │   ├── measurement/
│       │   ├── schedule/
│       │   ├── procurement/
│       │   ├── finance/
│       │   ├── sinapi/           # SINAPI catalog + Elasticsearch
│       │   ├── security/         # JWT + OAuth2 + RBAC + multi-tenant
│       │   ├── report/           # PDF/Excel generation
│       │   ├── config/           # Cache, WebSocket, Security, OpenAPI
│       │   └── shared/           # Cross-cutting concerns
│       ├── main/resources/
│       │   ├── application.yaml
│       │   └── db/migration/     # Flyway (V1–V13)
│       └── test/                 # Testcontainers + ArchUnit
├── web/                          # Angular 19 frontend
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── src/app/pages/            # Feature pages (30+)
│   ├── src/app/core/             # Guards, interceptors, services
│   ├── src/app/shared/           # Shared components, pipes
│   ├── src/app/layout/           # Shell layout
│   ├── src/assets/i18n/          # Translations (pt-BR, en, es)
│   └── cypress/                  # E2E tests
├── helm/                         # Kubernetes deployment (Helm chart)
├── compose.yaml                  # Quick start (API + PG + Web)
├── compose.dev.yaml              # Full-stack (all services)
└── .github/workflows/            # CI/CD (GitHub Actions)
```

## 🔐 Security

- **Authentication**: JWT (self-issued) or OAuth2/Keycloak
- **Authorization**: RBAC with fine-grained permissions per module
- **Multi-tenant**: Row-level isolation via `tenant_id`
- **CORS**: Configurable per environment

## 📝 API Design

- RESTful with ProblemDetail (RFC 9457) error responses
- Pagination: `?page=0&size=20&sort=createdAt,desc`
- Filtering: `?status=ACTIVE&search=fundação`
- OpenAPI 3.1 spec auto-generated (SpringDoc)
- Virtual Threads enabled (`spring.threads.virtual.enabled=true`)

## 🗃️ Database

- **PostgreSQL 17** with Flyway migrations (V1–V13)
- UUID primary keys
- JSONB for flexible data
- `tsvector` full-text search indexes
- HikariCP connection pool (max 20)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Run tests (`cd api && mvn test -s .mvn/settings.xml`)
4. Commit (`git commit -m 'feat: add amazing feature'`)
5. Push (`git push origin feature/amazing-feature`)
6. Open a Pull Request

## 📄 License

This project is licensed under the MIT License — see [LICENSE](LICENSE) for details.

---

<p align="center">
  Built with ❤️ for the construction industry<br/>
  <strong>Java 25 • Spring Boot 4 • Angular 19 • PrimeNG 19 • PostgreSQL 17</strong>
</p>
