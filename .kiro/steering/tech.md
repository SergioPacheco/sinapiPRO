---
description: Tech stack — language, frameworks, database, build, deployment
inclusion: always
---

# Tech Steering

## Language and runtime
- Java 11
- Spring Boot 2.7.18

## Frameworks
- Web: Spring MVC + Thymeleaf 3.x (server-side rendering)
- Persistence: Spring Data JPA / Hibernate
- Security: Spring Security 5 (form login, roles)
- Validation: Bean Validation (JSR 303)
- Reports: JasperReports 6.20

## Database
- MySQL (production)
- Flyway ou scripts manuais para schema (verificar `arquivo.sql` e `docs/schema.sql`)

## Build and packaging
- Maven 3.x (wrapper incluído: `mvnw`)
- Artifact: JAR executável (Spring Boot)
- Deploy: Heroku (Procfile presente)

## Key dependencies
- Thymeleaf Layout Dialect (managed by Spring Boot BOM)
- Thymeleaf Extras Spring Security 5
- JasperReports 6.20.6 + jasperreports-fonts
- Apache POI 5.2.5
- Spring Boot DevTools (dev)

## Feedback loops
```bash
./mvnw compile
./mvnw test
```
