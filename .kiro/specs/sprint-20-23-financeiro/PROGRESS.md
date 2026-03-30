# Sprints 20–23 — Financeiro — PROGRESS

## Status: ✅ COMPLETE (2026-03-29)

### Sprint 20 — Plano de Contas + Conta Bancária
- `PlanoContas` (hierárquico, pai-filho), `ContaBancaria`, `HistoricoBancario`
- V23: plano_contas, conta_bancaria, historico_bancario

### Sprint 21 — Contas a Pagar
- `Despesa`, `PagamentoDespesa`
- V24: despesa, pagamento_despesa
- **Lógica**: situação automática ABERTA→PARCIAL→PAGA

### Sprint 22 — Contas a Receber
- `Receita`, `RecebimentoReceita`
- V25: receita, recebimento_receita
- **Lógica**: situação automática ABERTA→PARCIAL→RECEBIDA

### Sprint 23 — Movimento Bancário
- `MovimentoBancario`
- V26: movimento_bancario
- **Lógica**: `MovimentoBancarioService` atualiza saldo da conta automaticamente
- **Lógica**: `ConciliacaoBancariaService` — conciliar/desconciliar, saldo vs extrato

### Relatórios FTL
- fluxo-caixa.ftl, balancete.ftl, dre.ftl
- inadimplencia.ftl, posicao-estoque.ftl
