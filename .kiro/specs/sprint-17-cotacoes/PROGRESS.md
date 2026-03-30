# Sprint 17 — Cotações — PROGRESS

## Status: ✅ COMPLETE (2026-03-29)

### Entidades criadas
- `Cotacao`, `CotacaoItem`, `CotacaoFornecedor`, `RespostaCotacao`

### Migration
- V20: cotacao, cotacao_item, cotacao_fornecedor, resposta_cotacao

### Lógica de negócio: `AnaliseCotacaoService`
- Análise comparativa: menor preço por item, totais por fornecedor, economia potencial
- Seleção automática do menor preço por item
- Geração de pedidos de compra agrupados por fornecedor
- Template AnaliseCotacao.html com tabela comparativa visual
