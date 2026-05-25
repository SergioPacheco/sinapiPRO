# 📚 Documentação — SinapiPRO

> ERP open source para gestão completa de obras | Java 25 + Angular 19 + PostgreSQL 17

## Estrutura

```
docs/
├── product/        ← O QUE o sistema faz (PRD, roadmap, backlog)
├── domain/         ← Regras de negócio e glossário
├── architecture/   ← COMO funciona (C4, banco, API, frontend)
├── operations/     ← Deploy, SLOs, runbooks
├── decisions/      ← ADRs, tech radar, log de decisões
├── reference/      ← Dados externos (planilhas SINAPI)
└── _archive/       ← Docs obsoletos (referência histórica)
```

---

## 📦 Product

| Documento | Descrição |
|-----------|-----------|
| [PRD.md](product/PRD.md) | Product Requirements Document — visão, personas, escopo |
| [roadmap.md](product/roadmap.md) | Sprints e tasks com status de implementação |
| [modules-backlog.md](product/modules-backlog.md) | Detalhamento dos módulos a implementar |

## 🏗️ Domain

| Documento | Descrição |
|-----------|-----------|
| [glossary.md](domain/glossary.md) | Glossário e regras de negócio do domínio |
| [business-flows.md](domain/business-flows.md) | Fluxos de processo ponta-a-ponta |
| [orcamento-rules.md](domain/orcamento-rules.md) | Lógicas de negócio do orçamento (BDI, bases, cálculos) |
| [orcafascio-benchmark.md](domain/orcafascio-benchmark.md) | Análise do OrçaFascio como referência funcional |

## 🔧 Architecture

| Documento | Descrição |
|-----------|-----------|
| [overview.md](architecture/overview.md) | Arquitetura C4, componentes, visão geral |
| [database.md](architecture/database.md) | Modelo de dados PostgreSQL (ER com Mermaid) |
| [api-flows.md](architecture/api-flows.md) | Diagramas de sequência dos fluxos principais |
| [frontend-plan.md](architecture/frontend-plan.md) | Arquitetura Angular (Feature-Shell + Clean) |
| [frontend-spa.md](architecture/frontend-spa.md) | Filosofia SPA — 12 páginas, fluxo inline |
| [multi-tenant.md](architecture/multi-tenant.md) | Avaliação multi-tenancy (Hibernate Filter) |
| [strato-migration.md](architecture/strato-migration.md) | Gap analysis Strato → SinapiPRO |

## ⚙️ Operations

| Documento | Descrição |
|-----------|-----------|
| [deployment.md](operations/deployment.md) | Deploy, Docker, observabilidade |
| [slos.md](operations/slos.md) | Service Level Objectives e error budget |
| [runbooks/incident-response.md](operations/runbooks/incident-response.md) | Playbook de resposta a incidentes |

## 📐 Decisions

| Documento | Descrição |
|-----------|-----------|
| [tech-radar.md](decisions/tech-radar.md) | Tecnologias: Adopt / Trial / Assess / Hold |
| [decisions-log.md](decisions/decisions-log.md) | Log de decisões técnicas |
| [adr/0001](decisions/adr/0001-monolith-modular-over-microservices.md) | Monolito modular > microservices |
| [adr/0002](decisions/adr/0002-uuid-as-primary-key.md) | UUID como primary key |
| [adr/0003](decisions/adr/0003-hibernate-filter-multi-tenancy.md) | Hibernate Filter para multi-tenancy |
| [adr/0004](decisions/adr/0004-hmac-jwt-over-rsa.md) | HMAC JWT > RSA |
| [adr/0005](decisions/adr/0005-virtual-threads-over-reactive.md) | Virtual Threads > Reactive |

## 📊 Reference

| Documento | Descrição |
|-----------|-----------|
| [sinapi/README.md](reference/sinapi/README.md) | Planilhas SINAPI (Dez/2024, SP, Não Desonerado) |

---

## Quick Start

```bash
# Backend
cd api && mvn spring-boot:run -s .mvn/settings.xml
# http://localhost:8080/swagger-ui.html

# Frontend
cd web && npx ng serve
# http://localhost:4200

# Full stack (Docker)
docker compose up --build
```
