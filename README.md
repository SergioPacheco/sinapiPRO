# SinapiPRO

ERP open source para gestão completa de obras da construção civil.

## Quick Start — Um comando

```bash
docker compose -f compose.dev.yaml up --build
```

Isso inicializa **tudo**: PostgreSQL + API (Java 25) + Frontend (Angular 19).

| Serviço | URL | Credenciais |
|---------|-----|-------------|
| Frontend | http://localhost:4200 | admin@sinapipro.dev / SinapiPro#2026 |
| API (Swagger) | http://localhost:8080/swagger-ui.html | — |
| PostgreSQL | localhost:5432 | sinapipro / sinapipro |

## Stack

| Camada | Tecnologia |
|--------|-----------|
| Backend | Java 25 + Spring Boot 4.0 + PostgreSQL 17.5 |
| Frontend | Angular 19 + PrimeNG 19 + ECharts |
| Auth | JWT (HMAC-SHA256) + RBAC granular (30+ permissões) |
| Infra | Docker Compose / Helm (K8s) |
| Observabilidade | Micrometer + Prometheus + Grafana + OpenTelemetry |

## Desenvolvimento Local

### Backend (API)
```bash
cd api
mvn compile -s .mvn/settings.xml        # compilar
mvn test -s .mvn/settings.xml           # testes (requer Docker)
mvn spring-boot:run -s .mvn/settings.xml # rodar (precisa PG via compose)
```

### Frontend (Web)
```bash
cd web
npm install --legacy-peer-deps
npx ng serve                             # http://localhost:4200 (hot-reload)
npx ng build                             # build produção
```

### Full Stack (Docker)
```bash
# Dev (API + PG + Frontend)
docker compose -f compose.dev.yaml up --build

# Showcase (+ Prometheus + Grafana + OTel)
cd api && docker compose -f compose.showcase.yaml up --build
```

## Estrutura do Projeto

```
sinapiPRO/
├── api/                    ← Backend (Java 25 + Spring Boot 4)
│   ├── src/main/java/     ← 30+ módulos de negócio
│   ├── src/main/resources/db/migration/  ← Flyway (V1–V11)
│   └── pom.xml
├── web/                    ← Frontend (Angular 19 + PrimeNG)
│   ├── src/app/pages/     ← 75+ componentes
│   └── package.json
├── helm/sinapipro/         ← Helm chart para Kubernetes
├── docs/                   ← Documentação completa
│   ├── architecture/       ← ADRs, diagramas, decisões técnicas
│   ├── domain/             ← Regras de negócio, glossário
│   ├── operations/         ← Deploy, runbooks, SLOs
│   └── product/            ← PRD, roadmap, backlog
├── compose.dev.yaml        ← Docker Compose (dev)
└── .kiro/                  ← Specs e steering (AI-assisted dev)
```

## Módulos

### Gestão de Obras
Obras, Orçamentos (planilha + BDI + ABC), Medições (workflow), Contratos, Cronograma (Gantt + CPM + Curva S), Diário de Obra

### Suprimentos & Financeiro
Requisições → Cotações → Pedidos, Estoque, Contas a Pagar/Receber, Fluxo de Caixa, Job Costing, CNAB 240, Faturas

### Execução & Qualidade
Segurança (inspeções + incidentes), RFI, Punch List, Submittals, Documentos, Equipamentos/Frota, Mão de Obra

### Comercial & Pós-Venda
Propostas, Contratos de Venda, Comissões, Pós-Venda (tickets), Entrega de Obra

### Analytics & Relatórios
EVM (CPI/SPI/EAC), DRE, Portfólio, Dashboards (ECharts), Relatórios PDF/Excel

### Administração
RBAC (roles + permissões granulares), Multi-tenant, Notificações (SSE), Portal do Fornecedor

## Segurança (RBAC)

8 perfis pré-definidos: ADMIN, ENGENHEIRO, COMPRADOR, FINANCEIRO, MESTRE_OBRA, COMERCIAL, SEGURANCA, VISUALIZADOR

Cada endpoint usa `@PreAuthorize("@perm.check('modulo.acao')")` com 30+ permissões granulares.

## Deploy (Kubernetes)

```bash
helm install sinapipro ./helm/sinapipro \
  --set api.image.tag=v1.0.0 \
  --set frontend.image.tag=v1.0.0 \
  --set ingress.host=sinapipro.empresa.com.br
```

## Documentação

Toda a documentação está em [`docs/`](./docs/README.md):
- [Arquitetura](./docs/architecture/overview.md)
- [Decisões (ADRs)](./docs/decisions/adr/)
- [Regras de Negócio](./docs/domain/business-flows.md)
- [PRD](./docs/product/PRD.md)
- [Roadmap](./docs/product/roadmap.md)
- [Deploy & Operações](./docs/operations/deployment.md)

## Licença

MIT
