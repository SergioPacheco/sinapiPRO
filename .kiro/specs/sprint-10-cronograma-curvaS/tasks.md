# Tasks — Sprint 10: Cronograma Financeiro + Curva S

## Feedback loops
```bash
./mvnw compile
```

## Task list

### Task 1: PlanejamentoFisicoDTO + método montarPlanejamentoFisico no PlanejamentoService
- Criar `dto/PlanejamentoFisicoDTO.java`
- Adicionar `montarPlanejamentoFisico(Long codigoOrcamento)` em `PlanejamentoService`
- Agrupa itens por etapa, calcula duração e % do total
- **Commit:** `feat(planejamento): DTO and service method for physical planning report`

### Task 2: Relatório Curva S (PDF)
- Criar `templates/relatorio/ftl/curva-s.ftl` (tabela acumulada + barras com marcos 25/50/75/100%)
- Adicionar endpoint `GET /relatorios/curvaS/{codigo}` em `RelatoriosController`
- **Commit:** `feat(relatorios): curva S PDF report`

### Task 3: Relatório Planejamento Físico (PDF)
- Criar `templates/relatorio/ftl/planejamento-fisico.ftl` (agrupado por etapa)
- Adicionar endpoint `GET /relatorios/planejamentoFisico/{codigo}` em `RelatoriosController`
- **Commit:** `feat(relatorios): physical planning PDF report grouped by stage`

### Task 4: Tela Cronograma Gantt interativo
- Criar `templates/planejamento/CronogramaGantt.html` (tabela Gantt simplificada)
- Adicionar endpoint `GET /planejamento/{codigoOrcamento}/gantt` em `PlanejamentoController`
- **Commit:** `feat(planejamento): interactive Gantt chart view`

### Task 5: Menu + compilar
- Adicionar links "Curva S", "Planejamento Físico" e "Cronograma Gantt" no menu lateral
- Verificar compilação
- **Commit:** `feat(planejamento): add report links to sidebar menu`
