# Runbook — Incident Response

## 1. Triage (primeiros 5 minutos)

```
1. Verificar dashboard: http://localhost:3000 (Grafana)
2. Identificar: latência? erros? indisponibilidade?
3. Verificar health: curl http://localhost:8081/actuator/health
4. Verificar logs: docker logs sinapipro-app --tail 100
```

### Severidade

| Sev | Critério | Ação |
|-----|----------|------|
| P1 | Sistema indisponível | Escalar imediatamente, rollback |
| P2 | Funcionalidade crítica degradada (medição, financeiro) | Investigar, comunicar stakeholders |
| P3 | Funcionalidade secundária com erro | Investigar no próximo horário comercial |
| P4 | Alerta cosmético / warning | Backlog |

---

## 2. Cenários comuns

### 2.1 Alta latência (p95 > 1s)

**Diagnóstico:**
```bash
# Verificar pool de conexões
curl -s http://localhost:8081/actuator/metrics/hikaricp.connections.active | jq .
curl -s http://localhost:8081/actuator/metrics/hikaricp.connections.pending | jq .

# Verificar queries lentas no PG
docker exec sinapipro-postgres psql -U sinapipro -c "SELECT pid, now()-query_start AS duration, query FROM pg_stat_activity WHERE state='active' AND now()-query_start > interval '5s';"
```

**Mitigação:**
1. Se pool saturado → aumentar `hikari.maximum-pool-size` (restart necessário)
2. Se query lenta → identificar e adicionar índice (V5 migration)
3. Se GC pressure → verificar heap: `curl localhost:8081/actuator/metrics/jvm.memory.used`

---

### 2.2 Email Circuit Breaker OPEN

**Diagnóstico:**
```bash
curl -s http://localhost:8081/actuator/metrics/resilience4j.circuitbreaker.state | jq .
# state=0 CLOSED, state=1 OPEN, state=2 HALF_OPEN
```

**Mitigação:**
1. Verificar se SMTP está acessível: `telnet ${MAIL_HOST} ${MAIL_PORT}`
2. Se SMTP down → aguardar recovery (CB reabre em 30s automaticamente)
3. Emails pendentes ficam com status `FAILED` no banco — reprocessar após recovery:
```sql
SELECT id, recipient_email, created_at FROM quotation_email WHERE sent_at IS NULL ORDER BY created_at;
```

**Impacto:** Fornecedores não recebem cotações. Operação de compras continua, emails são reenviados manualmente.

---

### 2.3 Erro 5xx em cascata

**Diagnóstico:**
```bash
# Últimos erros
docker logs sinapipro-app --tail 200 2>&1 | grep -i "error\|exception"

# Métricas de erro por endpoint
curl -s 'http://localhost:8081/actuator/metrics/http.server.requests?tag=status:500' | jq .
```

**Mitigação:**
1. Se `DataAccessException` → verificar conexão com PG
2. Se `OutOfMemoryError` → restart imediato + investigar leak
3. Se `ConstraintViolationException` → bug de validação, não é infra

---

### 2.4 Database não responde

**Diagnóstico:**
```bash
docker exec sinapipro-postgres pg_isready -U sinapipro
docker logs sinapipro-postgres --tail 50
```

**Mitigação:**
1. Se container parou → `docker compose up -d postgres`
2. Se disco cheio → `docker system prune` + expandir volume
3. Se corrompido → restore do último backup:
```bash
docker exec -i sinapipro-postgres psql -U sinapipro < backup_YYYYMMDD.sql
```

---

### 2.5 Flyway migration falhou no startup

**Diagnóstico:**
```bash
docker logs sinapipro-app 2>&1 | grep -i flyway
# Verificar tabela de histórico
docker exec sinapipro-postgres psql -U sinapipro -c "SELECT * FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 5;"
```

**Mitigação:**
1. Se migration com erro de sintaxe → corrigir SQL, incrementar versão
2. Se migration parcial → `flyway repair` (marca como resolvida)
3. **NUNCA** editar migration já aplicada — criar nova migration corretiva

---

## 3. Rollback

### Rollback de aplicação
```bash
# Voltar para imagem anterior
docker tag sinapipro-api:previous sinapipro-api:latest
docker compose -f compose.showcase.yaml up -d app
```

### Rollback de schema (se migration V5 causou problema)
```sql
-- Criar V6 que reverte V5
-- Exemplo: V5 adicionou coluna, V6 remove
ALTER TABLE budget DROP COLUMN IF EXISTS new_column;
```

---

## 4. Comunicação

| Quando | Para quem | Canal |
|--------|-----------|-------|
| P1 detectado | Time inteiro | Slack #incidents |
| Mitigação aplicada | Stakeholders | Email resumo |
| Postmortem pronto | Time + gestão | Documento compartilhado |

---

## 5. Postmortem template

```markdown
## Incidente: [título]
**Data:** YYYY-MM-DD HH:MM
**Duração:** X minutos
**Severidade:** P1/P2/P3
**Impacto:** [quem foi afetado, quantos usuários]

### Timeline
- HH:MM — Alerta disparado
- HH:MM — Investigação iniciada
- HH:MM — Causa identificada
- HH:MM — Mitigação aplicada
- HH:MM — Serviço restaurado

### Causa raiz
[Explicação técnica]

### O que funcionou
- [ex: alertas dispararam corretamente]

### O que não funcionou
- [ex: runbook desatualizado]

### Action items
- [ ] [Ação preventiva 1]
- [ ] [Ação preventiva 2]
```
