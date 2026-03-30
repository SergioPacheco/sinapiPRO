# Sprint 15 — Contratos e Medições — PROGRESS

## Status: ✅ COMPLETE (2026-03-29)

### Entidades criadas
- `Contrato` (obra, cliente, itens, medições, situação)
- `ContratoItem` (descrição, unidade, qtd, valor)
- `Medicao` (contrato, número, data, período, situação)
- `MedicaoItem` (contratoItem, qtd medida, % exec, valor)

### Migration
- V18: contrato, contrato_item, medicao, medicao_item

### Lógica de negócio: `MedicaoContratoService`
- Cálculo por item: quantidade medida × valor unitário
- % executado por item e acumulado do contrato
- Retenção configurável (padrão 5% — prática brasileira)
- Aprovação gera Despesa automaticamente com vencimento +30 dias
- Saldo disponível para medição
