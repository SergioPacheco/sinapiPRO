# Spec: Implementação Completa dos Módulos

## Visão Geral

Cada módulo do SinapiPRO deve ter profundidade funcional completa.
O menu lateral mostra todos os sub-itens quando a obra está selecionada.

---

## Módulo 1: ORÇAMENTO ✅ (implementado)

### Sub-telas
- [x] Listar Orçamentos
- [x] Compor Orçamento (planilha)
- [x] BDI / Taxas
- [x] Cronograma Financeiro (backend)
- [x] Análise de Compras (backend)
- [x] Efetivar/Cancelar
- [x] Relatórios (Sintético, Analítico, ABC)
- [x] Copiar Orçamento (backend)
- [x] Backup (JSON export)

---

## Módulo 2: CONTRATOS

### Sub-telas a implementar
- [ ] Lista de Contratos (por obra)
- [ ] Cadastro de Contrato (fornecedor, valor, prazo, itens do orçamento)
- [ ] Medição do Contrato (período, itens medidos, qtd executada)
- [ ] Serviços da Medição (grid com itens, qtd contratada × anterior × atual × saldo)
- [ ] Reajuste (por índice: INCC, IGPM, CUB)
- [ ] Fiscais (vincular fiscais ao contrato)
- [ ] Vencimentos (parcelas, datas, valores)
- [ ] Aditivos (change orders)

### Endpoints necessários
- GET/POST /projects/{id}/contracts
- GET/PUT /contracts/{id}
- GET/POST /contracts/{id}/measurements
- GET/POST /contracts/{id}/items
- POST /contracts/{id}/adjustments
- GET/POST /contracts/{id}/installments

---

## Módulo 3: MEDIÇÕES

### Sub-telas a implementar
- [ ] Lista de Medições (por obra/contrato)
- [ ] Nova Medição (período, importar itens do contrato)
- [ ] Preenchimento (qtd executada por item, memória de cálculo)
- [ ] Workflow (DRAFT → SUBMITTED → APPROVED → PAID)
- [ ] Boletim de Medição (PDF)
- [ ] Medição por Empreiteiro
- [ ] Relatório Acumulado

### Endpoints existentes
- GET/POST /projects/{id}/measurements ✅
- PUT /measurements/{id}/approve ✅
- PUT /measurements/{id}/reject ✅

---

## Módulo 4: SUPRIMENTOS

### Sub-telas a implementar
- [ ] Gerar Requisição (itens do orçamento ou manual)
- [ ] Autorizar Requisição (workflow por alçada)
- [ ] Cotação (selecionar fornecedores, enviar)
- [ ] Resposta de Cotação (fornecedor responde preço/prazo)
- [ ] Análise de Cotação (mapa comparativo)
- [ ] Gerar Pedido de Compra
- [ ] Pedidos em Atraso
- [ ] Baixa de Pedidos (recebimento)
- [ ] Limite de Compra (por obra)
- [ ] Cronograma de Compras

### Endpoints existentes
- GET/POST /projects/{id}/procurement ✅
- GET/POST /procurement/quotations ✅

---

## Módulo 5: FINANCEIRO

### Sub-telas a implementar
- [ ] Contas a Pagar (despesas, NFs)
- [ ] Autorização de Pagamento
- [ ] Efetuar Pagamento
- [ ] Contas a Receber (medições aprovadas → faturamento)
- [ ] Movimento Bancário
- [ ] Emissão de Cheques
- [ ] Adiantamentos a Fornecedor
- [ ] Disponibilidade Financeira (saldo)
- [ ] Fluxo de Caixa

### Endpoints existentes
- GET/POST /payables ✅
- GET/POST /receivables ✅
- GET/POST /bank-accounts/{id}/transactions ✅

---

## Módulo 6: MÃO DE OBRA

### Sub-telas a implementar
- [ ] Apontamento de Horas (por funcionário/obra/etapa)
- [ ] Banco de Horas (crédito/débito)
- [ ] Encerrar Competência (fechar mês)
- [ ] Tabela de Preço MO
- [ ] Relatório de Produtividade

### Endpoints existentes
- GET/POST /projects/{id}/timesheets ✅
- GET/POST /projects/{id}/labor ✅

---

## Módulo 7: DIÁRIO DE OBRA

### Sub-telas a implementar
- [ ] Lista de Diários (por obra)
- [ ] Registro Diário (clima, equipe, atividades, materiais, fotos)
- [ ] Relatório RDO (PDF)

### Endpoints existentes
- GET/POST /projects/{id}/daily-logs ✅

---

## Módulo 8: CADASTROS

### Sub-telas a implementar
- [ ] Fornecedores (CRUD completo)
- [ ] Clientes (CRUD completo)
- [ ] Funcionários (CRUD completo)
- [ ] Bancos/Contas
- [ ] Centros de Custo
- [ ] Índices Econômicos (INCC, IGPM)

### Endpoints existentes
- GET/POST /suppliers ✅
- GET/POST /registry/clients ✅
- GET/POST /registry/employees ✅

---

## Módulo 9: COMERCIAL (Vendas Imobiliárias)

### Sub-telas a implementar
- [ ] Empreendimentos
- [ ] Unidades
- [ ] Tabela de Preços
- [ ] Contratos de Venda
- [ ] Parcelas / Cobrança
- [ ] Comissões
- [ ] Distrato/Cessão

### Endpoints existentes
- GET/POST /commercial ✅
- GET/POST /developments/{id}/sales ✅

---

## Prioridade de Implementação

| Sprint | Módulo | Justificativa |
|--------|--------|---------------|
| 1 | Contratos + Medições | Fluxo direto do orçamento |
| 2 | Suprimentos | Compras vinculadas ao orçamento |
| 3 | Financeiro | Pagamentos das medições/compras |
| 4 | Mão de Obra + Diário | Execução da obra |
| 5 | Cadastros (aprofundar) | Base para todos os módulos |
| 6 | Comercial | Módulo independente |

---

## Padrão de Implementação (por sub-tela)

1. Rota no `app.routes.ts`
2. Componente Angular standalone
3. Grid com p-table ou spreadsheet (conforme complexidade)
4. CRUD completo (listar, criar, editar, excluir)
5. Workflow quando aplicável (status + ações)
6. Relatório PDF quando aplicável
