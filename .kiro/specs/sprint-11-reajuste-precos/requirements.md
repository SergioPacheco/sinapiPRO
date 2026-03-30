# Requirements — Sprint 11: Reajuste de Preços + Aplicação em Lote

## Contexto
Orçamentos usam preços da base SINAPI (BasePrecoItem). Atualmente os preços são definidos manualmente por item. Sprint 11 adiciona operações em lote para atualizar preços e um mecanismo de baseline para comparação.

## User Stories

### US-1: Como orçamentista, quero reajustar preços do orçamento por percentual ou valor para refletir variações de mercado
- Aplicar reajuste percentual (ex: +5.3%) a todos os itens ou filtrado por espécie
- Aplicar reajuste por valor absoluto (ex: +R$10,00) a itens selecionados
- Visualizar preview antes de confirmar

### US-2: Como orçamentista, quero aplicar preços SINAPI em lote para atualizar o orçamento com a base de referência
- Selecionar base de preço (BasePreco) e mês de referência
- Atualizar valorUnitario de todos os itens que possuem insumo com preço na base
- Opção de aplicar preço onerado ou desonerado

### US-3: Como orçamentista, quero aplicar preços filtrados por classe/espécie para atualizar parcialmente
- Filtrar por Espécie (MAO_DE_OBRA, MATERIAL, EQUIPAMENTO)
- Aplicar preço SINAPI apenas aos itens filtrados

### US-4: Como orçamentista, quero digitação rápida de itens para agilizar a entrada de dados
- Tela simplificada: composição/insumo, quantidade, valor unitário
- Adicionar múltiplos itens sem recarregar a página
- Vinculação automática à etapa selecionada

### US-5: Como gestor, quero gravar um baseline do orçamento para comparar com versões futuras
- Salvar snapshot dos preços atuais (data, valores por item)
- Listar baselines gravados
- Comparar baseline vs valores atuais (diferença absoluta e %)

## Acceptance Criteria
- [ ] Reajuste percentual e por valor com preview
- [ ] Aplicação de preço SINAPI em lote (onerado/desonerado)
- [ ] Filtro por espécie na aplicação de preços
- [ ] Tela de digitação rápida de itens
- [ ] Gravação e listagem de baselines
- [ ] Comparativo baseline vs atual
- [ ] Migration V14 para tabela de baseline
