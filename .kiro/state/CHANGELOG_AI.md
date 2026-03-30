# AI Changelog

<!-- Log of AI-assisted changes with rationale and scope. -->

## Template

### [YYYY-MM-DD] Change title
- **Spec:** <!-- Which spec this relates to -->
- **Task:** <!-- Which task triggered this change -->
- **Files changed:** <!-- List of files -->
- **What changed:** <!-- Brief description -->
- **Why:** <!-- Rationale -->
- **Verified:** <!-- Yes/No — was this verified by the verifier agent or human? -->

---

### [2026-03-29] Sprint 8: CRUD Cadastros Auxiliares
- **Spec:** sprint-8-cadastros
- **Task:** Tasks 1-4 (Migration V12 + 3 CRUDs)
- **Files changed:** V12 migration, TipoUnidade (9 files), EspecieInsumo (9 files), TipoUsuario (9 files)
- **What changed:** Created 3 full CRUD modules following TipoCusto pattern: Model + Repository + Filter + RepositoryQueries + RepositoryImpl + Service + Controller + 2 Thymeleaf templates each
- **Why:** Backlog items 12.1, 12.2, 10.8 — cadastros auxiliares do Strato (TS1_TPUN, TS1_ESPE, TS1_USUA)
- **Verified:** Yes — BUILD SUCCESS (238 source files, 0 errors)

### [2026-03-29] Sprint 5: Relatórios Avançados (itens faltantes)
- **Spec:** sprint-5-relatorios
- **Task:** Tasks 1-5 (3 relatórios + CSV + RTF)
- **Files changed:** RelatoriosController.java, RelatorioOrcamentoAnalitico.html, RelatorioGlobalMaterialMO.html, RelatorioServicosOrcamento.html
- **What changed:** Added 3 Thymeleaf report views (Analítico, Global Material+MO, Serviços) + CSV/RTF export endpoints
- **Why:** Backlog items 7.6, 7.7, 7.11, 7.13 — relatórios avançados do Strato (ORC00200, impressão analítica)
- **Verified:** Yes — BUILD SUCCESS

### [2026-03-29] Sprint 6: Comparativo Venda vs Execução
- **Spec:** sprint-6-fluxo-orcamento
- **Task:** Task 4 (Comparativo)
- **Files changed:** OrcamentosController.java, OrcamentosRepository.java, ComparativoVendaExecucao.html
- **What changed:** Added comparativo endpoint + template showing side-by-side Venda vs Execução with differences and percentages
- **Why:** Backlog item 1.5 — comparativo orçado vs realizado
- **Verified:** Yes — BUILD SUCCESS

<!-- Add new entries above this line -->

### [2026-03-29] Migração Strato → SinapiPRO — COMPLETA (Fases 1-11)
- **Spec:** plano-migracao-completa.md
- **Task:** Sprints 9-37 (29 sprints)
- **Files changed:** ~350 novos arquivos Java + ~120 templates + 19 migrations Flyway (V14-V32)
- **What changed:** Implementação completa de todos os 11 módulos do Strato:
  Orçamento Avançado, Cadastros, Operacional, Suprimentos, Financeiro,
  Comercial, Mão de Obra, Financeiro Avançado, Atendimento/CRM, Faturamento, GED/Frota
- **Why:** Migração incremental do sistema legado Strato para SinapiPRO (Spring Boot + Thymeleaf)
- **Verified:** Yes — BUILD SUCCESS (498 source files, 0 errors)

### [2026-03-29] Refatoração: formatação IntelliJ standard
- **Spec:** coding-standards
- **Task:** Reformatação de 289 arquivos Java
- **Files changed:** 289 arquivos Java
- **What changed:** Métodos expandidos, anotações em linhas separadas, getters/setters expandidos,
  try-catch expandidos, parâmetros de setters renomeados para corresponder ao campo
- **Why:** Padronização do código para o formato IntelliJ
- **Verified:** Yes — BUILD SUCCESS (498 source files, 0 errors)
