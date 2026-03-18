---
description: Security rules — auth, input validation, secrets, dependency scanning
inclusion: auto
---

# Security Steering

## Authentication and authorization
- All endpoints require authentication unless explicitly marked public
- Enforce authorization server-side, never trust client-side checks alone
- Never store passwords in plain text — use bcrypt or equivalent

## Input validation
- Validate all inputs at the API boundary
- Reject unexpected fields
- Sanitize any input rendered in HTML (XSS prevention)
- File uploads: validate MIME type, enforce size limits

## SQL injection prevention
- Use parameterized queries exclusively
- Never use string concatenation in SQL queries

## Secrets management
- Never commit secrets, tokens, API keys, or credentials to git
- Use environment variables or a secrets manager
- If a secret was accidentally committed: rotate immediately

## Dependency security
- Run dependency vulnerability scanning in CI
- Critical CVEs: patch within 7 days
- High CVEs: patch within 30 days
