# SinapiPRO

> Sistema de Gestão de Obras e Orçamentos baseado na tabela SINAPI

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.org/projects/jdk/25/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue.svg)](https://www.postgresql.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

SinapiPRO é um sistema ERP open source para gestão de obras e orçamentos da construção civil, utilizando a tabela SINAPI (Sistema Nacional de Pesquisa de Custos e Índices da Construção Civil) como base de referência de preços.

---

## ✨ Funcionalidades

### Orçamento
- Criação de orçamentos com insumos e composições SINAPI
- Tipos: Estimativa → Venda → Execução (fluxo completo)
- BDI detalhado (insumo, serviço, terceiro, ferramenta)
- Leis Sociais, Taxa de Administração
- Reajuste de preços em lote (percentual, valor, SINAPI)
- Baseline do orçamento (snapshot + comparativo)
- Planejamento Físico-Financeiro com Curva S
- **Job Costing (EVM)**: PV, EV, AC, CPI, SPI, EAC, VAC

### Suprimentos
- Cotações com análise comparativa de preços
- Geração automática de pedidos pelo menor preço
- Pedidos de Compra com recebimento parcial/total
- Estoque com **Custo Médio Ponderado** (NBC TG 16)
- Requisições de Insumos

### Medições & Contratos
- Medições com workflow: DRAFT → SUBMITTED → APPROVED → PAID
- Contratos com aditivos (change orders) e retenção configurável
- Progress Billing automático (medição aprovada → fatura)
- Acumulado, saldo e percentual medido

### Operacional
- Diário de Obra (MO, Equipamentos, Ocorrências, Serviços)
- Cronograma com Caminho Crítico (CPM) e Curva S
- Equipamentos com controle de utilização e manutenção
- Segurança do Trabalho (inspeções, incidentes, checklists)
- RFI (Request for Information) e Punch List
- Submittals e Documentos
- Timesheet com produtividade de mão de obra
- Previsão de atrasos por clima (Weather Delays)

### Analytics & Relatórios
- **EVM** (Earned Value Management): PV, EV, AC, CPI, SPI, EAC, VAC
- Fluxo de Caixa projetado
- Portfolio Analytics (multi-projeto)
- Notificações em tempo real via SSE

---

## 🚀 Como Executar

### Pré-requisitos
- Java 25 (Temurin)
- Docker (para PostgreSQL via Docker Compose)
- Maven 3.9+ (ou SDKMAN: `sdk install maven`)

### Início Rápido

```bash
# 1. Clone o repositório
git clone https://github.com/SergioPacheco/sinapiPRO.git
cd sinapiPRO/api

# 2. Inicia o sistema (PostgreSQL sobe automaticamente via Docker Compose)
mvn spring-boot:run -s .mvn/settings.xml
```

O Spring Boot Docker Compose inicia o PostgreSQL automaticamente. Flyway executa as migrations no startup.

### Acesso

```
API:     http://localhost:8080
Swagger: http://localhost:8080/swagger-ui.html
Health:  http://localhost:8080/actuator/health
```

**Autenticação (JWT):**
```bash
# Obter token
curl -X POST http://localhost:8080/api/v1/auth/token \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@sinapipro.com","password":"admin123","grantType":"PASSWORD"}'
```

### Stack Completa (Observabilidade)

```bash
docker compose -f compose.showcase.yaml up -d
```

Inclui: App + PostgreSQL + Prometheus + Grafana + OpenTelemetry Collector

### Comandos Maven

```bash
cd api
mvn compile -s .mvn/settings.xml          # compilar
mvn test -s .mvn/settings.xml             # testes (requer Docker/Testcontainers)
mvn spring-boot:run -s .mvn/settings.xml  # executar
mvn package -s .mvn/settings.xml          # gerar JAR
mvn package -Pnative                      # GraalVM Native Image
```

---

## 🗄️ Banco de Dados

O sistema usa **PostgreSQL 17** com **Flyway** para migrations automáticas.

| Migration | Descrição |
|---|---|
| V1 | Schema core (budget, supplier, invoice, security) |
| V2 | Dados de demonstração (seed) |
| V3 | Composições SINAPI (catálogo) |
| V4 | Dados SINAPI (insumos, preços, composições) |
| V5 | Orçamento detalhado (stages, items, BDI) |
| V6 | Job Costing (cost codes, transactions) |
| V7 | Cronograma (activities, dependencies) |
| V8 | Medições (measurements, items) |
| V9 | Contratos (contracts, change orders) |
| V10 | Suprimentos (purchase requests, quotations, orders) |
| V11 | Diário de Obra |
| V12 | Equipamentos |
| V13 | Documentos, RFI, Punch List, Segurança |
| V14 | Submittals, Weather, Timesheet, Notificações |

**Features PostgreSQL utilizadas:**
- UUID como primary key (distributed-friendly)
- JSONB para metadata flexível
- `tsvector` para full-text search
- Índices parciais e compostos

---

## 🏗️ Arquitetura

```
api/src/main/java/com/sinapipro/api/
├── config/              ← Security (JWT/OAuth2), OpenAPI, Rate Limiting
├── shared/              ← Cross-cutting concerns
│   ├── domain/          ← AuditableEntity (UUID + timestamps)
│   ├── error/           ← ProblemDetail (RFC 9457) exception handler
│   ├── events/          ← SSE event publisher (Reactor)
│   ├── observability/   ← Micrometer Observations + metrics
│   └── api/             ← PageResponse, EventStreamController
├── budget/              ← Orçamentos (CRUD + BDI + Curva ABC + Reajuste)
├── supplier/            ← Fornecedores
├── invoice/             ← Faturas/Notas Fiscais
├── sinapi/              ← Catálogo SINAPI (composições + insumos + preços)
├── measurement/         ← Medições de Obra
├── contract/            ← Contratos e Aditivos
├── procurement/         ← Suprimentos (cotação → pedido → recebimento)
├── jobcosting/          ← Job Costing & EVM
├── schedule/            ← Cronograma (CPM + Curva S)
├── dailylog/            ← Diário de Obra
├── equipment/           ← Equipamentos e Utilização
├── safety/              ← Segurança do Trabalho
├── rfi/                 ← Request for Information
├── punchlist/           ← Punch List
├── document/            ← Gestão de Documentos
├── submittal/           ← Submittals
├── weather/             ← Weather Delays
├── timetracking/        ← Timesheet e Produtividade
├── notification/        ← Notificações (SSE)
├── analytics/           ← EVM, Cash Flow, Portfolio
├── forecast/            ← Previsão de Atrasos
└── security/            ← JWT Token Service
```

### Padrão por Módulo (Vertical Slicing)
```
{module}/
├── api/            ← @RestController + request/response records
├── application/    ← @Service + business logic
└── domain/         ← @Entity + Repository interface
```

---

## ⚡ Stack Tecnológica

| Camada | Tecnologia |
|--------|-----------|
| **Linguagem** | Java 25 (Virtual Threads, Structured Concurrency, String Templates, Gatherers, Sealed Classes, Pattern Matching) |
| **Framework** | Spring Boot 4.0.5, Spring Framework 7, Spring Security 7 |
| **API** | REST + OpenAPI 3 (SpringDoc), ProblemDetail (RFC 9457) |
| **Persistência** | Spring Data JPA, Hibernate 7, Flyway |
| **Banco** | PostgreSQL 17 (UUID PKs, JSONB, tsvector) |
| **Segurança** | OAuth2 Resource Server, JWT (HMAC-SHA256), stateless |
| **Observabilidade** | Micrometer + Prometheus + OpenTelemetry + Grafana |
| **Eventos** | Server-Sent Events (Reactor Flux) |
| **Testes** | JUnit 5, Testcontainers, Spring Security Test |
| **Build** | Maven 3.9+, JaCoCo (coverage), GraalVM Native Image |
| **Deploy** | Docker, Docker Compose |

### Features Java 25 Utilizadas

| Feature | JEP | Uso no Projeto |
|---------|-----|----------------|
| Virtual Threads | 444 | `spring.threads.virtual.enabled=true` — todas as requests |
| Structured Concurrency | 480 | Operações paralelas em services (cotações, analytics) |
| String Templates | 465 | Mensagens de log, construção de strings |
| Gatherers (Stream API) | 485 | Operações de stream customizadas (sliding windows, folding) |
| Sealed Classes | 409 | Hierarquias de exceção, domain events |
| Pattern Matching (switch) | 441 | Exception handler, validações |
| Pattern Matching (instanceof) | 394 | Type checks sem cast explícito |
| Records | 395 | DTOs, value objects, resultados de cálculo |
| Sequenced Collections | 431 | `.getFirst()`, `.getLast()` |
| Unnamed Variables | 456 | Lambdas com `_` para params não usados |
| Scoped Values | 487 | Contexto de request sem ThreadLocal |
| Flexible Constructor Bodies | 492 | Validação antes de `this()`/`super()` |
| Module Imports | 476 | `import module java.base` |

---

## 🔐 Segurança

API stateless com JWT (OAuth2 Resource Server):

| Scope/Role | Acesso |
|---|---|
| `SCOPE_sinapipro.read` | Leitura em todos os endpoints |
| `SCOPE_sinapipro.write` | Criação, atualização, exclusão |
| `ROLE_ADMIN` | Actuator endpoints |

---

## 🧪 Testes

```bash
cd api
mvn test -s .mvn/settings.xml
```

- **Testcontainers** — PostgreSQL real nos testes de integração
- **Spring Security Test** — testes com JWT mockado
- **JaCoCo** — relatório de cobertura em `target/site/jacoco/`

---

## 📊 Observabilidade

| Ferramenta | Endpoint/Porta |
|---|---|
| Prometheus metrics | `/actuator/prometheus` |
| Health check | `/actuator/health` |
| Grafana dashboards | `http://localhost:3000` (via compose.showcase.yaml) |
| OpenTelemetry traces | Exportados via OTLP |
| SSE events | `/api/v1/events/stream` |

---

## 🤝 Como Contribuir

1. **Fork** o repositório
2. Crie uma branch: `git checkout -b feat/minha-funcionalidade`
3. Trabalhe no diretório `api/` (módulo ativo)
4. Execute os testes: `cd api && mvn test -s .mvn/settings.xml`
5. Commit: `git commit -m "feat(modulo): descrição da mudança"`
6. Push: `git push origin feat/minha-funcionalidade`
7. Abra um **Pull Request**

### Padrões de commit

```
feat(modulo): nova funcionalidade
fix(modulo): correção de bug
refactor(modulo): refatoração sem mudança de comportamento
test(modulo): adição/correção de testes
docs: atualização de documentação
```

---

## 📄 Licença

Este projeto está licenciado sob a **MIT License** — veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## 👥 Autores

- **Sergio Pacheco** — *Desenvolvimento* — [@SergioPacheco](https://github.com/SergioPacheco)

---

## 🙏 Agradecimentos

- [SINAPI/CEF](https://www.caixa.gov.br/poder-publico/modernizacao-gestao/sinapi) — Base de dados de custos da construção civil
- [Spring Boot](https://spring.io/projects/spring-boot) — Framework principal
- [OpenJDK](https://openjdk.org/) — Java 25 com features modernas
- [PostgreSQL](https://www.postgresql.org/) — Banco de dados
- Comunidade da construção civil brasileira

---

*SinapiPRO — Gestão inteligente de obras para a construção civil brasileira* 🏗️
