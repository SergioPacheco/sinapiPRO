# Arquitetura — Visão Geral

## Decisão: Monolito Modular

SinapiPRO é um **monolito modular** com vertical slicing. Cada módulo de negócio é independente internamente mas compartilha o mesmo deploy.

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend (Angular 19)                  │
│  PrimeNG + ECharts │ SSE Notifications │ JWT Auth            │
└──────────────────────────────┬──────────────────────────────┘
                               │ HTTP/REST
┌──────────────────────────────▼──────────────────────────────┐
│                     API (Spring Boot 4)                       │
│                                                              │
│  ┌─────────┐ ┌──────────┐ ┌─────────┐ ┌──────────────┐    │
│  │ Budget  │ │Measurement│ │ Finance │ │ Procurement  │    │
│  │ api/    │ │ api/      │ │ api/    │ │ api/         │    │
│  │ app/    │ │ app/      │ │ app/    │ │ app/         │    │
│  │ domain/ │ │ domain/   │ │ domain/ │ │ domain/      │    │
│  └─────────┘ └──────────┘ └─────────┘ └──────────────┘    │
│                                                              │
│  ┌─────────────────── Shared ───────────────────────────┐   │
│  │ Security (RBAC) │ Storage │ Email │ Events │ Audit   │   │
│  └──────────────────────────────────────────────────────┘   │
└──────────────────────────────┬──────────────────────────────┘
                               │ JDBC
┌──────────────────────────────▼──────────────────────────────┐
│                    PostgreSQL 17.5                            │
│  UUID PKs │ JSONB │ tsvector FTS │ Flyway migrations        │
└─────────────────────────────────────────────────────────────┘
```

## Padrão por Módulo

```
{module}/
├── api/            ← @RestController + DTOs (records) + @PreAuthorize
├── application/    ← @Service + @Transactional + business logic
└── domain/         ← @Entity + Repository (Spring Data)
```

## Segurança (3 camadas)

1. **SecurityFilterChain** — JWT scope check (baseline)
2. **@PreAuthorize("@perm.check(...)")** — permissão granular por endpoint
3. **ProjectAccessInterceptor** — restrição por obra (user_project_access)

## Integrações

| Serviço | Interface | Implementações |
|---------|-----------|----------------|
| Storage | `StorageService` | `LocalStorageService` (dev), `S3StorageService` (prod) |
| Email | `EmailService` | `SmtpEmailService` (prod), `LogEmailService` (dev) |
| Bancário | `Cnab240Parser` | Parser CNAB 240 FEBRABAN |
| Clima | `WeatherService` | OpenWeatherMap API |

## Tech Stack

| Componente | Tecnologia | Versão |
|-----------|-----------|--------|
| Runtime | Java (Temurin) | 25 |
| Framework | Spring Boot | 4.0.5 |
| ORM | Hibernate | 7.x |
| Database | PostgreSQL | 17.5 |
| Migrations | Flyway | 11.x |
| Frontend | Angular | 19.1 |
| UI Components | PrimeNG | 19.x |
| Charts | Apache ECharts | 5.x |
| Container | Docker | multi-stage |
| Orchestration | Kubernetes + Helm | 3.x |
| Observability | Micrometer + Prometheus + Grafana | — |
