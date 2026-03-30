# Requirements — Sprint 9: Planejamento Físico-Financeiro

## User Stories

### US-1: Como engenheiro, quero distribuir os itens do orçamento no tempo para planejar a execução da obra
- Definir data início e data fim para cada item/etapa do orçamento
- Definir percentual de execução por período
- Visualizar o cronograma resultante

### US-2: Como gestor, quero ver o cronograma financeiro para saber quanto gastar por mês
- Relatório mostrando distribuição de custos por mês
- Totalização por etapa e geral

### US-3: Como gestor, quero ver a Curva S para acompanhar o avanço acumulado
- Gráfico/tabela com valores acumulados planejados vs período
- Percentual acumulado

## Acceptance Criteria
- [ ] Tela para definir datas de início/fim por item do orçamento
- [ ] Cálculo automático de distribuição linear de custos
- [ ] Relatório cronograma financeiro (PDF via FreeMarker)
- [ ] Relatório curva S (PDF via FreeMarker)
- [ ] Dados persistidos em tabela planejamento_item
