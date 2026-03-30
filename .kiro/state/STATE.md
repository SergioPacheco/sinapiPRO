# Project State

## Last updated
2026-03-29

## Active spec
none — migração completa (Fases 1-11, Sprints 9-37)

## Current status
Migração Strato → SinapiPRO **COMPLETA**. 498 source files Java compilando sem erros.
32 migrations Flyway (V1–V32). 203 templates Thymeleaf/FTL.

## What's implemented

### Core (Sprints 1-8 — pré-migração)
- CRUD: Orçamento, Item, Composição, Insumo, Etapa, Cliente, Obra, Usuário, Estado, Cidade
- CRUD: BasePreco, BaseInsumo, TipoCusto, Tributo, Fornecedor, FornecedorInsumo
- CRUD: TipoUnidade, EspecieInsumo, TipoUsuario
- Importação SINAPI (insumos + composições via XLS)
- Relatórios FreeMarker (PDF): ListaInsumos, ListaComposicoes, OrcamentoAnalitico, GlobalMaterialMO, ServicosOrcamento
- Exportação: PDF, XLS, JSON, CSV, RTF
- Spring Security: form login, roles, BCrypt, PrimeiroAcessoFilter, HistoricoSenha
- Audit trail: AuditService + AuditLog
- Taxas: BDI, Leis Sociais, Taxa Adm, BDI detalhado
- Fluxo: Estimativa → Venda → Execução + Comparativo

### Fase 1 — Orçamento Avançado (Sprints 9-11)
- Planejamento Físico-Financeiro (PlanejamentoItem, PlanejamentoService)
- Cronograma Financeiro + Curva S (PDF) + Gantt interativo
- Reajuste de Preços (percentual, valor, SINAPI em lote)
- Baseline do orçamento (snapshot + comparativo)
- Digitação rápida de itens

### Fase 2 — Cadastros Completos (Sprints 12-13)
- UnidadeMedida, DivisaoInsumo, SubDivisaoInsumo, Indice, FormaPagamento, TipoObra
- Empresa, Departamento, Cargo, Funcao, Funcionario
- ClienteEndereco, ClienteReferencia

### Fase 3 — Operacional de Obra (Sprints 14-16)
- Diário de Obra (DiarioObra + itens: MaoObra, Equipamento, Ocorrencia, Servico)
- Cadastros auxiliares: DiarioClima, DiarioArea, DiarioAcidente
- Contratos e Medições (Contrato, ContratoItem, Medicao, MedicaoItem)
- Requisições de Insumos (Requisicao, RequisicaoItem)

### Fase 4 — Suprimentos (Sprints 17-19)
- Cotações (Cotacao, CotacaoItem, CotacaoFornecedor, RespostaCotacao)
- Pedidos de Compra (PedidoCompra, PedidoItem, NotaFiscal)
- Estoque (Estoque, MovimentoEstoque) + Equipamentos

### Fase 5 — Financeiro (Sprints 20-23)
- Plano de Contas (hierárquico), Conta Bancária, Histórico Bancário
- Contas a Pagar (Despesa, PagamentoDespesa) — situação automática
- Contas a Receber (Receita, RecebimentoReceita) — situação automática
- Movimento Bancário — atualiza saldo da conta automaticamente

### Fase 6 — Comercial (Sprints 24-27)
- Unidades de Venda (espelho), SituacaoUnidade, CaracteristicaUnidade
- Propostas, Vendas, ParcelasVenda
- Tabela de Preços (com reajuste), Comissões (cálculo automático)
- Relatórios: Mapa de Vendas, Resumo de Vendas, Resumo por Corretor (FTL)

### Fase 7 — Mão de Obra (Sprints 28-29)
- Competências, Banco de Horas (saldo automático), Movimentação de Horas
- Prestação de Contas

### Fase 8 — Financeiro Avançado (Sprints 30-32)
- Boletos (emissão, cancelamento), Cheques
- Relatórios FTL: Fluxo de Caixa, Balancete, DRE

### Fase 9 — Atendimento/CRM (Sprints 33-34)
- Atendimentos, Ordens de Serviço, Notificações

### Fase 10 — Faturamento/NF (Sprint 35)
- Nota Fiscal de Serviço (cálculo automático ISS)

### Fase 11 — Módulos de Apoio (Sprints 36-37)
- GED (Gestão Eletrônica de Documentos)
- Frota (Veículos + Agendamentos de Manutenção)

## Migrations Flyway
V1–V13: core (pré-migração)
V14: baseline
V15: cadastros infra (unidade_medida, divisao_insumo, etc.)
V16: pessoas/empresa (empresa, departamento, cargo, funcao, funcionario, etc.)
V17: diário de obra
V18: contrato/medição
V19: requisição
V20: cotação
V21: pedido de compra
V22: estoque
V23: plano de contas
V24: despesa
V25: receita
V26: movimento bancário
V27: unidade de venda
V28: venda
V29: tabela de preços/comissão
V30: mão de obra
V31: boleto/cheque
V32: atendimento/NF/GED/frota

## Blockers
- Nexus corporativo indisponível — compilar com `-s /tmp/settings-local.xml -o`
- Cobertura de testes baixa nos services (apenas 4 test files)

## Next steps
1. Adicionar testes unitários para services críticos
2. Melhorar templates das Fases 9-11 (atualmente básicos)
3. Integrar relatórios financeiros (fluxo-caixa, balancete, DRE) com endpoints
