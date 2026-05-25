# Lógicas de Negócio — Sistema de Orçamento de Obras Web

> Extraído do manual OrçaFascio (https://suporte.orcafascio.com/)
> Aplicável ao módulo de Orçamentos do SinapiPRO

---

## 1. Estrutura do Orçamento

### 1.1 Hierarquia
```
Orçamento
├── Etapa (nível 1)
│   ├── Sub-etapa (nível 2)
│   │   ├── Item (composição ou insumo)
│   │   └── Item
│   └── Item
└── Etapa
    └── Item
```

### 1.2 Regras
- Orçamento tem N etapas hierárquicas (ilimitado)
- Cada item pode ser: composição SINAPI, composição própria, ou insumo avulso
- Cada item tem: código, descrição, unidade, quantidade, preço unitário
- Preço unitário pode vir da base (SINAPI/ORSE) ou ser informado manualmente
- Máscara de item: permite códigos personalizados (letras + números)
- Um orçamento pode ter múltiplas bases de preço simultaneamente (SINAPI + ORSE + própria)

---

## 2. Bases de Preço

### 2.1 Bases Oficiais Suportadas
- SINAPI (Caixa Econômica Federal)
- ORSE (Sergipe)
- SICRO (DNIT)
- SEINFRA (Ceará)
- SETOP (Minas Gerais)
- EMOP (Rio de Janeiro)
- TCPO (Pini)
- Bases estaduais diversas

### 2.2 Regras de Base
- Cada base tem data de referência (mês/ano)
- Preços variam por estado (UF)
- Preços podem ser desonerados ou não desonerados
- Sistema deve permitir compatibilização entre bases (unificar sem duplicar)
- Atualização de data base: recalcula todos os preços para novo mês/ano
- Divergência de data: alerta quando item tem data diferente da base do orçamento

### 2.3 Lógica de Atualização de Data Base
```
Para cada item do orçamento:
  1. Buscar preço na base oficial para nova data/UF
  2. Se encontrou → atualizar preço unitário
  3. Se não encontrou → manter preço anterior + marcar com TAG de divergência
  4. Recalcular totais
```

---

## 3. Composições

### 3.1 Estrutura de uma Composição
```
Composição (serviço)
├── Insumo 1 (material) — coeficiente × preço = custo parcial
├── Insumo 2 (mão de obra) — coeficiente × preço = custo parcial
├── Insumo 3 (equipamento) — coeficiente × preço = custo parcial
└── Sub-composição (auxiliar) — coeficiente × custo unitário = custo parcial
Custo unitário = Σ custos parciais
```

### 3.2 Tipos de Insumo
- Material
- Mão de obra (horista ou mensalista)
- Equipamento
- Serviço terceirizado
- Transporte

### 3.3 Banco Próprio
- Usuário pode criar composições e insumos próprios
- Importação via planilha Excel (XLSX)
- Formato: código, descrição, unidade, preço, tipo
- Composições próprias podem referenciar insumos de bases oficiais
- Cópia de composição existente para edição
- Sincronização: ao alterar preço na base própria, pode atualizar no orçamento

---

## 4. BDI (Benefícios e Despesas Indiretas)

### 4.1 Componentes do BDI
| Componente | Descrição |
|-----------|-----------|
| Administração Central | Custos administrativos da empresa |
| Lucro | Margem de lucro |
| Despesas Financeiras | Custo do capital |
| Seguros e Garantias | Seguros obrigatórios |
| Riscos | Contingências |
| Tributos (ISS) | Imposto sobre serviços |
| Tributos (PIS) | Contribuição social |
| Tributos (COFINS) | Contribuição social |
| Tributos (CPRB/INSS) | Contribuição previdenciária |

### 4.2 Fórmula do BDI (TCU)
```
BDI = [(1 + AC + S + R + G) × (1 + DF) × (1 + L)] / (1 - I) - 1

Onde:
AC = Administração Central
S = Seguros
R = Riscos
G = Garantias
DF = Despesas Financeiras
L = Lucro
I = Tributos (ISS + PIS + COFINS + CPRB)
```

### 4.3 Aplicação do BDI
- Pode ser aplicado sobre preço unitário OU sobre valor total
- BDI diferenciado por tipo de item (material, equipamento, mão de obra)
- BDI diferenciado por etapa
- Relatório de composição do BDI (detalhamento dos percentuais)

### 4.4 Regras TCU
- Valores de referência por tipo de obra (construção, reforma, consultoria)
- Faixas aceitáveis para cada componente
- Truncamento obrigatório (não arredondamento) conforme cartilha TCU

---

## 5. Encargos Sociais

### 5.1 Tipos
- Encargos sobre mão de obra horista
- Encargos sobre mão de obra mensalista
- Encargos do Simples Nacional (diferenciados)
- Encargos das bases oficiais (SINAPI já inclui)

### 5.2 Regras
- Mão de obra pode ser horista ou mensalista (impacta cálculo)
- Conversão horista ↔ mensalista deve ser possível
- Encargos sociais são aplicados sobre o salário base
- Bases oficiais já incluem encargos — não duplicar

---

## 6. Arredondamento

### 6.1 Métodos
| Método | Descrição | Uso |
|--------|-----------|-----|
| Truncamento | Corta casas decimais sem arredondar | TCU/licitações |
| Arredondamento ABNT | Regra do 5 (banker's rounding) | Geral |
| Arredondamento simples | ≥5 arredonda para cima | Comum |

### 6.2 Regras
- Arredondamento pode ser diferente por base de preço
- Casas decimais configuráveis (2, 4, 6)
- Truncamento é obrigatório para obras públicas (cartilha TCU)
- Arredondamento afeta: preço unitário, coeficiente, custo parcial, total

---

## 7. Cronograma Físico-Financeiro

### 7.1 Estrutura
```
Etapa/Serviço | Mês 1 | Mês 2 | Mês 3 | ... | Total
Fundação      | 40%   | 60%   |       |     | 100%
Estrutura     |       | 30%   | 70%   |     | 100%
Total (R$)    | X     | Y     | Z     |     | Total
```

### 7.2 Regras
- Pode trabalhar com etapas ou serviços individuais
- Percentual por período (mensal, quinzenal, semanal)
- Soma dos percentuais de cada item = 100%
- Valor financeiro = percentual × valor total do item
- Adição de dias/períodos ao cronograma
- Curva S derivada do cronograma

---

## 8. Relatórios

### 8.1 Tipos de Relatório
| Relatório | Conteúdo |
|-----------|----------|
| Sintético | Etapas + itens + valores (sem composição) |
| Sintético com fórmulas | Inclui memória de cálculo (qtd × preço) |
| Analítico | Composições abertas (insumos + coeficientes) |
| Curva ABC de Insumos | Ranking de insumos por impacto no custo |
| Curva ABC de Serviços | Ranking de serviços por valor |
| Composição de BDI | Detalhamento dos percentuais |
| Cronograma Físico-Financeiro | Distribuição temporal |
| Memória de Cálculo | Justificativa das quantidades |

### 8.2 Personalização
- Logo da empresa
- Cabeçalho customizado
- Rodapé customizado
- Cores personalizadas
- Exportação: PDF e Excel

---

## 9. Operações sobre o Orçamento

### 9.1 CRUD Básico
- Criar orçamento (nome, cliente, obra, data base, UF, bases)
- Duplicar orçamento (cópia completa)
- Enviar orçamento para outro usuário
- Excluir orçamento (lixeira com recuperação)
- Importar orçamento sintético (planilha Excel)

### 9.2 Operações sobre Itens
- Adicionar item (busca na base ou próprio)
- Duplicar item
- Substituir item (mantém estrutura, troca composição)
- Recuperar item excluído
- Mover item entre etapas
- Filtrar itens (por descrição, código, etapa, tag)
- Tags para marcação de itens
- Caixa de seleção (operações em lote)

### 9.3 Operações de Valor
- Ajustar valor do orçamento (aplicar desconto/acréscimo global)
- Retornar ao valor original da base
- Comparar preço com valores de licitação
- Comparação entre orçamentos (diff)

### 9.4 Importação/Exportação
- Importar orçamento sintético (Excel)
- Importar com máscara de item
- Importar itens de outro orçamento (com valor da base ou do orçamento origem)
- Exportar relatórios (PDF/Excel)

---

## 10. Gerador de Propostas para Pregão

### 10.1 Funcionalidade
- Gerar propostas com diferentes percentuais de desconto
- Simular cenários de preço para licitação
- Aplicar desconto linear ou por item
- Gerar documento formatado para submissão

---

## 11. Memória de Cálculo

### 11.1 Objetivo
Justificar as quantidades de cada item do orçamento.

### 11.2 Estrutura
```
Item: Alvenaria de vedação
Quantidade: 450 m²
Memória:
  Parede 1: 10m × 3m = 30 m²
  Parede 2: 8m × 3m = 24 m²
  ...
  Total: 450 m²
```

### 11.3 Regras
- Cada item pode ter memória de cálculo
- Fórmulas livres (comprimento × altura, perímetro × altura, etc.)
- Resultado da memória alimenta a quantidade do item
- Exportável no relatório sintético com fórmulas

---

## 12. Funcionalidades de Colaboração

- Enviar orçamento para outro usuário (compartilhamento)
- Migrar orçamentos entre contas
- Controle de acesso por licença
- Histórico de alterações

---

## 13. Comparativo: OrçaFascio vs. SinapiPRO (gap analysis)

| Funcionalidade | OrçaFascio | SinapiPRO | Gap |
|---------------|:----------:|:---------:|:---:|
| Etapas hierárquicas | ✅ | ✅ | — |
| Composições SINAPI | ✅ | ✅ | — |
| Banco próprio (insumos) | ✅ | ✅ | — |
| Banco próprio (composições) | ✅ | ✅ | — |
| Importação XLSX | ✅ | ✅ | — |
| BDI configurável | ✅ | ✅ | — |
| BDI diferenciado por tipo | ✅ | ❌ | **Gap** |
| Curva ABC | ✅ | ✅ | — |
| Múltiplas bases simultâneas | ✅ | ❌ | **Gap** |
| Atualização de data base | ✅ | ❌ | **Gap** |
| Arredondamento configurável | ✅ | ❌ | **Gap** |
| Truncamento TCU | ✅ | ❌ | **Gap** |
| Máscara de item | ✅ | ❌ | **Gap** |
| Memória de cálculo | ✅ | ❌ | **Gap** |
| Cronograma físico-financeiro | ✅ | ✅ (separado) | — |
| Relatório sintético PDF | ✅ | ✅ | — |
| Relatório analítico PDF | ✅ | ❌ | **Gap** |
| Personalização de relatório | ✅ | ❌ | **Gap** |
| Comparação entre orçamentos | ✅ | ❌ | **Gap** |
| Gerador de propostas/pregão | ✅ | ❌ | **Gap** |
| Substituição de itens | ✅ | ❌ | **Gap** |
| Duplicação de itens/etapas | ✅ | ❌ | **Gap** |
| Lixeira com recuperação | ✅ | ❌ | **Gap** |
| Tags em itens | ✅ | ❌ | **Gap** |
| Encargos sociais configuráveis | ✅ | ❌ | **Gap** |
| Ajustar valor global | ✅ | ✅ (price-adjustment) | — |
| Enviar para outro usuário | ✅ | ❌ | **Gap** |
| Importar de outro orçamento | ✅ | ❌ | **Gap** |

---

## 14. Priorização de Gaps para SinapiPRO

### P1 — Essencial para orçamento profissional
1. **Atualização de data base** — recalcular preços para novo mês/UF
2. **Arredondamento/Truncamento** — obrigatório para obras públicas
3. **BDI diferenciado** — por tipo de item (material vs MO vs equipamento)
4. **Memória de cálculo** — justificativa de quantidades
5. **Relatório analítico** — composições abertas

### P2 — Diferencial competitivo
6. **Múltiplas bases** — SINAPI + ORSE + própria no mesmo orçamento
7. **Máscara de item** — códigos personalizados
8. **Comparação entre orçamentos** — diff de versões
9. **Duplicação de itens/etapas** — produtividade
10. **Substituição de itens** — manter estrutura, trocar composição

### P3 — Nice to have
11. Gerador de propostas para pregão
12. Tags em itens
13. Lixeira com recuperação
14. Personalização de relatórios (logo, cores)
15. Encargos sociais configuráveis
16. Importar de outro orçamento


---

# Lógicas de Negócio — Módulo de Medição de Obras

> Extraído do manual OrçaFascio — OF Medição

---

## 15. Estrutura do Módulo de Medição

### 15.1 Hierarquia
```
Obra
├── Membros (equipe da obra)
│   ├── Supervisores
│   ├── Fiscais (aprovadores)
│   └── Empreiteiros (executores)
├── Medição 1 (período 1)
│   ├── Itens medidos (do orçamento/contrato)
│   ├── Memória de cálculo por item
│   └── Anexos (fotos, documentos)
├── Medição 2 (período 2)
└── ...
```

### 15.2 Atores/Perfis
| Perfil | Papel |
|--------|-------|
| Supervisor | Cria obra, gerencia membros, visão geral |
| Fiscal | Aprova/rejeita medições, audita quantidades |
| Empreiteiro | Executa serviços, solicita medição |
| Cliente | Visualiza medições aprovadas |

---

## 16. Fluxo de Medição

### 16.1 Workflow Completo
```
Criar medição (período)
→ Importar itens do orçamento/contrato
→ Informar quantidades executadas
→ Preencher memória de cálculo
→ Anexar evidências (fotos, documentos)
→ Solicitar aprovação (empreiteiro → fiscal)
→ Fiscal analisa
→ Aprovar OU Rejeitar (com justificativa)
→ Se aprovada → gerar boletim + liberar pagamento
→ Se rejeitada → corrigir e resubmeter
```

### 16.2 Regras de Negócio
- Medição é vinculada a uma obra e a um orçamento/contrato
- Itens da medição vêm do orçamento (não são criados do zero)
- Quantidade medida não pode exceder saldo contratado
- Medição acumula: medição atual + medições anteriores = acumulado
- Saldo = contratado - acumulado
- Retenção contratual é aplicada automaticamente
- Medição só pode ser editada enquanto não aprovada
- Após aprovação, medição é imutável

---

## 17. Funcionalidades da Medição

### 17.1 Criação
- Criar nova medição (número sequencial, período início/fim)
- Importar itens do orçamento vinculado
- Importar medição de planilha Excel

### 17.2 Preenchimento
- Informar quantidade executada por item
- Memória de cálculo por item (fórmulas de quantificação)
- Alterar memória de cálculo (edição livre)
- Valores calculados automaticamente (qtd × preço unitário)

### 17.3 Aprovação
- Solicitar aprovação (envia para fiscal)
- Fiscal pode aprovar ou rejeitar
- Rejeição requer justificativa
- Múltiplos fiscais podem ser adicionados à obra
- Histórico de aprovações/rejeições

### 17.4 Empreiteiros
- Cadastro de empreiteiros vinculados à obra
- Medição pode ser por empreiteiro (quem executou)
- Controle de pagamento por empreiteiro

### 17.5 Relatórios
- Boletim de medição (PDF)
- Relatório acumulado
- Relatório de saldo
- Relatório por empreiteiro
- Exportação Excel

### 17.6 Configurações
- Configurar retenção (%)
- Configurar BDI na medição
- Configurar casas decimais
- Configurar campos obrigatórios

---

## 18. Gestão da Obra (contexto da medição)

### 18.1 Cadastro da Obra
- Nome, código, endereço
- Cliente vinculado
- Contrato/orçamento vinculado
- Data início/fim previsto
- Membros (supervisores, fiscais, empreiteiros)

### 18.2 Membros da Obra
- Adicionar/remover membros
- Definir perfil (supervisor, fiscal, empreiteiro)
- Fiscal pode ser adicionado a qualquer momento
- Empreiteiro vinculado a itens específicos

### 18.3 Anexos da Obra
- Upload de documentos gerais da obra
- Fotos de progresso
- Contratos digitalizados
- ART/RRT

### 18.4 Lixeira
- Medições excluídas vão para lixeira
- Recuperação possível
- Exclusão definitiva após período

---

## 19. Cálculos da Medição

### 19.1 Fórmulas Básicas
```
Valor medido = Quantidade executada × Preço unitário
Valor acumulado = Σ valores medidos (todas as medições)
Saldo = Valor contratado - Valor acumulado
Retenção = Valor medido × % retenção
Valor líquido = Valor medido - Retenção
```

### 19.2 Memória de Cálculo
```
Item: Alvenaria de vedação (m²)
Memória:
  Ambiente 1: 4,50m × 2,80m = 12,60 m²
  Ambiente 2: 3,20m × 2,80m = 8,96 m²
  Desconto porta: -0,80m × 2,10m = -1,68 m²
  Total executado: 19,88 m²
```

### 19.3 Controle Acumulado
```
| Item      | Contratado | Med.1 | Med.2 | Med.3 | Acumulado | Saldo |
|-----------|:----------:|:-----:|:-----:|:-----:|:---------:|:-----:|
| Alvenaria | 200 m²     | 50    | 60    | 40    | 150       | 50    |
| Pintura   | 300 m²     | 0     | 80    | 100   | 180       | 120   |
```

---

## 20. Comparativo: OrçaFascio Medição vs. SinapiPRO

| Funcionalidade | OrçaFascio | SinapiPRO | Gap |
|---------------|:----------:|:---------:|:---:|
| Criar medição por período | ✅ | ✅ | — |
| Itens do orçamento/contrato | ✅ | ✅ | — |
| Quantidade executada | ✅ | ✅ | — |
| Workflow aprovação | ✅ | ✅ | — |
| Acumulado + saldo | ✅ | ✅ | — |
| Retenção contratual | ✅ | ✅ | — |
| Boletim PDF | ✅ | ✅ | — |
| Memória de cálculo | ✅ | ❌ | **Gap** |
| Múltiplos fiscais | ✅ | ❌ | **Gap** |
| Medição por empreiteiro | ✅ | ❌ | **Gap** |
| Importar medição (Excel) | ✅ | ❌ | **Gap** |
| Anexos/fotos na medição | ✅ | ❌ | **Gap** |
| Lixeira de medições | ✅ | ❌ | **Gap** |
| Rejeição com justificativa | ✅ | ❌ | **Gap** |
| Histórico de aprovações | ✅ | ❌ | **Gap** |
| Relatório por empreiteiro | ✅ | ❌ | **Gap** |
| Configuração de casas decimais | ✅ | ❌ | **Gap** |

### Gaps Priorizados

**P1 — Essencial:**
1. Memória de cálculo na medição
2. Anexos/fotos por medição
3. Rejeição com justificativa
4. Histórico de aprovações

**P2 — Importante:**
5. Múltiplos fiscais (perfis de aprovação)
6. Medição por empreiteiro
7. Importar medição (Excel)

**P3 — Nice to have:**
8. Lixeira de medições
9. Relatório por empreiteiro
10. Configuração de casas decimais
