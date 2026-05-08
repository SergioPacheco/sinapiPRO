# SinapiPRO Showcase Roadmap

## Objective

Transform `sinapiPRO` from a Spring Boot 2.7 / Java 11 monolith with MVC views into a deliberately overengineered showcase for modern Spring, emphasizing:

- Spring Boot 4
- Java 25
- API-first design
- PostgreSQL
- observability
- security
- native/AOT
- containerized local platform
- automated testing
- selective reactive programming

The goal is not minimalism. The goal is to demonstrate range.

## Current Baseline

- Java 11
- Spring Boot 2.7.18
- Spring MVC + Thymeleaf
- Spring Data JPA + Hibernate
- Flyway
- monolithic layered structure
- MySQL/MariaDB oriented setup
- unit tests only

## Target Showcase State

- Java 25
- Spring Boot 4.0.x stable line
- Spring Framework 7
- Jakarta EE 11 APIs
- PostgreSQL as primary database
- REST API under `/api/v1`
- OpenAPI
- OAuth2 Resource Server with JWT
- Actuator + Micrometer + Prometheus + Grafana
- structured JSON logging
- OpenTelemetry traces
- Testcontainers
- GitHub Actions
- AOT and Native Image profile
- Docker Compose full platform
- a bounded reactive slice with WebFlux

## What To Exhibit

### 1. Platform Modernization

- upgrade the project to Spring Boot 4 and Java 25
- migrate `javax.*` imports to `jakarta.*`
- move to Tomcat 11 / Servlet 6.1 baseline
- remove deprecated Spring Security configuration style
- standardize build on modern Maven wrapper and reproducible plugin configuration

### 2. API-First Backend

- create `/api/v1/projects`
- create `/api/v1/budgets`
- create `/api/v1/suppliers`
- create `/api/v1/invoices`
- create `/api/v1/reports`
- replace form-centric controller flows with JSON endpoints for the main use cases
- use DTOs dedicated to input and output
- use records where appropriate
- use cursor or page-based pagination consistently
- add filtering and sorting parameters
- version the API explicitly

### 3. Error Handling

- use RFC-style API error responses with `ProblemDetail`
- map validation errors into deterministic response payloads
- map business exceptions separately from infrastructure exceptions
- include correlation id in responses and logs

### 4. Security

- migrate to Spring Security bean-based configuration
- use OAuth2 Resource Server + JWT for API endpoints
- keep session/form authentication only if the legacy UI remains temporarily
- define clear public vs private endpoints
- implement authority-based access per domain capability
- add security tests for forbidden, unauthorized and happy-path access

### 5. PostgreSQL and Performance

- move the main persistence target to PostgreSQL
- reorganize Flyway migrations by domain
- add indexes with explicit rationale
- add representative query tuning notes
- capture `EXPLAIN ANALYZE` examples for critical reports
- create read-optimized projections for reporting use cases
- optionally add materialized views for heavy dashboards

### 6. Observability

- expose `health`, `info`, `metrics`, `prometheus`, `httpexchanges`
- separate application and management ports
- add Micrometer observations around business flows
- add traces over OTLP
- emit structured JSON logs
- propagate correlation id
- create Grafana dashboards for:
  - request latency
  - error rate
  - JVM
  - datasource
  - custom budget/report metrics

### 7. Testing

- JUnit 5
- Mockito where unit-level mocking still makes sense
- MockMvc for servlet API tests
- WebTestClient for reactive endpoints
- Testcontainers with PostgreSQL
- repository integration tests
- security tests
- contract tests for key endpoints
- JaCoCo report in CI

### 8. Packaging and Runtime

- `docker compose up -d` should bring up:
  - app
  - postgres
  - pgadmin
  - prometheus
  - grafana
  - optional tempo or zipkin
- add a native build profile
- optionally publish OCI image through Buildpacks

### 9. Presentation Layer

- keep Thymeleaf only as a compatibility shell or admin console
- the portfolio focus must shift to REST, docs, metrics, traces and containers
- publish Swagger UI screenshots
- publish Grafana screenshots
- publish architecture diagram
- publish a short demo video

## Reactive Programming Strategy

Reactive programming should be shown, but not faked.

### Do Not Do

- do not wrap blocking JPA repositories in `Mono` and call it reactive
- do not convert the entire ERP monolith to WebFlux just for aesthetics
- do not mix blocking persistence under non-blocking endpoints on the hot path

### Recommended Reactive Slice

Use WebFlux for bounded capabilities where reactive adds visible value:

- server-sent events for report generation progress
- streaming notifications for workflow/status changes
- ingestion pipeline for SINAPI imports
- asynchronous integration client layer with `WebClient`
- live dashboard feeds
- optional RSocket channel for event streaming demos

### Recommended Reactive Architecture

- keep the main transactional core on the servlet stack first
- introduce a dedicated reactive module or package for streaming/integration concerns
- if a feature is truly reactive end-to-end, prefer R2DBC or event/read-model patterns for that slice
- use `WebClient` even in the servlet stack where non-blocking outbound IO is useful
- use `WebTestClient` for reactive endpoint tests

### Best Reactive Demo Candidates In This Project

- `/api/v1/reports/{id}/progress` via SSE
- `/api/v1/notifications/stream`
- `/api/v1/sinapi/import/jobs/{id}/events`
- `/api/v1/dashboard/live`

## Proposed Package Structure

```text
src/main/java
└── com.sinapipro
    ├── budget
    │   ├── api
    │   ├── application
    │   ├── domain
    │   └── infrastructure
    ├── supplier
    ├── invoice
    ├── reporting
    ├── notification
    ├── security
    ├── observability
    ├── shared
    └── config
```

## Showcase Features Backed By Spring Boot 4

- Actuator endpoints and observability
- Micrometer observation and tracing integration
- OpenTelemetry support
- structured logging formats
- Docker Compose integration
- Testcontainers service connections
- AOT processing
- Native Image support

## Execution Plan

### Phase 0. Freeze and Branch

- create a dedicated migration branch
- capture current screenshots and behavior
- keep the current Boot 2.7 app runnable until the new baseline is stable

### Phase 1. Foundation

- upgrade to Java 25
- upgrade to Spring Boot 4
- migrate `javax` to `jakarta`
- remove obsolete security configuration
- switch database profile to PostgreSQL

### Phase 2. API Core

- create `shared` API conventions
- create `ProblemDetail` error contract
- implement first vertical slice:
  - budgets
  - suppliers
  - invoices
- add OpenAPI and examples

### Phase 3. Platform

- Actuator
- metrics
- Prometheus
- Grafana
- structured logs
- correlation id
- tracing

### Phase 4. Quality

- Testcontainers
- CI
- JaCoCo
- security tests
- contract tests

### Phase 5. Reactive Slice

- WebFlux streaming endpoints
- `WebClient` integration client
- SSE progress feed for long-running jobs
- optional RSocket demo

### Phase 6. Native and Delivery

- AOT profile
- Native Image build
- Buildpacks/OCI image
- polished docker compose platform

### Phase 7. Portfolio Presentation

- premium README
- screenshots
- diagrams
- benchmark notes
- architecture decisions
- demo video

## Recommended First Vertical Slice

If the objective is maximum impact with minimum randomness, start here:

1. Boot 4 + Java 25 migration
2. PostgreSQL + Flyway validation
3. `/api/v1/budgets` with OpenAPI + ProblemDetail + JWT
4. Testcontainers for PostgreSQL
5. Actuator + Prometheus + Grafana
6. SSE progress endpoint for report generation

That sequence already makes the project look dramatically more current.

## What This Project Should Say On A CV

Built and modernized SinapiPRO into a Java 25 / Spring Boot 4 showcase using PostgreSQL, REST APIs, Spring Security, OpenAPI, Flyway, Testcontainers, Docker Compose, structured observability, and selective reactive programming with WebFlux for streaming and integrations.
