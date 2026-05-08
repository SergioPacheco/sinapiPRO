# Specs & Tasks — SinapiPRO Showcase API

> Baseado em pesquisa de mercado (ERP Research 2026, CMiC, Senior/Mega) sobre requisitos mínimos para sistemas de gestão de obras.

---

## Análise: O que o mercado exige vs. o que temos

| Requisito de Mercado | Status Atual | Prioridade |
|---------------------|--------------|------------|
| Job Costing (custo por código/fase) | ✅ Implementado | P0 |
| Cronograma / Planejamento Físico | ✅ Implementado (CPM + Curva S) | P0 |
| Medições de Obra (avanço físico) | ✅ Implementado (workflow + integração JC) | P0 |
| Composições SINAPI (insumos + coeficientes) | ✅ Implementado (cálculo + importação CSV) | P1 |
| Contratos e Aditivos | ✅ Implementado (change orders + financial summary) | P1 |
| Suprimentos (cotação → pedido → recebimento) | ✅ Implementado (fluxo completo + JC integration) | P1 |
| Orçamento detalhado (etapas → itens → composições) | ✅ Implementado (hierarquia + BDI + reajuste) | P1 |
| Relatórios (Curva ABC, Curva S, Cronograma Financeiro) | ✅ Implementado | P2 |
| Diário de Obra | ✅ Implementado (validações + summary) | P2 |
| Dashboard com indicadores | ✅ Implementado (EVM + Cash Flow + Portfolio) | P2 |
| Gestão de Equipamentos | ✅ Implementado (usage + maintenance alerts) | P3 |
| BDI (Benefícios e Despesas Indiretas) | ✅ Implementado (6 componentes) | P3 |

---

## SPEC 1: Job Costing & Cost Codes (P0)

### Descrição
Sistema de custeio por obra com estrutura hierárquica de códigos de custo (divisão → fase → tipo de custo). Permite rastrear orçado vs. realizado vs. comprometido em cada nível.

### Requisitos
- REQ-1.1: Estrutura de Cost Codes hierárquica (3 níveis: divisão, fase, tipo)
- REQ-1.2: Budget at Completion (BAC) por cost code
- REQ-1.3: Actual Cost (AC) alimentado por invoices/medições
- REQ-1.4: Committed Cost (custos comprometidos via contratos/POs)
- REQ-1.5: Variance analysis (BAC - AC - Committed = Saldo disponível)
- REQ-1.6: WIP Report (Work in Progress) — over/under billing

### Tasks
- [ ] Criar migration V3: tabelas `cost_code`, `cost_code_budget`, `cost_transaction`
- [ ] Criar módulo `jobcosting/` (domain, application, api)
- [ ] Entity `CostCode` com hierarquia (parent_id self-reference)
- [ ] Entity `CostTransaction` (tipo: ACTUAL, COMMITTED, BUDGET)
- [ ] Repository com queries de agregação (sum by code, by phase, by project)
- [ ] Service `JobCostingService` com cálculo de variância e saldo
- [ ] Controller REST `/api/v1/budgets/{id}/cost-codes` (CRUD + summary)
- [ ] Endpoint `/api/v1/budgets/{id}/cost-codes/summary` (BAC, AC, Committed, Variance)
- [ ] Testes unitários do cálculo de variância
- [ ] Teste de integração do fluxo completo

---

## SPEC 2: Cronograma & Planejamento Físico (P0)

### Descrição
Planejamento de atividades com datas, predecessoras, percentual de avanço e geração de Curva S. Permite comparar planejado vs. realizado.

### Requisitos
- REQ-2.1: Atividades com data início/fim planejada e real
- REQ-2.2: Predecessoras (finish-to-start)
- REQ-2.3: Percentual de avanço por atividade
- REQ-2.4: Peso de cada atividade no total da obra
- REQ-2.5: Curva S (planejado vs. realizado acumulado por período)
- REQ-2.6: Caminho crítico (CPM simplificado)

### Tasks
- [ ] Criar migration V4: tabelas `schedule_activity`, `activity_dependency`
- [ ] Criar módulo `schedule/` (domain, application, api)
- [ ] Entity `ScheduleActivity` (budget_id, name, planned_start, planned_end, actual_start, actual_end, weight, progress_pct)
- [ ] Entity `ActivityDependency` (predecessor_id, successor_id, type)
- [ ] Service `ScheduleService` com cálculo de Curva S
- [ ] Service `CriticalPathService` com CPM (topological sort + forward/backward pass)
- [ ] Controller REST `/api/v1/budgets/{id}/schedule` (CRUD atividades)
- [ ] Endpoint `/api/v1/budgets/{id}/schedule/s-curve` (dados para gráfico)
- [ ] Endpoint `/api/v1/budgets/{id}/schedule/critical-path` (lista de atividades críticas)
- [ ] Testes unitários do CPM e Curva S
- [ ] Teste de integração

---

## SPEC 3: Medições de Obra (P0)

### Descrição
Registro de medições periódicas (quinzenais/mensais) dos serviços executados, com cálculo de valor medido, acumulado e saldo a medir. Vinculado a contratos.

### Requisitos
- REQ-3.1: Medição vinculada a um budget e período
- REQ-3.2: Itens de medição com quantidade medida no período
- REQ-3.3: Cálculo automático: valor medido = qtd × preço unitário
- REQ-3.4: Acumulado de medições anteriores
- REQ-3.5: Saldo a medir = total contratado - acumulado
- REQ-3.6: Retenção configurável (% retido por medição)
- REQ-3.7: Status da medição: DRAFT → SUBMITTED → APPROVED → PAID

### Tasks
- [ ] Criar migration V5: tabelas `measurement`, `measurement_item`
- [ ] Criar módulo `measurement/` (domain, application, api)
- [ ] Entity `Measurement` (budget_id, period_start, period_end, status, retention_pct)
- [ ] Entity `MeasurementItem` (measurement_id, cost_code_id, quantity, unit_price)
- [ ] Service `MeasurementService` com cálculo de acumulado e saldo
- [ ] Workflow de aprovação (state machine: DRAFT → SUBMITTED → APPROVED)
- [ ] Controller REST `/api/v1/budgets/{id}/measurements` (CRUD + approve)
- [ ] Endpoint `/api/v1/budgets/{id}/measurements/{mid}/approve` (POST)
- [ ] Testes unitários dos cálculos
- [ ] Teste de integração do workflow

---

## SPEC 4: Composições SINAPI (P1)

### Descrição
Catálogo de composições de serviços baseado na tabela SINAPI, com insumos, coeficientes e preços por estado/mês de referência.

### Requisitos
- REQ-4.1: Composição com código SINAPI, descrição, unidade
- REQ-4.2: Itens da composição (insumo + coeficiente)
- REQ-4.3: Preço do insumo por estado e mês de referência
- REQ-4.4: Cálculo automático do custo unitário da composição
- REQ-4.5: Busca por código ou descrição (full-text search)
- REQ-4.6: Importação de planilha SINAPI (CSV/XLSX)

### Tasks
- [ ] Criar migration V6: tabelas `composition`, `composition_item`, `material`, `material_price`
- [ ] Criar módulo `sinapi/` (domain, application, api)
- [ ] Entity `Composition` (sinapi_code, description, unit, group)
- [ ] Entity `CompositionItem` (composition_id, material_id, coefficient)
- [ ] Entity `Material` (sinapi_code, description, unit, origin)
- [ ] Entity `MaterialPrice` (material_id, state, reference_month, price)
- [ ] Service `CompositionCostService` — calcula custo unitário por estado/mês
- [ ] Controller REST `/api/v1/compositions` (list, search, get, calculate)
- [ ] Endpoint `/api/v1/compositions/{id}/cost?state=RN&month=2026-01`
- [ ] Full-text search com `tsvector` no PostgreSQL
- [ ] Testes unitários do cálculo de custo
- [ ] Seed data com composições reais SINAPI

---

## SPEC 5: Contratos & Aditivos (P1)

### Descrição
Gestão de contratos com fornecedores/empreiteiros, incluindo aditivos (alterações de escopo/valor), retenções e vinculação com medições.

### Requisitos
- REQ-5.1: Contrato vinculado a budget e supplier
- REQ-5.2: Itens do contrato (serviço, quantidade, preço unitário)
- REQ-5.3: Aditivos (change orders) com justificativa e aprovação
- REQ-5.4: Valor original + aditivos = valor atualizado
- REQ-5.5: Retenção contratual configurável
- REQ-5.6: Status: DRAFT → ACTIVE → COMPLETED → CANCELLED

### Tasks
- [ ] Criar migration V7: tabelas `contract`, `contract_item`, `change_order`
- [ ] Criar módulo `contract/` (domain, application, api)
- [ ] Entity `Contract` (budget_id, supplier_id, number, value, retention_pct, status)
- [ ] Entity `ContractItem` (contract_id, description, quantity, unit_price)
- [ ] Entity `ChangeOrder` (contract_id, number, description, amount, status, approved_at)
- [ ] Service `ContractService` com cálculo de valor atualizado
- [ ] Controller REST `/api/v1/contracts` (CRUD + change orders)
- [ ] Testes

---

## SPEC 6: Suprimentos — Cotação → Pedido → Recebimento (P1)

### Descrição
Fluxo completo de compras: solicitação → cotação com múltiplos fornecedores → análise comparativa → geração de pedido pelo menor preço → recebimento parcial/total.

### Requisitos
- REQ-6.1: Solicitação de compra vinculada a budget + cost code
- REQ-6.2: Cotação enviada a N fornecedores
- REQ-6.3: Mapa comparativo de preços (análise automática)
- REQ-6.4: Geração de pedido de compra pelo menor preço
- REQ-6.5: Recebimento parcial com atualização de estoque
- REQ-6.6: Integração com cost transactions (committed → actual)

### Tasks
- [ ] Criar migration V8: tabelas `purchase_request`, `quotation`, `quotation_response`, `purchase_order`, `receiving`
- [ ] Criar módulo `procurement/` (domain, application, api)
- [ ] Entities: PurchaseRequest, Quotation, QuotationResponse, PurchaseOrder, Receiving
- [ ] Service `QuotationAnalysisService` — mapa comparativo, menor preço
- [ ] Service `PurchaseOrderService` — geração automática a partir de cotação
- [ ] Service `ReceivingService` — recebimento parcial + atualização de committed→actual
- [ ] Controllers REST para cada entidade
- [ ] Testes

---

## SPEC 7: Orçamento Detalhado (P1 — Refatoração)

### Descrição
Refatorar o Budget atual (simplificado) para suportar estrutura hierárquica: Etapas → Sub-etapas → Itens → Composições SINAPI. Com BDI e Leis Sociais.

### Requisitos
- REQ-7.1: Etapas hierárquicas (N níveis)
- REQ-7.2: Item de orçamento vinculado a composição SINAPI
- REQ-7.3: Quantidade × custo unitário = custo direto
- REQ-7.4: BDI configurável (administração, lucro, impostos)
- REQ-7.5: Preço de venda = custo direto × (1 + BDI)
- REQ-7.6: Curva ABC de insumos e serviços
- REQ-7.7: Reajuste de preços em lote (por percentual ou nova referência SINAPI)

### Tasks
- [ ] Criar migration V9: tabelas `budget_stage`, `budget_item`, `bdi_config`
- [ ] Refatorar módulo `budget/` para suportar hierarquia
- [ ] Entity `BudgetStage` (budget_id, parent_id, name, order)
- [ ] Entity `BudgetItem` (stage_id, composition_id, quantity, unit_cost, bdi_pct)
- [ ] Entity `BdiConfig` (budget_id, administration, profit, taxes, social_charges)
- [ ] Service `BudgetCalculationService` — totalização hierárquica com BDI
- [ ] Service `AbcCurveService` — Curva ABC de insumos
- [ ] Service `PriceAdjustmentService` — reajuste em lote
- [ ] Endpoints REST para stages, items, BDI, ABC curve
- [ ] Testes

---

## SPEC 8: Dashboard & Relatórios (P2)

### Descrição
Endpoints de agregação para alimentar dashboards: resumo financeiro, avanço físico, indicadores de performance (CPI, SPI).

### Requisitos
- REQ-8.1: Portfolio summary (total budgets, total amount, open invoices)
- REQ-8.2: Budget health (BAC, EAC, VAC, CPI, SPI por budget)
- REQ-8.3: Cash flow projection (receitas vs. despesas por mês)
- REQ-8.4: Curva ABC endpoint
- REQ-8.5: Curva S endpoint (planejado vs. realizado)

### Tasks
- [ ] Criar módulo `analytics/` (application, api)
- [ ] Service `PortfolioAnalyticsService` — agregações cross-budget
- [ ] Service `EarnedValueService` — EVM (PV, EV, AC, CPI, SPI, EAC, VAC)
- [ ] Service `CashFlowService` — projeção de fluxo de caixa
- [ ] Controller REST `/api/v1/analytics/portfolio`
- [ ] Controller REST `/api/v1/analytics/budgets/{id}/earned-value`
- [ ] Controller REST `/api/v1/analytics/budgets/{id}/cash-flow`
- [ ] Materialized views no PostgreSQL para performance
- [ ] Testes

---

## SPEC 9: Diário de Obra (P2)

### Descrição
Registro diário de atividades no canteiro: mão de obra presente, equipamentos utilizados, condições climáticas, ocorrências e serviços executados.

### Requisitos
- REQ-9.1: Registro por data e obra
- REQ-9.2: Mão de obra (funcionário, função, horas)
- REQ-9.3: Equipamentos (equipamento, horas, ociosidade)
- REQ-9.4: Condições climáticas (manhã, tarde)
- REQ-9.5: Ocorrências (acidentes, paralisações, visitas)
- REQ-9.6: Serviços executados (atividade, local, observações)
- REQ-9.7: Fotos/anexos (referência a GED)

### Tasks
- [ ] Criar migration V10: tabelas `daily_log`, `daily_log_labor`, `daily_log_equipment`, `daily_log_weather`, `daily_log_occurrence`, `daily_log_service`
- [ ] Criar módulo `dailylog/` (domain, application, api)
- [ ] Entities para cada sub-registro
- [ ] Controller REST `/api/v1/budgets/{id}/daily-logs`
- [ ] Testes

---

## Ordem de Implementação Sugerida

```
Fase 1 (Core — sem isso não é sistema de obras):
  SPEC 4: Composições SINAPI ← base de tudo
  SPEC 7: Orçamento Detalhado ← depende de SPEC 4
  SPEC 1: Job Costing ← depende de SPEC 7

Fase 2 (Execução — controle da obra em andamento):
  SPEC 2: Cronograma
  SPEC 3: Medições
  SPEC 5: Contratos

Fase 3 (Suprimentos + Visibilidade):
  SPEC 6: Suprimentos
  SPEC 8: Dashboard & Relatórios
  SPEC 9: Diário de Obra
```

---

## Referências

- [ERP Research — Construction ERP Requirements Checklist 2026](https://erpresearch.com/pages/en-us/erp-requirements-construction)
- [CMiC — Top 10 Critical Features in Construction ERP](https://cmicglobal.com/resources/top-10-construction-erp-software-features/)
- [Senior/Mega — Funcionalidades ERP Construção](https://site.senior.com.br/construcao/funcionalidades-erp-mega/)
- PMBOK 7th Edition — Earned Value Management
- NBR ISO 21500 — Orientações sobre gerenciamento de projetos
- SINAPI/CEF — Sistema Nacional de Pesquisa de Custos e Índices
