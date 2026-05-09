# 📚 Documentação — SinapiPRO

> Sistema de Gestão de Obras e Orçamentos | Java 25 + Spring Boot 4 + PostgreSQL 17

## Índice

| Documento | Descrição |
|-----------|-----------|
| [architecture.md](architecture.md) | Arquitetura do sistema (C4, componentes, deploy) |
| [database.md](database.md) | Modelo de dados PostgreSQL (ER com Mermaid) |
| [api-flows.md](api-flows.md) | Diagramas de sequência dos fluxos principais |
| [domain.md](domain.md) | Regras de negócio e glossário do domínio |
| [deployment.md](deployment.md) | Deploy, observabilidade e operação |

## Stack

```
Java 25 (Virtual Threads, Structured Concurrency, Sealed Classes, Gatherers)
Spring Boot 4.0.5 / Spring Framework 7 / Spring Security 7
PostgreSQL 17 (UUID PKs, JSONB, tsvector, índices parciais)
Flyway (migrations V1–V14)
Micrometer + Prometheus + OpenTelemetry + Grafana
Docker Compose (dev + showcase)
```

## Quick Start

```bash
cd api
mvn spring-boot:run -s .mvn/settings.xml
# PostgreSQL sobe automaticamente via Docker Compose
# Swagger: http://localhost:8080/swagger-ui.html
```
