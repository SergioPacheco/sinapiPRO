# Tasks — Sprint 3: Composição Avançada + BDI Detalhado

## Feedback loops
```bash
./mvnw compile -s ~/.m2/settings-local.xml
./mvnw test -s ~/.m2/settings-local.xml
```

## Task list

### Task 1: Migration V09 — campos percentuais na composição + BDI detalhado no orçamento
- **Files:** `V09__adicionar_percentuais_composicao_bdi_detalhado.sql`
- **Done criteria:**
  - [ ] Composicao: percentual_taxacao, percentual_tributacao, percentual_perdas, percentual_bonificacao
  - [ ] Orcamento: percentual_bdi_insumo, percentual_bdi_servico, percentual_bdi_terceiro, percentual_bdi_ferramenta
  - [ ] Feedback loops pass
- **Commit:** `migration: add composicao percentuais and orcamento BDI detalhado`

### Task 2: Composicao — campos percentuais + getters/setters + tela
- **Files:** `Composicao.java`, `CadastroComposicao.html`
- **Done criteria:**
  - [ ] 4 campos BigDecimal na entity
  - [ ] Campos na tela de cadastro
  - [ ] Feedback loops pass
- **Commit:** `feat(composicao): add percentual taxacao/tributacao/perdas/bonificacao`

### Task 3: Orcamento — BDI detalhado (4 campos) + tela
- **Files:** `Orcamento.java`, `CadastroOrcamento.html`
- **Done criteria:**
  - [ ] 4 campos BDI na entity (insumo/servico/terceiro/ferramenta)
  - [ ] Campos na tela de cadastro
  - [ ] Feedback loops pass
- **Commit:** `feat(orcamento): add BDI detalhado — insumo/servico/terceiro/ferramenta`

### Task 4: Cálculo de tributos no custo final do item
- **Files:** `Orcamento.java` ou `OrcamentoService.java`
- **Done criteria:**
  - [ ] Soma percentuais dos tributos associados ao insumo/composição do item
  - [ ] Valor tributos = valorItem × soma(tributo.percentual) / 100
  - [ ] Método calculaValorTributos() no Orcamento
  - [ ] Feedback loops pass
- **Commit:** `feat(orcamento): calculate tributos in final cost`

## Execution order
1 → 2 → 3 → 4 (sequencial)
