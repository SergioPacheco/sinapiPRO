# SinapiPRO — Specs & Backlog

## Entrega 1: Orçamento Detalhe (vertical completa) ✅ DONE

Fatia vertical ponta-a-ponta: Backend endpoint → Frontend tela → Integração → Teste

### Escopo
- Tela de detalhe do orçamento com etapas e itens
- CRUD de itens (adicionar composição SINAPI, quantidade, BDI)
- Cálculo automático de totais (custo direto, com BDI)
- Curva ABC (top itens por impacto)
- Export Excel da planilha orçamentária

### Tasks
- [ ] Frontend: BudgetDetailComponent (etapas + itens em tabela editável)
- [ ] Frontend: AddItemDialog (busca composição SINAPI + quantidade + BDI)
- [ ] Frontend: ABC Curve chart (ECharts)
- [ ] Frontend: Export Excel button (chama endpoint existente)
- [ ] Backend: endpoint GET /users/me (necessário para AuthService.loadProfile)
- [ ] Teste E2E: fluxo criar orçamento → adicionar itens → ver ABC → exportar

---

## Entrega 2: Financeiro ✅ DONE

- [x] Frontend: FinanceComponent (tabs: Pagar, Receber, Fluxo de Caixa)
- [x] Frontend: PayableListComponent (lista + ações pagar/cancelar)
- [x] Frontend: ReceivableListComponent (lista + ação receber)
- [x] Frontend: CashFlowChart (ECharts - projeção por mês)
- [ ] Backend: ajustar endpoints para filtros por período

---

## Entrega 3: Suprimentos ✅ DONE (já existia em web/)

---

## Entrega 4: Cronograma ✅ DONE

- [x] Frontend: GanttChartComponent (ECharts custom render + caminho crítico)
- [x] Frontend: CriticalPathHighlight (visual no Gantt)
- [ ] Frontend: SCurveChart (planejado vs realizado) — parcial no schedule.component
- [ ] Frontend: BaselineComparison

---

## Entrega 5: Notificações + SSE — parcial (notification-list existe)

- [x] Frontend: NotificationListComponent (já existe)
- [ ] Frontend: NotificationBell no topbar (contador unread)
- [ ] Frontend: SSE connection (EventSource → /api/v1/events/stream)

---

## Entrega 6: Integrações ✅ DONE

- [x] Storage: StorageService interface + LocalStorageService (dev) + S3StorageService (prod/MinIO)
- [x] Email: EmailService interface + SmtpEmailService (prod) + LogEmailService (dev)
- [ ] Bancário: CNAB 240/400 parser para CnabService
- [ ] Weather: integração OpenWeatherMap

---

## Entrega 7: Features Frontend Adicionais ✅ DONE

- [x] Analytics (EVM + DRE + Portfólio com ECharts gauge)
- [x] Aftersales (tickets pós-obra com workflow)
- [x] Delivery (checklists de entrega)
- [x] Supplier Portal (cotações editáveis + pedidos)

---

## Entrega 8: Infra/DevOps ✅ DONE

- [x] Helm chart para Kubernetes (helm/sinapipro/)
- [x] CNAB 240 parser (Cnab240Parser.java)
- [x] Weather API integration (WeatherService + OpenWeatherMap)
- [x] SSE Notification Bell (NotificationBellComponent)
- [ ] Gotenberg sidecar para PDF em prod (config only, não precisa de código)
- [ ] Playwright E2E test suite (futuro)
- [ ] Coverage report JaCoCo (futuro)

---

---

## Stack Frontend
- Angular 19.1 (standalone, signals, lazy routes) — diretório: `web/`
- PrimeNG (componentes UI)
- SCSS dark mode (graphite/navy)
- Apache ECharts (charts: ABC, Cash Flow, EVM)
- Docker: multi-stage (node → nginx)
- **Diretório único**: `web/` (frontend/ e web-legacy/ removidos)

## Convenções
- 1 entrega = 1 fatia vertical (backend + frontend + teste)
- Cada feature = lazy-loaded standalone component
- Permissões verificadas no template via `auth.hasPermission()`
