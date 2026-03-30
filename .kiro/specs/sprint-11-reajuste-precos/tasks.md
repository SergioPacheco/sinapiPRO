# Tasks — Sprint 11: Reajuste de Preços + Aplicação em Lote

## Feedback loops
```bash
./mvnw compile
```

## Task list

### Task 1: Migration V14 — tabelas baseline
- Criar `V14__criar_tabelas_baseline.sql` (orcamento_baseline + orcamento_baseline_item)
- **Commit:** `migration: add baseline tables`

### Task 2: Entities + Repository (Baseline)
- Criar `OrcamentoBaseline.java`, `OrcamentoBaselineItem.java`
- Criar `OrcamentoBaselineRepository.java`
- **Commit:** `feat(baseline): entities and repository`

### Task 3: DTOs + ReajusteService
- Criar `ReajustePreviewDTO.java`
- Criar `ReajusteService.java` (reajustarPercentual, reajustarValor, aplicarPrecoSinapi, previewReajuste)
- **Commit:** `feat(reajuste): service with percentage, value and SINAPI batch price update`

### Task 4: BaselineService
- Criar `BaselineComparativoDTO.java`
- Criar `BaselineService.java` (gravarBaseline, listarBaselines, compararBaseline)
- **Commit:** `feat(baseline): service with save, list and compare`

### Task 5: ReajusteController + tela Reajuste.html
- Criar `ReajusteController.java` (GET tela, POST percentual/valor/sinapi, GET preview)
- Criar `templates/reajuste/Reajuste.html` (3 abas: percentual, valor, SINAPI)
- **Commit:** `feat(reajuste): controller and view with tabs`

### Task 6: BaselineController + telas Baseline
- Criar `BaselineController.java` (GET listar, POST gravar, GET comparativo)
- Criar `templates/baseline/Baseline.html` + `BaselineComparativo.html`
- **Commit:** `feat(baseline): controller and views`

### Task 7: DigitacaoRapidaController + tela
- Criar `DigitacaoRapidaController.java` (GET tela, POST adicionar item AJAX)
- Criar `templates/orcamento/DigitacaoRapida.html`
- **Commit:** `feat(orcamento): quick item entry controller and view`

### Task 8: Menu + compilar
- Adicionar links "Reajuste de Preços", "Baseline", "Digitação Rápida" no menu lateral
- Verificar compilação
- **Commit:** `feat(orcamento): add price adjustment and baseline links to menu`
