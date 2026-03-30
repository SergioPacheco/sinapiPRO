# Sprint 18 — Pedidos de Compra — PROGRESS

## Status: ✅ COMPLETE (2026-03-29)

### Entidades criadas
- `PedidoCompra`, `PedidoItem`, `NotaFiscal`

### Migration
- V21: pedido_compra, pedido_item, nota_fiscal

### Lógica de negócio: `BaixaPedidoService`
- Recebimento total/parcial com validação de quantidade
- Atualiza estoque automaticamente com custo médio ponderado
- Cria item de estoque se não existir para a obra
- FormBaixaPedido.html com tabela de recebimento por item
