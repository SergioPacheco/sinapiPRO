# Tasks — Sprint 4: Fornecedores e Insumos Avançados

## Feedback loops
```bash
./mvnw compile -s ~/.m2/settings-local.xml
./mvnw test -s ~/.m2/settings-local.xml
```

## Task list

### Task 1: Migration V10 — tabelas fornecedor, fornecedor_insumo, campos insumo
- **Files:** `V10__criar_tabelas_fornecedor.sql`
- **Commit:** `migration: add fornecedor, fornecedor_insumo tables and insumo fields`

### Task 2: CRUD Fornecedor — entity + repo + service + controller + views
- **Files:** Fornecedor.java, FornecedoresRepository, FornecedorFilter, FornecedoresRepositoryImpl, FornecedorService, FornecedoresController, CadastroFornecedor.html, PesquisaFornecedores.html
- **Commit:** `feat(fornecedor): full CRUD`

### Task 3: FornecedorInsumo — entity associativa com preço
- **Files:** FornecedorInsumo.java (entity com preço + data cotação)
- **Commit:** `feat(fornecedor): FornecedorInsumo association with price`

### Task 4: Insumo.origem (PROPRIO/TERCEIRO) + Insumo.tipoEquipamento (INTERNO/EXTERNO)
- **Files:** OrigemInsumo.java (enum), TipoEquipamento.java (enum), Insumo.java
- **Commit:** `feat(insumo): add origem and tipoEquipamento enums`
