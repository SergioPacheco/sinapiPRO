# SinapiPRO — Arquitetura Frontend SPA

> Princípio: **Mínimo de páginas, máximo de fluxo.**
> O SinapiPRO resolve com **12 páginas** + modais/drawers/steps inline.

---

## Filosofia UX

```
✅ SinapiPRO: 1 página por MÓDULO, fluxo inteiro inline

O usuário NUNCA sai da página para completar um processo.
Ações são: modais, drawers laterais, steppers inline, tabs.
```

---

## Mapa de Páginas (12 rotas principais)

| # | Rota | Módulo | Fluxos que resolve |
|---|------|--------|-------------------|
| 1 | `/obras` | Obras | Lista + CRUD obra (modal) |
| 2 | `/obras/:id` | Obra Detail | **Tudo da obra** via tabs (orçamento, cronograma, medições, diário, suprimentos, financeiro, equipes, documentos) |
| 3 | `/orcamentos/:id` | Orçamento | Composição + digitação rápida + efetivação + cronograma financeiro + análise compras (tudo inline) |
| 4 | `/financeiro` | Financeiro | Contas pagar/receber + movimentação + conciliação (tabs + filtros) |
| 5 | `/suprimentos` | Suprimentos | Requisição → cotação → pedido → recebimento (stepper + kanban) |
| 6 | `/comercial` | Vendas | Empreendimentos → unidades → contratos → parcelas (master-detail) |
| 7 | `/mao-de-obra` | MO | Competência + apontamento + banco horas (tabs por período) |
| 8 | `/cadastros` | Cadastros | Hub com modais (clientes, fornecedores, funcionários, etc.) |
| 9 | `/relatorios` | Relatórios | Catálogo de relatórios com filtros + preview |
| 10 | `/dashboard` | Analytics | Dashboard executivo (ECharts) |
| 11 | `/configuracoes` | Config | Parâmetros, usuários, permissões |
| 12 | `/atendimento` | OS | Kanban de tickets + ficha lateral |

---

## Detalhamento por Fluxo

### 1. Contas a Pagar — PÁGINA ÚNICA `/financeiro` (tab "Pagar")

```
┌─────────────────────────────────────────────────────────────────┐
│ [Pagar] [Receber] [Banco] [Conciliação] [Cheques]               │
├─────────────────────────────────────────────────────────────────┤
│ Filtros: [Obra ▼] [Status ▼] [Período ▼] [Fornecedor ▼]         │
├─────────────────────────────────────────────────────────────────┤
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ Tabela de despesas (TanStack Table)                         │ │
│ │ NF 001 | Fornecedor X | R$ 5.000 | 3 parcelas | ABERTA      │ │
│ │ NF 002 | Fornecedor Y | R$ 12.000 | 1 parcela | AUTORIZADA  │ │
│ └─────────────────────────────────────────────────────────────┘ │
│                                                                 │
│ [+ Nova Despesa]  [Autorizar Selecionadas]  [Pagar Selecionadas]│
└─────────────────────────────────────────────────────────────────┘

Ações (todas via DRAWER lateral, sem sair da página):
• "+ Nova Despesa" → Drawer com form (NF + fornecedor + valor + parcelas)
  └── Ao salvar: calcula retenções automaticamente, gera parcelas
• "Autorizar" → Modal de confirmação (batch)
• "Pagar" → Drawer com: parcela, banco, método, juros/multa/desconto
  └── Ao confirmar: gera mov. bancária automaticamente
• Click na linha → Drawer de detalhe (parcelas + retenções + histórico)
```

**SinapiPRO: 1 tab + drawers.**

---

### 2. Contas a Receber — MESMA PÁGINA `/financeiro` (tab "Receber")

```
Mesmo layout, com ações específicas:
• "Gerar Parcelas" → Drawer (Price/SAC, taxa, primeira data)
• "Emitir Boleto" → Ação inline (gera PDF, mostra link)
• "Baixar" → Drawer (valor recebido, juros, multa, desconto)
• "Importar CNAB" → Modal upload (processa retorno, baixa automática)
```

---

### 3. Suprimentos — PÁGINA ÚNICA `/suprimentos` (Kanban + Stepper)

```
┌─────────────────────────────────────────────────────────────────┐
│ [Kanban] [Lista] [Cronograma]                                   │
├─────────────────────────────────────────────────────────────────┤
│ REQUISIÇÃO    │ COTAÇÃO      │ PEDIDO       │ RECEBIMENTO       │
│ ┌───────────┐ │ ┌──────────┐ │ ┌──────────┐ │ ┌──────────────┐  │
│ │ REQ-001   │ │ │ COT-001  │ │ │ PED-001  │ │ │ REC-001      │  │
│ │ Cimento   │ │ │ 3 fornec │ │ │ Forn. X  │ │ │ NF 12345     │  │
│ │ [Cotar →] │ │ │[Analisar]│ │ │[Receber] │ │ │ ✓ Completo   │  │
│ └───────────┘ │ └──────────┘ │ └──────────┘ │ └──────────────┘  │
│ ┌───────────┐ │              │              │                   │
│ │ REQ-002   │ │              │              │                   │
│ └───────────┘ │              │              │                   │
└─────────────────────────────────────────────────────────────────┘

Click no card → Drawer lateral com TODO o fluxo:
• Requisição: itens, quantidades, obra, [Autorizar] [Enviar p/ Cotação]
• Cotação: fornecedores convidados, respostas, mapa comparativo inline
• Pedido: itens, preços, [Aprovar] [Enviar por email]
• Recebimento: conferência item a item, [Receber Parcial] [Receber Total]
```

**SinapiPRO: 1 página com kanban + drawer.**

---

### 4. Vendas Imobiliárias — PÁGINA ÚNICA `/comercial` (Master-Detail)

```
┌─────────────────────────────────────────────────────────────────┐
│ Empreendimento: [Residencial Aurora ▼]                          │
├──────────────────────┬──────────────────────────────────────────┤
│ UNIDADES             │ DETALHE DA UNIDADE / CONTRATO            │
│ ┌──────────────────┐ │ ┌──────────────────────────────────────┐ │
│ │ Apto 101 - VAGO  │ │ │ Apto 201 — VENDIDO                   │ │
│ │ Apto 102 - VAGO  │ │ │                                      │ │
│ │ Apto 201 - VEND. │◀│ │ Comprador: João Silva                │ │
│ │ Apto 202 - RESER │ │ │ Contrato: CV-2024-001                │ │
│ │ Apto 301 - VAGO  │ │ │ Valor: R$ 450.000                    │ │
│ └──────────────────┘ │ │ Entrada: R$ 90.000                   │ │
│                      │ │ Parcelas: 60x Price (INCC)           │ │
│ [+ Vender]           │ │                                      │ │
│ [Mapa Visual]        │ │ [Parcelas] [Reajustar] [Distrato]    │ │
│                      │ │ [Cessão] [Comissão] [Imprimir]       │ │
│                      │ └──────────────────────────────────────┘ │
└──────────────────────┴──────────────────────────────────────────┘

"+ Vender" → Stepper inline (3 steps):
  Step 1: Selecionar unidade + comprador
  Step 2: Condições (Price/SAC, entrada, parcelas, índice)
  Step 3: Revisão + [Gerar Contrato]

"Parcelas" → Tabela inline com ações: [Receber] [Reajustar] [Renegociar]
"Distrato" → Modal com cálculo de multa + valor a devolver
```

**SinapiPRO: 1 página master-detail.**

---

### 5. Mão de Obra — PÁGINA ÚNICA `/mao-de-obra` (Tabs por competência)

```
┌─────────────────────────────────────────────────────────────────┐
│ Competência: [Mai/2026 ▼] [ABERTA]  Obra: [Todas ▼]             │
├─────────────────────────────────────────────────────────────────┤
│ [Apontamento] [Banco Horas] [Folha Resumo] [Tabela Preços]      │
├─────────────────────────────────────────────────────────────────┤
│ Funcionário    │ Normal │ HE50% │ HE100% │ Noturna │ Total      │
│ João Silva     │  176h  │  12h  │   4h   │   8h    │ 200h       │
│ Maria Santos   │  160h  │   8h  │   0h   │   0h    │ 168h       │
│ [+ Lançar]     │        │       │        │         │            │
└─────────────────────────────────────────────────────────────────┘

"+ Lançar" → Drawer: funcionário, data, tipo hora, horas, obra/etapa
"Fechar Competência" → Modal confirmação (impede novos lançamentos)
Tab "Banco Horas" → Saldo por funcionário + lançamentos crédito/débito
```

**SinapiPRO: 1 página com tabs.**

---

### 6. Orçamento — PÁGINA ÚNICA `/orcamentos/:id` (Tudo inline)

```
┌─────────────────────────────────────────────────────────────────┐
│ Orçamento: ORC-2024-001 | Status: RASCUNHO | R$ 2.450.000       │
├─────────────────────────────────────────────────────────────────┤
│ [Composição] [Cronograma $] [Análise Compras] [BDI] [Relatórios]│
├─────────────────────────────────────────────────────────────────┤
│ ETAPAS (tree)          │ ITENS DA ETAPA                         │
│ ▼ 01. Serviços Prelim. │ Código  │ Descrição    │ Qtd │ Total   │
│   ▼ 01.01 Limpeza      │ 73948/2 │ Limpeza terr │ 500 │ 12.500  │
│   ▼ 01.02 Tapume       │ 74209/1 │ Tapume madei │ 120 │ 8.400   │
│ ▼ 02. Infraestrutura   │         │              │     │         │
│ ▼ 03. Superestrutura   │ [+ Item] [Digitação Rápida] [Importar] │
└─────────────────────────────────────────────────────────────────┘

"+ Item" → Drawer: busca composição SINAPI + quantidade + BDI
"Digitação Rápida" → Modal tabular (cola do Excel, batch insert)
"Efetivar" → Botão no header (RASCUNHO → EM EXECUÇÃO, com confirmação)
Tab "Cronograma $" → Distribuição por mês (editable grid)
Tab "Análise Compras" → Orçado × Comprado × Saldo (read-only)
```

**SinapiPRO: 1 página com tree + tabs.**

---

## Componentes Reutilizáveis (Design System)

| Componente | Uso | Implementação |
|---|---|---|
| **Drawer** | Detalhe/edição sem sair da página | Panel lateral 400-600px, overlay |
| **Stepper** | Fluxos multi-step (venda, cotação) | Steps horizontais com validação |
| **Kanban** | Workflow visual (suprimentos, OS) | Colunas drag-and-drop |
| **Master-Detail** | Lista + detalhe (comercial) | Split panel responsivo |
| **Editable Grid** | Digitação rápida, cronograma | TanStack Table + inline edit |
| **Modal** | Confirmações, ações destrutivas | Dialog centered, backdrop |
| **Tab Panel** | Sub-módulos dentro da página | Lazy-loaded tabs |
| **Tree View** | Etapas do orçamento, plano contas | Expandable/collapsible |
| **Lookup** | Busca de entidade (fornecedor, composição) | Autocomplete + modal de busca |
| **Timeline** | Histórico de aprovações, medições | Vertical timeline component |

---

## Resumo: Eficiência de Navegação

| Fluxo | Páginas SinapiPRO | Técnica |
|---|:---:|---|
| Contas a Pagar | **1 tab** | Drawer para ações |
| Contas a Receber | **1 tab** | Drawer + upload CNAB |
| Suprimentos | **1 página** | Kanban + drawer |
| Vendas Imobiliárias | **1 página** | Master-detail + stepper |
| Mão de Obra | **1 página** | Tabs por competência |
| Orçamento | **1 página** | Tree + tabs + grid |
| **TOTAL** | **12 páginas** | SPA com drawers/modais |

---

## Regras de UX

1. **Zero navegação para completar um fluxo** — tudo acontece na mesma página
2. **Drawer > Modal** — drawer mantém contexto visual, modal é só para confirmação
3. **Ações em batch** — selecionar múltiplos itens e agir (autorizar, pagar, receber)
4. **Feedback imediato** — toast notifications, não redirect para página de sucesso
5. **Lazy loading** — tabs carregam sob demanda, não tudo de uma vez
6. **Keyboard shortcuts** — Ctrl+N (novo), Ctrl+S (salvar), Esc (fechar drawer)
7. **Filtros persistentes** — ao voltar para a lista, filtros mantidos
8. **URL reflete estado** — `/financeiro?tab=pagar&status=ABERTA&obra=uuid` (deep link)
