---
description: Product context — what this project is, who uses it, business rules
inclusion: always
---

# Product Steering

## Product name
SinapiPRO

## Description
Sistema de orçamento de obras baseado na tabela SINAPI (Sistema Nacional de Pesquisa de Custos e Índices da Construção Civil). Permite criar orçamentos, gerenciar composições, insumos, etapas e gerar relatórios.

## Key users
- Engenheiros civis e orçamentistas (criação de orçamentos)
- Gestores de obras (acompanhamento de custos)
- Administradores do sistema (gestão de usuários e permissões)

## Core constraints
- Valores monetários sempre com precisão decimal (BigDecimal)
- Dados SINAPI são referência — nunca alterar a base original
- Orçamentos vinculados a obras e clientes
- Relatórios JasperReports para impressão/PDF
- Autenticação via Spring Security com roles (ADMIN, USER)

## Business rules
- Composições SINAPI têm insumos com coeficientes e preços por estado
- Orçamentos são compostos por etapas → itens → composições
- Preços variam por estado e mês de referência
- Soft-delete não implementado — cuidado com exclusões
- Validação de orçamento antes de gerar relatório

## Out of scope
- Migração para microservices
- Reescrita do frontend (Thymeleaf → SPA)
