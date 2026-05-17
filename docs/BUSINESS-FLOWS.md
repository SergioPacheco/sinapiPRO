# Arquitetura de Fluxos de Negócio — SinapiPRO

> Documento de referência para orquestração de processos ponta-a-ponta.
> Identifica o estado atual (CRUDs soltos), os fluxos ideais e as lacunas a resolver.

---

## Diagnóstico: O Problema Atual

O SinapiPRO tem **34 controllers** e **10 abas no workspace da obra**, mas funciona como uma coleção de CRUDs independentes. O usuário cadastra algo e não sabe para onde ir depois.

### Sintomas

| Sintoma | Exemplo |
|---------|---------|
| **Sem "next action"** | Crio uma obra → caio no resumo vazio → e agora? |
| **Sem dashboard de processo** | Não sei se a obra está no orçamento, contrato ou execução |
| **Abas sem sequência** | As 10 abas são iguais visualmente, sem indicar progresso |
| **Cadastros desconectados** | Cadastro fornecedor em Cadastros, mas na hora do contrato não encontro |
| **Sem workflow de status** | Medição não tem DRAFT→SUBMITTED→APPROVED visível |
| **Sem onboarding de obra** | Obra nova não guia o usuário pelos passos obrigatórios |

### Anti-padrões identificados

1. **Menu → Lista → Form → Salvar → Lista** (loop sem saída)
2. **Abas como menu** (10 abas = 10 mundos isolados)
3. **Sem breadcrumb de processo** (onde estou no ciclo da obra?)
4. **Sem indicadores de completude** (orçamento feito? contrato assinado?)

---

## Referências de Mercado

### Procore (líder mundial)
- **Fases claras**: Preconstruction → Construction → Financials → Closeout
- **Cada fase tem workflows**: tarefas sequenciais com responsáveis
- **Automação**: quando uma etapa termina, a próxima é ativada
- **Dashboard por fase**: mostra % completo, pendências, próximas ações

### Buildertrend
- **Wizard de nova obra**: guia passo-a-passo (dados → orçamento → cronograma)
- **Kanban de status**: obras em cards com status visual
- **Notificações de próxima ação**: "Orçamento aprovado → Gerar contrato"

### Padrões SaaS (Asana, Linear, HubSpot)
- **Multi-Step Wizard**: quebra processos complexos em etapas
- **Kanban Board**: visualização de status por colunas
- **Onboarding Progress**: checklist de setup com % completo
- **Inline Add/Create**: cadastro rápido sem sair do contexto (✅ já implementamos)
- **Command Palette (⌘K)**: navegação rápida por ação
- **Next Action**: após salvar, sugere o próximo passo

---

## Fluxo Macro do Negócio (Ideal)

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        CICLO DE VIDA DA OBRA                                     │
├──────────────┬──────────────┬──────────────┬──────────────┬──────────────────────┤
│ CAPTAÇÃO     │ PLANEJAMENTO │ EXECUÇÃO     │ ENCERRAMENTO │ PÓS-OBRA            │
│              │              │              │              │                      │
│ Lead         │ Orçamento    │ Diário       │ Punch List   │ Garantia             │
│ Proposta     │ Cronograma   │ Medições     │ Vistoria     │ Manutenção           │
│ Contrato     │ Suprimentos  │ Apontamento  │ Documentos   │ Tickets              │
│              │ Equipes      │ Financeiro   │ Entrega      │                      │
│              │              │ Segurança    │              │                      │
└──────────────┴──────────────┴──────────────┴──────────────┴──────────────────────┘
     ↓ auto          ↓ auto          ↓ auto          ↓ auto
  Obra criada    Obra PLANNING    Obra IN_PROGRESS  Obra COMPLETED
```

---

## Fluxos Detalhados por Fase

### Fase 1: Captação (Comercial → Obra)

```
Lead/Cliente ──→ Proposta Comercial ──→ Aprovação ──→ Contrato ──→ OBRA CRIADA
                                                                        │
                                                          ┌─────────────┘
                                                          ▼
                                                   Wizard "Nova Obra"
                                                   (dados + cliente + endereço)
```

**Estado atual**: ❌ Comercial existe mas não conecta com criação de obra
**Gap**: Não existe fluxo Proposta → Contrato → Obra automático
**Solução**: Botão "Converter em Obra" na proposta aprovada

---

### Fase 2: Planejamento (Obra criada → Pronta para executar)

```
OBRA CRIADA
    │
    ├──→ [1] Orçamento (obrigatório)
    │         └── Composições SINAPI + BDI + Curva ABC
    │
    ├──→ [2] Cronograma (obrigatório)
    │         └── Tarefas + CPM + Baseline
    │
    ├──→ [3] Contratos (obrigatório)
    │         └── Fornecedor + Valor + Retenção
    │
    ├──→ [4] Equipes (recomendado)
    │         └── Engenheiro + Mestre + Equipes
    │
    └──→ [5] Suprimentos iniciais (recomendado)
              └── Pedidos da Curva ABC
    
    ✅ Checklist completo → Status muda para IN_PROGRESS
```

**Estado atual**: ❌ Nenhuma dessas etapas é guiada. Usuário precisa saber sozinho.
**Gap**: Falta um "Setup Wizard" ou "Checklist de Planejamento"
**Solução**: 
- Dashboard de setup na aba Resumo com checklist visual
- Cada item mostra ✅/⬜ e link direto para a ação
- Quando todos obrigatórios estão ✅, botão "Iniciar Execução"

---

### Fase 3: Execução (Dia-a-dia da obra)

```
OBRA IN_PROGRESS
    │
    ├── Diário ──────────── Registro diário (clima, equipe, atividades, fotos)
    │                            └── Gera: atrasos climáticos automáticos
    │
    ├── Medições ─────────── DRAFT → SUBMITTED → APPROVED → PAID
    │                            └── Gera: contas a pagar/receber
    │
    ├── Suprimentos ──────── Requisição → Cotação → Pedido → Recebimento
    │                            └── Gera: movimentação de estoque
    │
    ├── Apontamento ──────── Horas por funcionário/equipe/atividade
    │                            └── Gera: custo de mão de obra (Job Costing)
    │
    ├── Financeiro ────────── Pagar + Receber + Fluxo de Caixa + NFs
    │                            └── Alimentado por: medições + suprimentos
    │
    ├── Job Costing ──────── Orçado vs Comprometido vs Realizado (EVM)
    │                            └── Alimentado por: tudo acima
    │
    ├── Segurança ────────── Inspeções + Incidentes + DDS
    │
    └── Documentos ────────── RFI + Submittals + Punch List + Arquivos
```

**Estado atual**: ⚠️ Módulos existem mas não se alimentam mutuamente
**Gaps**:
- Medição aprovada não gera conta a pagar automaticamente
- Pedido recebido não atualiza estoque automaticamente
- Apontamento não alimenta Job Costing automaticamente
- Diário não calcula atrasos climáticos
**Solução**: Event-driven — cada ação dispara eventos que atualizam módulos dependentes

---

### Fase 4: Encerramento

```
OBRA → ENCERRAMENTO
    │
    ├── Punch List ──────── Pendências → Resolução → Verificação
    │
    ├── Vistoria Final ──── Checklist de entrega
    │
    ├── Documentos ──────── As-built, manuais, garantias
    │
    └── Entrega ─────────── Termo de recebimento → Status COMPLETED
```

**Estado atual**: ❌ Delivery existe como rota mas sem fluxo orquestrado
**Gap**: Não existe checklist de encerramento nem transição automática
**Solução**: Wizard de encerramento com checklist obrigatório

---

### Fase 5: Pós-Obra

```
OBRA COMPLETED
    │
    └── Pós-Venda ──────── Tickets + SLA + Garantia
                               └── Vinculado à obra original
```

**Estado atual**: ✅ Módulo aftersales existe
**Gap**: Não vincula automaticamente à obra entregue

---

## Mapa de Conexões Faltantes

```
┌─────────────────────────────────────────────────────────────────┐
│                    CONEXÕES QUE FALTAM                            │
├─────────────────────────────┬───────────────────────────────────┤
│ ORIGEM                      │ DESTINO (deveria gerar)           │
├─────────────────────────────┼───────────────────────────────────┤
│ Proposta aprovada           │ → Criar obra automaticamente      │
│ Orçamento aprovado          │ → Gerar cronograma base           │
│ Orçamento (Curva ABC)       │ → Sugerir pedidos de compra       │
│ Contrato criado             │ → Gerar contas a pagar (parcelas) │
│ Medição aprovada            │ → Gerar conta a receber           │
│ Pedido recebido             │ → Atualizar estoque               │
│ Apontamento de horas        │ → Alimentar Job Costing           │
│ Diário (chuva)              │ → Registrar atraso climático      │
│ Punch List 100% resolvido   │ → Habilitar entrega               │
│ Entrega assinada            │ → Mudar status para COMPLETED     │
│ Obra COMPLETED              │ → Criar ticket pós-venda          │
└─────────────────────────────┴───────────────────────────────────┘
```

---

## Soluções de UX Propostas

### 1. Dashboard de Processo (aba Resumo)

Substituir o resumo atual por um **dashboard de fase** que mostra:

```
┌─────────────────────────────────────────────────────────────┐
│  OBRA: Residencial Aurora  │  Fase: EXECUÇÃO  │  72% ████░░│
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ⬜ Planejamento          ✅ Execução          ⬜ Encerramento│
│  ├── ✅ Orçamento         ├── ✅ Diário (hoje)  ├── ⬜ Punch  │
│  ├── ✅ Cronograma        ├── ⚠️ Medição #3     ├── ⬜ Docs   │
│  ├── ✅ Contrato          ├── ✅ Suprimentos    └── ⬜ Entrega│
│  └── ✅ Equipes           └── ⚠️ Financeiro                  │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ PRÓXIMAS AÇÕES                                       │   │
│  │ • Aprovar Medição #3 (R$ 45.000)         [Aprovar]  │   │
│  │ • Pedido #12 aguardando recebimento      [Receber]  │   │
│  │ • Diário de hoje não preenchido          [Preencher]│   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### 2. Wizard de Nova Obra (Multi-Step)

```
Step 1: Dados Básicos     → Nome, código, endereço
Step 2: Cliente           → Lookup + cadastro rápido (✅ já implementado)
Step 3: Equipe            → Engenheiro responsável + mestre de obras
Step 4: Datas e Valores   → Início, previsão, valor previsto
Step 5: Confirmação       → Preview antes de criar
```

### 3. Stepper de Status nas Medições

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│ RASCUNHO │ →  │ ENVIADA  │ →  │ APROVADA │ →  │   PAGA   │
│  (edit)  │    │ (review) │    │ (finance)│    │  (done)  │
└──────────┘    └──────────┘    └──────────┘    └──────────┘
     ●               ○               ○               ○
```

### 4. Kanban de Obras (visão gerencial)

```
┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│ PLANEJAMENTO│  │  EXECUÇÃO   │  │ ENCERRAMENTO│  │  CONCLUÍDAS │
├─────────────┤  ├─────────────┤  ├─────────────┤  ├─────────────┤
│ ┌─────────┐ │  │ ┌─────────┐ │  │ ┌─────────┐ │  │ ┌─────────┐ │
│ │ Aurora  │ │  │ │ Sunset  │ │  │ │ Marina  │ │  │ │ Central │ │
│ │ 45%     │ │  │ │ 72%     │ │  │ │ 95%     │ │  │ │ 100%    │ │
│ └─────────┘ │  │ └─────────┘ │  │ └─────────┘ │  │ └─────────┘ │
│ ┌─────────┐ │  │ ┌─────────┐ │  │             │  │             │
│ │ Tower   │ │  │ │ Plaza   │ │  │             │  │             │
│ │ 20%     │ │  │ │ 58%     │ │  │             │  │             │
│ └─────────┘ │  │ └─────────┘ │  │             │  │             │
└─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘
```

### 5. Command Palette (⌘K)

Navegação rápida por ação:
- "Nova medição" → abre form de medição na obra ativa
- "Aprovar medição" → lista medições pendentes
- "Ir para obra Aurora" → navega direto
- "Novo pedido de compra" → abre form com contexto

---

## Priorização de Implementação

| # | Melhoria | Impacto | Esforço | Prioridade |
|---|----------|---------|---------|------------|
| 1 | Dashboard de Processo (aba Resumo) | 🔴 Alto | Médio | **P0** |
| 2 | Wizard de Nova Obra (stepper) | 🔴 Alto | Baixo | **P0** |
| 3 | Next Actions após salvar | 🟡 Médio | Baixo | **P1** |
| 4 | Stepper de status (Medições) | 🟡 Médio | Baixo | **P1** |
| 5 | Kanban de Obras (lista) | 🟡 Médio | Médio | **P1** |
| 6 | Eventos entre módulos | 🔴 Alto | Alto | **P2** |
| 7 | Wizard de Encerramento | 🟡 Médio | Médio | **P2** |
| 8 | Command Palette (⌘K) | 🟢 Baixo | Médio | **P3** |
| 9 | Proposta → Obra automático | 🟡 Médio | Médio | **P3** |

---

## Padrão Técnico: Event-Driven entre Módulos

Para resolver as conexões faltantes, usar **Domain Events** no backend:

```java
// Quando medição é aprovada
@EventListener
public void onMeasurementApproved(MeasurementApprovedEvent event) {
    // Gera conta a receber automaticamente
    receivableService.createFromMeasurement(event.measurement());
    // Notifica financeiro
    notificationService.notify(event.projectId(), "Medição aprovada - gerar fatura");
}

// Quando pedido é recebido
@EventListener
public void onOrderReceived(OrderReceivedEvent event) {
    // Atualiza estoque
    inventoryService.addStock(event.items());
}
```

No frontend, usar **SSE (Server-Sent Events)** para atualizar dashboards em tempo real (já temos infraestrutura de notificações).

---

## Checklist de Implementação por Módulo

### Obra (project)
- [ ] Wizard multi-step para criação
- [ ] Dashboard de fase no Resumo (substituir resumo atual)
- [ ] Indicadores de completude por fase
- [ ] Botão "Iniciar Execução" quando planejamento completo
- [ ] Kanban view na lista de obras

### Orçamento (budget)
- [ ] Após aprovar → sugerir "Gerar cronograma" e "Gerar pedidos ABC"
- [ ] Link direto para criar contrato a partir do orçamento

### Contrato (contract)
- [ ] Após criar → sugerir "Gerar parcelas financeiras"
- [ ] Vincular fornecedor (✅ já implementado com lookup)

### Medições (measurement)
- [ ] Stepper visual de status (DRAFT → SUBMITTED → APPROVED → PAID)
- [ ] Após aprovar → gerar conta a receber automaticamente
- [ ] Notificação para financeiro

### Suprimentos (procurement)
- [ ] Após receber pedido → atualizar estoque automaticamente
- [ ] Vincular fornecedor na geração (✅ já implementado com lookup)
- [ ] Sugerir pedidos a partir da Curva ABC do orçamento

### Financeiro (finance)
- [ ] Alimentado automaticamente por medições e contratos
- [ ] Dashboard de fluxo de caixa com projeção

### Diário de Obra (daily-log)
- [ ] Registrar clima → calcular atrasos automaticamente
- [ ] Vincular atividades ao cronograma

### Encerramento (delivery)
- [ ] Wizard de encerramento com checklist
- [ ] Punch List 100% → habilitar entrega
- [ ] Termo de recebimento → mudar status para COMPLETED

---

## Resumo Executivo

O SinapiPRO tem todos os módulos necessários para um ERP de construção completo. O que falta é a **cola entre eles** — a orquestração que transforma CRUDs isolados em um processo fluido.

As 3 ações de maior impacto imediato:
1. **Dashboard de Processo** na aba Resumo (mostra onde a obra está e o que fazer)
2. **Wizard de Nova Obra** (guia o usuário nos primeiros passos)
3. **Next Actions** após cada operação (nunca deixar o usuário sem saber o próximo passo)

Referência: Procore resolve isso com "workflows que definem tarefas, colocam em ordem e estabelecem o responsável". Cada ação tem um dono e um próximo passo claro.
