# Backlog — Requisitos do SinapiPRO

Atualizado em: 2026-03-18 (pós Sprint 1 + Sprint 2)
Fonte: `/docs/requisitos/`, `/docs/levantamento-de-requisitos/sinapiPRO.md`

Legenda: ✅ Implementado | 🔶 Parcial | ❌ Não implementado

---

## 1. ORÇAMENTO

| # | Requisito | Status |
|---|-----------|--------|
| 1.1 | CRUD orçamento | ✅ |
| 1.2 | TipoOrcamento (ESTIMATIVA/VENDA/EXECUCAO) | ✅ Sprint 1 |
| 1.3 | Estimativa → gerar Venda (cópia) | ❌ Sprint 6 |
| 1.4 | Venda → gerar Execução (cópia) | ❌ Sprint 6 |
| 1.5 | Comparativo Venda vs Execução | ❌ Sprint 6 |
| 1.6 | Associar serviços ao orçamento | ✅ |
| 1.7 | Curva ABC | ✅ |
| 1.8 | Taxas em cascata (LS → BDI → TaxAdm) | ✅ Sprint 1 |
| 1.9 | Leis Sociais só sobre Mão de Obra | ✅ Sprint 1 |
| 1.10 | Export JSON REST API | ✅ |
| 1.11 | Export XLS (Apache POI) | ✅ |

## 2. TIPO DE CUSTO

| # | Requisito | Status |
|---|-----------|--------|
| 2.1 | CRUD Tipo de Custo | ✅ Sprint 2 |
| 2.2 | Agrupar itens por tipo de custo | ✅ Sprint 2 (campo em Item) |

## 3. COMPOSIÇÃO / SERVIÇO

| # | Requisito | Status |
|---|-----------|--------|
| 3.1 | CRUD Composição | ✅ |
| 3.2 | Itens da composição (insumos + coeficientes) | ✅ |
| 3.3 | Classificação: Classe + Grupo | ✅ |
| 3.4 | % taxação na composição | ❌ Sprint 3 |
| 3.5 | % tributação na composição | ❌ Sprint 3 |
| 3.6 | % perdas na composição | ❌ Sprint 3 |
| 3.7 | % bonificação (lucro) por serviço | ❌ Sprint 3 |
| 3.8 | Reutilizar composição em múltiplos orçamentos | ✅ |

## 4. INSUMO

| # | Requisito | Status |
|---|-----------|--------|
| 4.1 | CRUD Insumo | ✅ |
| 4.2 | Espécie (Material/MO/Equipamento) | ✅ |
| 4.3 | Origem do insumo (próprio/terceiro) | ❌ Sprint 4 |
| 4.4 | Associar insumo a fornecedor | ❌ Sprint 4 |
| 4.5 | Preço diferenciado por fornecedor e região | ❌ Sprint 4 |
| 4.6 | Tributos por insumo | ✅ Sprint 2 |
| 4.7 | Equipamento: tipo interno/externo | ❌ Sprint 4 |
| 4.8 | Histórico de preços (popup) | ✅ |

## 5. TRIBUTOS

| # | Requisito | Status |
|---|-----------|--------|
| 5.1 | CRUD Tributo | ✅ Sprint 2 |
| 5.2 | Associar tributos a insumos | ✅ Sprint 2 |
| 5.3 | Associar tributos a composições | ✅ Sprint 2 |
| 5.4 | Tributos variam por região | ✅ Sprint 2 (FK estado) |
| 5.5 | Cálculo de tributos no custo final | ❌ Sprint 3 |

## 6. ETAPAS / AGRUPAMENTO

| # | Requisito | Status |
|---|-----------|--------|
| 6.1 | CRUD Etapas | ✅ |
| 6.2 | 4 níveis de etapas (1. / 1.1. / 1.1.1. / 1.1.1.1.) | ✅ Sprint 1 |
| 6.3 | Etapa pai-filho (auto-referência) | ✅ Sprint 1 |

## 7. RELATÓRIOS

| # | Requisito | Status |
|---|-----------|--------|
| 7.1 | Relatório Composições — Sintético | ✅ |
| 7.2 | Relatório Composições — Semi-Sintético | 🔶 |
| 7.3 | Relatório Composições — Analítico | 🔶 |
| 7.4 | Relatório Composições — filtros avançados | 🔶 |
| 7.5 | Relatório Orçamento — Sintético | ✅ |
| 7.6 | Relatório Orçamento — Analítico | ❌ Sprint 5 |
| 7.7 | Relatório Global Material+MO | ❌ Sprint 5 |
| 7.8 | Relatório Orçamento — opções de impressão | 🔶 |
| 7.9 | Relatório Insumos do Orçamento | ✅ |
| 7.10 | Relatório Insumos — filtros avançados | 🔶 |
| 7.11 | Relatório Serviços do Orçamento | ❌ Sprint 5 |
| 7.12 | Conversão de valores para índice/data | ❌ Sprint 5 |
| 7.13 | Exportação multi-formato (PDF✅, XLS✅, RTF❌, CSV❌) | 🔶 Sprint 5 |
| 7.14 | Impressão por extenso do total | 🔶 |

## 8. TELA RESUMO ORÇAMENTO

| # | Requisito | Status |
|---|-----------|--------|
| 8.1 | BDI detalhado: Insumo/Serviço/Terceiro/Ferramenta | ❌ Sprint 3 |
| 8.2 | Resumo por espécie com todas as taxas | 🔶 |

## 9. FORNECEDORES

| # | Requisito | Status |
|---|-----------|--------|
| 9.1 | CRUD Fornecedor | ❌ Sprint 4 |
| 9.2 | Associar fornecedor a insumo (com preço) | ❌ Sprint 4 |
| 9.3 | Preço do insumo determinado pelo fornecedor | ❌ Sprint 4 |

## 10. USUÁRIOS / SEGURANÇA

| # | Requisito | Status |
|---|-----------|--------|
| 10.1 | CRUD Usuário | ✅ |
| 10.2 | Grupos e Permissões | ✅ |
| 10.3 | Situação ativo/inativo | ✅ |
| 10.4 | Histórico de senhas (3 anteriores) | ❌ Sprint 7 |
| 10.5 | Troca de senha no primeiro acesso | ❌ Sprint 7 |
| 10.6 | Audit trail (registro de alterações) | ❌ Sprint 7 |
| 10.7 | Data último acesso | ❌ Sprint 7 |
| 10.8 | Tipo de Usuário (CRUD) | ❌ Sprint 8 |

## 11. REGIÃO

| # | Requisito | Status |
|---|-----------|--------|
| 11.1 | CRUD Estado/Cidade | ✅ |
| 11.2 | Preços variam por região | ✅ |
| 11.3 | Tributos variam por região | ✅ Sprint 2 |

## 12. CADASTROS AUXILIARES

| # | Requisito | Status |
|---|-----------|--------|
| 12.1 | CRUD Tipo de Unidade | ❌ Sprint 8 |
| 12.2 | CRUD Espécie de Insumo (dinâmico) | ❌ Sprint 8 |
| 12.3 | CRUD Obra | ✅ |
| 12.4 | CRUD Cliente | ✅ |
| 12.5 | Importação SINAPI | ✅ |

---

## Resumo quantitativo

| Status | Qtd |
|--------|-----|
| ✅ Implementado | 37 |
| 🔶 Parcial | 7 |
| ❌ Não implementado | 19 |
| **Total** | **63** |

---

## Sprints restantes

### Sprint 3 — Composição avançada + BDI detalhado
- 3.4 % taxação na composição
- 3.5 % tributação na composição
- 3.6 % perdas na composição
- 3.7 % bonificação por serviço
- 5.5 Cálculo de tributos no custo final
- 8.1 BDI detalhado (4 campos)

### Sprint 4 — Fornecedores e insumos avançados
- 9.1 CRUD Fornecedor
- 9.2 Associar fornecedor a insumo
- 9.3 Preço do insumo por fornecedor
- 4.3 Origem do insumo (próprio/terceiro)
- 4.7 Equipamento interno/externo

### Sprint 5 — Relatórios avançados
- 7.6 Relatório Orçamento Analítico
- 7.7 Relatório Global Material+MO
- 7.11 Relatório Serviços do Orçamento
- 7.13 Exportação CSV + RTF
- 7.12 Conversão de valores por índice

### Sprint 6 — Fluxo Estimativa→Venda→Execução
- 1.3 Estimativa → Venda (cópia)
- 1.4 Venda → Execução (cópia)
- 1.5 Comparativo Venda vs Execução

### Sprint 7 — Segurança e auditoria
- 10.4 Histórico de senhas
- 10.5 Troca de senha no primeiro acesso
- 10.6 Audit trail
- 10.7 Data último acesso

### Sprint 8 — Cadastros auxiliares
- 12.1 CRUD Tipo de Unidade
- 12.2 CRUD Espécie de Insumo (dinâmico)
- 10.8 Tipo de Usuário
