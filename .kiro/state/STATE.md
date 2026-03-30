# Project State

## Last updated
2026-03-30

## Active spec
none — migração completa + deploy funcionando

## Current status
**PRODUÇÃO** — Sistema rodando em http://localhost:8090

- 514 arquivos Java compilando sem erros
- 36 migrations Flyway (V1–V36)
- 214 templates Thymeleaf/FTL
- 39 testes unitários passando (BUILD SUCCESS)
- Banco populado com dados de demonstração (V36)

## Sprints concluídas

| Fase | Sprints | Status |
|---|---|---|
| Core (pré-migração) | 1–8 | ✅ |
| Fase 1 — Orçamento Avançado | 9–11 | ✅ |
| Fase 2 — Cadastros Completos | 12–13 | ✅ |
| Fase 3 — Operacional de Obra | 14–16 | ✅ |
| Fase 4 — Suprimentos | 17–19 | ✅ |
| Fase 5 — Financeiro | 20–23 | ✅ |
| Fase 6 — Comercial | 24–27 | ✅ |
| Fase 7 — Mão de Obra | 28–29 | ✅ |
| Fase 8 — Financeiro Avançado | 30–32 | ✅ |
| Fase 9 — Atendimento/CRM | 33–34 | ✅ |
| Fase 10 — Faturamento/NF | 35 | ✅ |
| Fase 11 — Módulos de Apoio | 36–37 | ✅ |

## Migrations Flyway

| Migration | Descrição |
|---|---|
| V1–V13 | Schema core (orçamento, insumos, composições, segurança) |
| V14 | Baseline do orçamento |
| V15 | Cadastros infra (unidade_medida, divisao_insumo, etc.) |
| V16 | Pessoas/empresa (empresa, departamento, cargo, funcao, funcionario) |
| V17 | Diário de obra (9 tabelas) |
| V18 | Contrato e medição |
| V19 | Requisição de insumos |
| V20 | Cotação |
| V21 | Pedido de compra |
| V22 | Estoque e equipamento |
| V23 | Plano de contas, conta bancária, histórico bancário |
| V24 | Despesa e pagamento |
| V25 | Receita e recebimento |
| V26 | Movimento bancário |
| V27 | Unidade de venda |
| V28 | Venda e parcelas |
| V29 | Tabela de preços e comissão |
| V30 | Mão de obra (competência, banco de horas) |
| V31 | Boleto e cheque |
| V32 | Atendimento, NF, GED, frota |
| V33 | Custo médio no estoque |
| V34 | Permissões por módulo |
| V35 | Dados iniciais (admin, grupos) |
| V36 | Dados de demonstração (seed) |

## Services de lógica de negócio

| Service | Lógica |
|---|---|
| `AnaliseCotacaoService` | Análise comparativa, menor preço, geração de pedidos |
| `MedicaoContratoService` | Cálculo por item, retenção 5%, aprovação → Despesa |
| `EstoqueService` | Custo Médio Ponderado (NBC TG 16) |
| `BaixaPedidoService` | Recebimento parcial/total → atualiza estoque |
| `VendaParcelasService` | Parcelas automáticas, reajuste INCC/IPCA |
| `AtendimentoSlaService` | SLA por prioridade, escalação automática |
| `EncerrarCompetenciaService` | Encerramento + transferência de saldo (CLT) |
| `ConciliacaoBancariaService` | Conciliação bancária, saldo vs extrato |
| `AvancoFisicoService` | % executado por serviço, curva de avanço |
| `AlertaManutencaoService` | Alertas por data e KM, intervalos padrão |
| `GedUploadService` | Upload real, validação OWASP, download |
| `JobCostingService` | EVM: PV, EV, AC, CPI, SPI, EAC, VAC (PMBOK) |
| `ValidacaoNegocioService` | Unidade já vendida, parcelas duplicadas, contrato encerrado |
| `RelatorioOperacionalService` | Inadimplência, posição de estoque |

## Acesso

```
URL:   http://localhost:8090
Email: admin@sinapipro.com
Senha: admin123
```

## Próximos passos sugeridos

- [ ] Testes de integração com banco H2
- [ ] API REST (OpenAPI/Swagger) para apps mobile
- [ ] Exportação XLS dos relatórios operacionais
- [ ] Importação de planilhas SINAPI mais recentes
- [ ] Testes end-to-end (Selenium/Playwright)
