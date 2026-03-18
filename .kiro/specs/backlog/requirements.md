# Backlog — Requisitos dos Docs vs. Implementação

Gerado em: 2026-03-18
Fonte: `/docs/requisitos/`, `/docs/levantamento-de-requisitos/sinapiPRO.md`

Legenda: ✅ Implementado | 🔶 Parcial | ❌ Não implementado

---

## 1. ORÇAMENTO (sinapiPRO.md + Requisitos.md)

| # | Requisito | Status | Tarefa |
|---|-----------|--------|--------|
| 1.1 | Cadastrar orçamento (CRUD) | ✅ | — |
| 1.2 | Situação: Estimativa / Venda / Execução | ❌ | Hoje só tem ABERTO/BLOQUEADO. Criar enum `TipoOrcamento` (ESTIMATIVA, VENDA, EXECUCAO), adicionar campo na entity, migration, tela |
| 1.3 | Orçamento de Estimativa → gerar Venda | ❌ | Fluxo de cópia: duplicar orçamento estimativa como venda |
| 1.4 | Orçamento de Venda → gerar Execução | ❌ | Fluxo de cópia: duplicar orçamento venda como execução |
| 1.5 | Comparativo Venda vs Execução | ❌ | Tela comparativa entre orçamento de venda e execução |
| 1.6 | Associar serviços ao orçamento | ✅ | Itens (composição/insumo/etapa) já funcionam |
| 1.7 | Curva ABC | ✅ | Implementado nesta sessão |
| 1.8 | Taxas em cascata (Leis Sociais → BDI → Taxa Adm) | 🔶 | Cálculo existe mas não é cascata conforme docs. Docs dizem: 100k × 12% = 112k × 30% = 145.6k × 10% = 160.16k. Código atual calcula separadamente |
| 1.9 | Leis Sociais só sobre Mão de Obra | 🔶 | `calculaValorLeisSociais()` aplica sobre `calculaValorMaoObra()` — correto. Mas falta validação na UI |

## 2. TIPO DE CUSTO (sinapiPRO.md)

| # | Requisito | Status | Tarefa |
|---|-----------|--------|--------|
| 2.1 | CRUD Tipo de Custo (código + descrição) | ❌ | Criar entity `TipoCusto`, repository, service, controller, tela |
| 2.2 | Agrupar itens do orçamento por tipo de custo (direto/indireto/administrativo) | ❌ | Adicionar campo `tipoCusto` em `Item`, filtro na tela do orçamento |

## 3. COMPOSIÇÃO / SERVIÇO (Requisitos.md + sinapiPRO.md)

| # | Requisito | Status | Tarefa |
|---|-----------|--------|--------|
| 3.1 | CRUD Composição | ✅ | — |
| 3.2 | Itens da composição (insumos + coeficientes) | ✅ | — |
| 3.3 | Classificação: Classe + Grupo | ✅ | ComposicaoClasse + ComposicaoGrupo |
| 3.4 | % de taxação na composição | ❌ | Campo `percentualTaxacao` na Composicao |
| 3.5 | % de tributação na composição | ❌ | Campo `percentualTributacao` na Composicao |
| 3.6 | % de perdas na composição | ❌ | Campo `percentualPerdas` na Composicao |
| 3.7 | % de bonificação (lucro) por serviço | ❌ | Campo `percentualBonificacao` no Item ou Composicao |
| 3.8 | Reutilizar composição em múltiplos orçamentos | ✅ | Composição é independente do orçamento |

## 4. INSUMO (Requisitos.md + sinapiPRO.md)

| # | Requisito | Status | Tarefa |
|---|-----------|--------|--------|
| 4.1 | CRUD Insumo | ✅ | — |
| 4.2 | Espécie (Material/Mão de Obra/Equipamento) | ✅ | Enum `Especie` |
| 4.3 | Origem do insumo (próprio/terceiro) | ❌ | Adicionar campo `origem` (enum PROPRIO/TERCEIRO) em Insumo |
| 4.4 | Associar insumo a fornecedor | ❌ | Criar entity `Fornecedor`, tabela associativa `fornecedor_insumo` com preço |
| 4.5 | Preço diferenciado por fornecedor e região | ❌ | Depende de 4.4 + região no preço |
| 4.6 | Tributos por insumo | ❌ | Criar entity `Tributo`, tabela associativa `tributo_insumo` |
| 4.7 | Equipamento: tipo interno/externo | ❌ | Adicionar campo `tipoEquipamento` (INTERNO/EXTERNO) em Insumo |
| 4.8 | Histórico de preços (popup) | ✅ | Endpoint + modal existem nas telas de insumos |

## 5. TRIBUTOS (sinapiPRO.md)

| # | Requisito | Status | Tarefa |
|---|-----------|--------|--------|
| 5.1 | CRUD Tributo (código, descrição, %, região) | ❌ | Criar entity `Tributo` (codigo, descricao, percentual, estado) |
| 5.2 | Associar tributos a insumos | ❌ | Tabela `tributo_insumo` |
| 5.3 | Associar tributos a serviços/composições | ❌ | Tabela `tributo_composicao` |
| 5.4 | Tributos variam por região | ❌ | Relacionamento Tributo → Estado |
| 5.5 | Cálculo de tributos no custo final | ❌ | Lógica de cálculo no service |

## 6. ETAPAS / AGRUPAMENTO (Telas.md + sinapiPRO.md)

| # | Requisito | Status | Tarefa |
|---|-----------|--------|--------|
| 6.1 | CRUD Etapas | ✅ | — |
| 6.2 | 4 níveis de etapas/subetapas (1. / 1.1. / 1.1.1. / 1.1.1.1.) | 🔶 | `Itemizar()` faz 2 níveis. Refatorar para suportar 4 níveis com hierarquia pai-filho |
| 6.3 | Grupo pode conter outro grupo | ❌ | Adicionar auto-referência em Etapa (etapaPai) |

## 7. RELATÓRIOS (Relatorios*.md)

| # | Requisito | Status | Tarefa |
|---|-----------|--------|--------|
| 7.1 | Relatório Composições — Sintético | ✅ | JasperReport COM00100 |
| 7.2 | Relatório Composições — Semi-Sintético | 🔶 | Existe no JasperReport mas filtro na tela pode estar incompleto |
| 7.3 | Relatório Composições — Analítico | 🔶 | Idem |
| 7.4 | Relatório Composições — filtros (ativo/inativo, período, preços, cadastro/importação) | 🔶 | Tela de filtro existe mas nem todos os filtros estão implementados |
| 7.5 | Relatório Orçamento — Sintético | ✅ | JasperReport ORC00100 |
| 7.6 | Relatório Orçamento — Analítico | ❌ | Novo JasperReport com itens expandidos |
| 7.7 | Relatório Orçamento — Global Material+MO | ❌ | Novo JasperReport agrupado por espécie |
| 7.8 | Relatório Orçamento — opções de impressão (cliente, extenso, leis/BDI, normas, zerados, base) | 🔶 | Algumas opções existem, outras não |
| 7.9 | Relatório Insumos do Orçamento | ✅ | JasperReport existente |
| 7.10 | Relatório Insumos — filtros (ordenar, agrupar etapa, totalizar espécie, omitir sem valor) | 🔶 | Filtros parciais |
| 7.11 | Relatório Serviços do Orçamento | ❌ | Novo JasperReport para serviços |
| 7.12 | Conversão de valores para índice/data | ❌ | Tela de conversão + lógica de cálculo |
| 7.13 | Exportação multi-formato (PDF, XLS, RTF, CSV, EMAIL) | 🔶 | PDF ✅, XLS ✅ (novo), RTF ❌, CSV ❌, EMAIL ❌ |
| 7.14 | Impressão por extenso do total | 🔶 | `Extenso.java` existe mas pode não estar integrado em todos os relatórios |

## 8. TELA RESUMO ORÇAMENTO (Telas.md)

| # | Requisito | Status | Tarefa |
|---|-----------|--------|--------|
| 8.1 | BDI detalhado: Insumo, Serviço, Terceiro, Ferramenta | ❌ | Hoje só tem BDI único. Criar 4 campos de BDI no Orcamento |
| 8.2 | Resumo por espécie (Material/MO/Equipamento) com Valor+BDI+TaxAdm+LeiSoc+Total | 🔶 | Cálculos existem no model mas tela de resumo pode estar incompleta |

## 9. FORNECEDORES (sinapiPRO.md)

| # | Requisito | Status | Tarefa |
|---|-----------|--------|--------|
| 9.1 | CRUD Fornecedor | ❌ | Criar entity, repository, service, controller, tela |
| 9.2 | Associar fornecedor a insumo (com preço) | ❌ | Tabela associativa com preço |
| 9.3 | Preço do insumo determinado pelo fornecedor | ❌ | Lógica de seleção de preço |

## 10. USUÁRIOS / SEGURANÇA (sinapiPRO.md)

| # | Requisito | Status | Tarefa |
|---|-----------|--------|--------|
| 10.1 | CRUD Usuário | ✅ | — |
| 10.2 | Grupos e Permissões | ✅ | — |
| 10.3 | Situação ativo/inativo | ✅ | Campo `ativo` em Usuario |
| 10.4 | Histórico de senhas (3 anteriores) | ❌ | Campos senha_anterior_1/2/3 ou tabela de histórico |
| 10.5 | Troca de senha no primeiro acesso | ❌ | Flag `primeiroAcesso` + redirect para troca |
| 10.6 | Registro de alterações (audit trail) | ❌ | Spring Data Auditing ou tabela de log |
| 10.7 | Data último acesso | ❌ | Campo `dataUltimoAcesso` em Usuario |
| 10.8 | Tipo de Usuário (CRUD) | ❌ | Hoje usa Grupo. Docs pedem TipoUsuario separado |

## 11. REGIÃO (sinapiPRO.md)

| # | Requisito | Status | Tarefa |
|---|-----------|--------|--------|
| 11.1 | CRUD Estado/Cidade | ✅ | — |
| 11.2 | Preços variam por região | ✅ | BasePreco vinculado a Estado |
| 11.3 | Tributos variam por região | ❌ | Depende de 5.4 |

## 12. CADASTROS AUXILIARES

| # | Requisito | Status | Tarefa |
|---|-----------|--------|--------|
| 12.1 | CRUD Tipo de Unidade | ❌ | Hoje unidade é String livre. Criar entity `TipoUnidade` |
| 12.2 | CRUD Espécie de Insumo | ❌ | Hoje é enum fixo. Docs pedem CRUD dinâmico |
| 12.3 | CRUD Obra | ✅ | — |
| 12.4 | CRUD Cliente | ✅ | — |
| 12.5 | Importação SINAPI | ✅ | — |

---

## Resumo quantitativo

| Status | Quantidade |
|--------|-----------|
| ✅ Implementado | 23 |
| 🔶 Parcial | 10 |
| ❌ Não implementado | 30 |
| **Total de requisitos** | **63** |

---

## Priorização sugerida (por valor de negócio + dependências)

### Sprint 1 — Fundação do orçamento (alto valor, base para tudo)
- [ ] **1.2** Tipos de orçamento (Estimativa/Venda/Execução)
- [ ] **1.8** Corrigir cálculo de taxas em cascata
- [ ] **6.2** 4 níveis de etapas/subetapas
- [ ] **6.3** Etapa pai-filho (auto-referência)

### Sprint 2 — Tributos e custos (requisito central do domínio)
- [ ] **5.1** CRUD Tributo
- [ ] **5.2** Associar tributos a insumos
- [ ] **5.3** Associar tributos a composições
- [ ] **5.5** Cálculo de tributos no custo final
- [ ] **2.1** CRUD Tipo de Custo
- [ ] **2.2** Agrupar itens por tipo de custo

### Sprint 3 — Composição avançada + BDI
- [ ] **3.4** % taxação na composição
- [ ] **3.5** % tributação na composição
- [ ] **3.6** % perdas na composição
- [ ] **3.7** % bonificação por serviço
- [ ] **8.1** BDI detalhado (Insumo/Serviço/Terceiro/Ferramenta)

### Sprint 4 — Fornecedores e insumos avançados
- [ ] **9.1** CRUD Fornecedor
- [ ] **9.2** Associar fornecedor a insumo
- [ ] **4.3** Origem do insumo (próprio/terceiro)
- [ ] **4.7** Equipamento interno/externo

### Sprint 5 — Relatórios avançados
- [ ] **7.6** Relatório Orçamento Analítico
- [ ] **7.7** Relatório Global Material+MO
- [ ] **7.11** Relatório Serviços do Orçamento
- [ ] **7.13** Exportação CSV + RTF
- [ ] **7.12** Conversão de valores por índice

### Sprint 6 — Fluxo de orçamento completo
- [ ] **1.3** Estimativa → Venda (cópia)
- [ ] **1.4** Venda → Execução (cópia)
- [ ] **1.5** Comparativo Venda vs Execução

### Sprint 7 — Segurança e auditoria
- [ ] **10.4** Histórico de senhas
- [ ] **10.5** Troca de senha no primeiro acesso
- [ ] **10.6** Audit trail
- [ ] **10.7** Data último acesso

### Sprint 8 — Cadastros auxiliares
- [ ] **12.1** CRUD Tipo de Unidade
- [ ] **12.2** CRUD Espécie de Insumo (dinâmico)
- [ ] **10.8** Tipo de Usuário
