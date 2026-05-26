# SinapiPRO — Specs & Tasks

> Objetivo: igualar funcionalidades do OrçaFascio nos módulos core
> Atualizado: 2026-05-23

---

## Sprint 1 — Orçamento Profissional (P1) ✅

| # | Task | Status |
|---|------|:------:|
| 1.1 | Atualização de data base (recalcular preços por mês/UF) | **DONE** |
| 1.2 | Truncamento TCU (arredondamento configurável por base) | **DONE** |
| 1.3 | BDI diferenciado por tipo de item (material, MO, equipamento) | **DONE** |
| 1.4 | Memória de cálculo (fórmulas de quantidades por item) | **DONE** |
| 1.5 | Relatório analítico PDF (composições abertas com insumos) | **DONE** |

---

## Sprint 2 — Medição Completa (P1) ✅

| # | Task | Status |
|---|------|:------:|
| 2.1 | Memória de cálculo na medição | **DONE** |
| 2.2 | Anexos/fotos por medição (upload + vinculação) | **DONE** |
| 2.3 | Rejeição com justificativa | **DONE** |
| 2.4 | Histórico de aprovações (timeline) | **DONE** |
| 2.5 | Serviço não orçado (item extra na medição) | **DONE** |
| 2.6 | Aditivo na medição (vincular change order) | **DONE** |

---

## Sprint 3 — Orçamento Avançado (P2) ✅

| # | Task | Status |
|---|------|:------:|
| 3.1 | Múltiplas bases simultâneas (SINAPI + ORSE + própria) | **DONE** |
| 3.2 | Máscara de item (códigos personalizados) | **DONE** |
| 3.3 | Comparação entre orçamentos (diff de versões) | **DONE** |
| 3.4 | Duplicação de itens e etapas | **DONE** |
| 3.5 | Substituição de itens | **DONE** |
| 3.6 | Importar itens de outro orçamento | **DONE** |

---

## Sprint 4 — Planejamento + Diário (P2) ✅

| # | Task | Status |
|---|------|:------:|
| 4.1 | Feriados/calendário por obra | **DONE** |
| 4.2 | Acompanhamento previsto×realizado | **DONE** |
| 4.3 | Iniciar planejamento do cronograma do orçamento | **DONE** |
| 4.4 | Gráfico de Gantt interativo | **DONE** |
| 4.5 | Vinculação de tarefas do diário ao planejamento | **DONE** |
| 4.6 | Entrada/saída de materiais no diário | **DONE** |
| 4.7 | Relatório fotográfico separado | **DONE** |

---

## Sprint 5 — Compras + Medição Avançada (P2) ✅

| # | Task | Status |
|---|------|:------:|
| 5.1 | Envio de cotação por e-mail (portal fornecedor) | **DONE** |
| 5.2 | Comprar a partir da Curva ABC | **DONE** |
| 5.3 | Múltiplos fiscais por obra (approvers) | **DONE** |
| 5.4 | Medição por empreiteiro | **DONE** |
| 5.5 | Importar medição via Excel | **DONE** |
| 5.6 | Relatório fotográfico na medição | **DONE** |

---

## Sprint 6 — Polish (P3) ✅

| # | Task | Status |
|---|------|:------:|
| 6.1 | Gerador de propostas para pregão | **DONE** |
| 6.2 | Tags em itens do orçamento | **DONE** |
| 6.3 | Lixeira com recuperação | **DONE** |
| 6.4 | Personalização de relatórios | **DONE** |
| 6.5 | Encargos sociais configuráveis | **DONE** |
| 6.6 | Portal do fornecedor | **DONE** |
| 6.7 | Assinatura digital do fiscal | **DONE** |

---

## Resumo Final

| Sprint | Total | Done |
|--------|:-----:|:----:|
| Sprint 1 | 5 | **5** |
| Sprint 2 | 6 | **6** |
| Sprint 3 | 6 | **6** |
| Sprint 4 | 7 | **7** |
| Sprint 5 | 6 | **6** |
| Sprint 6 | 7 | **7** |
| **TOTAL** | **37** | **37** |

---

## Endpoints Backend (referência)

### Budget (`/api/v1/budgets/{id}`)
- `POST /update-base-date` — recalcular preços SINAPI
- `PUT /bdi` / `PUT /bdi/batch` — BDI por tipo
- `GET/PUT /items/{id}/memo` — memória de cálculo
- `PATCH /items/{id}/custom-code` — máscara
- `POST /duplicate` — duplicar itens
- `POST /import-items` — importar de outro orçamento
- `GET /reports/analytical.pdf` — relatório analítico
- `GET /reports/worksheet.pdf` — planilha sintética
- `GET /abc-curve` — curva ABC
- `GET/POST /proposals` — propostas pregão
- `GET/POST /items/{id}/tags` — tags
- `GET/PUT /social-charges` — encargos sociais

### Measurement (`/api/v1/projects/{id}/measurements`)
- `POST /{id}/extra-items` — serviço extra
- `POST /{id}/reject` — rejeição com motivo
- `GET /{id}/history` — histórico aprovações
- `PUT /{id}/change-order` — vincular aditivo
- `GET/PUT /{id}/items/{iid}/memo` — memória de cálculo
- `POST /{id}/import` — importar Excel
- `GET /{id}/reports/bulletin.pdf` — boletim PDF
- `GET /{id}/reports/photo-report.pdf` — relatório fotográfico

### Schedule (`/api/v1/projects/{id}/schedule`)
- `GET/POST` — atividades
- `GET /gantt` — dados Gantt
- `GET /tracking` — previsto×realizado
- `GET /critical-path` — caminho crítico
- `GET /s-curve` — curva S
- `POST /distribute-dates` — distribuir datas
- `GET/POST /holidays` — feriados
- `GET/POST /baselines` — baselines
- `GET /reports/physical-financial.pdf` — cronograma PDF

### Daily Log (`/api/v1/projects/{id}/daily-logs`)
- `POST /{id}/tasks` — vincular tarefa ao planejamento
- `POST /{id}/materials` — entrada/saída materiais
- `POST /{id}/sign` — assinatura digital
- `GET /{id}/reports/photo-report.pdf` — relatório fotográfico

### Procurement (`/api/v1/projects/{id}/procurement`)
- `POST /requests/{id}/quotations` — criar cotação
- `POST /quotations/{id}/generate-order` — gerar pedido
- Portal fornecedor: `GET/POST /api/v1/supplier-portal/quotation`

---

## ═══════════════════════════════════════════════════════════════
## GAPS IDENTIFICADOS — Análise Strato vs SinapiPRO (2026-05-23)
## ═══════════════════════════════════════════════════════════════

---

## Sprint 7 — Financeiro Completo (P1)

| # | Task | Status |
|---|------|:------:|
| 7.1 | Contas a pagar com parcelamento automático (boleto, cheque, transferência) | **DONE** |
| 7.2 | Contas a receber com baixa parcial e juros/multa | **DONE** |
| 7.3 | Conciliação bancária (importar OFX/CNAB, match automático) | **DONE** |
| 7.4 | Rateio de custos entre obras (% configurável) | **DONE** |
| 7.5 | Retenções fiscais (ISS, INSS, IR, PIS/COFINS) na NF | **DONE** |
| 7.6 | Fluxo de caixa projetado (previsto × realizado × projetado) | **DONE** |
| 7.7 | DRE por obra (Demonstrativo de Resultado do Exercício) | **DONE** |
| 7.8 | Aging report (inadimplência por faixa de atraso) | **DONE** |

---

## Sprint 8 — Mão de Obra e Folha (P1)

| # | Task | Status |
|---|------|:------:|
| 8.1 | Cadastro de funcionários completo (documentos, dependentes, histórico) | **DONE** |
| 8.2 | Apontamento de horas por obra/etapa (integração timesheet) | **DONE** |
| 8.3 | Controle de faltas, atestados e afastamentos | **DONE** |
| 8.4 | Cálculo de encargos sociais por funcionário (INSS, FGTS, férias, 13º) | **DONE** |
| 8.5 | Apropriação de mão de obra por centro de custo | **DONE** |
| 8.6 | Relatório de produtividade (HH/unidade por serviço) | **DONE** |
| 8.7 | Controle de EPI por funcionário (entrega, validade, CA) | **DONE** |

---

## Sprint 9 — Vendas Imobiliárias (P1)

| # | Task | Status |
|---|------|:------:|
| 9.1 | Tabela de vendas (unidades com preço, status, reserva) | **DONE** |
| 9.2 | Proposta comercial com simulação de parcelas | **DONE** |
| 9.3 | Contrato de venda com cláusulas e índice de reajuste | **DONE** |
| 9.4 | Controle de parcelas (geração, reajuste INCC/IGPM, baixa) | **DONE** |
| 9.5 | Distrato e rescisão (cálculo de multa, devolução) | **DONE** |
| 9.6 | Cessão/transferência de contrato | **DONE** |
| 9.7 | Comissões de corretores (cálculo, controle de pagamento) | **DONE** |
| 9.8 | Repasse bancário (documentação, acompanhamento) | **DONE** |

---

## Sprint 10 — Avaliação de Fornecedores e Bancos (P2)

| # | Task | Status |
|---|------|:------:|
| 10.1 | Avaliação periódica de fornecedores (critérios configuráveis, nota) | **DONE** |
| 10.2 | Ranking de fornecedores por categoria/nota | **DONE** |
| 10.3 | Cadastro de bancos e contas bancárias da empresa | **DONE** |
| 10.4 | Integração bancária (remessa/retorno CNAB 240/400) | **DONE** |
| 10.5 | Cheques emitidos/recebidos (controle, custódia, compensação) | **DONE** |

---

## Sprint 11 — Fiscal e Impostos (P2)

| # | Task | Status |
|---|------|:------:|
| 11.1 | Cadastro de impostos com alíquotas por UF/município | **DONE** |
| 11.2 | Cálculo automático de retenções na entrada de NF | **DONE** |
| 11.3 | Guias de recolhimento (DARF, GPS, ISS) | **DONE** |
| 11.4 | Livro de entrada/saída de notas fiscais | **DONE** |
| 11.5 | Integração com prefeitura (NFS-e) | **DONE** |

---

## Sprint 12 — Frota e Equipamentos Avançado (P2)

| # | Task | Status |
|---|------|:------:|
| 12.1 | Cadastro completo de veículos (placa, RENAVAM, seguro, IPVA) | **DONE** |
| 12.2 | Controle de abastecimento com km/litro | **DONE** |
| 12.3 | Manutenção preventiva com agenda (km ou tempo) | **DONE** |
| 12.4 | Multas e sinistros vinculados ao veículo/motorista | **DONE** |
| 12.5 | Custo operacional por veículo (TCO) | **DONE** |
| 12.6 | Locação de equipamentos (controle de diárias, devolução) | **DONE** |

---

## Sprint 13 — Índices Econômicos e Reajustes (P2)

| # | Task | Status |
|---|------|:------:|
| 13.1 | Cadastro de índices (INCC, IGPM, CUB, IPCA) com histórico mensal | **DONE** |
| 13.2 | Reajuste automático de parcelas por índice | **DONE** |
| 13.3 | Reajuste de contratos de empreitada | **DONE** |
| 13.4 | Simulação de reajuste (what-if) | **DONE** |
| 13.5 | Importação automática de índices (API IBGE/FGV) | **DONE** |

---

## Sprint 14 — Ordem de Serviço / Atendimento (P2)

| # | Task | Status |
|---|------|:------:|
| 14.1 | Ordem de serviço com workflow (abrir → atribuir → executar → encerrar) | **DONE** |
| 14.2 | Tarefas dentro da OS (checklist de atividades) | **DONE** |
| 14.3 | SLA e controle de prazo (alertas de atraso) | **DONE** |
| 14.4 | Estatísticas de atendimento (tempo médio, backlog, por categoria) | **DONE** |
| 14.5 | Notificação por email ao cliente (abertura, andamento, encerramento) | **DONE** |
| 14.6 | Anexos na OS (fotos, documentos) | **DONE** |
| 14.7 | Histórico de atendimentos por unidade/cliente | **DONE** |

---

## Sprint 15 — Relatórios Financeiros e Gerenciais (P2)

| # | Task | Status |
|---|------|:------:|
| 15.1 | Mapa de custos (orçado × comprometido × realizado) | **DONE** |
| 15.2 | Fluxo de caixa realizado (por período/obra) | **DONE** |
| 15.3 | Posição financeira do cliente (saldo devedor, inadimplência) | **DONE** |
| 15.4 | Curva ABC de fornecedores (volume de compras) | **DONE** |
| 15.5 | Curva ABC de insumos (consumo por obra) | **DONE** |
| 15.6 | Relatório de medições consolidado por contrato | **DONE** |
| 15.7 | Relatório gerencial resumo (dashboard executivo PDF) | **DONE** |
| 15.8 | Apropriação de custos por obra (rateio visualizado) | **DONE** |

---

## Sprint 16 — Cadastros Avançados (P3)

| # | Task | Status |
|---|------|:------:|
| 16.1 | Cadastro de transportadores | **DONE** |
| 16.2 | Cadastro de representantes comerciais | **DONE** |
| 16.3 | Plano de contas contábil (hierárquico) | **DONE** |
| 16.4 | Parâmetros por obra (configurações específicas) | **DONE** |
| 16.5 | Divisão/subdivisão de insumos (classificação hierárquica) | **DONE** |
| 16.6 | Múltiplos telefones/endereços por pessoa (billing, work, home) | **DONE** |
| 16.7 | Campo WhatsApp em cliente/fornecedor | **DONE** |
| 16.8 | Histórico de alterações em cadastros (audit trail) | **DONE** |

---

## Resumo Geral (incluindo sprints anteriores)

| Sprint | Total | Done | **DONE** |
|--------|:-----:|:----:|:----:|
| Sprint 1–6 (OrçaFascio) | 37 | **37** | 0 |
| Sprint 7 — Financeiro Completo | 8 | **8** | 0 |
| Sprint 8 — Mão de Obra | 7 | **7** | 0 |
| Sprint 9 — Vendas Imobiliárias | 8 | **8** | 0 |
| Sprint 10 — Fornecedores/Bancos | 5 | **5** | 0 |
| Sprint 11 — Fiscal/Impostos | 5 | **5** | 0 |
| Sprint 12 — Frota Avançado | 6 | **6** | 0 |
| Sprint 13 — Índices/Reajustes | 5 | **5** | 0 |
| Sprint 14 — Ordem de Serviço | 7 | **7** | 0 |
| Sprint 16 — Cadastros Avançados | 8 | **8** | 0 |
| Sprint 17 — Relatórios Financeiros PDF | 10 | **10** | 0 |
| Sprint 18 — Relatórios Suprimentos PDF | 8 | **8** | 0 |
| Sprint 19 — Relatórios Medição/Obra PDF | 8 | **8** | 0 |
| Sprint 20 — Relatórios Comercial/Vendas PDF | 8 | **8** | 0 |
| Sprint 21 — Relatórios Orçamento Avançado PDF | 8 | **8** | 0 |
| Sprint 22 — Relatórios MO/Estoque PDF | 8 | **8** | 0 |
| Sprint 23 — Relatórios Gerenciais/Dashboard PDF | 8 | **8** | 0 |
| Sprint 24 — Infraestrutura de Relatórios | 10 | **10** | 0 |
| Sprint 25 — Relatórios OS/Atendimento | 8 | **8** | 0 |
| Sprint 26 — Relatórios Financeiros Adicionais | 11 | **11** | 0 |
| Sprint 27 — Relatórios Comerciais/Cadastros Adicionais | 12 | **12** | 0 |
| **TOTAL** | **194** | **194** | **0** |

---

## Campos Faltantes em Tabelas Existentes (field-level gaps)

### project (faltam do Strato TB2_OBRA)
- `total_built_area` numeric(14,2) — área construída total
- `branch_id` UUID FK — filial
- `accounting_code` varchar — classificação contábil
- `financial_control_enabled` boolean — controle disponibilidade
- `stock_control_enabled` boolean — controle estoque
- `budget_control_enabled` boolean — controle orçado×realizado
- `cost_apportionment_enabled` boolean — rateio habilitado
- `apportionment_rate` numeric(5,2) — % rateio custos indiretos
- `purchase_limit_no_auth` numeric(18,2) — limite compra sem autorização
- `development_id` UUID FK — empreendimento vinculado
- `labor_price_table_id` UUID FK — tabela preço MO

### client (faltam do Strato TB2_PESS tipo C)
- `trade_name` varchar — razão social
- `gross_income` numeric(18,2) — renda bruta
- `spouse_income` numeric(18,2) — renda cônjuge
- `billing_address` jsonb — endereço cobrança
- `work_address` jsonb — endereço trabalho
- `preferred_due_day` int — dia vencimento preferido
- `commission_rate` numeric(5,2) — taxa comissão
- `billing_by_email` boolean — boleto por email
- `whatsapp` varchar — WhatsApp

### supplier (faltam do Strato TB2_PESS tipo F)
- `whatsapp` varchar — WhatsApp
- `cell_phone` varchar — celular (separado do phone)
- `commercial_phone` varchar — telefone comercial

---

## Módulos do Strato NÃO portados (decisão consciente)

| Módulo Strato | Motivo |
|---|---|
| acesso (ponto eletrônico) | Sistema HR dedicado |
| importacoes/fidc (Construgiro) | Produto financeiro proprietário |
| strato8 (migração legada) | Ferramenta interna de migração |
| menu/window (UI framework) | Angular cuida da navegação |
| util/backup | Infraestrutura DevOps |

---

## ═══════════════════════════════════════════════════════════════
## RELATÓRIOS PDF — Stack: Playwright + JTE/OpenHTMLtoPDF + FastExcel
## ═══════════════════════════════════════════════════════════════

> Fonte: análise dos 471 relatórios Jasper do Strato
> Tecnologia: JTE + OpenHTMLtoPDF (tabular), Playwright (gráficos), FastExcel (Excel)
> Priorização: P1 = essencial para operação, P2 = importante, P3 = nice-to-have

---

## Sprint 17 — Relatórios Financeiros PDF (P1)

| # | Task | Tecnologia | Strato equiv. | Status |
|---|------|-----------|---------------|:------:|
| 17.1 | Boleto bancário (ficha compensação FEBRABAN) | JTE | BO1xxxxx (17) | **DONE** |
| 17.2 | Recibo de pagamento (fornecedor) | JTE | RIC00100-170 (17) | **DONE** |
| 17.3 | Extrato de conta corrente (por período) | JTE | MCX00100-104 (9) | **DONE** |
| 17.4 | Extrato de movimentação bancária | JTE | MVB00200-500 (8) | **DONE** |
| 17.5 | Posição de contas a pagar (aging PDF) | JTE | VPG00100-530 (14) | **DONE** |
| 17.6 | Posição de contas a receber (aging PDF) | JTE | VRC00100-510 (13) | **DONE** |
| 17.7 | DRE por obra (PDF formatado) | JTE | DRE00100-200 (2) | **DONE** |
| 17.8 | Fluxo de caixa projetado (PDF) | Playwright | FCX00100-200 (3) | **DONE** |
| 17.9 | Balancete financeiro | JTE | BAL00100-410 (8) | **DONE** |
| 17.10 | Mapa de custos por obra (PDF) | Playwright | CUS00100-500 (9) | **DONE** |

---

## Sprint 18 — Relatórios de Suprimentos PDF (P1)

| # | Task | Tecnologia | Strato equiv. | Status |
|---|------|-----------|---------------|:------:|
| 18.1 | Requisição de compra (impressão) | JTE | REQ00100-500 (3) | **DONE** |
| 18.2 | Mapa comparativo de cotações (multi-fornecedor) | JTE | COT00100-500 (6) | **DONE** |
| 18.3 | Pedido de compra (impressão completa) | JTE | PED00100-700 (10) | **DONE** |
| 18.4 | Pedidos em atraso (relatório gerencial) | JTE | PED00300 | **DONE** |
| 18.5 | Curva ABC de insumos (por obra) | Playwright | ABC00100-201 (4) | **DONE** |
| 18.6 | Curva ABC de fornecedores | Playwright | ABC00200 | **DONE** |
| 18.7 | Cronograma de compras (previsto × realizado) | JTE | — (novo) | **DONE** |
| 18.8 | Nota de recebimento de materiais | JTE | — (novo) | **DONE** |

---

## Sprint 19 — Relatórios de Medição e Obra PDF (P1)

| # | Task | Tecnologia | Strato equiv. | Status |
|---|------|-----------|---------------|:------:|
| 19.1 | Boletim de medição acumulada | JTE | MED00200-204 (5) | **DONE** |
| 19.2 | Medição por empreiteiro | JTE | MDC00100-401 (9) | **DONE** |
| 19.3 | Resumo de medições (consolidado por contrato) | JTE | MED00300-500 (3) | **DONE** |
| 19.4 | Memória de cálculo da medição (PDF) | JTE | MED00110 | **DONE** |
| 19.5 | RDO completo (com fotos inline) | Playwright | RDO00100-107 (8) | **DONE** |
| 19.6 | Cronograma físico-financeiro (Gantt PDF) | Playwright | CRO00100-400 (3) | **DONE** |
| 19.7 | Curva S (previsto × realizado) | Playwright | — (existente, melhorar) | **DONE** |
| 19.8 | Diário de obra consolidado (período) | JTE | — (novo) | **DONE** |

---

## Sprint 20 — Relatórios Comerciais/Vendas PDF (P2)

| # | Task | Tecnologia | Strato equiv. | Status |
|---|------|-----------|---------------|:------:|
| 20.1 | Ficha do imóvel/unidade | JTE | IMO00100-224 (9) | **DONE** |
| 20.2 | Contrato de venda (impressão) | JTE | COM00100-500 (11) | **DONE** |
| 20.3 | Posição de vendas por empreendimento | JTE | UES00300-500 (10) | **DONE** |
| 20.4 | Extrato do cliente (parcelas + pagamentos) | JTE | VRC00200-221 | **DONE** |
| 20.5 | Relatório de comissões (por corretor) | JTE | COM00400-401 | **DONE** |
| 20.6 | Inadimplência por empreendimento | JTE | VRC00500-510 | **DONE** |
| 20.7 | Proposta comercial (impressão) | JTE | — (novo) | **DONE** |
| 20.8 | Distrato / Rescisão (demonstrativo) | JTE | — (novo) | **DONE** |

---

## Sprint 21 — Relatórios de Orçamento Avançados PDF (P2)

| # | Task | Tecnologia | Strato equiv. | Status |
|---|------|-----------|---------------|:------:|
| 21.1 | Orçamento analítico completo (composições abertas) | JTE | ORC00200-211 (4) | **DONE** |
| 21.2 | Orçamento sintético (resumo por etapa) | JTE | ORC00100 | **DONE** |
| 21.3 | Planilha de composição de preços unitários (CPU) | JTE | ORC00300-304 (5) | **DONE** |
| 21.4 | Cronograma financeiro do orçamento | Playwright | CRO00200 | **DONE** |
| 21.5 | Análise de compras (orçado × comprado × saldo) | JTE | — (novo) | **DONE** |
| 21.6 | Comparativo entre orçamentos (diff PDF) | JTE | — (novo) | **DONE** |
| 21.7 | Listagem de insumos (consolidada) | JTE | INS00100-731 (14) | **DONE** |
| 21.8 | BDI detalhado por tipo | JTE | — (existente, PDF) | **DONE** |

---

## Sprint 22 — Relatórios de Mão de Obra e Estoque PDF (P2)

| # | Task | Tecnologia | Strato equiv. | Status |
|---|------|-----------|---------------|:------:|
| 22.1 | Folha resumo (horas por funcionário/obra) | JTE | MDO00100-301 (4) | **DONE** |
| 22.2 | Banco de horas (extrato por funcionário) | JTE | — (novo) | **DONE** |
| 22.3 | Produtividade (HH/unidade por serviço) | JTE | — (novo) | **DONE** |
| 22.4 | Posição de estoque (por obra/almoxarifado) | JTE | EST00100-200 (4) | **DONE** |
| 22.5 | Movimentação de estoque (entradas/saídas) | JTE | MVE00100-600 (8) | **DONE** |
| 22.6 | Controle de EPI (entregas por funcionário) | JTE | — (existente, PDF) | **DONE** |
| 22.7 | Ficha de equipamento (histórico manutenção) | JTE | EQP00100-200 (2) | **DONE** |
| 22.8 | Etiquetas de patrimônio | JTE | ETQ00100-110 (2) | **DONE** |

---

## Sprint 23 — Relatórios Gerenciais e Dashboard PDF (P2)

| # | Task | Tecnologia | Strato equiv. | Status |
|---|------|-----------|---------------|:------:|
| 23.1 | Dashboard executivo (PDF multi-obra) | Playwright | — (novo) | **DONE** |
| 23.2 | Relatório gerencial resumo (1 página por obra) | Playwright | RES00100-106 (8) | **DONE** |
| 23.3 | EVM — Earned Value (gráfico + indicadores) | Playwright | — (existente, PDF) | **DONE** |
| 23.4 | Posição financeira consolidada (multi-obra) | JTE | — (novo) | **DONE** |
| 23.5 | Relatório de contratos (vigentes + aditivos) | JTE | CRT00100-400 (5) | **DONE** |
| 23.6 | Relatório de segurança (incidentes + EPIs) | JTE | SEG00100 (1) | **DONE** |
| 23.7 | Relatório de avaliação de fornecedores | JTE | AVA00100 (1) | **DONE** |
| 23.8 | Exportação Excel genérica (qualquer listagem) | FastExcel | — (novo) | **DONE** |

---

## Sprint 24 — Infraestrutura de Relatórios (P1 — pré-requisito)

| # | Task | Tecnologia | Status |
|---|------|-----------|:------:|
| 24.1 | Setup JTE (Java Template Engine) + templates base | JTE | **DONE** |
| 24.2 | Setup OpenHTMLtoPDF com CSS paged media | OpenHTMLtoPDF | **DONE** |
| 24.3 | Setup Playwright Java (headless Chrome) | Playwright | **DONE** |
| 24.4 | ReportService abstrato (factory pattern por tipo) | Java | **DONE** |
| 24.5 | Template base PDF (header empresa, footer paginação) | JTE/CSS | **DONE** |
| 24.6 | Setup FastExcel para exportação streaming | FastExcel | **DONE** |
| 24.7 | Endpoint genérico de exportação Excel | Java | **DONE** |
| 24.8 | Fila assíncrona para relatórios pesados (>30s) | Virtual Threads | **DONE** |
| 24.9 | Gotenberg Docker compose (dev) | Docker | **DONE** |
| 24.10 | Testes de geração de PDF (smoke tests) | Testcontainers | **DONE** |

---

## Resumo — Relatórios

| Sprint | Módulo | Qtd | Prioridade | Tecnologia principal |
|--------|--------|:---:|:----------:|---------------------|
| 24 | Infraestrutura | 10 | **P1** | JTE + OpenHTMLtoPDF + Playwright + FastExcel |
| 17 | Financeiro | 10 | **P1** | JTE (8) + Playwright (2) |
| 18 | Suprimentos | 8 | **P1** | JTE (6) + Playwright (2) |
| 19 | Medição/Obra | 8 | **P1** | JTE (5) + Playwright (3) |
| 20 | Comercial/Vendas | 8 | **P2** | JTE (8) |
| 21 | Orçamento Avançado | 8 | **P2** | JTE (7) + Playwright (1) |
| 22 | MO/Estoque | 8 | **P2** | JTE (8) |
| 23 | Gerencial/Dashboard | 8 | **P2** | Playwright (3) + JTE (4) + FastExcel (1) |
| **TOTAL** | | **68** | | JTE: 46, Playwright: 11, FastExcel: 1, Infra: 10 |

### Ordem de execução recomendada:
1. **Sprint 24** (infraestrutura) — obrigatório antes de qualquer relatório
2. **Sprint 17** (financeiro) — mais demandado pelos usuários
3. **Sprint 18** (suprimentos) — ciclo de compras precisa de impressão
4. **Sprint 19** (medição/obra) — core do negócio
5. **Sprint 20-23** (P2) — após validação dos P1 com usuários
6. **Sprint 25-27** (P2) — relatórios adicionais identificados na análise completa do Strato

---

## Sprint 25 — Relatórios Ordem de Serviço / Atendimento (P2)

| # | Task | Tecnologia | Strato equiv. | Status |
|---|------|-----------|---------------|:------:|
| 25.1 | Ficha de atendimento (OS individual) | JTE | ATE00100-106 | **DONE** |
| 25.2 | Histórico de atendimentos por cliente | JTE | ATE00200-210 | **DONE** |
| 25.3 | Atendimentos por período | JTE | ATE00300-310 | **DONE** |
| 25.4 | Atendimentos por categoria | JTE | ATE00120 | **DONE** |
| 25.5 | Relatório de SLA (tempo médio, backlog) | Playwright | — (novo) | **DONE** |
| 25.6 | Backlog de atendimentos pendentes | JTE | — (novo) | **DONE** |
| 25.7 | Solicitações pendentes de aprovação | JTE | SLC00100-106 (6) | **DONE** |
| 25.8 | Histórico de aprovações por obra | JTE | SLC00300-403 (5) | **DONE** |

---

## Sprint 26 — Relatórios Financeiros Adicionais (P2)

| # | Task | Tecnologia | Strato equiv. | Status |
|---|------|-----------|---------------|:------:|
| 26.1 | Informe de rendimentos (DIRF) | JTE | IRF00100-104 (5) | **DONE** |
| 26.2 | Distribuição de despesas (rateio por obra) | JTE | DIS00100-400 (5) | **DONE** |
| 26.3 | Acompanhamento financeiro por obra | JTE | ACF00100-320 (5) | **DONE** |
| 26.4 | Evolução de saldo bancário | Playwright | — (novo) | **DONE** |
| 26.5 | Plano de contas (hierárquico) | JTE | PCO00100-401 (5) | **DONE** |
| 26.6 | Razão contábil | JTE | RAZ00100-200 (2) | **DONE** |
| 26.7 | Fechamento mensal (competência) | JTE | PER00100-500 (7) | **DONE** |
| 26.8 | Notas fiscais emitidas/recebidas | JTE | NOF00100-200 (2) | **DONE** |
| 26.9 | Cheques emitidos | JTE | CHE00001-999 (2) | **DONE** |
| 26.10 | Resumo despesas por natureza/fornecedor | JTE | RSD00100-200 (4) | **DONE** |
| 26.11 | Liberações financeiras | JTE | LIB00100-200 (2) | **DONE** |

---

## Sprint 27 — Relatórios Comerciais/Cadastros Adicionais (P2)

| # | Task | Tecnologia | Strato equiv. | Status |
|---|------|-----------|---------------|:------:|
| 27.1 | Vendas por corretor | JTE | VOR00100-700 (7) | **DONE** |
| 27.2 | Vendas por período | JTE | VRS00200-230 (4) | **DONE** |
| 27.3 | Vendas resumo por status | JTE | — (novo) | **DONE** |
| 27.4 | Tabela de preços (listagem) | JTE | PRC00100-330 (8) + TPE00100-401 (5) | **DONE** |
| 27.5 | Contrato com aditivos | JTE | CRT00100-400 (5) | **DONE** |
| 27.6 | Medições do contrato | JTE | MDC00100-401 (9) | **DONE** |
| 27.7 | Saldo contratual | JTE | — (novo) | **DONE** |
| 27.8 | Ranking de fornecedores | JTE | RNK00100-210 (3) | **DONE** |
| 27.9 | Plano de compras mensal | JTE | PCM00100-400 (4) | **DONE** |
| 27.10 | Custo unitário (detalhado) | JTE | CUN00100-111 (3) | **DONE** |
| 27.11 | Manutenção preventiva equipamentos | JTE | MRO00100-300 (3) | **DONE** |
| 27.12 | Ficha cadastral cliente/fornecedor | JTE | FIC00100-101 (2) + PES00100-200 (2) | **DONE** |

---

## Resumo — Relatórios (atualizado)

| Sprint | Módulo | Qtd | Prioridade |
|--------|--------|:---:|:----------:|
| 24 | Infraestrutura | 10 | **P1** |
| 17 | Financeiro | 10 | **P1** |
| 18 | Suprimentos | 8 | **P1** |
| 19 | Medição/Obra | 8 | **P1** |
| 20 | Comercial/Vendas | 8 | **P2** |
| 21 | Orçamento Avançado | 8 | **P2** |
| 22 | MO/Estoque | 8 | **P2** |
| 23 | Gerencial/Dashboard | 8 | **P2** |
| 25 | Ordem de Serviço/Atendimento | 8 | **P2** |
| 26 | Financeiro Adicional | 11 | **P2** |
| 27 | Comercial/Cadastros Adicional | 12 | **P2** |
| **TOTAL** | | **99** | |

### Cobertura Strato após implementação:
- Strato: 471 relatórios Jasper
- SinapiPRO (existentes): 13 relatórios
- SinapiPRO (após sprints 17-27): **89 endpoints PDF** (cobrindo ~99 relatórios únicos)
- Relatórios Strato redundantes (variações de layout/agrupamento): ~350
- **Cobertura funcional: ~95%** dos casos de uso reais
- Não portados: COC (condomínio — módulo não portado), PADRAO (template interno)
