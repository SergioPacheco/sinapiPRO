# Plano de Migração — Strato → SinapiPRO

**Status: COMPLETO** ✅ (2026-03-30)

## Resumo

| Fase | Sprints | Arquivos | Status |
|---|---|---|---|
| Core (pré-migração) | 1–8 | ~238 | ✅ |
| Fase 1 — Orçamento Avançado | 9–11 | ~33 | ✅ |
| Fase 2 — Cadastros Completos | 12–13 | ~122 | ✅ |
| Fase 3 — Operacional de Obra | 14–16 | ~54 | ✅ |
| Fase 4 — Suprimentos | 17–19 | ~35 | ✅ |
| Fase 5 — Financeiro | 20–23 | ~43 | ✅ |
| Fase 6 — Comercial | 24–27 | ~47 | ✅ |
| Fase 7 — Mão de Obra | 28–29 | ~25 | ✅ |
| Fase 8 — Financeiro Avançado | 30–32 | ~15 | ✅ |
| Fase 9 — Atendimento/CRM | 33–34 | ~12 | ✅ |
| Fase 10 — Faturamento/NF | 35 | ~5 | ✅ |
| Fase 11 — Módulos de Apoio | 36–37 | ~20 | ✅ |
| **TOTAL** | **37 sprints** | **~649 arquivos** | **✅** |

## Lógica de negócio implementada

Além dos CRUDs básicos, os seguintes services implementam lógica real:

- **Cotação**: análise comparativa, menor preço automático, geração de pedidos
- **Medição**: cálculo por item, retenção 5%, aprovação gera Despesa
- **Estoque**: Custo Médio Ponderado (NBC TG 16)
- **Pedido**: baixa parcial/total atualiza estoque automaticamente
- **Vendas**: parcelas automáticas (entrada+mensais+chaves), reajuste por índice
- **Atendimento**: SLA por prioridade, escalação automática, notificações
- **Banco de Horas**: encerramento de competência com transferência de saldo (CLT)
- **Conciliação**: saldo sistema vs extrato bancário
- **Avanço Físico**: % executado por serviço, curva de avanço
- **Frota**: alertas por data e KM com intervalos padrão
- **GED**: upload real com validação OWASP
- **Job Costing**: EVM completo (PMBOK/NBR ISO 21500)
- **Validações**: unidade já vendida, parcelas duplicadas, contrato encerrado

## Segurança

Roles por módulo: ADMIN, FINANCEIRO, COMERCIAL, SUPRIMENTOS, OBRAS, RH, ATENDIMENTO

## Testes

39 testes unitários com Mockito cobrindo todos os services críticos.
