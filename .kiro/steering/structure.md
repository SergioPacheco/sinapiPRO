---
description: Project structure — directory layout, module boundaries, naming conventions
inclusion: always
---

# Structure Steering

## Repository layout
```
sinapiPRO/
├── api/                    ← MÓDULO ATIVO (Java 25 + Spring Boot 4)
│   ├── pom.xml
│   ├── compose.yaml                ← Dev PostgreSQL
│   ├── compose.showcase.yaml       ← Full stack (app + PG + Prometheus + Grafana)
│   ├── Dockerfile
│   ├── .mvn/settings.xml           ← Maven Central direto
│   └── src/
│       ├── main/java/.../showcase/
│       │   ├── budget/             ← Módulo de orçamentos
│       │   │   ├── api/            ← Controllers REST + DTOs (records)
│       │   │   ├── application/    ← Services + business logic
│       │   │   └── domain/         ← Entities + Repositories
│       │   ├── supplier/           ← Módulo de fornecedores
│       │   ├── invoice/            ← Módulo de faturas
│       │   ├── sinapi/             ← Catálogo SINAPI (composições + insumos)
│       │   ├── security/           ← JWT + OAuth2
│       │   ├── shared/             ← Cross-cutting (errors, events, observability)
│       │   └── config/             ← Security, OpenAPI
│       ├── main/resources/
│       │   ├── application.yaml
│       │   └── db/migration/       ← Flyway (PostgreSQL)
│       └── test/
├── src/                             ← MÓDULO LEGADO (Java 11 + Spring Boot 2.7)
│   └── main/java/.../sinapiPRO/    ← Referência de domínio apenas
├── .github/workflows/
│   └── showcase-ci.yml             ← CI/CD (build + test + docker + OWASP)
└── .kiro/
    ├── steering/                    ← Este diretório
    ├── docs/specs-and-tasks.md      ← Specs do api
    └── specs/                       ← Specs do legado (histórico)
```

## Showcase API — Module boundaries

### Padrão por domínio (vertical slicing)
Cada módulo de negócio segue a estrutura:
```
{module}/
├── api/            ← @RestController + request/response records
├── application/    ← @Service + business logic + exceptions
└── domain/         ← @Entity + Repository interface
```

### Regras
- **api/** — Controllers REST, DTOs (records), validação de entrada. Sem lógica de negócio.
- **application/** — Services com `@Transactional`. Orquestra domínio + eventos + métricas.
- **domain/** — Entities JPA + Repository (Spring Data). Sem dependência de outros módulos.
- **shared/** — Código cross-cutting: base entity, error handler, events, observability.
- **config/** — Configuração Spring (Security, OpenAPI). Sem lógica.

### Naming conventions (api)
- Controllers: `{Entity}Controller` (e.g., `BudgetController`)
- Services: `{Entity}Service` ou `{Concept}Service` (e.g., `CompositionCostService`)
- Repositories: `{Entity}Repository` (Spring Data interface)
- Entities: singular em inglês (e.g., `Budget`, `Supplier`, `Composition`)
- DTOs: `Create{Entity}Request`, `Update{Entity}Request`, `{Entity}Response`
- Exceptions: `{Domain}NotFoundException`, `{Entity}AlreadyExistsException`

### Naming conventions (legado — somente referência)
- Controllers: `{Entity}sController` (plural, e.g., `OrcamentosController`)
- Services: `Cadastro{Entity}Service` ou `{Entity}Service`
- Repositories: `{Entity}sRepository` (plural)
- Models: singular em português (e.g., `Orcamento`, `Composicao`)
