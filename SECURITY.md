# Segurança — SinapiPRO

## Reportar Vulnerabilidades

**Não abra issues públicas para vulnerabilidades de segurança.**

Envie um e-mail para: **sergio@sinapipro.com** com:
- Descrição da vulnerabilidade
- Passos para reproduzir
- Impacto potencial

Responderemos em até 72 horas.

## Versões Suportadas

| Versão | Suporte |
|---|---|
| 1.x (atual) | ✅ Recebe patches de segurança |

## Práticas de Segurança

- Senhas armazenadas com BCrypt (cost 10)
- CSRF protection habilitado
- Validação de upload de arquivos (OWASP)
- Controle de acesso por roles (Spring Security)
- Audit trail de operações críticas
