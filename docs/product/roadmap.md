# Roadmap — SinapiPRO

## Fase 1: MVP ✅ CONCLUÍDA

### Backend
- [x] 30+ módulos de negócio (orçamento, medição, financeiro, suprimentos, etc.)
- [x] RBAC completo (395 endpoints com permissões granulares)
- [x] User auto-provisioning (JWT → AppUser)
- [x] Project-scoped access control
- [x] Storage adapter (Local + S3/MinIO)
- [x] Email adapter (SMTP + fallback log)
- [x] CNAB 240 parser (integração bancária)
- [x] Weather API (OpenWeatherMap)
- [x] Relatórios (PDF via Playwright, Excel via FastExcel)
- [x] 45 testes unitários

### Frontend
- [x] 75+ componentes (Angular 19 + PrimeNG + ECharts)
- [x] Login + Dashboard (EVM KPIs)
- [x] Orçamentos (lista + detalhe + itens + BDI + Curva ABC + export)
- [x] Medições (workflow approve/reject)
- [x] Financeiro (pagar/receber/fluxo de caixa)
- [x] Suprimentos (requisições + cotações + estoque)
- [x] Cronograma (Gantt + CPM)
- [x] Analytics (EVM + DRE + Portfólio)
- [x] Cadastros completos (clientes, fornecedores, funcionários)
- [x] Segurança, RFI, Punch List, Documentos, Equipamentos
- [x] Comercial, Pós-Venda, Entrega de Obra
- [x] Portal do Fornecedor
- [x] Notificações com SSE
- [x] Settings/RBAC (gestão de usuários e roles)

### Infra
- [x] Docker Compose (dev + showcase)
- [x] Helm chart (Kubernetes)
- [x] CI/CD (GitHub Actions)
- [x] Observabilidade (Prometheus + Grafana + OTel)

---

## Fase 2: Polimento (próxima)

- [ ] Testes E2E com Playwright
- [ ] Cobertura JaCoCo > 80%
- [ ] Gotenberg sidecar (PDF em produção)
- [ ] PWA (offline-first para diário de obra em campo)
- [ ] Mobile app (Capacitor ou Flutter)
- [ ] Integração com ERP contábil (exportação fiscal)
- [ ] Assinatura digital (ICP-Brasil) para medições
- [ ] OCR para notas fiscais (upload → extração automática)

---

## Fase 3: Escala

- [ ] Multi-tenant SaaS (onboarding self-service)
- [ ] Marketplace de composições (compartilhar entre empresas)
- [ ] IA para estimativa de custos (ML baseado em histórico)
- [ ] BIM integration (IFC import → orçamento automático)
- [ ] App mobile nativo (campo: fotos, GPS, offline)
