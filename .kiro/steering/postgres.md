---
inclusion: fileMatch
fileMatchPattern: ["**/*.sql", "**/migration*/**", "**/flyway/**", "**/alembic/**"]
description: PostgreSQL patterns — zero-downtime migrations, query optimization, operational rules
---

# PostgreSQL-Specific Steering

## Zero-downtime migrations
Schema changes must be compatible with both old and new application code running simultaneously.

### Safe operations (no lock, backward-compatible)
- `ADD COLUMN ... DEFAULT NULL`
- `CREATE INDEX CONCURRENTLY`
- `ADD CONSTRAINT ... NOT VALID` + `VALIDATE CONSTRAINT` in separate transaction

### Dangerous operations (require planning)
- `ADD COLUMN ... NOT NULL DEFAULT value` — rewrites table on PG < 11, safe on PG 11+
- `ALTER COLUMN TYPE` — rewrites table, locks it
- `DROP COLUMN` — safe if no code references it, but verify first
- `RENAME COLUMN` — breaks all code referencing old name

### Multi-step migration pattern
```sql
-- Step 1: Add nullable column (deploy with old code)
ALTER TABLE orders ADD COLUMN status_v2 TEXT;

-- Step 2: Backfill in batches (deploy with old code)
UPDATE orders SET status_v2 = status WHERE status_v2 IS NULL LIMIT 1000;

-- Step 3: Deploy new code that writes to both columns

-- Step 4: Add NOT NULL constraint
ALTER TABLE orders ALTER COLUMN status_v2 SET NOT NULL;

-- Step 5: Drop old column (after verifying no code uses it)
ALTER TABLE orders DROP COLUMN status;
```

## Performance patterns
- Use `EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT)` to understand query plans
- Prefer `LIMIT` + keyset pagination (`WHERE id > :last_id ORDER BY id LIMIT 20`) over `OFFSET`
- Use `MATERIALIZED VIEW` for expensive aggregations that don't need real-time data
- Batch inserts: use `INSERT INTO ... VALUES (...), (...), (...)` or `COPY` for bulk loads
- Vacuum: ensure autovacuum is tuned for high-write tables (`autovacuum_vacuum_scale_factor`)

## Operational rules
- Never run `VACUUM FULL` on production during business hours — it locks the table
- `CREATE INDEX CONCURRENTLY` instead of `CREATE INDEX` — avoids table lock
- Monitor `pg_stat_activity` for long-running queries and idle-in-transaction connections
- Set `statement_timeout` on the application connection to prevent runaway queries
- Set `idle_in_transaction_session_timeout` to kill abandoned transactions
