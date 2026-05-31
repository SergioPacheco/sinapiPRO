# Deploy & Operações

## Desenvolvimento Local (um comando)

```bash
docker compose -f compose.dev.yaml up --build
```

Isso inicia:
- **PostgreSQL 17.5** (porta 5432, dados persistidos em volume)
- **API** (porta 8080, Spring Boot com Flyway auto-migration)
- **Frontend** (porta 4200, nginx servindo Angular build)

### Credenciais padrão
| Serviço | Usuário | Senha |
|---------|---------|-------|
| App (login) | admin@sinapipro.dev | SinapiPro#2026 |
| PostgreSQL | sinapipro | sinapipro |

### Parar
```bash
docker compose -f compose.dev.yaml down        # mantém dados
docker compose -f compose.dev.yaml down -v     # apaga volumes (reset)
```

---

## Desenvolvimento sem Docker

### Pré-requisitos
- Java 25 (SDKMAN: `sdk install java 25-tem`)
- Node 20+ (nvm: `nvm use 20`)
- PostgreSQL 17 rodando localmente (ou via `cd api && docker compose up`)

### Backend
```bash
cd api
docker compose up -d                          # sobe apenas PG
mvn spring-boot:run -s .mvn/settings.xml      # API em http://localhost:8080
```

### Frontend
```bash
cd web
npm install --legacy-peer-deps
npx ng serve                                  # http://localhost:4200 (proxy → 8080)
```

---

## Produção (Kubernetes)

### Pré-requisitos
- Cluster K8s (EKS, GKE, AKS ou on-prem)
- Helm 3.x
- Container registry (GHCR, ECR, etc.)
- PostgreSQL gerenciado (RDS, Cloud SQL)
- S3/MinIO para storage de documentos

### Build das imagens
```bash
# API
cd api && docker build -t ghcr.io/sinapipro/api:v1.0.0 .
docker push ghcr.io/sinapipro/api:v1.0.0

# Frontend
cd web && docker build -t ghcr.io/sinapipro/web:v1.0.0 .
docker push ghcr.io/sinapipro/web:v1.0.0
```

### Deploy com Helm
```bash
# Criar secret do banco
kubectl create secret generic sinapipro-db-secret \
  --from-literal=url=jdbc:postgresql://pg-host:5432/sinapipro \
  --from-literal=username=sinapipro \
  --from-literal=password=<SENHA_SEGURA>

# Instalar
helm install sinapipro ./helm/sinapipro \
  --set api.image.tag=v1.0.0 \
  --set frontend.image.tag=v1.0.0 \
  --set ingress.host=sinapipro.empresa.com.br \
  --set storage.type=s3 \
  --set storage.bucket=sinapipro-docs

# Atualizar
helm upgrade sinapipro ./helm/sinapipro --set api.image.tag=v1.1.0
```

### Variáveis de ambiente (produção)
```yaml
SPRING_PROFILES_ACTIVE: prod
SPRING_DATASOURCE_URL: jdbc:postgresql://host:5432/sinapipro
SINAPIPRO_SECURITY_SECRET: <JWT_SECRET_32+_CHARS>
SINAPIPRO_STORAGE_TYPE: s3
SINAPIPRO_STORAGE_S3_BUCKET: sinapipro-documents
SPRING_MAIL_HOST: smtp.empresa.com.br
SPRING_MAIL_PORT: 587
SPRING_MAIL_USERNAME: noreply@empresa.com.br
SINAPIPRO_WEATHER_API_KEY: <OPENWEATHERMAP_KEY>
```

---

## Observabilidade

### Stack (compose.showcase.yaml)
- **Prometheus** — métricas (porta 9090)
- **Grafana** — dashboards (porta 3000, admin/admin)
- **OpenTelemetry Collector** — traces distribuídos
- **Spring Actuator** — /actuator/health, /actuator/metrics, /actuator/prometheus

### Health checks
```
GET /actuator/health           → status geral
GET /actuator/health/readiness → pronto para receber tráfego
GET /actuator/health/liveness  → processo vivo
```

---

## Backup & Recovery

```bash
# Backup
pg_dump -h localhost -U sinapipro sinapipro > backup_$(date +%Y%m%d).sql

# Restore
psql -h localhost -U sinapipro sinapipro < backup_20260527.sql
```

## Flyway Migrations

Migrations em `api/src/main/resources/db/migration/` (V1–V11). Executam automaticamente no startup.

```bash
# Verificar status
cd api && mvn flyway:info -s .mvn/settings.xml

# Reparar (se migration falhou)
cd api && mvn flyway:repair -s .mvn/settings.xml
```
