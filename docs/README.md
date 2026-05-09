# 📚 Documentação — SinapiPRO

> Sistema de Gestão de Obras e Orçamentos | Java 25 + Angular 20 + PostgreSQL 17

## Índice

| Documento | Descrição |
|-----------|-----------|
| [architecture.md](architecture.md) | Arquitetura do sistema (C4, componentes, decisões) |
| [database.md](database.md) | Modelo de dados PostgreSQL (ER com Mermaid) |
| [api-flows.md](api-flows.md) | Diagramas de sequência dos fluxos principais |
| [domain.md](domain.md) | Regras de negócio e glossário do domínio |
| [deployment.md](deployment.md) | Deploy, observabilidade e operação |
| [frontend-plan.md](frontend-plan.md) | Arquitetura Angular (Feature-Shell + Clean Architecture) |

## Stack

```
Backend:  Java 25 + Spring Boot 4.0.5 + PostgreSQL 17 + Flyway
Frontend: Angular 20 + Material + ng-matero + ApexCharts
Infra:    Docker Compose + Prometheus + Grafana + OpenTelemetry
```

## Quick Reference

```bash
# Backend
cd api && mvn spring-boot:run -s .mvn/settings.xml
# http://localhost:8080/swagger-ui.html

# Frontend
cd web && nvm use 22 && npx ng serve
# http://localhost:4200 (proxy → :8080)
# Login: admin@sinapipro.com / admin123
```
