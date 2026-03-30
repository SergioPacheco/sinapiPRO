# CHANGELOG_AI — SinapiPRO

Registro de todas as mudanças assistidas por IA.

---

## [2026-03-30] Qualidade e Deploy

### Refatoração e Deploy
- **fix**: 35 controllers com `@PostMapping({"/novo", "{\\d+}"})` → `/{codigo}`
- **feat**: `run.sh` — script de deploy local com criação automática do banco
- **feat**: `Dockerfile` + `docker-compose.yml`
- **feat**: V35 dados iniciais (admin, grupos, permissões)
- **feat**: V36 dados de demonstração (22 tabelas, dados realistas)
- **docs**: README.md completo, LICENSE MIT, CONTRIBUTING.md, SECURITY.md
- **docs**: limpeza do `/docs` (14 arquivos removidos)

### Análise e Melhorias
- **feat**: `JobCostingService` — EVM completo (PV, EV, AC, CPI, SPI, EAC, VAC)
- **feat**: `ControllerAdviceExceptionHandler` expandido (409, 400, 500)
- **refactor**: imports inline removidos do RelatoriosController
- **refactor**: 43 arquivos reformatados (código compacto expandido)
- **feat**: `ValidacaoNegocioService` — unidade já vendida, parcelas duplicadas, contrato encerrado
- **feat**: `RelatorioOperacionalService` — inadimplência, posição de estoque
- **feat**: SecurityConfig — roles por módulo (FINANCEIRO, COMERCIAL, SUPRIMENTOS, OBRAS, RH)
- **feat**: V34 migration — grupos e permissões no banco

---

## [2026-03-29] Migração Strato → SinapiPRO — COMPLETA

### Fase 1 — Orçamento Avançado (Sprints 9–11)
- Sprint 9: Planejamento Físico-Financeiro (V13, PlanejamentoItem, PlanejamentoService)
- Sprint 10: Cronograma + Curva S (curva-s.ftl, planejamento-fisico.ftl, CronogramaGantt)
- Sprint 11: Reajuste de Preços + Baseline (ReajusteService, BaselineService, DigitacaoRapida)

### Fase 2 — Cadastros Completos (Sprints 12–13)
- Sprint 12: UnidadeMedida, DivisaoInsumo, SubDivisaoInsumo, Indice, FormaPagamento, TipoObra (V15)
- Sprint 13: Empresa, Departamento, Cargo, Funcao, Funcionario, ClienteEndereco, ClienteReferencia (V16)

### Fase 3 — Operacional de Obra (Sprints 14–16)
- Sprint 14: Diário de Obra (V17, 9 tabelas, AvancoFisicoService)
- Sprint 15: Contratos e Medições (V18, MedicaoContratoService com retenção)
- Sprint 16: Requisições de Insumos (V19)

### Fase 4 — Suprimentos (Sprints 17–19)
- Sprint 17: Cotações (V20, AnaliseCotacaoService — análise comparativa, geração de pedidos)
- Sprint 18: Pedidos de Compra (V21, BaixaPedidoService — recebimento atualiza estoque)
- Sprint 19: Estoque (V22, EstoqueService — Custo Médio Ponderado NBC TG 16)

### Fase 5 — Financeiro (Sprints 20–23)
- Sprint 20: Plano de Contas + Conta Bancária (V23)
- Sprint 21: Contas a Pagar (V24, situação automática)
- Sprint 22: Contas a Receber (V25, situação automática)
- Sprint 23: Movimento Bancário (V26, ConciliacaoBancariaService)

### Fase 6 — Comercial (Sprints 24–27)
- Sprint 24: Unidades de Venda (V27, espelho colorido)
- Sprint 25: Vendas (V28, VendaParcelasService — parcelas automáticas, reajuste por índice)
- Sprint 26: Tabela de Preços + Comissões (V29)
- Sprint 27: Relatórios Comerciais (mapa-vendas, resumo-corretor)

### Fase 7 — Mão de Obra (Sprints 28–29)
- Sprint 28: Banco de Horas (V30, EncerrarCompetenciaService — CLT Art. 59)
- Sprint 29: Prestação de Contas

### Fase 8 — Financeiro Avançado (Sprints 30–32)
- Sprint 30: Boletos (V31, BoletoService)
- Sprint 31: Cheques (ChequeService)
- Sprint 32: Relatórios FTL (fluxo-caixa, balancete, DRE)

### Fase 9 — Atendimento/CRM (Sprints 33–34)
- Sprint 33: Atendimento (V32, AtendimentoSlaService — SLA, escalação automática)
- Sprint 34: Ordens de Serviço + Notificações

### Fase 10 — Faturamento/NF (Sprint 35)
- Sprint 35: Nota Fiscal de Serviço (cálculo automático ISS)

### Fase 11 — Módulos de Apoio (Sprints 36–37)
- Sprint 36: GED (GedUploadService — OWASP) + Frota (AlertaManutencaoService)
- Sprint 37: Relatórios Gerais (inadimplência, posição estoque, Job Costing EVM)

### Refatoração Geral
- 289 arquivos reformatados para padrão IntelliJ
- 39 testes unitários (Mockito) para services críticos
- Global Exception Handler (@ControllerAdvice)

---

## [2026-03-29] Sprints 1–8 (pré-migração)

- Sprint 1: CRUD Orçamento, Etapas, Itens, BDI, Leis Sociais
- Sprint 2: Tributos, Tipo de Custo
- Sprint 3: Composição avançada (BDI detalhado, perdas, bonificação)
- Sprint 4: Fornecedores, FornecedorInsumo
- Sprint 5: Relatórios FreeMarker (migração de JasperReports)
- Sprint 6: Fluxo Estimativa→Venda→Execução, Comparativo
- Sprint 7: Spring Security, BCrypt, Audit Trail, Histórico de Senhas
- Sprint 8: TipoUnidade, EspecieInsumo, TipoUsuario
