---
description: Tech stack — language, frameworks, database, build, deployment
inclusion: always
---

# Tech Steering

## Project Structure

Este repositório contém **dois módulos**:

| Módulo | Path | Status |
|--------|------|--------|
| Legacy (Thymeleaf) | `/` (raiz) | Mantido como referência, não recebe novas features |
| **Showcase API** | `/api/` | **Ativo** — onde todo desenvolvimento acontece |

---

## Showcase API (módulo ativo)

### Language and runtime
- Java 25 (LTS, Temurin)
- Virtual Threads habilitadas (`spring.threads.virtual.enabled=true`)

### Frameworks
- Spring Boot 4.0.5
- Spring Framework 7.0.6
- Spring Security 7.0 (OAuth2 Resource Server, JWT, stateless)
- Spring Data JPA / Hibernate 7
- Jakarta EE 11 (jakarta.* namespace)
- SpringDoc OpenAPI 3.0.3 (Swagger UI)
- Micrometer + Prometheus + OpenTelemetry (observabilidade)
- Reactor (SSE events)

### Database
- PostgreSQL 17.5
- Flyway (migrations V1–V4)
- JSONB, UUID PKs, tsvector full-text search

### Build and packaging
- Maven 3.9+ (usar `mvn` via SDKMAN, não o `./mvnw` da raiz que é antigo)
- Settings local: `api/.mvn/settings.xml` (bypassa Nexus corporativo)
- Artifact: JAR executável (Spring Boot)
- Docker: `compose.showcase.yaml` (app + PG + Prometheus + Grafana + OTel)
- GraalVM Native Image: profile `native` configurado

### Key dependencies
- spring-boot-starter-web, data-jpa, security, validation, actuator, oauth2-resource-server, webflux
- spring-boot-docker-compose (dev)
- spring-boot-testcontainers (test)
- springdoc-openapi-starter-webmvc-ui
- micrometer-registry-prometheus
- micrometer-tracing-bridge-otel
- flyway-database-postgresql
- postgresql driver
- JaCoCo 0.8.13 (coverage)

### Feedback loops
```bash
cd api
mvn compile -s .mvn/settings.xml
mvn test-compile -s .mvn/settings.xml
mvn test -s .mvn/settings.xml    # requer Docker (Testcontainers)
```

---

## Legacy Module (raiz — somente referência)

### Language and runtime
- Java 11
- Spring Boot 2.7.18

### Frameworks
- Spring MVC + Thymeleaf 3.x (server-side rendering)
- Spring Security 5 (form login, roles)
- FreeMarker + Flying Saucer (PDF reports)

### Database
- MySQL / MariaDB
- Flyway (migrations V1–V36)

### Build
```bash
./mvnw compile
./mvnw test
```

> ⚠️ O módulo legado NÃO deve receber novas features. Serve como referência de domínio para migrar regras de negócio para o api.
