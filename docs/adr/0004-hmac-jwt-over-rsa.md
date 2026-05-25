# ADR-0004 — HMAC-SHA256 JWT sobre RSA

## Status
Aceito

## Contexto
A API é stateless e usa JWT para autenticação. Precisamos assinar tokens. Duas opções principais: chave simétrica (HMAC) ou assimétrica (RSA/EC).

O sistema é um monolito — o mesmo processo que emite o token também o valida. Não há necessidade de distribuir uma chave pública para terceiros.

## Decisão
Usar **HMAC-SHA256** (HS256) com secret de 48+ bytes, gerenciado via variável de ambiente `JWT_SECRET`.

## Alternativas consideradas
1. **RSA (RS256)** — permite validação sem conhecer a chave privada. Necessário quando há múltiplos serviços ou IdP externo. Overhead: gerenciar par de chaves, rotação mais complexa.
2. **ECDSA (ES256)** — menor que RSA, mesma assimetria. Mesmo overhead de gerenciamento.
3. **HMAC-SHA256 (escolhido)** — simples, rápido, suficiente para monolito. Uma única secret compartilhada entre emissor e validador (que são o mesmo processo).

## Consequências
### Positivas
- Configuração mínima (1 variável de ambiente)
- Performance superior (~10x mais rápido que RSA para sign/verify)
- Sem gerenciamento de certificados

### Negativas
- Se extrairmos um serviço que precisa validar tokens, ele precisará da mesma secret (risco de exposição)
- Não suporta JWKS endpoint (clientes externos não podem validar sem a secret)

### Riscos
- Secret exposta = todos os tokens comprometidos (mitigado: rotação + env var)
- Se migrarmos para microserviços, precisaremos migrar para RSA/EC

## Como medir sucesso
- Secret nunca commitada no Git
- Token validation < 1ms (p99)
- Rotação de secret sem downtime (suportar 2 secrets simultâneas durante transição)

## Plano de rollback
Migrar para RS256 + JWKS quando houver necessidade de validação por terceiros ou extração de serviço de autenticação.
