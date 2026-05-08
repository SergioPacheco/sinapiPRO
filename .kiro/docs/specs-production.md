# Specs & Tasks — Produção

> Roadmap para transformar o SinapiPRO API de projeto de portfólio em produto SaaS pronto para construtoras.

---

## Visão Geral

```
FASE 1 — Fundação para Produção (sem isso não roda)
  SPEC P1: Multi-tenancy + Usuários reais
  SPEC P2: Importação SINAPI completa
  SPEC P3: Validações de negócio robustas

FASE 2 — Frontend Angular
  SPEC P4: Projeto Angular 19 + estrutura
  SPEC P5: Módulo de Autenticação
  SPEC P6: Módulo de Orçamento (tela principal)
  SPEC P7: Módulo de Execução (medições, cronograma, diário)

FASE 3 — Produção
  SPEC P8: Relatórios PDF
  SPEC P9: Upload de arquivos (GED)
  SPEC P10: Auditoria e notificações
  SPEC P11: Deploy AWS/Cloud + Infra
```

---

## FASE 1 — Fundação para Produção

### SPEC P1: Multi-tenancy + Usuários + Permissões

**Problema:** Hoje tem 1 usuário in-memory. Construtora tem 10-50 usuários com papéis diferentes.

**Modelo:** Multi-tenant por schema compartilhado com `tenant_id` (discriminator column). Mais simples que schema-per-tenant, escala bem até ~100 tenants.

#### Requisitos
- REQ-P1.1: Tabela `tenant` (construtora) com plano, limites, status
- REQ-P1.2: Tabela `user` com email, senha (bcrypt), tenant_id, status
- REQ-P1.3: Tabela `role` e `user_role` (ADMIN, ENGENHEIRO, ORCAMENTISTA, FINANCEIRO, ESTAGIARIO)
- REQ-P1.4: Tabela `permission` granular (BUDGET_READ, BUDGET_WRITE, MEASUREMENT_APPROVE, etc.)
- REQ-P1.5: Filtro automático por tenant_id em todas as queries (Hibernate Filter ou Spring AOP)
- REQ-P1.6: JWT com claims: tenant_id, user_id, roles, permissions
- REQ-P1.7: Registro de tenant (self-service signup)
- REQ-P1.8: Convite de usuários por email
- REQ-P1.9: Recuperação de senha (token por email)
- REQ-P1.10: LGPD: exportação e exclusão de dados pessoais

#### Tasks
- [ ] Migration: tabelas tenant, app_user, role, user_role, permission, role_permission
- [ ] Entity Tenant, AppUser, Role, Permission
- [ ] TenantContext (ThreadLocal ou RequestScope) para filtro automático
- [ ] Hibernate @Filter ou @Where para tenant isolation
- [ ] Refatorar todas as entities para ter tenant_id
- [ ] Refatorar SecurityConfiguration para autenticar do banco
- [ ] Endpoint POST /api/v1/auth/register (criar tenant + admin user)
- [ ] Endpoint POST /api/v1/auth/forgot-password
- [ ] Endpoint POST /api/v1/tenants/{id}/users (convite)
- [ ] Middleware que injeta tenant_id do JWT em toda request
- [ ] Testes de isolamento (tenant A não vê dados do tenant B)

---

### SPEC P2: Importação SINAPI Completa

**Problema:** Tem 3 composições de exemplo. A tabela SINAPI real tem ~10.000 composições e ~40.000 insumos, publicada mensalmente pela CEF em Excel/CSV.

**Formato CEF:** Planilhas Excel com abas separadas para insumos e composições, organizadas por estado e mês de referência.

#### Requisitos
- REQ-P2.1: Upload de planilha SINAPI (XLSX) via endpoint
- REQ-P2.2: Parser que extrai insumos (código, descrição, unidade, preço por estado)
- REQ-P2.3: Parser que extrai composições (código, descrição, unidade, itens com coeficientes)
- REQ-P2.4: Importação incremental (não duplica, atualiza preços)
- REQ-P2.5: Validação de integridade (insumo referenciado existe)
- REQ-P2.6: Job assíncrono com progresso (SSE ou polling)
- REQ-P2.7: Histórico de importações (data, arquivo, qtd importada, erros)
- REQ-P2.8: Suporte a múltiplos estados e meses de referência

#### Tasks
- [ ] Adicionar dependência Apache POI ao pom.xml
- [ ] Criar módulo `import/` (domain, application, api)
- [ ] Entity ImportJob (status, progress, errors, file_name)
- [ ] Service SinapiImportService (parse XLSX → upsert materials + compositions)
- [ ] Endpoint POST /api/v1/admin/sinapi/import (multipart upload)
- [ ] Endpoint GET /api/v1/admin/sinapi/imports (histórico)
- [ ] Processamento assíncrono com @Async + Virtual Threads
- [ ] Progresso via SSE (reutilizar OperationEventPublisher)
- [ ] Testes com arquivo SINAPI real (subset)
- [ ] Seed com dados SINAPI RN completo (Jan/2026)

---

### SPEC P3: Validações de Negócio Robustas

**Problema:** Hoje aceita qualquer dado. Em produção, dados inconsistentes causam prejuízo.

#### Requisitos
- REQ-P3.1: Medição não pode exceder saldo do contrato
- REQ-P3.2: Medição só pode ser aprovada se todos os itens têm cost_code válido
- REQ-P3.3: Contrato não pode ser ativado sem pelo menos 1 item
- REQ-P3.4: Change order não pode exceder 25% do valor original (configurável)
- REQ-P3.5: Orçamento APPROVED não pode ter itens alterados (somente via change order)
- REQ-P3.6: Cost transaction ACTUAL não pode exceder budgeted + committed
- REQ-P3.7: Cronograma: data fim não pode ser anterior a data início
- REQ-P3.8: Diário de obra: não pode criar 2 registros para o mesmo dia (já tem unique)
- REQ-P3.9: Validação de CNPJ/CPF para fornecedores
- REQ-P3.10: Validação de email único por tenant

#### Tasks
- [ ] Criar módulo `validation/` com validators reutilizáveis
- [ ] Implementar cada regra como método no service correspondente
- [ ] Custom exceptions: BusinessRuleViolationException (retorna 422)
- [ ] Adicionar ao ApiExceptionHandler
- [ ] Testes unitários para cada regra
- [ ] Testes de integração para fluxos completos (criar contrato → medir → aprovar)

---

## FASE 2 — Frontend Angular

### SPEC P4: Projeto Angular 19 + Estrutura

**Stack:** Angular 19, standalone components, signals, Angular Material, Feature-Sliced Design.

#### Requisitos
- REQ-P4.1: Projeto Angular 19 com standalone components (sem NgModules)
- REQ-P4.2: Angular Material como design system
- REQ-P4.3: Estrutura Feature-Sliced Design (layers: app, pages, features, entities, shared)
- REQ-P4.4: HTTP interceptor para JWT (auto-refresh)
- REQ-P4.5: Guards de rota por permissão
- REQ-P4.6: Layout responsivo (sidebar + content)
- REQ-P4.7: Tema customizável (cores da construtora)
- REQ-P4.8: i18n preparado (pt-BR default)
- REQ-P4.9: Proxy para API em dev (`/api` → localhost:8080)
- REQ-P4.10: Docker build (nginx + SPA)

#### Tasks
- [ ] `ng new sinapipro-web --standalone --style=scss --routing`
- [ ] Instalar Angular Material + CDK
- [ ] Criar estrutura de pastas (app/pages/features/entities/shared)
- [ ] Criar layout shell (sidebar, topbar, content area)
- [ ] Criar serviço HTTP base com interceptor JWT
- [ ] Criar guard de autenticação e permissão
- [ ] Configurar proxy.conf.json
- [ ] Dockerfile (multi-stage: build → nginx)
- [ ] Adicionar ao docker-compose

---

### SPEC P5: Módulo de Autenticação (Angular)

#### Requisitos
- REQ-P5.1: Tela de login (email + senha)
- REQ-P5.2: Tela de registro (criar construtora)
- REQ-P5.3: Tela de recuperação de senha
- REQ-P5.4: Auto-refresh de token (interceptor)
- REQ-P5.5: Redirect para login quando 401
- REQ-P5.6: Armazenamento seguro de tokens (httpOnly cookie ou memory)

#### Tasks
- [ ] Criar feature `auth/` (login, register, forgot-password)
- [ ] Serviço AuthService (login, refresh, logout)
- [ ] Interceptor HTTP (attach token, handle 401, refresh)
- [ ] Guard CanActivate para rotas protegidas
- [ ] Store de usuário logado (signal-based)
- [ ] Telas com Angular Material (form fields, buttons, snackbar)

---

### SPEC P6: Módulo de Orçamento (Angular)

#### Requisitos
- REQ-P6.1: Lista de orçamentos com filtros e paginação
- REQ-P6.2: Criação/edição de orçamento (dados gerais)
- REQ-P6.3: Árvore de etapas (drag-and-drop para reordenar)
- REQ-P6.4: Adição de itens a etapas (busca de composição SINAPI com autocomplete)
- REQ-P6.5: Configuração de BDI (formulário com 6 campos)
- REQ-P6.6: Resumo do orçamento (custo direto + BDI + total)
- REQ-P6.7: Curva ABC (tabela + gráfico de barras)
- REQ-P6.8: Exportação para PDF/Excel

#### Tasks
- [ ] Criar feature `budget/` (list, detail, stages, items, bdi, abc)
- [ ] Componente tree para etapas hierárquicas (CDK Tree)
- [ ] Autocomplete de composições SINAPI (debounce + full-text search)
- [ ] Formulário de BDI com cálculo em tempo real
- [ ] Gráfico Curva ABC (ngx-charts ou Chart.js)
- [ ] Tabela de itens com edição inline
- [ ] Serviço BudgetService (CRUD + stages + items + BDI + ABC)

---

### SPEC P7: Módulo de Execução (Angular)

#### Requisitos
- REQ-P7.1: Cronograma com Gantt simplificado (barras horizontais)
- REQ-P7.2: Curva S (gráfico de linhas: planejado vs realizado)
- REQ-P7.3: Medições com workflow visual (DRAFT → SUBMITTED → APPROVED)
- REQ-P7.4: Job Costing dashboard (cards com BAC, AC, Committed, Variance)
- REQ-P7.5: EVM dashboard (CPI, SPI, gauges)
- REQ-P7.6: Diário de obra (formulário por dia com seções colapsáveis)
- REQ-P7.7: Contratos com timeline de change orders

#### Tasks
- [ ] Criar feature `execution/` (schedule, measurements, jobcosting, dailylog, contracts)
- [ ] Componente Gantt simplificado (CSS grid + barras)
- [ ] Gráfico Curva S (ngx-charts line chart)
- [ ] Cards de EVM com indicadores coloridos (verde/amarelo/vermelho)
- [ ] Stepper para workflow de medição
- [ ] Formulário de diário de obra com seções dinâmicas
- [ ] Timeline de change orders

---

## FASE 3 — Produção

### SPEC P8: Relatórios PDF

**Problema:** Construtora precisa imprimir medição, boletim de medição, planilha orçamentária, cronograma.

#### Requisitos
- REQ-P8.1: Geração de PDF server-side (FreeMarker + Flying Saucer ou JasperReports)
- REQ-P8.2: Relatório: Planilha Orçamentária (etapas + itens + BDI + total)
- REQ-P8.3: Relatório: Boletim de Medição (itens medidos + acumulado + saldo)
- REQ-P8.4: Relatório: Cronograma Físico-Financeiro
- REQ-P8.5: Relatório: Curva ABC
- REQ-P8.6: Relatório: Diário de Obra
- REQ-P8.7: Logo da construtora no cabeçalho (configurável por tenant)
- REQ-P8.8: Download via endpoint GET /api/v1/reports/{type}?budgetId=...&format=pdf

#### Tasks
- [ ] Adicionar FreeMarker + Flying Saucer ao pom.xml
- [ ] Criar módulo `report/` (application, api)
- [ ] Templates FreeMarker para cada relatório (HTML → PDF)
- [ ] Endpoint GET /api/v1/reports/budget-sheet
- [ ] Endpoint GET /api/v1/reports/measurement-bulletin
- [ ] Endpoint GET /api/v1/reports/schedule
- [ ] Endpoint GET /api/v1/reports/abc-curve
- [ ] Endpoint GET /api/v1/reports/daily-log
- [ ] Configuração de logo por tenant
- [ ] Testes (gerar PDF e verificar que não está vazio)

---

### SPEC P9: Upload de Arquivos (GED)

#### Requisitos
- REQ-P9.1: Upload de arquivos vinculados a qualquer entidade (budget, measurement, dailylog)
- REQ-P9.2: Armazenamento em S3 (ou MinIO local)
- REQ-P9.3: Validação OWASP (tipo MIME, tamanho, extensão)
- REQ-P9.4: Thumbnails para imagens
- REQ-P9.5: Download com URL pré-assinada (presigned URL)
- REQ-P9.6: Limite de storage por tenant (configurável por plano)

#### Tasks
- [ ] Adicionar AWS SDK S3 ao pom.xml
- [ ] Criar módulo `storage/` (domain, application, api)
- [ ] Entity Document (entity_type, entity_id, file_name, content_type, size, s3_key)
- [ ] Service StorageService (upload, download, delete)
- [ ] Validação OWASP (magic bytes, extensão, tamanho)
- [ ] Endpoint POST /api/v1/documents (multipart)
- [ ] Endpoint GET /api/v1/documents/{id}/download (presigned redirect)
- [ ] MinIO no docker-compose para dev
- [ ] Testes

---

### SPEC P10: Auditoria + Notificações

#### Requisitos
- REQ-P10.1: Audit log: quem fez o quê, quando, em qual entidade
- REQ-P10.2: Notificações in-app (medição submetida, contrato aprovado, etc.)
- REQ-P10.3: Notificações por email (configurável por usuário)
- REQ-P10.4: SSE para notificações real-time no frontend
- REQ-P10.5: Retenção de audit log (90 dias default, configurável)

#### Tasks
- [ ] Migration: tabelas audit_log, notification
- [ ] AOP aspect para capturar operações de escrita automaticamente
- [ ] Entity AuditLog (user_id, tenant_id, entity_type, entity_id, action, timestamp, diff_json)
- [ ] Entity Notification (user_id, type, message, read, created_at)
- [ ] Service NotificationService (criar + enviar email + SSE)
- [ ] Integração com Spring Mail (SMTP configurável por tenant)
- [ ] Endpoint GET /api/v1/notifications (lista do usuário)
- [ ] Endpoint PATCH /api/v1/notifications/{id}/read
- [ ] SSE stream filtrado por user_id

---

### SPEC P11: Deploy Cloud + Infra

#### Requisitos
- REQ-P11.1: Deploy em AWS (ECS Fargate ou EC2 + Docker)
- REQ-P11.2: RDS PostgreSQL (Multi-AZ para produção)
- REQ-P11.3: S3 para arquivos
- REQ-P11.4: CloudFront para frontend Angular (SPA)
- REQ-P11.5: HTTPS com certificado (ACM + ALB)
- REQ-P11.6: Domínio customizado (sinapipro.com.br)
- REQ-P11.7: Backup automático (RDS snapshots + S3 lifecycle)
- REQ-P11.8: Monitoramento (CloudWatch + Prometheus/Grafana)
- REQ-P11.9: Rate limiting (API Gateway ou Bucket4j)
- REQ-P11.10: CI/CD: push → build → test → deploy staging → deploy prod

#### Tasks
- [ ] Terraform/CDK para infraestrutura (VPC, ECS, RDS, S3, CloudFront, ALB)
- [ ] Dockerfile otimizado (multi-stage, JRE slim)
- [ ] Task definition ECS com health check
- [ ] RDS PostgreSQL 17 com backup automático
- [ ] S3 bucket com lifecycle rules
- [ ] CloudFront distribution para frontend
- [ ] ACM certificate + Route53
- [ ] GitHub Actions: deploy staging (push develop) + deploy prod (push main)
- [ ] Secrets no AWS Secrets Manager (não em env vars)
- [ ] Rate limiting com Bucket4j (por tenant)
- [ ] Runbook de operações (backup, restore, scale up/down)

---

## Estimativa de Esforço

| Fase | Specs | Esforço estimado | Resultado |
|------|-------|-----------------|-----------|
| **1 — Fundação** | P1, P2, P3 | 3-4 semanas | Backend pronto para multi-tenant |
| **2 — Frontend** | P4, P5, P6, P7 | 4-6 semanas | Sistema usável por construtora |
| **3 — Produção** | P8, P9, P10, P11 | 3-4 semanas | SaaS deployado e operacional |
| **Total** | 11 specs | **10-14 semanas** | Produto MVP |

---

## Stack Final (Produção)

| Camada | Tecnologia |
|--------|-----------|
| Frontend | Angular 19 + Angular Material + Signals + Standalone |
| Backend | Java 25 + Spring Boot 4 + Spring Security 7 |
| Banco | PostgreSQL 17 (RDS) |
| Storage | AWS S3 / MinIO |
| Cache | Redis (sessões, rate limit) — futuro |
| Email | Spring Mail + SES |
| PDF | FreeMarker + Flying Saucer |
| Infra | AWS ECS Fargate + RDS + S3 + CloudFront |
| CI/CD | GitHub Actions |
| Monitoramento | Prometheus + Grafana + CloudWatch |
| Tracing | OpenTelemetry → Grafana Tempo |

---

## Ordem de Implementação Recomendada

```
Semana 1-2:  SPEC P1 (multi-tenancy + users) — sem isso nada mais funciona
Semana 3:    SPEC P2 (importação SINAPI) — sem dados reais não testa nada
Semana 4:    SPEC P3 (validações) — protege os dados
Semana 5:    SPEC P4 (Angular setup) + SPEC P5 (auth frontend)
Semana 6-7:  SPEC P6 (orçamento frontend) — tela mais importante
Semana 8-9:  SPEC P7 (execução frontend)
Semana 10:   SPEC P8 (relatórios PDF) + SPEC P9 (upload)
Semana 11:   SPEC P10 (auditoria + notificações)
Semana 12:   SPEC P11 (deploy AWS)
Semana 13-14: Testes E2E, ajustes, documentação, beta com construtora real
```
