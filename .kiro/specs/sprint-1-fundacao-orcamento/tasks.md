# Tasks — Sprint 1: Fundação do Orçamento

## Feedback loops

Run these after every task. Do NOT commit if any fail.

```bash
./mvnw compile
./mvnw test
```

## Task list

### Task 1: Migration V06 — adicionar coluna tipo_orcamento
- **Priority:** high (migration primeiro, tudo depende disso)
- **Description:** Criar migration Flyway V06 que adiciona `tipo_orcamento VARCHAR(30) DEFAULT 'ESTIMATIVA'` na tabela `orcamento`. Dados existentes recebem ESTIMATIVA.
- **Files:** `src/main/resources/db/migration/V06__adicionar_tipo_orcamento.sql`
- **Done criteria:**
  - [ ] Migration roda sem erro
  - [ ] Dados existentes têm tipo_orcamento = 'ESTIMATIVA'
  - [ ] Feedback loops pass
- **Commit:** `migration(orcamento): add tipo_orcamento column`
- **Parallel:** yes (independente das tasks 4-6)
- **Risks:** Nenhum — aditiva
- **Dependencies:** nenhuma

### Task 2: Enum TipoOrcamento + campo no Orcamento
- **Priority:** high
- **Description:** Criar enum `TipoOrcamento` (ESTIMATIVA, VENDA, EXECUCAO) com descrição. Adicionar campo `tipoOrcamento` na entity `Orcamento` com `@Enumerated(EnumType.STRING)` e default ESTIMATIVA.
- **Files:** `src/main/java/.../model/TipoOrcamento.java`, `src/main/java/.../model/Orcamento.java`
- **Done criteria:**
  - [ ] Enum criado com 3 valores e descrições
  - [ ] Campo mapeado na entity com `@Column(name = "tipo_orcamento")`
  - [ ] Default = ESTIMATIVA
  - [ ] Feedback loops pass
- **Commit:** `feat(orcamento): add TipoOrcamento enum and field`
- **Parallel:** no
- **Risks:** Nenhum
- **Dependencies:** Task 1

### Task 3: Telas — combo tipo no cadastro + coluna na pesquisa
- **Priority:** medium
- **Description:** Adicionar combo `tipoOrcamento` na tela `CadastroOrcamento.html` (com `Desoneracao.values()` como referência de padrão). Adicionar coluna na `PesquisaOrcamentos.html`. Passar `TipoOrcamento.values()` no controller.
- **Files:** `CadastroOrcamento.html`, `PesquisaOrcamentos.html`, `OrcamentosController.java`
- **Done criteria:**
  - [ ] Combo aparece no cadastro com 3 opções
  - [ ] Coluna tipo visível na pesquisa
  - [ ] Salvar orçamento persiste o tipo
  - [ ] Feedback loops pass
- **Commit:** `feat(orcamento): add tipo selection in UI`
- **Parallel:** no
- **Risks:** Nenhum
- **Dependencies:** Task 2

### Task 4: Migration V07 — adicionar coluna etapa_pai
- **Priority:** high
- **Description:** Criar migration Flyway V07 que adiciona `codigo_etapa_pai BIGINT(20) NULL` com FK para `etapa(codigo)`.
- **Files:** `src/main/resources/db/migration/V07__adicionar_etapa_pai.sql`
- **Done criteria:**
  - [ ] Migration roda sem erro
  - [ ] Etapas existentes têm codigo_etapa_pai = NULL
  - [ ] FK constraint funciona
  - [ ] Feedback loops pass
- **Commit:** `migration(etapa): add codigo_etapa_pai for hierarchy`
- **Parallel:** yes (independente das tasks 1-3)
- **Risks:** Nenhum — aditiva
- **Dependencies:** nenhuma

### Task 5: Etapa — campo etapaPai + ajuste no controller
- **Priority:** high
- **Description:** Adicionar `@ManyToOne etapaPai` na entity `Etapa`. Ajustar `EtapasController` para passar lista de etapas como opções de pai. Ajustar tela de cadastro de etapa com combo opcional de etapa pai.
- **Files:** `Etapa.java`, `EtapasController.java`, template de etapa
- **Done criteria:**
  - [ ] Campo `etapaPai` mapeado com `@JoinColumn(name = "codigo_etapa_pai")`
  - [ ] Combo de etapa pai na tela (opcional, pode ser vazio = raiz)
  - [ ] Salvar etapa com pai funciona
  - [ ] Etapas sem pai continuam funcionando
  - [ ] Feedback loops pass
- **Commit:** `feat(etapa): add parent reference for hierarchy`
- **Parallel:** no
- **Risks:** Etapas existentes não devem quebrar (pai = null = raiz)
- **Dependencies:** Task 4

### Task 6: Refatorar Itemizar() para 4 níveis
- **Priority:** high (mais arriscado — sem testes)
- **Description:** Reescrever `Orcamento.Itemizar()` para gerar numeração hierárquica baseada na relação etapaPai. Suportar 4 níveis: 1. / 1.1. / 1.1.1. / 1.1.1.1. Itens dentro de cada etapa recebem subnumeração.
- **Files:** `Orcamento.java` (método `Itemizar()`)
- **Done criteria:**
  - [ ] Etapas raiz numeradas como 1., 2., 3.
  - [ ] Subetapas numeradas como 1.1., 1.2.
  - [ ] Itens dentro de etapa numerados como 1.1.1., 1.1.2.
  - [ ] Até 4 níveis de profundidade
  - [ ] Orçamentos existentes (sem hierarquia) continuam com numeração correta
  - [ ] Feedback loops pass
- **Commit:** `feat(orcamento): rewrite Itemizar() for 4-level hierarchy`
- **Parallel:** no
- **Risks:** 5/5 — sem testes, lógica complexa, afeta todos os orçamentos
- **Dependencies:** Task 5

### Task 7: Corrigir cálculo de taxas em cascata
- **Priority:** high (muda valores financeiros)
- **Description:** Corrigir `calculaValorLeisSociais()`, `calculaValorBDI()`, `calculaValorTaxaAdm()` e `calculaValorTotalComTaxas()` para aplicar taxas em cascata conforme docs: SubTotal → +LeisSociais → +BDI → +TaxaAdm. Adicionar null checks nos percentuais.
- **Files:** `Orcamento.java`
- **Done criteria:**
  - [ ] Exemplo dos docs: 100k × 12% LS = 112k × 30% BDI = 145.6k × 10% TaxAdm = 160.16k ✓
  - [ ] Leis Sociais aplicadas SOMENTE sobre mão de obra
  - [ ] BDI aplicado sobre (subtotal + leis sociais)
  - [ ] Taxa Adm aplicada sobre (subtotal + leis sociais + BDI)
  - [ ] Null checks: percentual null → BigDecimal.ZERO
  - [ ] Feedback loops pass
- **Commit:** `fix(orcamento): cascade tax calculation per business rules`
- **Parallel:** no
- **Risks:** 4/5 — muda valores financeiros de todos os orçamentos. Sem testes automatizados.
- **Dependencies:** nenhuma (pode rodar em paralelo com tasks 1-6)

## Execution order

Tasks ordenadas por risco (maior primeiro):
1. **Task 7** — Cálculo cascata (risco 4/5 — muda valores financeiros)
2. **Task 6** — Itemizar 4 níveis (risco 5/5 — sem testes, lógica complexa)
3. **Task 1** — Migration tipo_orcamento (base para 2 e 3)
4. **Task 4** — Migration etapa_pai (base para 5 e 6)
5. **Task 2** — Enum + campo TipoOrcamento
6. **Task 5** — Etapa pai + controller
7. **Task 3** — Telas tipo orçamento

## Notes
- Tasks 1-3 (tipo orçamento) e Tasks 4-6 (etapas) são independentes entre si
- Task 7 (cascata) é independente de tudo — pode ser feita primeiro
- Cada task = um commit atômico
- Testar manualmente com orçamento existente após tasks 6 e 7
