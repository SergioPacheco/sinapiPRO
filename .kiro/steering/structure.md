---
description: Project structure — directory layout, module boundaries, naming conventions
inclusion: always
---

# Structure Steering

## Project layout
```
src/main/java/br/edu/ifrn/sinapiPRO/
├── config/              ← Spring config, formatters
├── controller/          ← Spring MVC controllers (Thymeleaf views)
│   ├── converter/       ← Type converters for form binding
│   ├── handler/         ← ControllerAdvice exception handler
│   └── page/            ← Pagination wrapper
├── dto/                 ← Data transfer objects
├── model/               ← JPA entities
│   └── validation/      ← Custom validation annotations
├── repository/          ← Spring Data JPA repositories
│   ├── filter/          ← Query filter objects
│   ├── helper/          ← Custom query implementations (Criteria API)
│   └── paginacao/       ← Pagination utilities
├── security/            ← Spring Security config
├── service/             ← Business logic
│   ├── event/           ← Application events
│   └── exception/       ← Custom exceptions
├── session/             ← Session-scoped beans
├── thymeleaf/           ← Custom Thymeleaf processors
├── utils/               ← Utilities, report helpers
└── validation/          ← Custom validators
```

## Module boundaries
- Controllers handle HTTP + Thymeleaf view binding. No business logic.
- Services contain business logic and transaction boundaries.
- Repositories handle data access. Custom queries in `helper/` subpackages.
- Models are JPA entities. Used directly in Thymeleaf views (no strict DTO separation for reads).

## Naming conventions
- Controllers: `{Entity}Controller` (e.g., `OrcamentosController`)
- Services: `Cadastro{Entity}Service` (e.g., `CadastroOrcamentoService`)
- Repositories: `{Entity}Repository` + `{Entity}RepositoryQueries` + `{Entity}RepositoryImpl`
- Models: entity name singular (e.g., `Orcamento`, `Composicao`, `Insumo`)
