---
description: Product context — what this project is, who uses it, business rules
inclusion: always
---

# Product Steering

## Product name
SinapiPRO Showcase API

## Description
Sistema de gestão de obras e orçamentos da construção civil, baseado na tabela SINAPI. Projeto de portfólio demonstrando arquitetura moderna com Java 25 + Spring Boot 4, focado em API-first design, observabilidade e boas práticas de engenharia.

## Key users (personas do showcase)
- Engenheiros civis e orçamentistas (criação de orçamentos com composições SINAPI)
- Gestores de obras (acompanhamento de custos, medições, cronograma)
- Gerentes de suprimentos (cotações, pedidos, controle de estoque)
- Diretores (dashboards, EVM, fluxo de caixa)

## Core constraints
- Valores monetários: `BigDecimal` com `numeric(18,2)` no banco
- Dados SINAPI são referência — nunca alterar a base original
- UUID como primary key (distributed-friendly, sem exposição de sequência)
- API stateless (JWT, sem sessão)
- Erros padronizados via ProblemDetail (RFC 9457)
- Observabilidade em todo service layer (Micrometer Observations)

## Business rules
- Composições SINAPI têm insumos com coeficientes e preços por estado/mês
- Custo unitário = Σ(coeficiente × preço do insumo)
- Orçamentos compostos por etapas → itens → composições + BDI
- Preços variam por estado e mês de referência SINAPI
- Medições com workflow: DRAFT → SUBMITTED → APPROVED → PAID
- Contratos com aditivos (change orders) e retenção
- Job Costing: orçado vs. realizado vs. comprometido por cost code
- Cronograma com Curva S (planejado vs. realizado acumulado)
- EVM: PV, EV, AC, CPI, SPI, EAC, VAC

## Domínios implementados
- `budget/` — orçamentos (CRUD + filtros + paginação)
- `supplier/` — fornecedores
- `invoice/` — faturas/notas fiscais
- `sinapi/` — composições + insumos + preços + cálculo de custo
- `security/` — JWT + OAuth2 + refresh tokens
- `shared/` — ProblemDetail, SSE events, observabilidade, base entity

## Domínios planejados (specs em `.kiro/docs/specs-and-tasks.md`)
- Job Costing & Cost Codes
- Cronograma & Planejamento Físico (Curva S, CPM)
- Medições de Obra
- Contratos & Aditivos
- Suprimentos (cotação → pedido → recebimento)
- Orçamento Detalhado (etapas, BDI, Curva ABC)
- Dashboard & Relatórios (EVM)
- Diário de Obra

## Out of scope
- Frontend (este é um backend API-first showcase)
- Microservices (monolito modular é a escolha deliberada)
- Multi-tenancy
