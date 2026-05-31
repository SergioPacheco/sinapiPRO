# SinapiPRO — Documentação

## Início Rápido

```bash
# Inicializar tudo com um comando:
docker compose -f compose.dev.yaml up --build

# Acessar:
# Frontend: http://localhost:4200 (admin@sinapipro.dev / SinapiPro#2026)
# Swagger:  http://localhost:8080/swagger-ui.html
```

---

## Índice

### 🏗️ Arquitetura
- [Visão Geral](./architecture/overview.md) — Monolito modular, vertical slicing
- [Banco de Dados](./architecture/database.md) — PostgreSQL 17, UUID PKs, JSONB, tsvector
- [Multi-tenant](./architecture/multi-tenant.md) — Hibernate Filter por tenant_id
- [Frontend SPA](./architecture/frontend-spa.md) — Angular 19, standalone components, signals
- [Fluxos da API](./architecture/api-flows.md) — Endpoints, autenticação, paginação

### 📐 Decisões Técnicas (ADRs)
- [ADR-0001](./decisions/adr/0001-monolith-modular-over-microservices.md) — Monolito modular > microservices
- [ADR-0002](./decisions/adr/0002-uuid-as-primary-key.md) — UUID como PK
- [ADR-0003](./decisions/adr/0003-hibernate-filter-multi-tenancy.md) — Multi-tenancy via Hibernate Filter
- [ADR-0004](./decisions/adr/0004-hmac-jwt-over-rsa.md) — HMAC JWT (simplicidade)
- [ADR-0005](./decisions/adr/0005-virtual-threads-over-reactive.md) — Virtual Threads > WebFlux
- [Tech Radar](./decisions/tech-radar.md) — Tecnologias adotadas/avaliadas/descartadas

### 📋 Domínio & Negócio
- [Fluxos de Negócio](./domain/business-flows.md) — Lead → Orçamento → Obra → Medição → Entrega
- [Regras de Orçamento](./domain/orcamento-rules.md) — BDI, composições, curva ABC
- [Glossário](./domain/glossary.md) — Termos do domínio construção civil
- [Benchmark OrcaFascio](./domain/orcafascio-benchmark.md) — Comparação com concorrentes

### 📦 Produto
- [PRD](./product/PRD.md) — Product Requirements Document
- [Roadmap](./product/roadmap.md) — Fases e entregas planejadas
- [Backlog de Módulos](./product/modules-backlog.md) — Status de cada módulo

### 🚀 Operações
- [Deploy](./operations/deployment.md) — Docker, Kubernetes, Helm
- [SLOs](./operations/slos.md) — Objetivos de nível de serviço
- [Incident Response](./operations/runbooks/incident-response.md) — Runbook de incidentes

### 📚 Referência
- [SINAPI](./reference/sinapi/README.md) — Catálogo de composições e insumos

---

## Estrutura do Repositório

```
sinapiPRO/
├── api/                    ← Java 25 + Spring Boot 4 (30+ módulos)
├── web/                    ← Angular 19 + PrimeNG (75+ componentes)
├── helm/sinapipro/         ← Helm chart (K8s deploy)
├── docs/                   ← Esta documentação
├── .kiro/                  ← AI steering & specs
├── compose.dev.yaml        ← Docker Compose (dev)
└── README.md               ← Quick start
```

## Convenções

- **Backend**: vertical slicing (`{module}/api/`, `{module}/application/`, `{module}/domain/`)
- **Frontend**: standalone components, signals, lazy routes, PrimeNG + ECharts
- **Auth**: JWT + RBAC com `@PreAuthorize("@perm.check('modulo.acao')")`
- **DB**: Flyway migrations, UUID PKs, `numeric(18,2)` para valores monetários
- **API**: REST, ProblemDetail (RFC 9457), paginação via `PageResponse<T>`
