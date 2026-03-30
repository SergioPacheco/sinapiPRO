# Sprints 24–27 — Comercial — PROGRESS

## Status: ✅ COMPLETE (2026-03-29)

### Sprint 24 — Unidades e Imóveis
- `SituacaoUnidade` (nome + cor), `UnidadeVenda`, `CaracteristicaUnidade`
- V27: situacao_unidade, unidade_venda, caracteristica_unidade
- Espelho de vendas com situação colorida

### Sprint 25 — Vendas e Incorporação
- `Proposta`, `Venda`, `ParcelaVenda`
- V28: proposta, venda, parcela_venda
- **Lógica**: `VendaParcelasService`
  - Geração automática: entrada + parcelas mensais + chaves
  - Ajuste de centavos na última parcela
  - Reajuste por índice (INCC/IPCA/CUB): valor × (1 + índice/100)
  - Busca valor na tabela de preços vigente

### Sprint 26 — Tabela de Preços e Comissões
- `TabelaPreco`, `TabelaPrecoItem`, `Comissao`
- V29: tabela_preco, tabela_preco_item, comissao
- **Lógica**: `TabelaPrecoService.aplicarReajuste()`, `ComissaoService` cálculo automático

### Sprint 27 — Relatórios Comerciais
- mapa-vendas.ftl, resumo-vendas.ftl, resumo-corretor.ftl
- Endpoints: GET /relatorios/mapaVendas, /resumoVendas, /resumoCorretor
