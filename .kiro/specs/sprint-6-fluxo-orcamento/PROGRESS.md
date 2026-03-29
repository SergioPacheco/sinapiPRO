# Sprint 6 — Fluxo Estimativa→Venda→Execução — PROGRESS

## Status: ✅ COMPLETE (2026-03-29)

### Task 1: OrcamentoService.copiarOrcamento() ✅
- Duplica orçamento com todos os itens
- Copia taxas, BDI detalhado, dados do cliente/obra

### Task 2: Estimativa → gerar Venda ✅
- Endpoint: `POST /orcamentos/gerarVenda/{codigo}`

### Task 3: Venda → gerar Execução ✅
- Endpoint: `POST /orcamentos/gerarExecucao/{codigo}`

### Task 4: Comparativo Venda vs Execução ✅
- Template: `orcamento/ComparativoVendaExecucao.html`
- Endpoint: `GET /orcamentos/comparativo?codigoVenda=X&codigoExecucao=Y`
- Tabela side-by-side: MO, Material, Equipamento, Sub-Total, LS, BDI, Total
- Mostra diferença absoluta e percentual

### BUILD: SUCCESS
