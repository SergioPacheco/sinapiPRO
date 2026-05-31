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
┌─────────────────────────────────────────────────────────────────┐
│                        Frontend (Angular 19)                      │
│  PrimeNG • ECharts • Signals • Standalone Components • PWA       │
└──────────────────────────────┬──────────────────────────────────┘
                               │ REST + WebSocket (STOMP)
┌──────────────────────────────▼──────────────────────────────────┐
│                    API (Spring Boot 4 + Java 25)                  │
│                                                                   │
│  ┌─────────┐ ┌──────────┐ ┌─────────┐ ┌──────────┐ ┌────────┐ │
│  │ Budget  │ │Measurement│ │Schedule │ │Procurement│ │Finance │ │
│  └────┬────┘ └─────┬────┘ └────┬────┘ └─────┬────┘ └───┬────┘ │
│       │             │           │             │           │       │
│  ┌────▼─────────────▼───────────▼─────────────▼───────────▼────┐ │
│  │              Shared (Events • Observability • Security)       │ │
│  └──────────────────────────────────────────────────────────────┘ │
└───────┬──────────────┬──────────────┬──────────────┬────────────┘
        │              │              │              │
   ┌────▼────┐   ┌────▼────┐   ┌────▼────┐   ┌────▼────┐
   │PostgreSQL│   │Elastic  │   │Keycloak │   │Prometheus│
   │  17.5   │   │Search 8 │   │  26.x   │   │+ Grafana │
   └─────────┘   └─────────┘   └─────────┘   └──────────┘
```

## ✨ Key Features

### Backend Showcase
| Feature | Technology |
|---------|-----------|
| Virtual Threads | Project Loom (Java 25) |
| Structured Concurrency | JEP 480 — parallel report generation |
| Cache with Metrics | Caffeine + Micrometer |
| Real-time Notifications | WebSocket + STOMP |
| Full-text Search | Elasticsearch 8 |
| PDF Reports | JTE + OpenHTMLtoPDF + Playwright |
| Excel Export | FastExcel (streaming) |
| Circuit Breaker | Resilience4j |
| Multi-tenant | Row-level isolation |
| RBAC | Fine-grained permissions |
| API Documentation | SpringDoc OpenAPI 3 |
| Observability | Micrometer + Prometheus + OpenTelemetry |
| Architecture Tests | ArchUnit |
| Integration Tests | Testcontainers |
| Docker Build | Jib (no Dockerfile needed) |
| Native Image | GraalVM (profile `native`) |

### Frontend Showcase
| Feature | Technology |
|---------|-----------|
| UI Components | PrimeNG 19 |
| Charts | Apache ECharts 6 |
| State Management | Angular Signals |
| Standalone Components | No NgModules |
| Dark Mode | CSS variables + theme toggle |
| Responsive | PrimeFlex grid |
| E2E Tests | Cypress |

### Domain Modules (35+)
Orçamentos • Medições • Cronograma • Diário de Obra • Suprimentos • Estoque • Financeiro • Job Costing • Equipamentos • Segurança • RFI • Punch List • Submittals • Documentos • Apontamento • Comercial • Pós-Venda • Analytics • Notificações • SINAPI

## 🚀 Quick Start

### Prerequisites
- Java 25 (Temurin)
- Node.js 22+
- Docker & Docker Compose

### Development (API only)
```bash
cd api
mvn spring-boot:run -s .mvn/settings.xml
# API: http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
# Actuator: http://localhost:8081/actuator
```

### Full Stack (Docker Compose)
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

### Build Docker Image (Jib — no Docker daemon needed)
```bash
cd api
mvn package -Pprod jib:dockerBuild -s .mvn/settings.xml
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
- JVM (heap, GC, threads)
- HTTP (latency p50/p95/p99, throughput)
- Cache (hit ratio, evictions)
- Database (connection pool, query time)
- Business (measurements approved, budgets created)

## 🧪 Testing Strategy

```bash
# Unit + Integration tests (requires Docker for Testcontainers)
cd api && mvn test -s .mvn/settings.xml

# Architecture boundary tests (ArchUnit)
# Automatically included in mvn test

# E2E tests (Cypress)
cd web && npx cypress run

# Coverage report
cd api && mvn verify -s .mvn/settings.xml
# Report: api/target/site/jacoco/index.html
```

## 🏛️ Project Structure

```
sinapiPRO/
├── api/                          # Spring Boot 4 API
│   ├── src/main/java/.../
│   │   ├── budget/               # Vertical slice: api/application/domain
│   │   ├── measurement/
│   │   ├── schedule/
│   │   ├── procurement/
│   │   ├── finance/
│   │   ├── sinapi/               # SINAPI catalog + Elasticsearch
│   │   ├── security/             # JWT + RBAC + multi-tenant
│   │   ├── report/               # PDF/Excel generation
│   │   ├── config/               # Cache, WebSocket, Security, OpenAPI
│   │   └── shared/               # Cross-cutting concerns
│   └── src/test/                 # Testcontainers + ArchUnit
├── web/                          # Angular 19 frontend
│   ├── src/app/pages/            # Feature modules
│   ├── src/app/core/             # Guards, interceptors, services
│   └── cypress/                  # E2E tests
├── helm/                         # Kubernetes deployment
├── compose.dev.yaml              # Full-stack Docker Compose
└── .github/workflows/            # CI/CD (GitHub Actions)
```

## 🔐 Security

- **Authentication**: JWT (self-issued) or OAuth2/Keycloak
- **Authorization**: RBAC with fine-grained permissions per module
- **Multi-tenant**: Row-level isolation via tenant_id
- **Rate Limiting**: In-memory (single node) or Redis (cluster)
- **CORS**: Configurable per environment

## 📝 API Design

- RESTful with ProblemDetail (RFC 9457) error responses
- Pagination: `?page=0&size=20&sort=createdAt,desc`
- Filtering: `?status=ACTIVE&search=fundação`
- HATEOAS-ready (Spring HATEOAS available)
- OpenAPI 3.1 spec auto-generated

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Run tests (`mvn test`)
4. Commit (`git commit -m 'feat: add amazing feature'`)
5. Push (`git push origin feature/amazing-feature`)
6. Open a Pull Request

## 📄 License

This project is licensed under the MIT License — see [LICENSE](LICENSE) for details.

---

<p align="center">
  Built with ❤️ for the construction industry<br/>
  <strong>Java 25 • Spring Boot 4 • Angular 19 • PostgreSQL 17</strong>
</p>
