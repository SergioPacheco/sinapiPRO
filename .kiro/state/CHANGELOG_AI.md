# CHANGELOG_AI — SinapiPRO

Registro de mudanças assistidas por IA.

---

## [2026-03-30] Qualidade, Deploy e Documentação

- **fix**: 35 controllers com `@PostMapping` inválido corrigidos
- **feat**: `run.sh` — script de deploy local com criação automática do banco
- **feat**: `Dockerfile` + `docker-compose.yml`
- **feat**: V35 dados iniciais (admin, grupos, permissões)
- **feat**: V36 dados de demonstração (22 tabelas, dados realistas)
- **docs**: README.md completo, LICENSE MIT, CONTRIBUTING.md, SECURITY.md
- **docs**: limpeza do `/docs` (14 arquivos removidos)
- **feat**: `JobCostingService` — EVM completo (PV, EV, AC, CPI, SPI, EAC, VAC)
- **feat**: `ControllerAdviceExceptionHandler` expandido (409, 400, 500)
- **refactor**: imports inline removidos, 43 arquivos reformatados
- **feat**: `ValidacaoNegocioService` — validações de negócio centralizadas
- **feat**: `RelatorioOperacionalService` — inadimplência, posição de estoque
- **feat**: SecurityConfig — roles por módulo (FINANCEIRO, COMERCIAL, SUPRIMENTOS, OBRAS, RH)
- **feat**: V34 migration — grupos e permissões no banco

---

## [2026-03-29] Implementação dos Módulos (Fases 1–11)

### Fase 1 — Orçamento Avançado (Sprints 9–11)
- Planejamento Físico-Financeiro, Cronograma, Curva S
- Reajuste de Preços em lote, Baseline, Digitação Rápida

### Fase 2 — Cadastros Completos (Sprints 12–13)
- UnidadeMedida, DivisaoInsumo, SubDivisaoInsumo, Indice, FormaPagamento, TipoObra
- Empresa, Departamento, Cargo, Funcao, Funcionario, ClienteEndereco, ClienteReferencia

### Fase 3 — Operacional de Obra (Sprints 14–16)
- Diário de Obra (MO, Equipamentos, Ocorrências, Serviços)
- Contratos e Medições com retenção configurável
- Requisições de Insumos

### Fase 4 — Suprimentos (Sprints 17–19)
- Cotações com análise comparativa e geração automática de pedidos
- Pedidos de Compra com recebimento parcial/total → atualiza estoque
- Estoque com Custo Médio Ponderado (NBC TG 16)

### Fase 5 — Financeiro (Sprints 20–23)
- Plano de Contas hierárquico, Contas Bancárias
- Contas a Pagar/Receber com situação automática
- Movimento Bancário com atualização de saldo
- Conciliação Bancária

### Fase 6 — Comercial (Sprints 24–27)
- Espelho de Vendas, Propostas, Vendas com parcelas automáticas
- Reajuste por índice (INCC/IPCA/CUB)
- Tabela de Preços, Comissões
- Relatórios: Mapa de Vendas, Resumo por Corretor

### Fase 7 — Mão de Obra (Sprints 28–29)
- Banco de Horas com encerramento de competência (CLT Art. 59)
- Prestação de Contas

### Fase 8 — Financeiro Avançado (Sprints 30–32)
- Boletos, Cheques
- Relatórios FTL: Fluxo de Caixa, Balancete, DRE

### Fase 9 — Atendimento/CRM (Sprints 33–34)
- SLA por prioridade, escalação automática, notificações
- Ordens de Serviço

### Fase 10 — Faturamento/NF (Sprint 35)
- Nota Fiscal de Serviço com cálculo automático de ISS

### Fase 11 — Módulos de Apoio (Sprints 36–37)
- GED com upload real (validação OWASP)
- Frota com alertas de manutenção por KM e data
- Relatórios: inadimplência, posição de estoque
- Job Costing (EVM — PMBOK/NBR ISO 21500)

---

## [2026-03-29] Core (Sprints 1–8)

- Sprint 1: CRUD Orçamento, Etapas, Itens, BDI, Leis Sociais
- Sprint 2: Tributos, Tipo de Custo
- Sprint 3: Composição avançada (BDI detalhado, perdas, bonificação)
- Sprint 4: Fornecedores, FornecedorInsumo
- Sprint 5: Relatórios FreeMarker (PDF)
- Sprint 6: Fluxo Estimativa→Venda→Execução, Comparativo
- Sprint 7: Spring Security, BCrypt, Audit Trail, Histórico de Senhas
- Sprint 8: TipoUnidade, EspecieInsumo, TipoUsuario
