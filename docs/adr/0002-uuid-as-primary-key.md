# ADR-0002 — UUID como Primary Key

## Status
Aceito

## Contexto
O sistema é multi-tenant e pode no futuro ter dados distribuídos ou sincronização offline (app mobile em obra). Chaves sequenciais (BIGSERIAL) criam dependência do banco para geração de IDs e expõem informações sobre volume de dados.

## Decisão
Usar **UUID v7 (time-ordered)** como primary key em todas as entidades, gerado pela JVM via `GenerationType.UUID` do Hibernate 7.

## Alternativas consideradas
1. **BIGSERIAL** — simples, compacto (8 bytes), ótimo para índices B-tree. Mas: não funciona bem em cenários distribuídos, expõe contagem.
2. **UUID v4 (random)** — universalmente único, mas fragmenta índices B-tree (inserções aleatórias).
3. **UUID v7 (time-ordered, escolhido)** — mantém ordenação temporal, reduz fragmentação de índice, geração client-side.
4. **ULID** — similar ao UUID v7 mas não é padrão IETF.

## Consequências
### Positivas
- IDs gerados na aplicação (sem round-trip ao banco)
- Merge de dados entre tenants/ambientes sem conflito
- Preparado para eventual distribuição
- Não expõe volume de dados na URL

### Negativas
- 16 bytes vs 8 bytes (índices maiores, ~2x)
- JOINs ligeiramente mais lentos que com integer
- URLs mais longas (`/budgets/550e8400-e29b-41d4-a716-446655440000`)

### Riscos
- Em tabelas com milhões de rows e muitos JOINs, o overhead de 16 bytes pode ser mensurável

## Como medir sucesso
- Query plans sem degradação significativa vs integer PKs
- Zero conflito de ID em imports/merges entre ambientes

## Plano de rollback
Não reversível sem migração completa. Mitigação: se performance for problema, adicionar índices parciais ou materialized views com integer surrogate keys para queries analíticas.
