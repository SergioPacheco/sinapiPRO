# SinapiPRO — Product Requirements Document (PRD)

> Versão: 1.0 | Data: 2026-05-23 | Status: Aprovado

---

## 1. Visão do Produto

### 1.1 O que é
SinapiPRO é um **ERP open source para gestão completa de obras da construção civil**. Cobre o ciclo inteiro desde a captação do cliente até a pós-obra, integrando orçamento, contratos, planejamento, execução, medições, suprimentos, financeiro, segurança e documentos.

### 1.2 Problema que resolve
Construtoras de pequeno e médio porte no Brasil usam planilhas Excel, sistemas legados (Delphi/VB6) ou ERPs genéricos que não entendem o fluxo da construção civil. O resultado: retrabalho, perda de controle financeiro, medições imprecisas e cronogramas irreais.

### 1.3 Proposta de valor
- **Gratuito e open source** — sem licença, sem vendor lock-in
- **Específico para construção civil** — SINAPI, BDI, medições, cronograma físico-financeiro
- **Moderno** — API-first, cloud-native, mobile-ready
- **Brasileiro** — legislação fiscal, CNAB, NFS-e, índices INCC/IGPM/CUB

### 1.4 Diferencial competitivo vs. mercado

| Concorrente | Fraqueza | SinapiPRO |
|---|---|---|
| Sienge | Caro, complexo, enterprise-only | Open source, simples |
| Mega ERP | Genérico, não entende construção | Vertical especializado |
| Planilhas | Sem integração, sem workflow | Fluxo integrado end-to-end |

---

## 2. Usuários-Alvo

| Persona | Papel | Necessidade principal |
|---|---|---|
| **Orçamentista** | Monta orçamentos com SINAPI/ORSE | Composições, BDI, memória de cálculo |
| **Engenheiro de obra** | Gerencia execução diária | Diário, medições, cronograma |
| **Gestor de obras** | Visão multi-obra | Dashboard, Job Costing, EVM |
| **Comprador** | Ciclo de suprimentos | Requisição → cotação → pedido |
| **Financeiro** | Contas a pagar/receber | Parcelamento, CNAB, DRE |
| **Diretor** | Decisão estratégica | Dashboard executivo, fluxo de caixa |
| **Corretor** | Vendas imobiliárias | Tabela de preços, contratos, comissões |
| **Técnico SST** | Segurança do trabalho | EPIs, treinamentos, incidentes |

---

## 3. Escopo Funcional

### 3.1 Módulos Core (implementados)

```
┌─────────────────────────────────────────────────────────────┐
│                    SINAPIPRO — MÓDULOS                       │
├─────────────────────────────────────────────────────────────┤
│ COMERCIAL          │ PLANEJAMENTO        │ EXECUÇÃO          │
│ • Obras            │ • Orçamentos        │ • Diário de Obra  │
│ • Contratos        │ • Cronograma (Gantt)│ • Medições        │
│ • Vendas Imob.     │ • Curva S / EVM     │ • Apontamento     │
│ • Propostas        │ • Baselines         │ • Equipes         │
├─────────────────────────────────────────────────────────────┤
│ SUPRIMENTOS        │ FINANCEIRO          │ CONTROLE          │
│ • Requisições      │ • Contas a Pagar    │ • Job Costing     │
│ • Cotações         │ • Contas a Receber  │ • Estoque         │
│ • Pedidos          │ • Mov. Bancária     │ • Equipamentos    │
│ • Recebimento      │ • DRE / Fluxo Caixa │ • Segurança (SST) │
├─────────────────────────────────────────────────────────────┤
│ CADASTROS          │ DOCUMENTOS          │ ANALYTICS         │
│ • Clientes         │ • GED               │ • Dashboards      │
│ • Fornecedores     │ • RFI               │ • Relatórios PDF  │
│ • Funcionários     │ • Submittals        │ • Aging Report    │
│ • Materiais/SINAPI │ • Punch List        │ • Curva ABC       │
└─────────────────────────────────────────────────────────────┘
```

### 3.2 Fluxo Macro de Negócio

```
Lead/Cliente → Proposta → Orçamento → Aprovação → Contrato
    → Obra criada → Planejamento (cronograma + equipes)
        → Execução (diário + medições + apontamento)
            → Suprimentos (requisição → cotação → pedido → recebimento)
                → Financeiro (NF → parcelas → pagamento → conciliação)
                    → Entrega → Pós-obra (garantia + manutenção)
```

---

## 4. Stack Tecnológica

### 4.1 Backend (API)

| Camada | Tecnologia | Versão |
|---|---|---|
| Runtime | Java (Temurin) | 25 LTS |
| Framework | Spring Boot | 4.0.x |
| ORM | Hibernate / Spring Data JPA | 7.x |
| Security | Spring Security + OAuth2 + JWT | 7.x |
| Database | PostgreSQL | 17.x |
| Migrations | Flyway | 11.x |
| Observability | Micrometer + Prometheus + OTel | — |
| Build | Maven | 3.9+ |
| Container | Docker + compose | — |
| Concurrency | Virtual Threads (Loom) | JEP 444 |

### 4.2 Frontend (planejado)

| Camada | Tecnologia |
|---|---|
| Framework | Angular 19+ (Signals, SSR) |
| UI | Tailwind CSS 4 |
| Gráficos | Apache ECharts |
| Tabelas | TanStack Table (headless) |
| State | Angular Signals |
| Build | Vite / esbuild |

### 4.3 Relatórios e Exportação

#### Estratégia de Relatórios (sem JasperReports)

| Tipo | Tecnologia | Justificativa |
|---|---|---|
| **PDF complexo** | **Playwright Java** (headless browser) | Reutiliza componentes Angular, consistência visual, Virtual Threads para paralelismo |
| **PDF leve/rápido** | **JTE + OpenHTMLtoPDF** | Templates compilados em Java nativo, CSS3 paged media, ideal para relatórios de milhares de páginas |
| **Excel/XLSX** | **FastExcel** | Streaming de baixo consumo de memória, substitui Apache POI |
| **Dashboards** | **Apache ECharts** (frontend) | Gráficos interativos com Signals, exportação PNG/SVG |
| **Tabelas analíticas** | **TanStack Table** (frontend) | Headless, filtros avançados, paginação virtual |

#### Arquitetura de Geração de PDF

```
┌──────────────┐     ┌──────────────────┐     ┌──────────────┐
│ API Request  │────▶│ ReportService    │────▶│ PDF Response │
│ GET /report  │     │ (Virtual Thread) │     │ byte[]       │
└──────────────┘     └────────┬─────────┘     └──────────────┘
                              │
                    ┌─────────┴─────────┐
                    │                   │
              ┌─────▼─────┐      ┌─────▼──────┐
              │ Playwright │      │ JTE +      │
              │ (complex)  │      │ OpenHTML   │
              │            │      │ (simple)   │
              │ Angular SSR│      │ HTML→PDF   │
              │ route      │      │ direct     │
              └────────────┘      └────────────┘
```

**Regras de decisão:**
- Relatório com gráficos/charts → Playwright (renderiza ECharts)
- Relatório tabular simples (boletim, NF) → JTE + OpenHTMLtoPDF
- Exportação de dados brutos → FastExcel (streaming)
- Dashboard interativo → ECharts no frontend (sem PDF)

**Infraestrutura (Kubernetes):**
- Opção A: Playwright embutido no pod Java (simples, ~200MB extra)
- Opção B: **Gotenberg** como sidecar/microserviço (escalável, API REST para HTML→PDF)

#### Dependências Maven (relatórios)

```xml
<!-- PDF via HTML (relatórios simples) -->
<dependency>
    <groupId>gg.jte</groupId>
    <artifactId>jte</artifactId>
    <version>3.x</version>
</dependency>
<dependency>
    <groupId>com.openhtmltopdf</groupId>
    <artifactId>openhtmltopdf-pdfbox</artifactId>
    <version>1.1.x</version>
</dependency>

<!-- PDF via browser (relatórios complexos) -->
<dependency>
    <groupId>com.microsoft.playwright</groupId>
    <artifactId>playwright</artifactId>
    <version>1.x</version>
</dependency>

<!-- Excel -->
<dependency>
    <groupId>org.dhatim</groupId>
    <artifactId>fastexcel</artifactId>
    <version>0.18.x</version>
</dependency>
```

---

## 5. Requisitos Não-Funcionais

### 5.1 Performance
- API response time: p95 < 200ms (CRUD), p95 < 2s (relatórios)
- Geração de PDF: < 5s para relatórios de até 100 páginas
- Suportar 500 usuários simultâneos por instância
- Virtual Threads: sem pool fixo, escala com I/O

### 5.2 Segurança
- OAuth2 + JWT (stateless)
- RBAC com permissões por módulo
- Multi-tenant (isolamento por tenant_id)
- Dados sensíveis criptografados em repouso
- Rate limiting na API pública

### 5.3 Disponibilidade
- SLA: 99.5% (excluindo manutenção programada)
- Deploy zero-downtime (rolling update)
- Migrations backward-compatible

### 5.4 Escalabilidade
- Horizontal: múltiplos pods stateless
- Database: read replicas para relatórios
- Relatórios pesados: fila assíncrona (notificação quando pronto)

### 5.5 Observabilidade
- Métricas: Micrometer → Prometheus → Grafana
- Traces: OpenTelemetry
- Logs: structured JSON → ELK/Loki
- Health checks: Spring Actuator

---

## 6. Modelo de Dados (resumo)

### Entidades principais
```
project (obra) ─┬── budget (orçamento) ── budget_item ── composition
                 ├── contract ── change_order
                 ├── measurement ── measurement_item
                 ├── schedule_activity (cronograma)
                 ├── daily_log ── tasks, materials, photos
                 ├── purchase_request → quotation → purchase_order → receiving
                 ├── payable ── payable_installment ── tax_retention
                 ├── receivable ── receivable_installment
                 ├── bank_transaction
                 ├── team ── team_member
                 └── document ── document_version

development (empreendimento) ── development_unit
    └── sale_contract ── sale_installment

employee ── hour_bank, salary_history, timesheet_entry
supplier ── supplier_evaluation, supplier_advance
client ── client_contact
```

### Convenções
- UUID como PK em todas as tabelas
- `created_at` / `updated_at` automáticos
- Soft delete via `active` boolean (cadastros)
- Status como enum string (não integer)
- Valores monetários: `numeric(18,2)`
- Quantidades: `numeric(14,4)`

---

## 7. API Design

### Convenções REST
- Base: `/api/v1/`
- Recursos no plural: `/projects`, `/budgets`, `/suppliers`
- Sub-recursos: `/projects/{id}/measurements`
- Ações: `POST /budgets/{id}/effectuate` (verbos como sub-recurso)
- Paginação: `?page=0&size=20` (Spring Data)
- Filtros: query params (`?status=ACTIVE&q=search`)
- Erros: RFC 9457 ProblemDetail
- Versionamento: URL path (`/v1/`)

### Autenticação
```
Authorization: Bearer <JWT>
Scopes: sinapipro.read, sinapipro.write, sinapipro.admin
```

### Endpoints (67 módulos, ~200+ endpoints)
Documentação interativa via **Swagger UI** em `/swagger-ui.html`

---

## 8. Roadmap

### Fase 1 — Core ERP (✅ Concluído)
- Orçamentos com SINAPI, BDI, memória de cálculo
- Medições com workflow e aprovação
- Cronograma com Gantt e Curva S
- Suprimentos completo (requisição → recebimento)
- Diário de obra

### Fase 2 — Financeiro Completo (✅ Concluído)
- Contas a pagar com parcelamento e retenções fiscais
- Contas a receber com Price/SAC e CNAB
- Movimentação bancária e conciliação
- DRE, Fluxo de Caixa, Aging Report

### Fase 3 — Módulos Avançados (✅ Concluído)
- Vendas imobiliárias (contratos, parcelas, reajuste, distrato)
- Mão de obra (competência, banco de horas, tabela de preços)
- Suprimentos avançado (limites, cronograma, multi-item)
- Orçamento avançado (efetivação, cronograma financeiro)

### Fase 4 — Frontend + Relatórios (🔜 Próximo)
- Angular 19 com Tailwind + Signals
- Relatórios PDF (Playwright + JTE)
- Exportação Excel (FastExcel)
- Dashboards com ECharts
- PWA / Mobile responsive

### Fase 5 — Integrações (📋 Planejado)
- NFS-e (prefeituras)
- CNAB 240/400 (bancos)
- API IBGE/FGV (índices econômicos)
- Portal do fornecedor
- Portal do cliente
- Integração contábil

### Fase 6 — Enterprise (📋 Planejado)
- RBAC granular (permissões por obra/módulo)
- Multi-empresa
- Auditoria completa (audit trail)
- Relatórios customizáveis
- API pública para integrações

---

## 9. Métricas de Sucesso

| Métrica | Meta |
|---|---|
| Tempo para criar orçamento completo | < 2h (vs. 8h em planilha) |
| Ciclo de medição (draft → aprovação) | < 48h |
| Tempo de geração de relatório PDF | < 5s |
| Adoção (construtoras usando) | 50 em 12 meses |
| Contribuidores open source | 10+ em 6 meses |
| Uptime | 99.5% |

---

## 10. Riscos e Mitigações

| Risco | Impacto | Mitigação |
|---|---|---|
| Complexidade fiscal (ISS, INSS, IR) | Alto | Alíquotas configuráveis, não hardcoded |
| Performance com muitos dados | Médio | Paginação, índices, read replicas |
| Adoção open source | Médio | Documentação excelente, Docker one-click |
| Concorrência com Sienge/UAU | Baixo | Foco em PMEs, simplicidade, custo zero |
| Mudanças legislativas | Médio | Tabelas de configuração, não código |

---

## 11. Decisões Arquiteturais (ADRs)

### ADR-001: Sem JasperReports
**Contexto:** JasperReports é o padrão Java para relatórios, mas usa XML/JRXML datado, IDE proprietária (Jaspersoft Studio), e não se integra bem com stack moderna.

**Decisão:** Usar Playwright Java + JTE/OpenHTMLtoPDF.

**Consequências:**
- (+) Relatórios com mesma identidade visual do sistema
- (+) Desenvolvedores frontend podem criar templates
- (+) Sem dependência de ferramenta proprietária
- (-) Curva de aprendizado para CSS paged media
- (-) Playwright adiciona ~200MB ao container

### ADR-002: PostgreSQL como único banco
**Decisão:** Não suportar MySQL/Oracle. PostgreSQL only.

**Consequências:**
- (+) JSONB, full-text search, CTEs recursivas, window functions
- (+) Migrations mais simples (um dialeto)
- (-) Empresas com Oracle/SQL Server precisam migrar

### ADR-003: Multi-tenant por coluna (tenant_id)
**Decisão:** Isolamento lógico via `tenant_id` em todas as tabelas, não schema separado.

**Consequências:**
- (+) Simples de implementar e escalar
- (+) Um único pool de conexões
- (-) Risco de data leak se filtro falhar (mitigado por Hibernate Filter)

### ADR-004: API-first, sem server-side rendering
**Decisão:** Backend é API REST pura. Frontend é SPA Angular separado.

**Consequências:**
- (+) Frontend e backend evoluem independentemente
- (+) Possibilita mobile app futuro
- (-) SEO não é relevante (sistema interno)
