# Sprints 28–29 — Mão de Obra — PROGRESS

## Status: ✅ COMPLETE (2026-03-29)

### Entidades criadas
- `Competencia` (mes, ano, encerrada)
- `BancoHoras` (funcionario, competencia, credito, debito, saldo)
- `MovimentacaoHora` (tipo: CREDITO/DEBITO/EXTRA)
- `PrestacaoContas` (funcionario, competencia, valor, tipo, situação)

### Migration
- V30: competencia, banco_horas, movimentacao_hora, prestacao_contas

### Lógica de negócio: `EncerrarCompetenciaService`
- Transfere saldo positivo → crédito na próxima competência
- Transfere saldo negativo → débito na próxima competência
- Cria próxima competência automaticamente se não existir
- Alerta quando saldo > 40h (limite CLT Art. 59)
- Relatório de encerramento com alertas
