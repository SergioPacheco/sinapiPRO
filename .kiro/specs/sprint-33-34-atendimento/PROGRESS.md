# Sprints 33–34 — Atendimento/CRM — PROGRESS

## Status: ✅ COMPLETE (2026-03-29)

### Entidades criadas
- `Atendimento` (cliente, obra, título, tipo, prioridade, situação, SLA)
- `OrdemServico` (atendimento, número, descrição, valor)
- `Notificacao` (título, mensagem, tipo, lida, atendimento)

### Migration
- V32: atendimento, ordem_servico, notificacao

### Lógica de negócio: `AtendimentoSlaService`
- SLA por prioridade: URGENTE=8h, ALTA=24h, NORMAL=72h, BAIXA=168h
- Escalação automática com notificação quando SLA vence
- Encerramento com registro de tempo de resolução
- Atendimentos em risco (< 20% do SLA restante)
- Fluxo: ABERTO → EM_ANDAMENTO → AGUARDANDO → ENCERRADO
