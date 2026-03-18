---
description: Tech stack — language, frameworks, database, build, deployment
inclusion: always
---

# Tech Steering

## Language and runtime
- Java 8 (source/target 1.8)
- Spring Boot 2.0.5

## Frameworks
- Web: Spring MVC + Thymeleaf 3.x (server-side rendering)
- Persistence: Spring Data JPA / Hibernate
- Security: Spring Security (form login, roles)
- Validation: Bean Validation (JSR 303)
- Reports: JasperReports 6.3

## Database
- MySQL (production)
- Flyway ou scripts manuais para schema (verificar `arquivo.sql` e `docs/schema.sql`)

## Build and packaging
- Maven 3.x (wrapper incluído: `mvnw`)
- Artifact: JAR executável (Spring Boot)
- Deploy: Heroku (Procfile presente)

## Key dependencies
- Thymeleaf Layout Dialect
- Thymeleaf Extras Spring Security
- JasperReports + jasperreports-fonts
- Spring Boot DevTools (dev)

## Feedback loops
```bash
./mvnw compile
./mvnw test
```
