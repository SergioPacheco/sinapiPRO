# SinapiPRO

ERP open source para gestão completa de obras da construção civil.

## Stack

| Camada | Tecnologia |
|--------|-----------|
| Backend | Java 25 + Spring Boot 4 + PostgreSQL 17 |
| Frontend | Angular 19 + PrimeNG 19 (Aura Dark) |
| Infra | Docker Compose (app + PG + Prometheus + Grafana + OTel) |

## Rodar com Docker

```bash
docker compose up --build
```

| Serviço | URL |
|---------|-----|
| Frontend | http://localhost:4200 |
| API (Swagger) | http://localhost:4080/swagger-ui.html |
| PostgreSQL | localhost:4432 (user: sinapipro / pass: sinapipro) |
| Grafana | http://localhost:4030 (admin/admin) |
| Prometheus | http://localhost:4090 |

## Desenvolvimento local

### Backend
```bash
cd api
mvn compile -s .mvn/settings.xml
mvn test -s .mvn/settings.xml    # requer Docker (Testcontainers)
```

### Frontend
```bash
cd web
npm install --legacy-peer-deps
npx ng serve                     # http://localhost:4200
npx ng build                     # produção
```

## Módulos

- **Obras** — Cadastro, workspace, dashboard por projeto
- **Orçamento** — Planilha hierárquica, BDI, memória de cálculo, curva ABC, relatórios PDF
- **Medições** — Workflow DRAFT→SUBMITTED→APPROVED→PAID, retenção, aditivos
- **Contratos** — Aditivos (change orders), retenção, financeiro vinculado
- **Cronograma** — CPM, Curva S, baselines, tracking previsto×realizado
- **Diário de Obra** — Clima, efetivo, atividades, materiais, ocorrências, assinatura digital
- **Suprimentos** — Requisição→Cotação→Pedido→Recebimento, portal do fornecedor
- **Financeiro** — Contas a pagar/receber, faturas, fluxo de caixa, job costing
- **Composições** — Catálogo SINAPI, composições próprias, versionamento copy-on-write
- **Segurança** — Inspeções, incidentes, EPIs, treinamentos, exames médicos
- **Documentos** — Upload, versionamento, vinculação a entidades
- **Notificações** — Alertas de vencimento, pendências, eventos em tempo real (SSE)

## Licença

MIT
