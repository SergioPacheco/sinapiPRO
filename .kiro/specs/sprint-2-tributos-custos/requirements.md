# Requirements — Sprint 2: Tributos e Custos

## Summary
CRUD de Tributo e Tipo de Custo, associação de tributos a insumos/composições, agrupamento de itens por tipo de custo.

## User stories
1. Como orçamentista, quero cadastrar tributos (ISS, ICMS, etc.) com percentual e região, para calcular impostos nos orçamentos.
2. Como orçamentista, quero associar tributos a insumos e composições, para que o custo reflita a carga tributária real.
3. Como orçamentista, quero classificar itens do orçamento por tipo de custo (direto/indireto/administrativo), para análise gerencial.

## Acceptance criteria

### CRUD Tributo
- [ ] Entity Tributo (codigo, descricao, percentual, estado)
- [ ] Tela de cadastro e pesquisa
- [ ] Validação: nome obrigatório, percentual >= 0

### Associação tributos ↔ insumos/composições
- [ ] ManyToMany Tributo ↔ Insumo
- [ ] ManyToMany Tributo ↔ Composicao

### CRUD Tipo de Custo
- [ ] Entity TipoCusto (codigo, descricao)
- [ ] Tela de cadastro e pesquisa

### Agrupamento por tipo de custo
- [ ] Campo tipoCusto em Item
- [ ] Combo na tela do orçamento atual
