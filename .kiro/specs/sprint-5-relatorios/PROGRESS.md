# Sprint 5 — Relatórios Avançados — PROGRESS

## Status: ✅ COMPLETE (2026-03-29)

### Task 1: Relatório Orçamento Analítico ✅
- Template: `relatorio/RelatorioOrcamentoAnalitico.html`
- Endpoint: `GET /relatorios/orcamentoAnalitico/{codigo}`
- Breakdown completo: itens + taxas (LS, BDI, TaxAdm) + total geral

### Task 2: Relatório Global Material + Mão de Obra ✅
- Template: `relatorio/RelatorioGlobalMaterialMO.html`
- Endpoint: `GET /relatorios/globalMaterialMO/{codigo}`
- Resumo por espécie com percentuais

### Task 3: Relatório Serviços do Orçamento ✅
- Template: `relatorio/RelatorioServicosOrcamento.html`
- Endpoint: `GET /relatorios/servicosOrcamento/{codigo}`
- Lista composições (tipo=COMPOSICAO) com totais

### Task 4: Exportação CSV ✅
- Endpoint: `GET /relatorios/exportCsv/{codigo}`
- Formato: separador `;`, UTF-8, download automático

### Task 5: Exportação RTF ✅
- Endpoint: `GET /relatorios/exportRtf/{codigo}`
- Formato: RTF básico com tabela, download automático

### BUILD: SUCCESS
