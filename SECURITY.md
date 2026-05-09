# Segurança — SinapiPRO

## Reportar Vulnerabilidades

**Não abra issues públicas para vulnerabilidades de segurança.**

Envie um e-mail para: **sergio@sinapipro.com** com:
- Descrição da vulnerabilidade
- Passos para reproduzir
- Impacto potencial

Responderemos em até 72 horas.

## Versões Suportadas

| Versão | Stack | Suporte |
|---|---|---|
| api 0.1.x (atual) | Java 25 + Spring Boot 4 + PostgreSQL 17 | ✅ Ativo |

## Práticas de Segurança

- API stateless com JWT (OAuth2 Resource Server, HMAC-SHA256)
- Senhas armazenadas com BCrypt
- Autorização por scopes (`sinapipro.read`, `sinapipro.write`) e roles (`ADMIN`)
- Validação de entrada via Bean Validation (Jakarta)
- Rate limiting por IP
- OWASP Dependency Check no CI
- Erros padronizados via ProblemDetail (RFC 9457) — sem leak de stack traces
