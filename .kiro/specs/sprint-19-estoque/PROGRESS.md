# Sprint 19 — Estoque — PROGRESS

## Status: ✅ COMPLETE (2026-03-29)

### Entidades criadas
- `Equipamento`, `Estoque`, `MovimentoEstoque`

### Migration
- V22: equipamento, estoque, movimento_estoque
- V33: coluna custo_medio na tabela estoque

### Lógica de negócio: `EstoqueService` (reescrito)
- Custo Médio Ponderado (CMP) na entrada — NBC TG 16
- Fórmula: novo_custo_medio = (qtd_atual × custo_atual + qtd_entrada × custo_entrada) / (qtd_atual + qtd_entrada)
- Baixa usa custo médio atual (não muda na saída)
- Validação de saldo insuficiente na saída
- Alerta de estoque mínimo
