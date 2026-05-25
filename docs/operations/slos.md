# SinapiPRO — Service Level Objectives

## API Endpoints (global)

| SLI | Target | Window | Alerting |
|-----|--------|--------|----------|
| Availability (2xx + 3xx / total) | ≥ 99.5% | 30 days rolling | < 99% triggers page |
| Latency p50 | ≤ 100ms | 5 min | — |
| Latency p95 | ≤ 300ms | 5 min | > 500ms triggers warn |
| Latency p99 | ≤ 1000ms | 5 min | > 2000ms triggers page |
| Error rate (5xx / total) | ≤ 0.5% | 5 min | > 1% triggers page |

## Business Metrics

| Metric | Description | Alert |
|--------|-------------|-------|
| `measurement.submitted.total` | Medições submetidas | — |
| `measurement.approved.total` | Medições aprovadas | — |
| `budget.created.total` | Orçamentos criados | — |
| `email.circuit_breaker.state` | Estado do CB de email | OPEN > 5min triggers warn |

## Infrastructure

| SLI | Target | Alert |
|-----|--------|-------|
| JVM Heap usage | < 80% | > 85% triggers warn |
| DB connection pool active | < 80% of max | > 90% triggers page |
| Flyway migration status | success | failure triggers page |

## Error Budget

- Monthly budget: 0.5% downtime = ~3.6h/month
- Burn rate alert: if consuming > 2x normal rate, warn
- If budget exhausted: freeze non-critical deploys
