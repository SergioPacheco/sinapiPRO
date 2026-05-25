# gestao-de-obras-frontend — Specs e Tasks Angular 19

## 1. Objetivo do frontend

Criar o frontend do sistema **gestao-de-obras-frontend** usando **Angular 19**, com arquitetura de **verdadeira SPA**, navegação mínima entre páginas e foco em fluxos reais de gestão de obras.

O sistema não deve ser uma coleção de telas CRUD. A interface deve guiar o usuário pelo ciclo de vida da obra:

```text
Configurar empresa
→ Criar obra
→ Orçar
→ Planejar
→ Executar diário
→ Comprar
→ Medir
→ Aprovar
→ Relatar
```

A entidade central da experiência é **Obra**.

Sempre que possível, cadastros pequenos devem ser resolvidos na própria tela por **modais, drawers, dialogs, autocomplete, inline edit ou quick add**, evitando navegação desnecessária.

---

## 2. Princípios obrigatórios de UX e arquitetura

### 2.1 SPA real

O frontend deve funcionar como uma aplicação de trabalho contínuo, não como um conjunto de páginas isoladas.

Regras:

```text
- Evitar navegação para telas simples de cadastro.
- Usar modais para cadastros rápidos.
- Usar drawers laterais para detalhes e edição contextual.
- Usar abas internas dentro da Obra.
- Usar master-detail para listas e detalhes.
- Manter o contexto da obra sempre visível.
- Evitar que o usuário precise voltar ao menu para continuar o fluxo.
```

---

### 2.2 A Obra é o workspace principal

Após selecionar uma obra, o usuário deve trabalhar quase tudo dentro do **Workspace da Obra**.

Estrutura sugerida:

```text
/obras
/obras/:obraId/workspace
```

Dentro do workspace:

```text
Visão Geral
Orçamento
Planejamento
Diário de Obras
Compras
Medições
Documentos/CDE
Relatórios/BI
Configurações da Obra
```

Essas seções devem ser abas, painéis ou subáreas internas, e não páginas totalmente desconectadas.

---

### 2.3 Cadastros mínimos em modal

Cadastros simples devem abrir em modal sem sair do fluxo.

Exemplos:

```text
- Novo setor
- Novo fiscal
- Novo empreiteiro
- Novo fornecedor
- Novo insumo próprio
- Nova composição simples
- Nova tag
- Novo feriado
- Novo membro da obra
- Novo anexo
- Novo serviço não orçado
- Novo aditivo
```

Cadastros complexos podem usar drawer ou tela interna do workspace.

---

### 2.4 Navegação mínima

Rotas principais permitidas:

```text
/login
/app/dashboard
/app/obras
/app/obras/:obraId/workspace
/app/admin
/app/configuracoes
```

Evitar rotas como:

```text
/app/fiscais/novo
/app/fornecedores/novo
/app/empreiteiros/novo
/app/tags/novo
/app/feriados/novo
```

Esses casos devem ser modais.

---

### 2.5 Multi-tenant obrigatório

O frontend deve assumir que o backend é multi-tenant.

Regras obrigatórias:

```text
- Usuário não pode visualizar dados de outra empresa.
- Usuário não pode visualizar obra sem permissão.
- Usuário não pode acessar módulo sem licença/permissão.
- Toda requisição deve carregar o contexto da empresa ativa.
- A empresa ativa deve ser explicitamente controlada no frontend.
- A obra ativa deve ser explicitamente controlada no workspace.
- Não confiar apenas no frontend para segurança, mas não exibir ações não permitidas.
```

O frontend deve tratar respostas `401`, `403` e `404` de forma clara.

---

## 3. Stack frontend

```text
Angular 19
TypeScript
Angular Router
Angular Signals
Angular Reactive Forms
Angular HttpClient
Angular Material ou PrimeNG
RxJS
Tailwind CSS opcional
Chart.js / ECharts / ngx-charts para dashboards
OpenAPI Generator opcional
ESLint
Prettier
Playwright ou Cypress
Jest/Vitest para testes unitários
```

Preferência arquitetural:

```text
- Standalone Components
- Signals para estado local e stores leves
- Lazy loading por feature
- Rotas protegidas por guards
- Interceptors para autenticação, tenant e erros
- Componentes burros + facade/store por feature
```

---

## 4. Estrutura sugerida de pastas

```text
src/app/
  core/
    auth/
    tenant/
    permissions/
    http/
    guards/
    interceptors/
    layout/
    errors/

  shared/
    components/
    dialogs/
    drawers/
    forms/
    tables/
    upload/
    money/
    pipes/
    directives/

  features/
    dashboard/
    obras/
    obra-workspace/
    orcamentos/
    base-composicoes/
    planejamento/
    diario-obras/
    compras/
    medicoes/
    documentos-cde/
    relatorios-bi/
    admin/

  api/
    generated/
    models/
    services/
```

---

## 5. Layout principal

### 5.1 Shell da aplicação

O shell deve conter:

```text
- Topbar
- Seletor de empresa ativa
- Usuário logado
- Menu compacto
- Breadcrumb contextual
- Notificações
- Atalhos rápidos
```

### 5.2 Menu lateral enxuto

Menu principal sugerido:

```text
Dashboard
Obras
Administração
Configurações
```

Não criar menu lateral com todos os cadastros.

Cadastros auxiliares devem aparecer no contexto do fluxo onde são usados.

---

## 6. Workspace da Obra

### 6.1 Objetivo

O workspace deve ser a área central de trabalho da obra.

Rota:

```text
/app/obras/:obraId/workspace
```

### 6.2 Header do workspace

Deve exibir:

```text
- Nome da obra
- Status da obra
- Responsável técnico
- Orçamento vigente
- Progresso planejado
- Progresso realizado
- Valor orçado
- Valor comprado
- Valor medido
- Ações rápidas
```

### 6.3 Abas internas

```text
Visão Geral
Orçamento
Planejamento
Diário
Compras
Medições
Documentos
Relatórios
Configurações
```

### 6.4 Ações rápidas

```text
+ Novo orçamento
+ Novo diário
+ Nova medição
+ Novo pedido de compra
+ Novo documento
+ Novo membro
+ Novo fiscal
+ Novo empreiteiro
```

Ações simples devem abrir modal.

---

## 7. Fluxos principais do frontend

# Fluxo 01 — Login, empresa ativa e permissões

## Objetivo

Permitir que o usuário entre no sistema, selecione a empresa ativa e carregue permissões/licenças.

## Jornada

```text
Usuário acessa login
→ autentica
→ sistema carrega empresas vinculadas
→ usuário escolhe empresa ativa se houver mais de uma
→ sistema carrega permissões e módulos licenciados
→ usuário acessa dashboard
```

## Telas/componentes

```text
LoginPage
CompanySelectorDialog
AppShell
PermissionGuard
TenantContextStore
```

## Regras UX

```text
- Se usuário possuir apenas uma empresa, selecionar automaticamente.
- Se possuir várias empresas, abrir modal de seleção.
- Exibir claramente empresa ativa no topo.
- Bloquear módulos não licenciados.
- Ocultar ações sem permissão.
```

## Tasks

```markdown
- [ ] Criar app Angular 19 standalone
- [ ] Configurar roteamento base
- [ ] Criar LoginPage
- [ ] Criar AuthService
- [ ] Criar AuthStore com signals
- [ ] Criar TenantContextStore
- [ ] Criar CompanySelectorDialog
- [ ] Criar PermissionStore
- [ ] Criar AuthGuard
- [ ] Criar TenantGuard
- [ ] Criar PermissionGuard
- [ ] Criar HttpAuthInterceptor
- [ ] Criar TenantInterceptor para enviar empresa ativa
- [ ] Criar tratamento global para 401
- [ ] Criar tratamento global para 403
- [ ] Criar layout AppShell
- [ ] Criar seletor de empresa na topbar
- [ ] Criar testes de login
- [ ] Criar testes de troca de empresa ativa
- [ ] Criar testes de bloqueio por permissão
```

---

# Fluxo 02 — Dashboard operacional

## Objetivo

Dar visão inicial das obras e pendências do usuário.

## Jornada

```text
Usuário entra no sistema
→ vê obras recentes
→ vê pendências de aprovação
→ vê medições aguardando ação
→ vê pedidos aguardando ação
→ acessa rapidamente uma obra
```

## Componentes

```text
DashboardPage
RecentWorksCard
PendingApprovalsCard
FinancialSummaryCard
PlanningProgressCard
QuickActionsPanel
```

## Tasks

```markdown
- [ ] Criar DashboardPage
- [ ] Criar card de obras recentes
- [ ] Criar card de pendências do usuário
- [ ] Criar card de orçamento x comprado x medido
- [ ] Criar card de progresso planejado x realizado
- [ ] Criar ações rápidas baseadas em permissão
- [ ] Criar skeleton loading
- [ ] Criar estado vazio
- [ ] Criar tratamento de erro
- [ ] Criar testes dos cards
```

---

# Fluxo 03 — Criar e gerenciar obra sem sair da lista

## Objetivo

Permitir criar obra rapidamente e entrar no workspace.

## Jornada

```text
Usuário acessa Obras
→ pesquisa obras
→ clica em Nova Obra
→ modal abre
→ usuário informa dados mínimos
→ obra é criada
→ sistema pergunta se deseja abrir workspace
```

## Componentes

```text
WorksPage
WorksTable
WorkCardList
CreateWorkDialog
EditWorkDrawer
WorkStatusBadge
```

## Regras UX

```text
- Nova obra deve ser modal.
- Edição rápida deve ser drawer.
- Lista deve permitir busca, filtros e status.
- Acesso ao workspace deve ser claro.
```

## Tasks

```markdown
- [ ] Criar WorksPage
- [ ] Criar WorksTable
- [ ] Criar filtros por status, responsável e período
- [ ] Criar CreateWorkDialog
- [ ] Criar formulário reativo de obra
- [ ] Criar EditWorkDrawer
- [ ] Criar validações de campos obrigatórios
- [ ] Criar ação para abrir workspace
- [ ] Criar confirmação de exclusão/cancelamento
- [ ] Criar testes de criação de obra
- [ ] Criar testes de filtros
- [ ] Criar testes de permissão para criar obra
```

---

# Fluxo 04 — Workspace da Obra

## Objetivo

Centralizar todos os módulos operacionais da obra em uma única experiência SPA.

## Jornada

```text
Usuário abre uma obra
→ vê resumo da obra
→ acessa abas internas
→ executa ações sem sair do workspace
→ mantém contexto da obra ativo
```

## Componentes

```text
WorkWorkspacePage
WorkWorkspaceHeader
WorkWorkspaceTabs
WorkQuickActions
WorkContextStore
WorkSummaryPanel
```

## Tasks

```markdown
- [ ] Criar rota /app/obras/:obraId/workspace
- [ ] Criar WorkContextStore
- [ ] Carregar dados da obra ativa
- [ ] Carregar permissões da obra
- [ ] Criar header fixo da obra
- [ ] Criar abas internas do workspace
- [ ] Criar ações rápidas contextuais
- [ ] Criar proteção contra acesso a obra não autorizada
- [ ] Criar breadcrumbs contextuais
- [ ] Criar skeleton loading do workspace
- [ ] Criar estado de obra não encontrada
- [ ] Criar testes de carregamento do workspace
- [ ] Criar testes de obra sem permissão
```

---

# Fluxo 05 — Configurações rápidas da obra

## Objetivo

Permitir configurar membros, fiscais, empreiteiros e anexos dentro do workspace.

## Jornada

```text
Usuário está no workspace
→ abre aba Configurações
→ adiciona membro/fiscal/empreiteiro por modal
→ anexa documentos
→ tudo permanece no contexto da obra
```

## Componentes

```text
WorkSettingsTab
WorkMembersPanel
WorkFiscalPanel
WorkContractorPanel
WorkAttachmentsPanel
AddMemberDialog
AddFiscalDialog
AddContractorDialog
AttachmentUploadDialog
```

## Tasks

```markdown
- [ ] Criar WorkSettingsTab
- [ ] Criar painel de membros
- [ ] Criar AddMemberDialog
- [ ] Criar painel de fiscais
- [ ] Criar AddFiscalDialog
- [ ] Criar painel de empreiteiros
- [ ] Criar AddContractorDialog
- [ ] Criar painel de anexos
- [ ] Criar upload de anexos
- [ ] Criar ações de remover/vincular/desvincular
- [ ] Criar validação de permissões por papel
- [ ] Criar testes de modais
- [ ] Criar testes de upload
```

---

# Fluxo 06 — Base própria dentro do contexto do orçamento

## Objetivo

Permitir criar insumos e composições próprias sem interromper a montagem do orçamento.

## Jornada

```text
Usuário monta orçamento
→ precisa de um insumo próprio
→ clica em + Novo Insumo
→ modal abre
→ insumo é criado
→ volta automaticamente para seleção do item
```

## Componentes

```text
OwnBaseQuickSearch
CreateOwnInputDialog
CreateOwnCompositionDialog
ImportOwnInputsDialog
CompositionDetailsDrawer
```

## Regras UX

```text
- Insumo próprio simples deve ser criado por modal.
- Composição complexa pode usar drawer.
- Importação XLSX deve ser modal com etapa de validação.
- Resultado de importação deve mostrar erros sem trocar de página.
```

## Tasks

```markdown
- [ ] Criar busca rápida de insumos próprios
- [ ] Criar CreateOwnInputDialog
- [ ] Criar CreateOwnCompositionDialog
- [ ] Criar CompositionDetailsDrawer
- [ ] Criar ImportOwnInputsDialog
- [ ] Criar ImportOwnCompositionsDialog
- [ ] Criar preview de importação
- [ ] Criar tabela de erros de importação
- [ ] Criar ação de atualizar preço
- [ ] Criar alerta de orçamento com item próprio desatualizado
- [ ] Criar testes de criação rápida de insumo
- [ ] Criar testes de importação com erro
```

---

# Fluxo 07 — Montar orçamento da obra

## Objetivo

Permitir montar orçamento dentro do workspace, com edição fluida e sem navegação excessiva.

## Jornada

```text
Usuário abre aba Orçamento
→ cria novo orçamento se não existir
→ define dados base em modal/wizard compacto
→ cria etapas inline
→ adiciona composições/insumos por drawer lateral
→ edita quantidades direto na tabela
→ visualiza totais em painel fixo
→ aprova orçamento
→ marca como vigente
```

## Componentes

```text
BudgetTab
BudgetHeader
BudgetVersionSelector
CreateBudgetDialog
BudgetTreeGrid
BudgetItemDrawer
AddBudgetItemDrawer
BudgetTotalsPanel
BudgetWarningsPanel
BudgetApprovalDialog
```

## Regras UX

```text
- Orçamento deve ser editável em árvore/tabela.
- Criar etapa deve ser inline.
- Adicionar item deve abrir drawer, não trocar página.
- Totais devem estar sempre visíveis.
- Alertas de data base/divergência devem aparecer no contexto.
```

## Tasks

```markdown
- [ ] Criar BudgetTab no workspace
- [ ] Criar CreateBudgetDialog
- [ ] Criar seletor de versão do orçamento
- [ ] Criar BudgetTreeGrid
- [ ] Implementar criação inline de etapa
- [ ] Implementar edição inline de quantidade
- [ ] Implementar edição inline de descrição quando permitido
- [ ] Criar AddBudgetItemDrawer
- [ ] Criar busca de composição oficial
- [ ] Criar busca de composição própria
- [ ] Criar busca de insumo próprio
- [ ] Criar BudgetItemDrawer para detalhes
- [ ] Criar painel de totais fixo
- [ ] Criar painel de alertas
- [ ] Criar ação de duplicar item
- [ ] Criar ação de excluir item para lixeira
- [ ] Criar ação de restaurar item
- [ ] Criar BudgetApprovalDialog
- [ ] Criar ação de marcar orçamento vigente
- [ ] Criar testes de criação de orçamento
- [ ] Criar testes de edição inline
- [ ] Criar testes de adicionar composição
- [ ] Criar testes de aprovação
```

---

# Fluxo 08 — Revisar orçamento, ajuste linear, curva ABC e relatórios

## Objetivo

Dar ferramentas de revisão sem tirar o usuário do orçamento.

## Jornada

```text
Usuário está no orçamento
→ abre painel de ferramentas
→ aplica ajuste linear com preview
→ consulta curva ABC
→ gera relatório
→ exporta PDF/Excel
```

## Componentes

```text
BudgetToolsPanel
LinearAdjustmentDialog
LinearAdjustmentPreview
AbcCurveDrawer
BudgetReportsDialog
ReportCustomizationDialog
```

## Tasks

```markdown
- [ ] Criar BudgetToolsPanel
- [ ] Criar LinearAdjustmentDialog
- [ ] Criar preview de ajuste linear
- [ ] Criar comparação antes/depois
- [ ] Criar alerta de itens críticos
- [ ] Criar confirmação de ajuste
- [ ] Criar AbcCurveDrawer
- [ ] Criar filtros da curva ABC
- [ ] Criar BudgetReportsDialog
- [ ] Criar personalização de relatório
- [ ] Criar download PDF/Excel
- [ ] Criar testes de preview de ajuste
- [ ] Criar testes de curva ABC
- [ ] Criar testes de exportação
```

---

# Fluxo 09 — Planejar a obra a partir do orçamento

## Objetivo

Transformar orçamento vigente em cronograma dentro do workspace.

## Jornada

```text
Usuário abre aba Planejamento
→ cria planejamento a partir do orçamento vigente
→ define calendário e jornada
→ atividades são geradas
→ usuário ajusta durações e predecessores
→ publica planejamento
```

## Componentes

```text
PlanningTab
CreatePlanningDialog
PlanningGantt
PlanningActivityGrid
PlanningCalendarDialog
PlanningPredecessorDrawer
PlanningSyncAlert
```

## Regras UX

```text
- Criação do planejamento deve ser wizard/modal.
- Edição de atividade deve ser inline ou drawer.
- Predecessores devem ser editados no contexto da atividade.
- Alterações pendentes do orçamento devem aparecer como alerta.
```

## Tasks

```markdown
- [ ] Criar PlanningTab
- [ ] Criar CreatePlanningDialog
- [ ] Criar wizard de calendário/jornada
- [ ] Criar inicialização por orçamento vigente
- [ ] Criar PlanningActivityGrid
- [ ] Criar edição inline de duração
- [ ] Criar edição inline de datas quando permitido
- [ ] Criar PlanningPredecessorDrawer
- [ ] Criar validação visual de ciclos
- [ ] Criar PlanningGantt simples
- [ ] Criar publicação do planejamento
- [ ] Criar alerta de orçamento alterado
- [ ] Criar preview de sincronização orçamento x planejamento
- [ ] Criar testes de criação do planejamento
- [ ] Criar testes de edição de duração
- [ ] Criar testes de predecessores
```

---

# Fluxo 10 — Diário de Obras alimentando execução

## Objetivo

Registrar execução diária com ligação direta ao planejamento.

## Jornada

```text
Usuário abre aba Diário
→ escolhe data
→ cria diário
→ copia último diário se desejar
→ adiciona tarefas do planejamento
→ informa progresso/status
→ registra materiais/fotos/ocorrências
→ fecha diário
→ planejamento é atualizado
```

## Componentes

```text
DailyLogTab
DailyLogCalendar
CreateDailyLogDialog
DailyLogEditor
PlanningTasksPickerDialog
MaterialMovementDialog
PhotoGalleryUploader
DailyLogCloseDialog
```

## Regras UX

```text
- Diário deve ser editado na própria aba.
- Seleção de tarefas do planejamento deve ser modal.
- Fotos devem usar uploader/galeria.
- Fechamento deve pedir confirmação.
```

## Tasks

```markdown
- [ ] Criar DailyLogTab
- [ ] Criar calendário/lista de diários
- [ ] Criar CreateDailyLogDialog
- [ ] Criar opção copiar último diário
- [ ] Criar DailyLogEditor
- [ ] Criar PlanningTasksPickerDialog
- [ ] Criar edição de progresso/status
- [ ] Criar MaterialMovementDialog
- [ ] Criar upload de fotos
- [ ] Criar galeria de fotos
- [ ] Criar registro de ocorrências
- [ ] Criar fechamento de diário
- [ ] Atualizar visualmente progresso do planejamento após fechamento
- [ ] Criar testes de criação de diário
- [ ] Criar testes de seleção de tarefa
- [ ] Criar testes de upload de fotos
```

---

# Fluxo 11 — Compras a partir do orçamento e curva ABC

## Objetivo

Permitir comprar materiais/serviços previstos sem navegar para vários cadastros.

## Jornada

```text
Usuário abre aba Compras
→ cria pedido
→ adiciona itens do orçamento/curva ABC
→ adiciona fornecedor por modal se necessário
→ envia para aprovação técnica
→ compras envia cotação
→ fornecedor responde por link externo
→ usuário compara preços
→ envia financeiro
→ emite ordem de compra
```

## Componentes

```text
PurchasesTab
PurchaseRequestsBoard
CreatePurchaseRequestDialog
BudgetItemsPickerDialog
AbcItemsPickerDialog
SupplierQuickCreateDialog
QuotationDrawer
SupplierQuotationPublicPage
QuotationComparisonTable
PurchaseApprovalPanel
PurchaseOrderDialog
```

## Regras UX

```text
- Pedido pode ser criado por modal.
- Itens do orçamento devem ser selecionados por modal de busca.
- Fornecedor novo deve ser criado sem sair do pedido.
- Cotação deve abrir em drawer.
- Comparativo de preços deve ser visual e direto.
```

## Tasks

```markdown
- [ ] Criar PurchasesTab
- [ ] Criar board/lista de pedidos por status
- [ ] Criar CreatePurchaseRequestDialog
- [ ] Criar BudgetItemsPickerDialog
- [ ] Criar AbcItemsPickerDialog
- [ ] Criar SupplierQuickCreateDialog
- [ ] Criar formulário de pedido
- [ ] Criar fluxo de envio para aprovação técnica
- [ ] Criar painel de aprovação técnica
- [ ] Criar QuotationDrawer
- [ ] Criar seleção de fornecedores
- [ ] Criar envio de cotação
- [ ] Criar página pública de resposta por token
- [ ] Criar QuotationComparisonTable
- [ ] Criar seleção de vencedor
- [ ] Criar envio ao financeiro
- [ ] Criar aprovação financeira
- [ ] Criar PurchaseOrderDialog
- [ ] Criar testes de fluxo de pedido
- [ ] Criar testes de fornecedor rápido
- [ ] Criar testes de cotação
```

---

# Fluxo 12 — Medição da execução

## Objetivo

Medir serviços executados com base no orçamento, diário e aprovação fiscal.

## Jornada

```text
Usuário abre aba Medições
→ cria medição
→ seleciona itens do orçamento
→ informa quantidades executadas
→ preenche memória de cálculo
→ adiciona fotos/anexos
→ adiciona serviço não orçado/aditivo
→ envia para aprovação
→ fiscal aprova ou rejeita
```

## Componentes

```text
MeasurementsTab
MeasurementsList
CreateMeasurementDialog
MeasurementEditor
BudgetMeasurementItemsPicker
MeasurementMemoryDrawer
MeasurementPhotoUploader
ExtraServiceDialog
AdditiveDialog
MeasurementApprovalPanel
MeasurementReportDialog
```

## Regras UX

```text
- Criar medição deve ser modal.
- Edição de itens medidos deve acontecer em tabela.
- Memória de cálculo deve abrir em drawer.
- Serviço não orçado e aditivo devem ser modais.
- Aprovação fiscal deve ser painel de ação claro.
```

## Tasks

```markdown
- [ ] Criar MeasurementsTab
- [ ] Criar MeasurementsList
- [ ] Criar CreateMeasurementDialog
- [ ] Criar MeasurementEditor
- [ ] Criar BudgetMeasurementItemsPicker
- [ ] Criar tabela de itens medidos
- [ ] Criar edição inline de quantidade medida
- [ ] Criar cálculo visual de acumulado e saldo
- [ ] Criar MeasurementMemoryDrawer
- [ ] Criar upload de fotos/anexos
- [ ] Criar ExtraServiceDialog
- [ ] Criar AdditiveDialog
- [ ] Criar envio para aprovação
- [ ] Criar painel de aprovação fiscal
- [ ] Criar rejeição com justificativa
- [ ] Criar relatório de medição
- [ ] Criar testes de criação de medição
- [ ] Criar testes de memória de cálculo
- [ ] Criar testes de aprovação/rejeição
```

---

# Fluxo 13 — Documentos / CDE

## Objetivo

Centralizar documentos da obra e versões.

## Jornada

```text
Usuário abre Documentos
→ cria pasta ou projeto
→ envia arquivo
→ controla versão
→ vincula documento a orçamento, diário, medição ou compra
```

## Componentes

```text
DocumentsTab
DocumentExplorer
CreateFolderDialog
UploadDocumentDialog
DocumentVersionDrawer
DocumentLinkDialog
```

## Tasks

```markdown
- [ ] Criar DocumentsTab
- [ ] Criar explorador de documentos
- [ ] Criar CreateFolderDialog
- [ ] Criar UploadDocumentDialog
- [ ] Criar controle de versão
- [ ] Criar DocumentVersionDrawer
- [ ] Criar vínculo de documento com entidades da obra
- [ ] Criar permissões por documento/pasta
- [ ] Criar testes de upload
- [ ] Criar testes de versionamento
```

---

# Fluxo 14 — Relatórios e BI da obra

## Objetivo

Consolidar orçamento, planejamento, diário, compras e medição.

## Jornada

```text
Usuário abre Relatórios
→ escolhe tipo de relatório
→ aplica filtros
→ visualiza indicadores
→ exporta PDF/Excel
```

## Componentes

```text
ReportsTab
WorkIndicatorsDashboard
BudgetVsMeasuredChart
PlannedVsActualChart
PurchasedVsBudgetChart
ReportsCatalog
ReportFilterDialog
ReportDownloadPanel
```

## Tasks

```markdown
- [ ] Criar ReportsTab
- [ ] Criar dashboard de indicadores da obra
- [ ] Criar gráfico orçado x medido
- [ ] Criar gráfico planejado x realizado
- [ ] Criar gráfico comprado x orçado
- [ ] Criar catálogo de relatórios
- [ ] Criar filtros de relatório
- [ ] Criar exportação PDF/Excel
- [ ] Criar estado de relatório em processamento
- [ ] Criar testes dos indicadores
- [ ] Criar testes de filtros
```

---

# Fluxo 15 — Administração sem poluir a operação

## Objetivo

Permitir administrar empresa, usuários, setores, permissões e licenças fora do fluxo operacional da obra.

## Jornada

```text
Administrador acessa Administração
→ gerencia usuários
→ gerencia setores
→ configura permissões
→ consulta módulos licenciados
→ configura encargos sociais
```

## Componentes

```text
AdminPage
UsersAdminPanel
SectorsAdminPanel
PermissionsAdminPanel
LicensesAdminPanel
SocialChargesPanel
```

## Tasks

```markdown
- [ ] Criar AdminPage
- [ ] Criar painel de usuários
- [ ] Criar AddUserDialog
- [ ] Criar painel de setores
- [ ] Criar AddSectorDialog
- [ ] Criar painel de permissões
- [ ] Criar matriz de permissões
- [ ] Criar painel de licenças/módulos
- [ ] Criar painel de encargos sociais
- [ ] Criar testes de permissão administrativa
```

---

## 8. Componentes compartilhados obrigatórios

```text
ConfirmDialog
FormDialogLayout
DrawerLayout
EntityAutocomplete
MoneyInput
PercentInput
QuantityInput
DateRangeFilter
StatusBadge
PermissionDirective
TenantAwarePage
EmptyState
ErrorState
LoadingSkeleton
FileUploader
InlineEditableCell
AuditTimeline
```

Tasks:

```markdown
- [ ] Criar ConfirmDialog reutilizável
- [ ] Criar FormDialogLayout reutilizável
- [ ] Criar DrawerLayout reutilizável
- [ ] Criar EntityAutocomplete genérico
- [ ] Criar MoneyInput
- [ ] Criar PercentInput
- [ ] Criar QuantityInput
- [ ] Criar DateRangeFilter
- [ ] Criar StatusBadge
- [ ] Criar diretiva *hasPermission
- [ ] Criar EmptyState
- [ ] Criar ErrorState
- [ ] Criar LoadingSkeleton
- [ ] Criar FileUploader
- [ ] Criar InlineEditableCell
- [ ] Criar AuditTimeline
```

---

## 9. Estado da aplicação

### 9.1 Stores obrigatórios

```text
AuthStore
TenantContextStore
PermissionStore
WorkContextStore
BudgetStore
PlanningStore
DailyLogStore
PurchaseStore
MeasurementStore
NotificationStore
```

### 9.2 Regras

```text
- AuthStore mantém usuário autenticado.
- TenantContextStore mantém empresa ativa.
- PermissionStore mantém permissões por empresa/módulo/obra.
- WorkContextStore mantém obra ativa.
- Stores de feature não devem vazar dados entre obras.
- Ao trocar empresa ativa, limpar obra ativa e caches de módulos.
- Ao trocar obra ativa, limpar stores do workspace anterior.
```

Tasks:

```markdown
- [ ] Criar AuthStore
- [ ] Criar TenantContextStore
- [ ] Criar PermissionStore
- [ ] Criar WorkContextStore
- [ ] Criar stores por feature
- [ ] Implementar limpeza de estado ao trocar empresa
- [ ] Implementar limpeza de estado ao trocar obra
- [ ] Criar testes de isolamento de estado
```

---

## 10. Segurança frontend

O frontend não é fonte final de segurança, mas deve impedir exposição acidental.

Regras:

```text
- Não renderizar menu de módulo não licenciado.
- Não renderizar botão sem permissão.
- Não manter dados de empresa anterior no estado após troca de empresa.
- Não reaproveitar cache entre tenants.
- Não usar localStorage para dados sensíveis de domínio.
- Tratar 403 com mensagem clara.
- Tratar 404 de entidade como possível falta de acesso.
```

Tasks:

```markdown
- [ ] Criar PermissionDirective
- [ ] Criar ModuleLicenseDirective
- [ ] Criar TenantGuard
- [ ] Criar WorkAccessGuard
- [ ] Criar limpeza segura de cache
- [ ] Criar tratamento de 403
- [ ] Criar tratamento de 404 contextual
- [ ] Criar testes de ocultação de ação sem permissão
- [ ] Criar testes de troca de tenant
```

---

## 11. API client

Preferência:

```text
- Gerar client por OpenAPI quando backend estiver estável.
- Enquanto não houver OpenAPI estável, criar services tipados manualmente.
```

Padrão:

```text
FeatureApiService → FeatureFacade/Store → Component
```

Evitar:

```text
Component chamando HttpClient diretamente.
```

Tasks:

```markdown
- [ ] Criar ApiConfig
- [ ] Criar HttpAuthInterceptor
- [ ] Criar TenantInterceptor
- [ ] Criar ErrorInterceptor
- [ ] Criar serviços API por feature
- [ ] Criar DTOs tipados
- [ ] Preparar integração com OpenAPI Generator
```

---

## 12. Testing strategy

### Unit tests

```text
- Stores
- Guards
- Interceptors
- Form validators
- Pipes
- Componentes compartilhados
```

### Integration/component tests

```text
- Modais de criação
- Drawers de edição
- Tabelas inline
- Workspace da obra
- Fluxos com permissão
```

### E2E tests

Fluxos obrigatórios:

```text
1. Login e seleção de empresa
2. Criar obra
3. Abrir workspace
4. Criar orçamento
5. Adicionar item ao orçamento
6. Aprovar orçamento
7. Criar planejamento
8. Criar diário e atualizar progresso
9. Criar pedido de compra
10. Criar medição e enviar para aprovação
11. Fiscal aprova medição
12. Trocar empresa e garantir isolamento visual
```

Tasks:

```markdown
- [ ] Configurar testes unitários
- [ ] Configurar Playwright ou Cypress
- [ ] Criar mocks de API
- [ ] Criar fixture de usuário multiempresa
- [ ] Criar fixture de usuário sem permissão
- [ ] Criar E2E de login
- [ ] Criar E2E de workspace
- [ ] Criar E2E de orçamento
- [ ] Criar E2E de diário
- [ ] Criar E2E de compras
- [ ] Criar E2E de medição
- [ ] Criar E2E de isolamento multi-tenant
```

---

## 13. Definition of Done frontend

Uma task só pode ser considerada pronta se:

```text
- Está vinculada a um fluxo real.
- Não cria navegação desnecessária.
- Usa modal/drawer para cadastro simples.
- Respeita empresa ativa.
- Respeita obra ativa quando aplicável.
- Respeita permissões/licença.
- Tem estado vazio.
- Tem loading.
- Tem tratamento de erro.
- Tem teste mínimo.
- Não deixa cache de outro tenant.
- Não acessa HttpClient diretamente no componente.
```

---

## 14. Prompt/steering para IA de desenvolvimento frontend

```text
Você é um Tech Lead Frontend especialista em Angular 19, SPA corporativa, UX de sistemas complexos e arquitetura multi-tenant.

Você está desenvolvendo o projeto gestao-de-obras-frontend.

Objetivo principal:
Criar uma verdadeira SPA de gestão de obras, com navegação mínima entre páginas, fluxo centralizado na Obra e cadastros simples resolvidos por modais, drawers, autocomplete, inline edit ou quick actions.

Regras absolutas:

1. A entidade central da experiência é Obra.
2. Após abrir uma obra, o usuário deve trabalhar no Workspace da Obra.
3. Não criar telas CRUD soltas sem relação com fluxo operacional.
4. Cadastros simples devem ser modais.
5. Detalhes e edições contextuais devem ser drawers.
6. Tabelas operacionais devem permitir edição inline quando seguro.
7. O menu principal deve ser enxuto.
8. O sistema é multi-tenant.
9. Usuário não pode ver dados de outra empresa.
10. Usuário não pode ver obra sem permissão.
11. Módulo sem licença/permissão não deve aparecer.
12. Toda chamada de API deve carregar empresa ativa.
13. Ao trocar empresa ativa, limpar obra ativa e caches de features.
14. Ao trocar obra ativa, limpar estado do workspace anterior.
15. Não usar localStorage para dados sensíveis de domínio.
16. Componentes não devem chamar HttpClient diretamente.
17. Use services/facades/stores por feature.
18. Use Angular standalone components.
19. Use Angular Signals para estado local/store leve.
20. Use Reactive Forms para formulários.
21. Toda tela deve ter loading, erro e empty state.
22. Toda ação crítica deve pedir confirmação.
23. Toda task deve ter teste mínimo.

Fluxo funcional obrigatório:

Login
→ Selecionar empresa ativa
→ Dashboard
→ Obras
→ Workspace da Obra
→ Orçamento
→ Planejamento
→ Diário
→ Compras
→ Medições
→ Relatórios

Não implemente páginas como /fornecedores/novo, /fiscais/novo, /tags/novo ou /feriados/novo.
Esses cadastros devem ser contextuais e resolvidos por modal no fluxo onde são usados.

Antes de implementar qualquer componente, responda:
- Qual fluxo esse componente suporta?
- Ele precisa ser página, modal, drawer ou componente inline?
- Ele depende de empresa ativa?
- Ele depende de obra ativa?
- Quais permissões controlam sua visibilidade?
- Qual estado precisa ser limpo ao trocar empresa ou obra?
```

---

## 15. Ordem ideal de implementação

```text
1. Setup Angular 19
2. Shell, autenticação, empresa ativa e permissões
3. Dashboard
4. Listagem e criação de obras
5. Workspace da obra
6. Configurações rápidas da obra
7. Orçamento
8. Base própria contextual
9. Planejamento
10. Diário de obras
11. Compras
12. Medições
13. Documentos/CDE
14. Relatórios/BI
15. Testes E2E completos
```

---

## 16. Resultado esperado

Ao final, o frontend deve parecer uma aplicação moderna de trabalho operacional:

```text
Poucas rotas
Pouco menu
Muito contexto
Muitas ações rápidas
Modais para cadastros pequenos
Drawers para detalhes
Workspace centralizado por obra
Permissões e tenant respeitados
Fluxos completos e não CRUDs soltos
```

O usuário deve sentir que está trabalhando em uma obra, não navegando por centenas de telas administrativas.
