# Project State

## Last updated
2026-03-30

## Status
**PRODUÇÃO** — Sistema rodando em http://localhost:8090

- 514 arquivos Java compilando sem erros
- 36 migrations Flyway (V1–V36)
- 214 templates Thymeleaf/FTL
- 39 testes unitários passando (BUILD SUCCESS)
- Banco populado com dados de demonstração (V36)

## Sprints concluídas

| Fase | Sprints | Módulos |
|---|---|---|
| Core | 1–8 | Orçamento, Insumos, Composições, Segurança, Relatórios |
| Fase 1 | 9–11 | Planejamento Físico-Financeiro, Reajuste, Baseline |
| Fase 2 | 12–13 | Cadastros de Infraestrutura e Pessoas |
| Fase 3 | 14–16 | Diário de Obra, Contratos, Requisições |
| Fase 4 | 17–19 | Cotações, Pedidos de Compra, Estoque |
| Fase 5 | 20–23 | Financeiro (Plano de Contas, Despesas, Receitas, Movimento) |
| Fase 6 | 24–27 | Comercial (Unidades, Vendas, Tabela de Preços) |
| Fase 7 | 28–29 | Mão de Obra (Banco de Horas, Prestação de Contas) |
| Fase 8 | 30–32 | Boletos, Cheques, Relatórios Financeiros |
| Fase 9 | 33–34 | Atendimento/CRM |
| Fase 10 | 35 | Faturamento (Nota Fiscal de Serviço) |
| Fase 11 | 36–37 | GED, Frota, Relatórios Gerais, Job Costing |

## Migrations Flyway

| Migration | Descrição |
|---|---|
| V1–V13 | Schema core (orçamento, insumos, composições, segurança) |
| V14 | Baseline do orçamento |
| V15 | Cadastros infra (unidade_medida, divisao_insumo, etc.) |
| V16 | Pessoas (empresa, departamento, cargo, funcao, funcionario) |
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
