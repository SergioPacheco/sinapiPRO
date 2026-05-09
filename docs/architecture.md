# 🏗️ Arquitetura — SinapiPRO

## Visão Geral (C4 — Context)

```mermaid
C4Context
    title SinapiPRO — Contexto do Sistema

    Person(eng, "Engenheiro/Orçamentista", "Cria orçamentos, medições, contratos")
    Person(gestor, "Gestor de Obra", "Acompanha execução, cronograma, EVM")
    Person(suprimentos, "Gerente de Suprimentos", "Cotações, pedidos, estoque")

    System(sinapipro, "SinapiPRO API", "REST API — Java 25 + Spring Boot 4")
    SystemDb(pg, "PostgreSQL 17", "UUID PKs, JSONB, tsvector")
    System_Ext(sinapi, "SINAPI/CEF", "Base de preços da construção civil")
    System_Ext(grafana, "Grafana + Prometheus", "Dashboards e alertas")

    Rel(eng, sinapipro, "HTTPS/JWT")
    Rel(gestor, sinapipro, "HTTPS/JWT")
    Rel(suprimentos, sinapipro, "HTTPS/JWT")
    Rel(sinapipro, pg, "JDBC/HikariCP")
    Rel(sinapipro, sinapi, "Import CSV/API")
    Rel(sinapipro, grafana, "Prometheus metrics + OTLP traces")
```

## Arquitetura de Componentes (C4 — Container)

```mermaid
C4Container
    title SinapiPRO — Containers

    Container(api, "API Application", "Java 25, Spring Boot 4", "REST endpoints, business logic, JWT auth")
    ContainerDb(db, "PostgreSQL 17", "Flyway migrations", "UUID PKs, JSONB, tsvector full-text search")
    Container(prometheus, "Prometheus", "Metrics scraping", "Coleta métricas /actuator/prometheus")
    Container(otel, "OTel Collector", "Trace pipeline", "Recebe spans OTLP, exporta para backend")
    Container(grafana, "Grafana", "Dashboards", "Visualização de métricas e traces")

    Rel(api, db, "JDBC", "HikariCP pool")
    Rel(prometheus, api, "HTTP GET", "/actuator/prometheus")
    Rel(api, otel, "gRPC", "OTLP spans")
    Rel(grafana, prometheus, "PromQL")
```

## Módulos Internos (Vertical Slicing)

```mermaid
graph TB
    subgraph "API Layer"
        BC[BudgetController]
        SC[SupplierController]
        MC[MeasurementController]
        CC[ContractController]
        PC[ProcurementController]
        JC[JobCostingController]
        SchC[ScheduleController]
        AC[AnalyticsController]
    end

    subgraph "Application Layer"
        BS[BudgetService]
        SS[SupplierService]
        MS[MeasurementService]
        CS[ContractService]
        PS[ProcurementService]
        JS[JobCostingService]
        SchS[CriticalPathService]
        AS[EarnedValueService]
    end

    subgraph "Domain Layer"
        BR[(BudgetRepository)]
        SR[(SupplierRepository)]
        MR[(MeasurementRepository)]
        CR[(ContractRepository)]
        PR[(PurchaseOrderRepository)]
        JR[(CostCodeRepository)]
        SchR[(ScheduleActivityRepository)]
    end

    subgraph "Shared / Cross-cutting"
        EH[ApiExceptionHandler<br/>ProblemDetail RFC 9457]
        EVT[OperationEventPublisher<br/>SSE Reactor Flux]
        OBS[BusinessObservationService<br/>Micrometer]
        SEC[SecurityConfiguration<br/>JWT OAuth2]
    end

    BC --> BS --> BR
    SC --> SS --> SR
    MC --> MS --> MR
    CC --> CS --> CR
    PC --> PS --> PR
    JC --> JS --> JR
    SchC --> SchS --> SchR
    AC --> AS

    BS --> EVT
    BS --> OBS
    MS --> JS
```

## Padrão por Módulo

```
{module}/
├── api/            ← @RestController + request/response records
├── application/    ← @Service + @Transactional + business logic
└── domain/         ← @Entity + Repository interface (Spring Data)
```

**Regras:**
- `api/` — sem lógica de negócio, apenas validação de entrada e mapeamento
- `application/` — orquestra domínio, eventos, métricas. Ponto de transação
- `domain/` — entities JPA + repository. Sem dependência de outros módulos
- `shared/` — cross-cutting: base entity, error handler, events, observability

## Features Java 25 na Arquitetura

| Feature | Uso Arquitetural |
|---------|-----------------|
| Virtual Threads | Todas as requests HTTP (zero thread pinning) |
| Structured Concurrency | Operações paralelas em services (approve + invoice) |
| Sealed Classes | Hierarquia de exceções (`DomainException`), domain events (`DomainEvent`) |
| Gatherers | Agregações em stream (Curva ABC, WIP Report, Measurement Summary) |
| Pattern Matching | Exception handler exhaustivo, validações de status |
| Records | DTOs, value objects, resultados de cálculo |
| Module Imports | `import module java.base` em todos os services |

## Decisões Arquiteturais

| Decisão | Justificativa |
|---------|---------------|
| Monolito modular | Complexidade de domínio, não de escala. Vertical slicing isola módulos |
| UUID como PK | Distributed-friendly, sem exposição de sequência, geração client-side |
| JSONB para metadata | Flexibilidade sem migrations para campos opcionais |
| tsvector full-text | Busca em composições/materiais SINAPI sem Elasticsearch |
| Stateless (JWT) | Escalabilidade horizontal sem session affinity |
| SSE (não WebSocket) | Unidirecional server→client, compatível com HTTP/2, sem state |
| ProblemDetail (RFC 9457) | Padrão de erro interoperável, extensível |
