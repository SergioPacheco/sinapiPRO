# Manual Técnico — SinapiPRO

> Versão: 1.0 | Última atualização: 2026-05-31
> Stack: Java 25 + Spring Boot 4.0.5 + Angular 19 + PostgreSQL 17

---

## Sumário

1. [Visão Geral da Arquitetura](#1-visão-geral-da-arquitetura)
2. [Stack Tecnológica e Justificativas](#2-stack-tecnológica-e-justificativas)
3. [Padrões Arquiteturais](#3-padrões-arquiteturais)
4. [Features Java 25 Utilizadas](#4-features-java-25-utilizadas)
5. [Features Spring Boot 4 / Spring 7](#5-features-spring-boot-4--spring-7)
6. [Segurança](#6-segurança)
7. [Observabilidade](#7-observabilidade)
8. [Persistência e Banco de Dados](#8-persistência-e-banco-de-dados)
9. [Cache](#9-cache)
10. [Comunicação em Tempo Real](#10-comunicação-em-tempo-real)
11. [Busca Full-Text](#11-busca-full-text)
12. [Geração de Relatórios](#12-geração-de-relatórios)
13. [Resiliência](#13-resiliência)
14. [Multi-Tenancy](#14-multi-tenancy)
15. [Testes](#15-testes)
16. [Infraestrutura e Deploy](#16-infraestrutura-e-deploy)
17. [Frontend Angular](#17-frontend-angular)
18. [Decisões Arquiteturais (ADRs)](#18-decisões-arquiteturais-adrs)

---

## 1. Visão Geral da Arquitetura

```
┌─────────────────────────────────────────────────────────────────────┐
│                     Frontend (Angular 19 + PrimeNG)                   │
│         Standalone Components • Signals • ECharts • Cypress          │
└────────────────────────────────┬────────────────────────────────────┘
                                 │ REST (JSON) + WebSocket (STOMP)
┌────────────────────────────────▼────────────────────────────────────┐
│                   API Layer (Spring Boot 4 + Java 25)                 │
│                                                                       │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │                    @RestController (api/)                      │   │
│  │  DTOs (records) • Validation • ProblemDetail errors            │   │
│  └──────────────────────────────┬───────────────────────────────┘   │
│                                 │                                     │
│  ┌──────────────────────────────▼───────────────────────────────┐   │
│  │                    @Service (application/)                     │   │
│  │  Business logic • @Transactional • @Observed • Events         │   │
│  └──────────────────────────────┬───────────────────────────────┘   │
│                                 │                                     │
│  ┌──────────────────────────────▼───────────────────────────────┐   │
│  │                    Domain (domain/)                            │   │
│  │  @Entity • Repository • Value Objects • Domain Events         │   │
│  └──────────────────────────────────────────────────────────────┘   │
└───────┬──────────┬──────────┬──────────┬──────────┬─────────────────┘
        │          │          │          │          │
   PostgreSQL  Elasticsearch  Keycloak  Prometheus  S3/Local
```

**Princípio**: Vertical Slicing — cada módulo de negócio é auto-contido com suas 3 camadas (`api/`, `application/`, `domain/`). Não há dependência lateral entre módulos exceto via eventos.

---

## 2. Stack Tecnológica e Justificativas

### Backend

| Tecnologia | Versão | Justificativa |
|-----------|--------|---------------|
| **Java** | 25 (Temurin) | LTS com Virtual Threads, Structured Concurrency, Scoped Values, Gatherers |
| **Spring Boot** | 4.0.5 | Última versão com Jakarta EE 11, Spring 7, observability nativa |
| **Spring Security** | 7.0 | OAuth2 Resource Server, JWT stateless, method security |
| **Spring Data JPA** | 4.0 | Hibernate 7, repositories declarativos |
| **PostgreSQL** | 17.5 | JSONB, UUID PKs, tsvector, window functions, CTEs |
| **Flyway** | 11.x | Migrations versionadas, baseline-on-migrate |
| **Elasticsearch** | 8.17 | Full-text search com analyzer brasileiro |
| **Caffeine** | 3.x | Cache local com métricas Micrometer |
| **Resilience4j** | 2.3 | Circuit breaker, retry, rate limiter |
| **Micrometer + OTel** | 1.14 | Métricas + tracing distribuído |
| **JTE** | 3.1 | Templates compilados para PDF |
| **OpenHTMLtoPDF** | 1.0.10 | HTML → PDF (relatórios tabulares) |
| **Playwright** | 1.49 | PDF via headless browser (gráficos) |
| **FastExcel** | 0.18 | Excel streaming (grandes volumes) |

### Frontend

| Tecnologia | Versão | Justificativa |
|-----------|--------|---------------|
| **Angular** | 19 | Standalone components, signals, SSR-ready |
| **PrimeNG** | 19 | UI components enterprise-grade |
| **ECharts** | 6 | Charts interativos (dashboards) |
| **Cypress** | 14 | E2E tests confiáveis |

### Infraestrutura

| Tecnologia | Justificativa |
|-----------|---------------|
| **Docker Compose** | Dev environment completo em um comando |
| **Jib** | Build de imagem Docker sem Dockerfile |
| **Helm** | Deploy em Kubernetes |
| **Keycloak** | Identity provider (OAuth2/OIDC) |
| **Prometheus + Grafana** | Métricas e dashboards |
| **OpenTelemetry** | Tracing distribuído |
| **SonarQube** | Qualidade de código |
| **MailHog** | Email em dev |

---

## 3. Padrões Arquiteturais

### 3.1 Vertical Slicing

Cada módulo de negócio segue:

```
{module}/
├── api/            → Controllers + DTOs (records)
├── application/    → Services + business logic
└── domain/         → Entities + Repositories
```

**Regras**:
- `api/` não contém lógica de negócio
- `application/` orquestra domínio + eventos + métricas
- `domain/` não depende de outros módulos
- Comunicação entre módulos: via Spring Events ou injeção direta do Repository

### 3.2 Domain Events

```java
public sealed interface MeasurementEvent {
    record Approved(UUID measurementId, UUID budgetId, BigDecimal amount) implements MeasurementEvent {}
    record Rejected(UUID measurementId, String reason) implements MeasurementEvent {}
}
```

Eventos são publicados via `ApplicationEventPublisher` e consumidos por listeners em outros módulos (ex: medição aprovada → gera fatura).

### 3.3 Error Handling (RFC 9457)

Todas as exceções de domínio estendem `DomainException` e são convertidas em `ProblemDetail` pelo `ApiExceptionHandler`:

```java
public sealed class DomainException extends RuntimeException
    permits DomainNotFoundException, DomainConflictException, DomainValidationException { }
```

Resposta HTTP:
```json
{
  "type": "https://sinapipro.dev/errors/not-found",
  "title": "Budget not found",
  "status": 404,
  "detail": "Budget with id 550e8400-... does not exist",
  "instance": "/api/v1/budgets/550e8400-..."
}
```

---

## 4. Features Java 25 Utilizadas

### 4.1 Virtual Threads (JEP 444)

```yaml
spring.threads.virtual.enabled: true
```

Todas as requests HTTP são processadas em virtual threads. Benefícios:
- Milhares de conexões simultâneas sem thread pool exhaustion
- I/O blocking (DB, HTTP) não bloqueia platform threads
- Sem necessidade de programação reativa (WebFlux)

### 4.2 Structured Concurrency (JEP 480)

```java
try (var scope = StructuredTaskScope.open()) {
    var budgetTask = scope.fork(() -> collectBudgetData(projectId));
    var scheduleTask = scope.fork(() -> collectScheduleData(projectId));
    var costsTask = scope.fork(() -> collectCostData(projectId));
    scope.join();
    // Se qualquer subtask falhar, todas são canceladas automaticamente
}
```

**Onde usamos**: `StructuredReportService`, `DelayForecastService`, `ProcurementService` (análise comparativa de cotações).

**Por que não usar CompletableFuture**: Structured Concurrency garante que subtasks não "vazam" — se o escopo fecha, tudo é cancelado. CompletableFuture não tem essa garantia.

### 4.3 Scoped Values (JEP 481)

```java
public static final ScopedValue<UUID> CURRENT = ScopedValue.newInstance();
```

Substitui `ThreadLocal` para propagação de tenant ID:
- **Imutável** dentro do escopo (sem race conditions)
- **Herdado automaticamente** por virtual threads filhas
- **Sem cleanup** necessário (sem `try/finally { remove() }`)
- **Melhor performance** com virtual threads (sem pinning)

### 4.4 Stream Gatherers (JEP 485)

```java
var total = items.stream()
    .gather(Gatherers.fold(() -> BigDecimal.ZERO, BigDecimal::add))
    .findFirst().orElse(BigDecimal.ZERO);
```

Permite operações intermediárias customizadas no Stream pipeline. Usado para agregações financeiras.

### 4.5 Pattern Matching com Guards (JEP 455)

```java
String severity = switch (delayDays) {
    case int d when d <= 5  -> "LOW";
    case int d when d <= 15 -> "MEDIUM";
    case int d when d <= 30 -> "HIGH";
    default -> "CRITICAL";
};
```

### 4.6 Sealed Interfaces

```java
public sealed interface DomainEvent permits
    MeasurementEvent, BudgetEvent, ProcurementEvent { }
```

Garante em compile-time que todos os tipos de evento são tratados em switch expressions.

### 4.7 Records (DTOs)

```java
public record CreateBudgetRequest(
    @NotBlank String code,
    @NotBlank String description,
    UUID projectId
) {}
```

Imutáveis, com `equals`/`hashCode`/`toString` automáticos. Usados para todos os DTOs de entrada e saída.

### 4.8 Module Imports (JEP 476)

```java
import module java.base;
```

Importa todo o módulo `java.base` de uma vez, eliminando dezenas de imports individuais.

### 4.9 Sequenced Collections

```java
var best = quotes.getFirst();           // Melhor cotação
var history = entries.reversed();       // Ordem cronológica inversa
```

---

## 5. Features Spring Boot 4 / Spring 7

### 5.1 @HttpExchange (Declarative HTTP Clients)

```java
@HttpExchange(url = "https://api.openweathermap.org/data/2.5")
public interface WeatherClient {
    @GetExchange("/forecast")
    Map<String, Object> getForecast(@RequestParam("lat") double lat, ...);
}
```

Registrado via `HttpServiceProxyFactory`:
```java
@Bean
WeatherClient weatherClient() {
    var restClient = RestClient.builder().baseUrl("...").build();
    return HttpServiceProxyFactory
        .builderFor(RestClientAdapter.create(restClient))
        .build()
        .createClient(WeatherClient.class);
}
```

**Vantagens sobre RestTemplate/WebClient manual**:
- Type-safe (erros de URL em compile-time)
- Testável (mock da interface)
- Integração automática com Resilience4j

### 5.2 @Observed (Observability Declarativa)

```java
@Service
@Observed(name = "budget.service")
public class BudgetService { ... }
```

Automaticamente gera:
- **Span** no trace distribuído (OpenTelemetry)
- **Timer** métrica (latência p50/p95/p99)
- **Counter** de invocações e erros

Sem necessidade de instrumentação manual com `Observation.start()`/`stop()`.

### 5.3 ProblemDetail (RFC 9457)

```java
spring.mvc.problemdetails.enabled: true
```

Todas as exceções retornam JSON padronizado com `type`, `title`, `status`, `detail`, `instance`.

### 5.4 RestClient (substitui RestTemplate)

```java
var response = RestClient.create()
    .get()
    .uri("/api/data")
    .retrieve()
    .body(MyDto.class);
```

API fluente, síncrona, compatível com Virtual Threads.

---

## 6. Segurança

### Arquitetura

```
Request → JWT Validation → UserProvisioningFilter → TenantInterceptor → Controller
```

### Camadas

| Camada | Mecanismo |
|--------|-----------|
| **Autenticação** | JWT (self-issued) ou OAuth2 (Keycloak) |
| **Autorização** | RBAC com `@PreAuthorize` + `PermissionEvaluatorBean` |
| **Multi-tenant** | Row-level isolation via Hibernate Filter |
| **Rate Limiting** | `RateLimitFilter` (in-memory, token bucket) |
| **CORS** | Configurável por ambiente |

### Roles e Permissions

```java
public enum DefaultRoles {
    ADMIN,      // Acesso total
    ENGINEER,   // Orçamentos, medições, cronograma
    PURCHASER,  // Suprimentos, cotações
    FINANCE,    // Financeiro, faturas
    VIEWER      // Somente leitura
}
```

### JWT Claims

```json
{
  "sub": "admin@sinapipro.dev",
  "tenant_id": "550e8400-...",
  "roles": ["ADMIN", "USER"],
  "scope": "sinapipro.read sinapipro.write",
  "exp": 1717200000
}
```

---

## 7. Observabilidade

### Stack

```
Application → Micrometer → Prometheus (métricas)
                         → OpenTelemetry (traces)
                         → Grafana (dashboards)
```

### Métricas Expostas

| Categoria | Exemplos |
|-----------|----------|
| JVM | Heap usage, GC pauses, thread count |
| HTTP | Latência (p50/p95/p99), throughput, error rate |
| Cache | Hit ratio, evictions, size |
| Database | Connection pool, query time |
| Business | `budget.service` timer, `measurement.service` counter |

### Endpoints

- `/actuator/health` — Health checks (liveness + readiness)
- `/actuator/prometheus` — Métricas Prometheus format
- `/actuator/metrics/{name}` — Métrica individual
- `/actuator/caches` — Status dos caches

### @Observed

Aplicado nos services críticos. Gera automaticamente:
- Span no trace (propagado para chamadas downstream)
- Timer métrica (`method.timed`)
- Error counter

---

## 8. Persistência e Banco de Dados

### Convenções

| Aspecto | Decisão |
|---------|---------|
| Primary Key | `UUID` (gerado no app, não no DB) |
| Naming | `snake_case` para tabelas e colunas |
| Audit | `created_at`, `updated_at` via `@MappedSuperclass` |
| Soft Delete | Via `TrashItem` (lixeira com restore) |
| Full-text | `tsvector` + GIN index para busca textual |

### Migrations (Flyway)

```
V1__init.sql                          → Schema base (30+ tabelas)
V2__demo_data.sql                     → Dados de demonstração
V3__additional_tables.sql             → Tabelas complementares
V4__fulltext_search_indexes.sql       → Índices GIN para busca
V5-V13                                → Evoluções incrementais
```

### Hibernate Configuration

```yaml
jpa:
  open-in-view: false                 # Sem lazy loading em controllers
  hibernate:
    ddl-auto: update                  # Dev only (prod: validate)
  properties:
    hibernate:
      default_batch_fetch_size: 64    # Evita N+1
```

---

## 9. Cache

### Estratégia

```java
@Configuration
@EnableCaching
public class CacheConfiguration {
    @Bean
    public CacheManager cacheManager() {
        var manager = new CaffeineCacheManager();
        manager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(Duration.ofMinutes(10))
            .recordStats());  // Expõe métricas ao Micrometer
        return manager;
    }
}
```

### Caches Definidos

| Cache | TTL | Uso |
|-------|-----|-----|
| `compositions` | 10min | Catálogo SINAPI (muda mensalmente) |
| `materials` | 10min | Preços de insumos |
| `projects` | 10min | Lista de projetos do tenant |
| `users` | 10min | Dados do usuário logado |
| `weather` | 10min | Previsão do tempo |

### Invalidação

- TTL-based (expira automaticamente)
- Manual via `@CacheEvict` em operações de escrita
- Endpoint `/actuator/caches` para monitoramento

---

## 10. Comunicação em Tempo Real

### WebSocket + STOMP

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }
}
```

### Canais

| Destino | Uso |
|---------|-----|
| `/topic/notifications` | Broadcast para todos os usuários |
| `/user/{id}/queue/alerts` | Notificações pessoais |
| `/topic/measurements/{id}` | Updates de medição em tempo real |

### Fluxo

1. Evento de domínio publicado (ex: medição aprovada)
2. `WebSocketNotificationService.broadcast()` envia via STOMP
3. Frontend Angular recebe e atualiza UI sem refresh

---

## 11. Busca Full-Text

### Dual Strategy

| Engine | Uso | Quando |
|--------|-----|--------|
| **PostgreSQL tsvector** | Busca em entidades JPA | Dados transacionais (orçamentos, contratos) |
| **Elasticsearch** | Busca em catálogo SINAPI | Alto volume, fuzzy matching, relevância |

### Elasticsearch — Composições SINAPI

```java
@Document(indexName = "compositions")
public class CompositionSearchDocument {
    @Field(type = FieldType.Text, analyzer = "brazilian")
    private String description;
}
```

Query com fuzzy matching:
```json
{"multi_match": {"query": "concreto fck 25", "fields": ["description^3", "code^2"], "fuzziness": "AUTO"}}
```

---

## 12. Geração de Relatórios

### Estratégia por Tipo

| Tipo | Tecnologia | Exemplo |
|------|-----------|---------|
| PDF tabular | JTE + OpenHTMLtoPDF | Boletim de medição, NF |
| PDF com gráficos | Playwright (headless) | Curva S, EVM |
| Excel | FastExcel (streaming) | Exportação de dados |
| Assíncrono | Virtual Threads + fila | Relatórios pesados |

### Structured Concurrency em Relatórios

```java
try (var scope = StructuredTaskScope.open()) {
    var budget = scope.fork(() -> collectBudgetData(projectId));
    var schedule = scope.fork(() -> collectScheduleData(projectId));
    var costs = scope.fork(() -> collectCostData(projectId));
    scope.join();
    // Coleta paralela, cancelamento automático em falha
}
```

---

## 13. Resiliência

### Circuit Breaker (Resilience4j)

```yaml
resilience4j:
  circuitbreaker:
    instances:
      emailService:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
```

### Padrões Aplicados

| Padrão | Onde | Config |
|--------|------|--------|
| Circuit Breaker | Email, APIs externas | 50% failure → open |
| Retry | Email, Weather API | 3 tentativas, backoff exponencial |
| Timeout | Todas as chamadas HTTP | 30s (server.tomcat.connection-timeout) |
| Bulkhead | Virtual Threads (implícito) | Sem thread pool exhaustion |

---

## 14. Multi-Tenancy

### Estratégia: Row-Level Isolation

```java
@Entity
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = UUID.class))
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class Budget extends TenantAwareEntity { }
```

### Fluxo

1. JWT contém `tenant_id` claim
2. `TenantInterceptor` extrai e seta no `TenantContext` (ScopedValue + ThreadLocal)
3. Hibernate Filter ativado na Session — todas as queries filtram por tenant
4. `TenantEntityListener` seta `tenant_id` automaticamente em novos registros

### Scoped Values (Java 25)

```java
public static final ScopedValue<UUID> CURRENT = ScopedValue.newInstance();
```

O tenant ID é imutável durante toda a request e automaticamente propagado para virtual threads filhas.

---

## 15. Testes

### Pirâmide

```
         ┌─────────┐
         │  E2E    │  Cypress (3 specs)
         │(Cypress)│
        ┌┴─────────┴┐
        │Integration │  Testcontainers + @SpringBootTest (15+)
        │(Testcont.) │
       ┌┴────────────┴┐
       │  Unit Tests   │  JUnit 5 + Mockito (40+)
       └───────────────┘
```

### Ferramentas

| Ferramenta | Uso |
|-----------|-----|
| JUnit 5 | Unit tests |
| Mockito | Mocking de dependências |
| Testcontainers | PostgreSQL real em testes de integração |
| ArchUnit | Validação de boundaries arquiteturais |
| Cypress | E2E no frontend |
| JaCoCo | Coverage report |

### ArchUnit — Regras Enforçadas

```java
@ArchTest
static final ArchRule domainShouldNotDependOnApplication =
    noClasses().that().resideInAPackage("..domain..")
        .should().dependOnClassesThat().resideInAPackage("..application..");
```

---

## 16. Infraestrutura e Deploy

### Docker Compose (Desenvolvimento)

```bash
docker compose -f compose.dev.yaml up
```

Serviços: PostgreSQL, Elasticsearch, Keycloak, Prometheus, Grafana, OTel, SonarQube, MailHog, API, Frontend.

### Jib (Build de Imagem)

```bash
mvn package jib:dockerBuild    # Local
mvn package jib:build          # Push direto para registry
```

Base image: `eclipse-temurin:25-jre` (sem Dockerfile necessário).

### Helm (Kubernetes)

```
helm/sinapipro/
├── Chart.yaml
├── values.yaml
└── templates/
    ├── api-deployment.yaml
    ├── web-deployment.yaml
    ├── services.yaml
    └── ingress.yaml
```

### CI/CD (GitHub Actions)

```yaml
# .github/workflows/showcase-ci.yml
- Build + Test (Testcontainers)
- SonarQube analysis
- Docker image build (Jib)
- OWASP dependency check
```

---

## 17. Frontend Angular

### Arquitetura

```
web/src/app/
├── core/           → Guards, interceptors, auth service
├── shared/         → Componentes reutilizáveis, pipes
├── layout/         → Shell (sidebar, header, footer)
└── pages/          → Feature modules (35+)
```

### Padrões

| Padrão | Implementação |
|--------|---------------|
| Standalone Components | Sem NgModules |
| Lazy Loading | Cada page é lazy-loaded via router |
| Interceptors | Auth (JWT), Error (toast), Base URL |
| Guards | AuthGuard (redireciona para login) |
| State | Angular Signals (local) |

### Comunicação com API

- HTTP: `HttpClient` com interceptors
- WebSocket: STOMP client para notificações
- Error handling: Interceptor global com toast

---

## 18. Decisões Arquiteturais (ADRs)

### ADR-001: Java 25 com Preview Features

**Contexto**: Projeto showcase, não produção.
**Decisão**: Usar `--enable-preview` para Structured Concurrency, Scoped Values, Gatherers.
**Consequência**: Requer flag em compile e runtime. Aceitável para showcase.

### ADR-002: Vertical Slicing sobre Layered Architecture

**Contexto**: 35+ módulos de domínio.
**Decisão**: Cada módulo é auto-contido (api/application/domain).
**Consequência**: Facilita navegação, evita "god packages", permite extração para microserviço.

### ADR-003: PostgreSQL tsvector + Elasticsearch (dual search)

**Contexto**: Busca textual em dados transacionais (poucos) e catálogo SINAPI (100k+ itens).
**Decisão**: tsvector para queries simples em JPA, Elasticsearch para catálogo.
**Consequência**: Complexidade de manter dois engines, mas performance otimizada para cada caso.

### ADR-004: Virtual Threads sobre WebFlux

**Contexto**: Necessidade de alta concorrência.
**Decisão**: Virtual Threads (Loom) com código imperativo.
**Consequência**: Código mais simples que reativo, mesma performance para I/O-bound workloads.

### ADR-005: Caffeine sobre Redis

**Contexto**: Showcase single-node.
**Decisão**: Caffeine (in-process) com métricas Micrometer.
**Consequência**: Não escala em cluster. Para produção multi-node, migrar para Redis.

### ADR-006: JWT self-issued + Keycloak opcional

**Contexto**: Simplicidade para dev, enterprise para prod.
**Decisão**: JWT gerado internamente (dev/demo) + suporte a Keycloak (prod).
**Consequência**: Dois modos de autenticação, configurável via properties.

### ADR-007: Jib sobre Dockerfile

**Contexto**: Build reproduzível sem Docker daemon.
**Decisão**: Jib Maven plugin.
**Consequência**: Não precisa de Docker instalado para build. Layers otimizadas automaticamente.

### ADR-008: @Observed sobre instrumentação manual

**Contexto**: Observabilidade em 35+ services.
**Decisão**: Annotation `@Observed` nos services críticos.
**Consequência**: Zero boilerplate. Spans + métricas automáticos. Trade-off: menos controle granular.

---

## Glossário

| Termo | Definição |
|-------|-----------|
| **BDI** | Benefícios e Despesas Indiretas (markup sobre custo direto) |
| **SINAPI** | Sistema Nacional de Pesquisa de Custos e Índices da Construção Civil |
| **EVM** | Earned Value Management (PV, EV, AC, CPI, SPI) |
| **CPM** | Critical Path Method (caminho crítico do cronograma) |
| **Job Costing** | Orçado vs. Comprometido vs. Realizado por centro de custo |
| **Medição** | Aferição de serviços executados para faturamento |
| **Curva S** | Gráfico acumulado de progresso físico/financeiro |
| **Punch List** | Lista de pendências para entrega da obra |
| **RFI** | Request for Information (solicitação de esclarecimento) |
| **Submittal** | Documento técnico submetido para aprovação |
