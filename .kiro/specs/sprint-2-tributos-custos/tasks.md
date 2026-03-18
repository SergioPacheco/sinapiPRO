# Tasks — Sprint 2: Tributos e Custos

## Feedback loops
```bash
./mvnw compile -s ~/.m2/settings-local.xml
./mvnw test -s ~/.m2/settings-local.xml
```

## Task list

### Task 1: Migration V08 — tabelas tipo_custo, tributo, associativas, item.tipo_custo
- **Files:** `src/main/resources/db/migration/V08__criar_tabelas_tributo_tipo_custo.sql`
- **Done criteria:**
  - [ ] Migration SQL válido
  - [ ] Feedback loops pass
- **Commit:** `migration: add tipo_custo, tributo, tributo_insumo, tributo_composicao tables`

### Task 2: CRUD TipoCusto — entity + repo + service + controller + views
- **Files:** `TipoCusto.java`, `TipoCustosRepository.java`, `TipoCustoFilter.java`, `TipoCustosRepositoryQueries.java`, `TipoCustosRepositoryImpl.java`, `TipoCustoService.java`, `TipoCustosController.java`, `CadastroTipoCusto.html`, `PesquisaTipoCustos.html`
- **Done criteria:**
  - [ ] CRUD completo funcional
  - [ ] Segue padrão Etapa
  - [ ] Feedback loops pass
- **Commit:** `feat(tipo-custo): full CRUD — entity, repo, service, controller, views`

### Task 3: CRUD Tributo — entity + repo + service + controller + views
- **Files:** `Tributo.java`, `TributosRepository.java`, `TributoFilter.java`, `TributosRepositoryQueries.java`, `TributosRepositoryImpl.java`, `TributoService.java`, `TributosController.java`, `CadastroTributo.html`, `PesquisaTributos.html`
- **Done criteria:**
  - [ ] CRUD completo com campo percentual e estado
  - [ ] Feedback loops pass
- **Commit:** `feat(tributo): full CRUD — entity, repo, service, controller, views`

### Task 4: Tributo ↔ Insumo ManyToMany
- **Files:** `Insumo.java`, `Tributo.java`
- **Done criteria:**
  - [ ] @ManyToMany mapeado em ambas entities
  - [ ] Feedback loops pass
- **Commit:** `feat(tributo): ManyToMany Tributo ↔ Insumo`

### Task 5: Tributo ↔ Composicao ManyToMany
- **Files:** `Composicao.java`, `Tributo.java`
- **Done criteria:**
  - [ ] @ManyToMany mapeado em ambas entities
  - [ ] Feedback loops pass
- **Commit:** `feat(tributo): ManyToMany Tributo ↔ Composicao`

### Task 6: Item.tipoCusto — campo + combo na tela
- **Files:** `Item.java`, template do orçamento atual
- **Done criteria:**
  - [ ] Campo @ManyToOne tipoCusto em Item
  - [ ] Combo na tela de edição de item
  - [ ] Feedback loops pass
- **Commit:** `feat(item): add tipoCusto field and UI combo`

## Execution order
1 → 2 → 3 → 4 → 5 → 6 (sequencial, cada um depende do anterior)
