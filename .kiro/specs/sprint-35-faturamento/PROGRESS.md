# Sprint 35 — Faturamento/NF — PROGRESS

## Status: ✅ COMPLETE (2026-03-29)

### Entidades criadas
- `NotaFiscalServico` (cliente, obra, valor, alíquota ISS, valor ISS, valor líquido)

### Migration
- V32: nota_fiscal_servico

### Lógica de negócio: `NotaFiscalServicoService`
- Cálculo automático de ISS: valor_iss = valor_servicos × aliquota / 100
- Cálculo de valor líquido: valor_liquido = valor_servicos - valor_iss
- Template com cálculo automático via JavaScript
