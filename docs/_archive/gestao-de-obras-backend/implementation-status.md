# Status de Implementação - Tasks de Gestão de Obras

Data: 2026-05-16

## Escopo auditado

- Medição (fluxo avançado: rejeição, histórico, memória de cálculo, item extra)
- Diário de obra (alinhamento DTO + operações rápidas + atraso climático)
- Compras (overdue, envio de cotação, mapa comparativo, geração por ABC)
- Orçamento/planejamento/perfil (itens já alterados no ciclo atual)
- Cadastros mestres (fornecedores)

## Entregas implementadas

### Medição

- Backend:
  - Endpoints adicionados em `MeasurementController`:
    - `POST /reject`
    - `GET /history`
    - `GET /items/{itemId}/memo`
    - `PUT /items/{itemId}/memo`
    - `POST /extra-items`
  - Resposta de medição com `rejectionReason`.
  - Resposta de item com `extra` e `contractorName`.
- Frontend:
  - `MeasurementService`: `getMemo`, `saveMemo`, `addExtraItem`.
  - `MeasurementService`: `history`.
  - Tela de detalhe:
    - ação de memória por item;
    - inclusão de item extra;
    - aprovação/rejeição na própria tela;
    - timeline de histórico de aprovação.
  - Tela de lista (kanban):
    - submeter/aprovar/rejeitar;
    - motivo de rejeição visível no card;
    - anexos e boletim PDF.

### Diário de obra

- Frontend:
  - Modelos alinhados ao contrato backend:
    - `logDate`, `weatherMorning`, `weatherAfternoon`, `observations`
    - `occurrenceCount`, `photoCount`
  - Formulário envia payload aderente ao backend.
  - Lista atualizada para novos campos.
  - Detalhe com inclusão rápida de mão de obra, equipamento, ocorrência, foto e registro/listagem de atraso climático.
- Backend:
  - Hardening por `projectId` aplicado nos endpoints de detalhe, inclusão rápida e relatório PDF, bloqueando acesso cruzado entre projetos.

### Compras

- Frontend:
  - `ProcurementService` com:
    - `listOverdue`
    - `sendQuotationEmail`
    - `comparativeMapUrl`
    - `generateFromAbc`
  - Lista de compras com visualização de atrasados e geração de compra a partir de itens ABC.

### Outras alterações no ciclo

- Orçamento:
  - diálogo de data-base;
  - diálogo de memória de orçamento;
  - ação de relatório analítico;
  - ajustes de BDI por tipo com persistência/alimentação ponta a ponta.
- Planejamento:
  - evolução da tela para painel de acompanhamento (baseline, tracking, distribuição, curva S).
- Perfil:
  - ajustes de i18n.
- Cadastros:
  - fornecedor elevado de cadastro raso para cadastro mestre com contato principal, website, categoria, status de homologação, prazo de pagamento, lead time, endereço comercial e observações.
  - frontend de fornecedor atualizado para refletir o contrato ampliado em lista e formulário.
  - migração dedicada adicionada para evolução do schema de `supplier`.
  - funcionário/empreiteiro elevado para cadastro mestre com código, especialidade, status de vínculo, celular, contato de emergência, endereço, centro de custo, empresa contratada e observações.
  - CRUD frontend aberto para funcionário/empreiteiro com rota de novo/edição.
  - migração dedicada adicionada para evolução do schema de `employee`.
  - equipes passaram a consumir `employeeId` e `projectId` válidos, com DTO estável no backend e criação/edição reais no frontend.

## Testes criados nesta etapa

### Backend

- `api/src/test/java/com/sinapipro/api/measurement/MeasurementControllerIntegrationTest.java`
  - valida rejeição e histórico;
  - valida gravação/leitura de memória de cálculo;
  - valida inclusão de item extra.
- `api/src/test/java/com/sinapipro/api/measurement/api/MeasurementControllerTest.java`
  - valida unitariamente os fluxos de rejeição/histórico/memo/item extra sem depender de container.
- `api/src/test/java/com/sinapipro/api/supplier/SupplierServiceTest.java`
  - valida criação e atualização do novo contrato de cadastro mestre de fornecedor.
- `api/src/test/java/com/sinapipro/api/registry/api/RegistryControllerTest.java`
  - valida criação, atualização e detalhe do contrato ampliado de funcionário/empreiteiro.
- `api/src/test/java/com/sinapipro/api/team/api/TeamControllerTest.java`
  - valida criação e atualização de equipes usando funcionários reais e vínculo de projeto válido.

### Frontend

- `web/src/app/routes/measurement/services/measurement.service.spec.ts`
  - valida endpoints de memo, item extra e histórico.
- `web/src/app/routes/procurement/services/procurement.service.spec.ts`
  - valida overdue, envio de e-mail, geração por ABC e URL de mapa comparativo.
- `web/src/app/routes/daily-log/services/daily-log.service.spec.ts`
  - valida operações de mão de obra e atraso climático.
- `web/src/app/routes/daily-log/daily-log-form/daily-log-form.spec.ts`
  - valida criação simples e criação com registro de atraso climático subsequente.
- `web/src/app/routes/measurement/measurement-detail/measurement-detail.spec.ts`
  - valida carregamento inicial (detail + history), aprovação e rejeição via UI.
- Ajustes de tipagem em specs de `daily-log-detail`, `measurement-detail`, `procurement-list` e `quotation-list` para compatibilidade com `tsconfig.spec`.
- `web/src/app/routes/budget/budget-worksheet/budget-worksheet.spec.ts`
  - valida carregamento inicial, atualização de data-base e persistência de memória de cálculo.
- `web/src/app/routes/schedule/schedule-list/schedule-list.spec.ts`
  - valida carregamento inicial, criação de baseline, cadastro de feriado e redistribuição de datas.

## Pendências identificadas

- Consolidar checklist de `tasks.md` com marcação `[x]` por fase após validação final de QA funcional.

## Validação de qualidade (auditoria técnica)

Legenda: `OK` = implementado e coerente, `PARCIAL` = funciona mas incompleto, `INCONSISTENTE` = risco de comportamento incorreto.

### Medição

- `OK` Endpoints de rejeição/histórico/memo/item extra existem no backend.
- `OK` Endpoints por `projectId` agora validam vínculo da medição com o projeto informado na rota.
- `OK` Timeline de histórico recebe eventos de `SUBMIT`, `APPROVE` e `REJECT`.
- `OK` Memória de cálculo no detalhe agora carrega linhas existentes e salva acumulando conteúdo.

### Diário de obra

- `OK` Modelo, formulário e listagem foram alinhados para `logDate/weatherMorning/weatherAfternoon/observations`.
- `OK` Fluxo rápido de inclusão no detalhe existe com cobertura básica de componente para carregamento e ações principais.
- `OK` Endpoints do controller agora validam o vínculo do diário com o `projectId` da rota para detalhe, mutações rápidas e PDF.

### Compras

- `OK` Serviço expõe `listOverdue/sendQuotationEmail/comparativeMapUrl/generateFromAbc`.
- `OK` Lista exibe overdue e geração por ABC.
- `OK` Navegação de cotações a partir do pedido contextualizada com `orderId/orderNumber`.
- `OK` Backend de cotações agora aceita filtro por `orderId` e retorna somente a cotação vinculada ao pedido quando aplicável.
- `OK` Hardening por projeto aplicado no controller para endpoints de cotação e pedido (bloqueio de acesso cruzado entre projetos por `projectId`).

### Orçamento/Planejamento/Perfil

- `OK` Há cobertura básica de componente para os novos fluxos de orçamento (data-base, memo) e planejamento (baseline, feriado, distribuição).
- `OK` Ação de memória de orçamento agora persiste via backend (`GET/PUT /budgets/{budgetId}/items/{itemId}/memo`) e consome no frontend.
- `OK` BDI por tipo alinhado entre frontend, controller e schema (`itemType` em `GET/PUT /bdi`, carga dos cinco tipos na UI e migração dedicada).
- `OK` Hardening por orçamento aplicado em operações por `stageId`/`itemId` no controller de orçamento (`list/create/delete`, bulk add e memo).
- `OK` Hardening por projeto aplicado no controller de planejamento para progresso, exclusão, dependências e detalhe de baseline.

### Testes

- `PARCIAL` Testes unitários de serviços/frontend e controller/backend foram criados.
- `OK` Árvore de specs frontend relevante para este ciclo compila em `tsconfig.spec`.
- `OK` Cobertura de componentes pendentes adicionada para `daily-log-detail` e `procurement-list`.
- `OK` Cobertura adicionada para `daily-log-form`.
- `OK` Cobertura adicionada para `budget-worksheet` e `schedule-list`.
- `OK` Cobertura unitária backend ampliada para `BudgetDetailController` nos cenários de isolamento por orçamento e atualização de data-base.
- `OK` Cobertura unitária backend adicionada para `ScheduleController` nos cenários de isolamento por projeto.
- `INCONSISTENTE` Testes de integração documentados não tiveram validação executável no ambiente atual:
  - Frontend bloqueado por versão de Node abaixo do mínimo do Angular CLI.
  - Backend integração bloqueado por ambiente Docker/Testcontainers.
- `OK` Execução de testes unitários backend com classes preview normalizada via Surefire (`--enable-preview`).
