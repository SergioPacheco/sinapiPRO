# Requirements — Sprint 1: Fundação do Orçamento

## Summary
Adicionar tipos de orçamento (Estimativa/Venda/Execução), corrigir cálculo de taxas em cascata, e suportar 4 níveis de etapas hierárquicas.

## User stories

1. Como orçamentista, quero classificar meus orçamentos como Estimativa, Venda ou Execução, para controlar o ciclo de vida do projeto.
2. Como orçamentista, quero que as taxas (Leis Sociais → BDI → Taxa Adm) sejam calculadas em cascata, para que o valor total reflita a regra de negócio correta.
3. Como orçamentista, quero organizar etapas em até 4 níveis (1. / 1.1. / 1.1.1. / 1.1.1.1.), para representar a estrutura real de uma obra.

## Acceptance criteria

### Tipos de orçamento
- [ ] Enum `TipoOrcamento` com valores ESTIMATIVA, VENDA, EXECUCAO
- [ ] Campo `tipo_orcamento` na tabela `orcamento` (migration)
- [ ] Combo de seleção na tela de cadastro de orçamento
- [ ] Coluna visível na tela de pesquisa de orçamentos
- [ ] Dados existentes migrados como ESTIMATIVA (default)

### Taxas em cascata
- [ ] Cálculo segue a regra: SubTotal × LeisSociais% = A; A × BDI% = B; B × TaxaAdm% = Total
- [ ] Leis Sociais aplicadas SOMENTE sobre Mão de Obra
- [ ] BDI e Taxa Adm aplicados sobre o acumulado (cascata)
- [ ] Valores exibidos corretamente no resumo e nos relatórios

### Etapas hierárquicas (4 níveis)
- [ ] Etapa tem referência a `etapaPai` (auto-referência nullable)
- [ ] Migration adiciona coluna `codigo_etapa_pai` na tabela `etapa`
- [ ] `Itemizar()` gera numeração em 4 níveis (1. / 1.1. / 1.1.1. / 1.1.1.1.)
- [ ] Tela de cadastro de etapa permite selecionar etapa pai
- [ ] Etapas existentes continuam funcionando (sem pai = nível 1)

## Constraints
- Java 8 / Spring Boot 2.0.5 — sem APIs novas
- Migrations Flyway (V06, V07)
- Não quebrar orçamentos existentes
- Manter compatibilidade com relatórios JasperReports existentes

## Out of scope
- Fluxo de cópia Estimativa→Venda→Execução (Sprint 6)
- Comparativo entre orçamentos (Sprint 6)
- BDI detalhado por tipo (Sprint 3)

## Open questions
- Nenhuma — requisitos claros nos docs
