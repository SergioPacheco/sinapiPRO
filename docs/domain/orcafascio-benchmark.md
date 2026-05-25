# Lógicas de Negócio Completas — OrçaFascio → SinapiPRO

> Extraído de https://suporte.orcafascio.com/ (todos os módulos)
> Módulos de projeto (Elétrico, Hidráulico, Estrutural, BIM) excluídos — não se aplicam ao SinapiPRO

---

## Módulos Relevantes Extraídos

| # | Módulo | Artigos | Relevância para SinapiPRO |
|---|--------|:-------:|:---:|
| 1 | Orçamento de Obras | 57 | ⭐⭐⭐ Core |
| 2 | Base de Composições | 14 | ⭐⭐⭐ Core |
| 3 | OF Medição | 29 | ⭐⭐⭐ Core |
| 4 | Medição (legado) | 10 | ⭐⭐⭐ Core |
| 5 | Planejamento | 14 | ⭐⭐ Importante |
| 6 | Diário de Obras | 8 | ⭐⭐ Importante |
| 7 | Compras | 10 | ⭐⭐ Importante |
| 8 | OF CDE | — | ⭐ Referência (GED) |
| 9 | Administrar Licença | — | ⭐ Referência (multi-tenant) |

---

## MÓDULO 1: ORÇAMENTO DE OBRAS

### Funcionalidades Mapeadas (57 artigos)

#### Criação e Gestão
- Criar orçamento (nome, cliente, obra, data base, UF, bases de preço)
- Interface com lista de orçamentos (filtros por data, criador, descrição)
- Duplicar/copiar orçamento
- Enviar orçamento para outro usuário
- Excluir orçamento (lixeira com recuperação)
- Migrar orçamentos entre contas

#### Estrutura do Orçamento
- Etapas hierárquicas (N níveis)
- Adicionar composições (SINAPI, ORSE, próprias)
- Adicionar insumos avulsos
- Máscara de item (códigos personalizados com letras+números)
- Duplicar itens e etapas
- Substituir itens (mantém estrutura, troca composição)
- Recuperar itens excluídos
- Tags para marcação de itens
- Filtrar lista de itens
- Caixa de seleção (operações em lote)

#### Bases de Preço
- Múltiplas bases simultâneas (SINAPI + ORSE + própria)
- Atualização de data base (recalcular preços para novo mês/UF)
- Compatibilização de bases (unificar sem duplicar)
- Retornar ao valor original da base
- TAG de divergência de data

#### BDI
- Cadastro de BDI (componentes configuráveis)
- Fórmula TCU: [(1+AC+S+R+G)×(1+DF)×(1+L)]/(1-I) - 1
- Aplicar sobre preço unitário OU valor total
- BDI diferenciado por tipo de item
- Relatório de composição de BDI
- Instruções TCU para aplicação

#### Encargos Sociais
- Horista vs mensalista
- Encargos das bases oficiais
- Encargos do Simples Nacional
- Conversão horista ↔ mensalista

#### Arredondamento
- Truncamento (obrigatório TCU)
- Arredondamento ABNT
- Arredondamento independente por base
- Casas decimais configuráveis

#### Cronograma Físico-Financeiro
- Distribuição percentual por período
- Trabalhar com etapas ou serviços
- Adição de dias ao cronograma
- Curva S derivada

#### Relatórios
- Sintético (etapas + itens + valores)
- Sintético com fórmulas (memória de cálculo)
- Analítico (composições abertas)
- Curva ABC de insumos
- Curva ABC de serviços
- Composição de BDI
- Personalização (logo, cabeçalho, rodapé, cores)
- Exportação PDF e Excel

#### Operações Avançadas
- Ajustar valor do orçamento (desconto/acréscimo global)
- Comparar preço com valores de licitação
- Comparação entre orçamentos (diff)
- Gerador de propostas para pregão
- Memória de cálculo (justificativa de quantidades)
- Importar orçamento sintético (Excel)
- Importar com máscara de item
- Importar itens de outro orçamento
- Insumos com preços zerados (tratamento)
- Atalhos de teclado

### Layout da Tela de Orçamento
```
┌─────────────────────────────────────────────────────────────┐
│ [Toolbar] Salvar | BDI | Relatórios | Ferramentas | Buscar  │
├─────────────────────────────────────────────────────────────┤
│ Árvore de Etapas (esquerda)  │  Planilha de Itens (direita) │
│                              │                               │
│ ▼ 01. Infraestrutura        │  Código | Descrição | Un | Qtd│
│   ▼ 01.01 Fundações         │  87548  | Concreto  | m³ | 120│
│     01.01.01 Estacas         │  92781  | Armação   | kg | 85 │
│   ▼ 01.02 Contenções        │  ...    | ...       | .. | .. │
│ ▼ 02. Superestrutura        │                               │
│   ...                        │  [+ Adicionar item]           │
├─────────────────────────────────────────────────────────────┤
│ Resumo: Total R$ 850.000,00 | BDI: 22,7% | Itens: 145      │
└─────────────────────────────────────────────────────────────┘
```

---

## MÓDULO 2: BASE DE COMPOSIÇÕES

### Funcionalidades Mapeadas (14 artigos)

#### Banco Próprio de Insumos
- Criar banco próprio
- Criação manual de insumos (código, descrição, unidade, preço, tipo)
- Importação via planilha Excel (XLSX)
- Atualizar preços de insumos existentes (importação de atualização)
- Busca de insumos (filtros por descrição, código)

#### Banco Próprio de Composições
- Criar banco próprio de composições
- Copiar composição existente para editar
- Importação via planilha Excel
- Busca de composições (filtros avançados)
- Criar composição de preço unitário (CPU)

#### Sincronização
- Valores no orçamento vs base própria (divergências)
- Atualizar itens no orçamento após alterar na base
- Atualização individual ou em massa

### Layout da Tela de Composições
```
┌─────────────────────────────────────────────────────────────┐
│ [Busca] Pesquisar composição...          [Filtros] [+ Nova] │
├─────────────────────────────────────────────────────────────┤
│ Código  │ Descrição                    │ Unidade │ Custo    │
│ 87548   │ Concreto fck=25MPa betoneira │ m³      │ R$ 450,50│
│ 92781   │ Armação CA-50 10mm           │ kg      │ R$ 12,80 │
├─────────────────────────────────────────────────────────────┤
│ Detalhe da Composição Selecionada:                          │
│ ┌─────────────────────────────────────────────────────────┐ │
│ │ Insumo        │ Unid │ Coeficiente │ Preço   │ Custo   │ │
│ │ Cimento CP-II │ kg   │ 320,000     │ R$ 0,72 │ R$230,40│ │
│ │ Areia média   │ m³   │ 0,660       │ R$ 95,00│ R$ 62,70│ │
│ │ Servente      │ h    │ 6,000       │ R$ 22,48│ R$134,88│ │
│ │ Betoneira     │ h    │ 1,000       │ R$ 1,85 │ R$  1,85│ │
│ ├─────────────────────────────────────────────────────────┤ │
│ │ Custo Unitário Total:                        R$ 450,50  │ │
│ └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

---

## MÓDULO 3: MEDIÇÃO (OF Medição + Medição legado)

### Funcionalidades Mapeadas (39 artigos combinados)

#### Gestão de Obras (contexto)
- Cadastrar obra (nome, código, endereço, cliente)
- Lista de obras (filtros)
- Membros da obra (supervisores, fiscais, empreiteiros)
- Anexos da obra (documentos, fotos)
- Lixeira de obras

#### Criação de Medição
- Criar nova medição (número sequencial, período)
- Importar itens do orçamento/contrato
- Importar medição via planilha Excel
- Adicionar serviço não orçado (extra)
- Adicionar aditivo

#### Preenchimento
- Informar quantidade executada por item
- Memória de cálculo por item (fórmulas)
- Alterar memória de cálculo
- Vincular empreiteiro ao item

#### Workflow de Aprovação
- Solicitar aprovação (empreiteiro → fiscal)
- Aprovar medição (fiscal)
- Rejeitar medição (com justificativa)
- Medição não editável após aprovação
- Múltiplos fiscais por obra

#### Empreiteiros
- Cadastrar empreiteiro
- Inserir empreiteiro na medição
- Medição por empreiteiro

#### Relatórios
- Boletim de medição (PDF)
- Relatório fotográfico
- Relatório acumulado
- Relatório por empreiteiro
- Exportação Excel

#### Configurações
- Configurar retenção (%)
- Configurar BDI na medição
- Configurar casas decimais
- Área de trabalho (personalização)

### Layout da Tela de Medição
```
┌─────────────────────────────────────────────────────────────┐
│ Obra: Residencial Parque das Flores    │ Medição #3 (Mai/26)│
├─────────────────────────────────────────────────────────────┤
│ [Toolbar] Salvar | Solicitar Aprovação | Relatórios | Config│
├─────────────────────────────────────────────────────────────┤
│ Item │ Descrição        │ Un │ Contrat.│ Anterior│ Atual│Saldo│
│ 1.1  │ Alvenaria vedação│ m² │ 200,00  │ 80,00   │ 50,00│70,00│
│ 1.2  │ Reboco interno   │ m² │ 400,00  │ 120,00  │ 80,00│200  │
│ 2.1  │ Pintura PVA      │ m² │ 300,00  │ 0,00    │ 100  │200  │
├─────────────────────────────────────────────────────────────┤
│ Valor Medido: R$ 18.500,00 │ Retenção: R$ 925,00           │
│ Valor Líquido: R$ 17.575,00│ Acumulado: R$ 52.300,00       │
└─────────────────────────────────────────────────────────────┘
```

---

## MÓDULO 4: PLANEJAMENTO

### Funcionalidades Mapeadas (14 artigos)

#### Criação
- Criar planejamento vinculado ao orçamento
- Iniciar com dados do cronograma físico-financeiro do orçamento
- Definir data de início
- Considerar itens agregados de etapas como distintos

#### Atividades
- Definir durações das atividades (dias úteis)
- Definir predecessores (FS, SS, FF, SF)
- Configurar colunas visíveis
- Itens agregados vs individuais

#### Calendário
- Definir feriados/dias não trabalhados
- Calendário por obra

#### Acompanhamento
- Acompanhamento do planejamento (previsto vs realizado)
- Atualizar após ajuste no orçamento
- Gráfico de Gantt ao lado do planejamento

#### Relatórios
- Relatórios do planejamento (Gantt, Curva S)
- Visualização no Revit (integração BIM)

### Layout da Tela de Planejamento
```
┌─────────────────────────────────────────────────────────────┐
│ [Toolbar] Salvar | Predecessores | Feriados | Relatórios    │
├──────────────────────────────┬──────────────────────────────┤
│ Atividade    │Dur│Início│Fim │ Jan  │ Fev  │ Mar  │ Abr    │
│ Terraplanagem│ 20│01/01 │28/01│████  │      │      │        │
│ Fundações    │ 45│29/01 │28/03│  ████│██████│      │        │
│ Estrutura    │ 90│01/03 │15/06│      │  ████│██████│████    │
│ Alvenaria    │ 60│01/05 │15/07│      │      │      │  ████  │
├──────────────────────────────┴──────────────────────────────┤
│ Caminho Crítico: Fundações → Estrutura → Alvenaria          │
│ Duração Total: 180 dias úteis                               │
└─────────────────────────────────────────────────────────────┘
```

---

## MÓDULO 5: DIÁRIO DE OBRAS

### Funcionalidades Mapeadas (8 artigos)

#### Criação
- Criar diário de obras (data, período)
- Vincular fiscal (interno ou externo)
- Validação de datas (período válido)

#### Registro Diário
- Tarefas executadas (vinculadas ao planejamento)
- Entrada e saída de materiais
- Relatório fotográfico (fotos com legenda)
- Condições climáticas

#### Fiscalização
- Cadastrar fiscal (interno/externo)
- Fiscal vinculado à obra
- Assinatura do fiscal

#### Relatórios
- Relatório do diário de obra (RDO) em PDF
- Relatório fotográfico separado
- Exportação por período

### Layout da Tela de Diário
```
┌─────────────────────────────────────────────────────────────┐
│ Diário de Obra │ Data: 09/05/2026 │ Clima: ☀️ Ensolarado    │
├─────────────────────────────────────────────────────────────┤
│ EQUIPE PRESENTE                                             │
│ Nome              │ Função         │ Horas │ Entrada│Saída  │
│ João Carlos       │ Mestre de Obras│ 8h    │ 07:00  │16:00  │
│ Pedro Oliveira    │ Pedreiro       │ 8h    │ 07:00  │16:00  │
├─────────────────────────────────────────────────────────────┤
│ ATIVIDADES EXECUTADAS                                       │
│ • Concretagem 3o pavimento Bloco A (vinculada: Estrutura)   │
│ • Armação 4o pavimento iniciada                             │
├─────────────────────────────────────────────────────────────┤
│ MATERIAIS                                                   │
│ Entrada: Cimento CP-II (200 sacos) │ NF: 4521              │
│ Saída: Areia média (5 m³) → Bloco A                        │
├─────────────────────────────────────────────────────────────┤
│ FOTOS [📷 Adicionar]                                        │
│ [foto1.jpg] Concretagem  │ [foto2.jpg] Armação             │
├─────────────────────────────────────────────────────────────┤
│ OCORRÊNCIAS                                                 │
│ ⚠️ Chuva 14h-16h — paralisação da concretagem              │
├─────────────────────────────────────────────────────────────┤
│ [Salvar] [Gerar RDO PDF] [Fechar Diário]                    │
└─────────────────────────────────────────────────────────────┘
```

---

## MÓDULO 6: COMPRAS

### Funcionalidades Mapeadas (10 artigos)

#### Personagens/Perfis
- Solicitante (engenheiro da obra)
- Comprador (setor de compras)
- Aprovador (gestor)
- Fornecedor (responde cotação)

#### Fluxo
```
Necessidade da obra (Curva ABC ou manual)
→ Criar pedido (itens + quantidades)
→ Inserir itens do orçamento (vinculação automática)
→ Cadastrar fornecedores
→ Enviar cotação para fornecedores
→ Fornecedores respondem (preço + prazo)
→ Mapa comparativo
→ Aprovar melhor proposta
→ Gerar pedido de compra
→ Recebimento
```

#### Funcionalidades
- Criar pedido a partir da Curva ABC do orçamento
- Inserir itens do orçamento no pedido
- Cadastrar fornecedor (CNPJ, contato, endereço)
- Enviar cotação por e-mail ao fornecedor
- Cotação editável até envio
- Apagar fornecedor após inserido
- Relatórios de compras

### Layout da Tela de Compras
```
┌─────────────────────────────────────────────────────────────┐
│ Pedido #001 │ Obra: Parque das Flores │ Status: Em Cotação  │
├─────────────────────────────────────────────────────────────┤
│ ITENS DO PEDIDO                                             │
│ Item          │ Unidade │ Quantidade │ Observação           │
│ Cimento CP-II │ saco    │ 500        │ Entrega em 5 dias    │
│ Aço CA-50 10mm│ kg      │ 8.000      │ Barra 12m            │
├─────────────────────────────────────────────────────────────┤
│ FORNECEDORES COTADOS                                        │
│ Fornecedor    │ Cimento (un) │ Aço (kg) │ Prazo │ Total    │
│ Nassau S.A.   │ R$ 45,00     │ R$ 7,00  │ 5 dias│ R$ 78.500│
│ Gerdau        │ —            │ R$ 6,80  │ 7 dias│ R$ 54.400│
│ Tropical      │ R$ 47,00     │ R$ 7,45  │ 10 d  │ R$ 83.100│
├─────────────────────────────────────────────────────────────┤
│ [Enviar Cotação] [Mapa Comparativo PDF] [Aprovar Melhor]    │
└─────────────────────────────────────────────────────────────┘
```

---

## MÓDULO 7: OF CDE (Ambiente Comum de Dados)

### Funcionalidades (referência para GED do SinapiPRO)
- Compartilhar projeto com outros profissionais
- Controle de versão de documentos
- Permissões por documento/pasta
- Histórico de alterações
- Download controlado

---

## MÓDULO 8: ADMINISTRAR LICENÇA

### Funcionalidades (referência para multi-tenant)
- Gerenciar usuários da licença
- Definir permissões por módulo
- Controle de acesso por perfil
- Configurações da empresa (logo, dados)

---

## RESUMO: GAPS DO SINAPIPRO vs. ORCAFASCIO

### Orçamento (16 gaps)
| P1 | Atualização de data base, Truncamento TCU, BDI diferenciado, Memória de cálculo, Relatório analítico |
| P2 | Múltiplas bases, Máscara de item, Comparação entre orçamentos, Duplicação, Substituição |
| P3 | Pregão, Tags, Lixeira, Personalização relatórios, Encargos sociais, Importar de outro |

### Medição (10 gaps)
| P1 | Memória de cálculo, Anexos/fotos, Rejeição com justificativa, Histórico aprovações |
| P2 | Múltiplos fiscais, Medição por empreiteiro, Importar Excel |
| P3 | Lixeira, Relatório por empreiteiro, Casas decimais |

### Planejamento (5 gaps)
| P1 | Feriados/calendário, Acompanhamento previsto×realizado |
| P2 | Iniciar do cronograma do orçamento, Gráfico de Gantt interativo |
| P3 | Visualização BIM |

### Diário de Obras (4 gaps)
| P1 | Vinculação de tarefas ao planejamento |
| P2 | Entrada/saída de materiais (integração estoque), Relatório fotográfico separado |
| P3 | Assinatura digital do fiscal |

### Compras (3 gaps)
| P1 | Envio de cotação por e-mail ao fornecedor |
| P2 | Comprar a partir da Curva ABC (integração orçamento→compras) |
| P3 | Portal do fornecedor (responder cotação online) |

---

## TOTAL DE GAPS IDENTIFICADOS: 38

| Prioridade | Quantidade | Esforço estimado |
|:---:|:---:|---|
| P1 | 14 | 2-3 sprints |
| P2 | 14 | 3-4 sprints |
| P3 | 10 | 2-3 sprints |
