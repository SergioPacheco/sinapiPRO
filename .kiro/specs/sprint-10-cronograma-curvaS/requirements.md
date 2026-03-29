# Requirements — Sprint 10: Cronograma Financeiro + Curva S

## Contexto
Sprint 9 criou a base: PlanejamentoItem, PlanejamentoService.calcularCronograma(), cronograma-financeiro.ftl (tabela + barras).
Sprint 10 expande com relatórios dedicados e cronograma interativo.

## User Stories

### US-1: Como gestor, quero um relatório de Curva S dedicado para apresentar o avanço planejado da obra
- PDF separado com tabela de valores acumulados + gráfico de barras horizontais
- Mostrar linha de referência 100% e marcos intermediários (25%, 50%, 75%)
- Incluir total geral do orçamento como referência

### US-2: Como engenheiro, quero um relatório de Planejamento Físico para ver a distribuição de itens no tempo
- PDF com tabela: item, descrição, data início, data fim, duração (meses), valor, % do total
- Agrupado por etapa
- Totalização por etapa e geral

### US-3: Como gestor, quero uma tela de cronograma interativo para visualizar o planejamento de forma gráfica
- Tela Thymeleaf com tabela tipo Gantt simplificado (barras por mês)
- Linhas = itens do orçamento, colunas = meses
- Células preenchidas indicam período de execução
- Totais por mês na última linha

## Origem no Strato
- `orcamento/impressaoorcamento/_RelatorioCurvaS.java`
- `orcamento/impressaoorcamento/_RelatorioPlanejamentoFisico.java`
- `orcamento/impressaoorcamento/_RelatorioCronogramaFinanceiro.java` (já parcial)

## Acceptance Criteria
- [ ] Relatório Curva S em PDF (curva-s.ftl) com acumulado + marcos
- [ ] Relatório Planejamento Físico em PDF (planejamento-fisico.ftl) agrupado por etapa
- [ ] Tela CronogramaFinanceiro.html com visualização Gantt simplificada
- [ ] Endpoints no RelatoriosController para os novos relatórios
- [ ] Acessível via menu Orçamentos → submenu Relatórios
