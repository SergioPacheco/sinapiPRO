# SinapiPRO

> Sistema de Gestão de Obras e Orçamentos baseado na tabela SINAPI

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/projects/jdk/25/)
[![Angular](https://img.shields.io/badge/Angular-20-red.svg)](https://angular.dev/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue.svg)](https://www.postgresql.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

SinapiPRO é um ERP open source para gestão de obras e orçamentos da construção civil, utilizando a tabela SINAPI como base de referência de preços.

---

## 🚀 Quick Start

```bash
# Stack completa em Docker
docker compose up --build
# App: http://localhost:4200
# API: http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
# Login: admin@sinapipro.dev / SinapiPro#2026
```

O Docker Compose sobe PostgreSQL, API, frontend, Prometheus, Grafana e OTel. Flyway executa as migrations no startup da API.

---

## 📁 Estrutura do Repositório

```
sinapiPRO/
├── api/                ← Backend REST API (Java 25, Spring Boot 4, PostgreSQL 17)
│   ├── src/main/java/  ← 26 módulos de domínio (vertical slicing)
│   ├── src/main/resources/db/migration/  ← 14 migrations Flyway
│   ├── compose.yaml    ← PostgreSQL para dev
│   └── pom.xml
├── web/                ← Frontend SPA (Angular 20, Material, ng-matero)
│   ├── src/app/routes/ ← 13 módulos de negócio (lazy-loaded)
│   ├── proxy.conf.json ← Proxy /api/v1 → localhost:8080
│   └── package.json
├── docs/               ← Documentação com Mermaid
│   ├── architecture.md ← Arquitetura C4 + componentes
│   ├── database.md     ← Modelo ER (PostgreSQL)
│   ├── api-flows.md    ← Diagramas de sequência
│   ├── domain.md       ← Regras de negócio + glossário
│   ├── deployment.md   ← Deploy + observabilidade
│   └── frontend-plan.md ← Arquitetura Angular
├── README.md
├── CONTRIBUTING.md
├── SECURITY.md
└── LICENSE
```

---

## ✨ Funcionalidades

| Módulo | Backend | Frontend |
|--------|:-------:|:--------:|
| Orçamentos (BDI, Curva ABC, Reajuste) | ✅ | ✅ CRUD |
| Catálogo SINAPI (composições + insumos) | ✅ | ✅ Busca full-text |
| Medições (workflow DRAFT→APPROVED→PAID) | ✅ | ✅ Workflow |
| Contratos + Aditivos | ✅ | ✅ CRUD |
| Suprimentos (cotação → pedido → recebimento) | ✅ | ✅ Sub-rotas |
| Cronograma (CPM + Curva S) | ✅ | ✅ Lista + Form |
| Diário de Obra | ✅ | ✅ CRUD |
| Equipamentos | ✅ | ✅ CRUD |
| Job Costing / EVM | ✅ | ✅ KPIs |
| Analytics (PV, EV, AC, CPI, SPI) | ✅ | ✅ Dashboard |
| Fornecedores | ✅ | ✅ CRUD |
| Segurança do Trabalho | ✅ | ✅ CRUD |
| Notificações (SSE real-time) | ✅ | — |
| RFI, Punch List, Submittals | ✅ | — |

---

## ⚡ Stack Tecnológica

### Backend

| Camada | Tecnologia |
|--------|-----------|
| Linguagem | Java 25 (Virtual Threads, Structured Concurrency, Sealed Classes, Gatherers) |
| Framework | Spring Boot 4.0.5, Spring Security 7 (JWT/OAuth2) |
| Banco | PostgreSQL 17 (UUID PKs, JSONB, tsvector) |
| Migrations | Flyway (V1–V14) |
| Observabilidade | Micrometer + Prometheus + OpenTelemetry |
| API | REST + OpenAPI 3 (Swagger UI) + ProblemDetail (RFC 9457) |

### Frontend

| Camada | Tecnologia |
|--------|-----------|
| Framework | Angular 20 (standalone, signals, zoneless) |
| UI | Angular Material + ng-matero extensions (mtx-grid, mtx-dialog) |
| State | Signals + Services |
| Charts | ApexCharts |
| i18n | ngx-translate (pt-BR) |
| Auth | JWT (interceptor + guard) |

---

## 🔐 Autenticação

```bash
# Obter token JWT
curl -X POST http://localhost:8080/api/v1/auth/token \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@sinapipro.dev","password":"SinapiPro#2026","grantType":"PASSWORD"}'
```

| Scope/Role | Acesso |
|---|---|
| `SCOPE_sinapipro.read` | Leitura em todos os endpoints |
| `SCOPE_sinapipro.write` | Criação, atualização, exclusão |
| `ROLE_ADMIN` | Actuator endpoints |

---

## 📊 Observabilidade

```bash
# Stack completa (Web + API + PG + Prometheus + Grafana + OTel)
docker compose up --build
```

| Serviço | URL |
|---------|-----|
| API | http://localhost:8080 |
| Swagger | http://localhost:8080/swagger-ui.html |
| Frontend | http://localhost:4200 |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3000 |

---

## 🧪 Testes

```bash
# Backend (requer Docker para Testcontainers)
cd api && mvn test -s .mvn/settings.xml

# Frontend
cd web && npx ng test
```

---

## 🤝 Como Contribuir

Veja [CONTRIBUTING.md](CONTRIBUTING.md) para detalhes.

```bash
git checkout -b feat/minha-funcionalidade
# Trabalhe em api/ ou web/
cd api && mvn compile -s .mvn/settings.xml   # verificar backend
cd web && npx ng build                        # verificar frontend
git commit -m "feat(modulo): descrição"
git push origin feat/minha-funcionalidade
```

---

## 📄 Licença

[MIT](LICENSE) — Sergio Pacheco

---

*SinapiPRO — Gestão inteligente de obras para a construção civil brasileira* 🏗️
