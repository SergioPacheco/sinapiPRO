# Sprint 9 — Planejamento Físico-Financeiro — PROGRESS

## Status: ✅ COMPLETE (2026-03-29)

### Task 1: Migration V13 ✅
- `V13__criar_tabela_planejamento_item.sql`

### Task 2: Entity + Repository ✅
- `PlanejamentoItem.java` (orcamento, item, dataInicio, dataFim, percentualExecutado)
- `PlanejamentoItemRepository.java`

### Task 3: DTOs + Service ✅
- `CronogramaMes.java` (periodo, valorPlanejado, valorAcumulado, percentual)
- `PlanejamentoService.java` (buscar, salvar, calcularCronograma com distribuição linear)

### Task 4: Controller + Telas ✅
- `PlanejamentoController.java` (GET/POST /planejamento/{id}, GET /planejamento/{id}/cronograma)
- `Planejamento.html` — definir datas início/fim por item
- `Cronograma.html` — tabela cronograma + curva S visual com progress bars

### Task 5: Relatório FreeMarker ✅
- `cronograma-financeiro.ftl` — PDF com tabela + curva S
- Endpoint: `GET /relatorios/cronograma/{codigo}`

### Task 6: Menu ✅
- Adicionado "Planejamento Físico-Financeiro" no submenu Orçamentos

### BUILD: SUCCESS (241 source files)
