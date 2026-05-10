# SinapiPRO

> Sistema de Gestão de Obras e Orçamentos baseado na tabela SINAPI

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/projects/jdk/25/)
[![Angular](https://img.shields.io/badge/Angular-20-red.svg)](https://angular.dev/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue.svg)](https://www.postgresql.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

SinapiPRO é um ERP open source para gestão completa de obras da construção civil, cobrindo o ciclo inteiro: captação → orçamento → contrato → planejamento → execução → medições → suprimentos → financeiro → segurança → documentos → entrega → pós-obra.

---

## 🚀 Quick Start

```bash
docker compose up --build
# App: http://localhost:4200
# API: http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
# Login: admin@sinapipro.dev / SinapiPro#2026
```

---

## 📁 Estrutura

```
sinapiPRO/
├── api/                ← Backend (Java 25, Spring Boot 4, PostgreSQL 17)
│   ├── src/main/java/  ← 34 controllers, ~225 endpoints
│   └── src/main/resources/db/migration/  ← V1 consolidada
├── web/                ← Frontend (Angular 20, Material, ng-matero)
│   └── src/app/routes/ ← 15 módulos lazy-loaded, 60+ componentes
├── docs/               ← Documentação Mermaid
└── .kiro/docs/         ← Specs, fluxos de negócio, arquitetura SPA
```

---

## ✨ Funcionalidades

| Módulo | Backend | Frontend | Acesso |
|--------|:-------:|:--------:|--------|
| Obras (workspace completo) | ✅ | ✅ | Menu global |
| Orçamentos (BDI, Curva ABC, PDF) | ✅ | ✅ | Aba da obra |
| Catálogo SINAPI (composições + insumos) | ✅ | ✅ | Cadastros |
| Medições (workflow DRAFT→PAID) | ✅ | ✅ | Aba da obra |
| Contratos + Aditivos | ✅ | ✅ | Aba da obra |
| Cronograma (CPM + Curva S + baselines) | ✅ | ✅ | Aba da obra |
| Diário de Obra + Atrasos Climáticos | ✅ | ✅ | Aba da obra |
| Suprimentos (cotação → pedido → recebimento) | ✅ | ✅ | Menu global + aba |
| Estoque/Almoxarifado | ✅ | ✅ | Aba da obra |
| Financeiro (pagar + receber + fluxo + NFs) | ✅ | ✅ | Menu global + aba |
| Job Costing / EVM | ✅ | ✅ | Aba da obra |
| Equipamentos (uso + manutenção + abastecimento) | ✅ | ✅ | Cadastros |
| Segurança do Trabalho | ✅ | ✅ | Menu global + aba |
| RFI | ✅ | ✅ | Aba da obra |
| Punch List | ✅ | ✅ | Aba da obra |
| Submittals | ✅ | ✅ | Aba da obra |
| Documentos (upload + versionamento) | ✅ | ✅ | Aba da obra |
| Apontamento de Horas | ✅ | ✅ | Aba da obra |
| Comercial (empreendimentos + propostas) | ✅ | ✅ | Menu global |
| Pós-Venda (tickets + SLA) | ✅ | ✅ | Menu global |
| Notificações (SSE real-time) | ✅ | ✅ | Topbar (sino) |
| Analytics / Relatórios | ✅ | ✅ | Menu global |
| Permissões / RBAC | ✅ | 🔲 | Configurações |
| Propostas Comerciais | ✅ | 🔲 | Comercial |
| Equipes | ✅ | 🔲 | Cadastros |

---

## 🏗️ Arquitetura de Navegação

```
Menu Global (9 itens)          Workspace da Obra (10 abas)
─────────────────────          ─────────────────────────────
Dashboard                      Resumo
Obras                    →     Orçamentos
Comercial                      Contratos
Suprimentos                    Cronograma
Financeiro                     Medições
Segurança do Trabalho          Execução (Diário + Apontamento)
Relatórios                     Suprimentos
Cadastros                      Financeiro (Custeio + Pagar + Receber + NFs)
Configurações                  Segurança
                               Documentos (+ RFI + Punch List + Submittals)
```

---

## ⚡ Stack

| Camada | Tecnologia |
|--------|-----------|
| Backend | Java 25, Spring Boot 4, Spring Security 7 (JWT), Hibernate 7, Flyway |
| Banco | PostgreSQL 17 (UUID PKs, JSONB, tsvector) |
| Frontend | Angular 20 (standalone, signals, zoneless), Material + ng-matero |
| Observabilidade | Micrometer + Prometheus + OpenTelemetry |
| Tema | Dark mode premium (graphite/navy) |

---

## 🔐 Autenticação

```bash
curl -X POST http://localhost:8080/api/v1/auth/token \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@sinapipro.dev","password":"SinapiPro#2026","grantType":"PASSWORD"}'
```

---

## 🧪 Testes

```bash
cd api && mvn test -s .mvn/settings.xml    # Backend (Testcontainers)
cd web && npx ng test                       # Frontend
```

---

## 📊 Observabilidade

| Serviço | URL |
|---------|-----|
| API | http://localhost:8080 |
| Swagger | http://localhost:8080/swagger-ui.html |
| Frontend | http://localhost:4200 |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3000 |

---

## 🤝 Como Contribuir

```bash
git checkout -b feat/minha-funcionalidade
cd api && mvn compile -s .mvn/settings.xml
cd web && npx ng build
git commit -m "feat(modulo): descrição"
```

Veja [CONTRIBUTING.md](CONTRIBUTING.md) para detalhes.

---

## 📄 Licença

[MIT](LICENSE) — Sergio Pacheco

---

*SinapiPRO — Gestão inteligente de obras para a construção civil brasileira* 🏗️
