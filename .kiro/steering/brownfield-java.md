---
inclusion: fileMatch
fileMatchPattern: ["**/*.java", "**/pom.xml", "**/build.gradle"]
description: Brownfield Java patterns — safe refactoring, migration rules, legacy constraints
---

# Brownfield Java Steering

## Context
This project is a brownfield Java application with existing code, existing users, and existing technical debt. All changes must be safe, incremental, and backward-compatible.

## Migration rules
- Never rewrite a module in one shot — migrate incrementally
- New code follows current standards; old code is refactored only when touched
- Every migration has a rollback plan documented in DECISIONS.md

## Dependency management
- Do not upgrade major framework versions without a spike and risk assessment
- When adding a new dependency, check compatibility with the existing Java version
- Prefer the existing library over a new one if both solve the problem

## Legacy patterns to preserve (until migrated)
- If the codebase uses a pattern (e.g., DAO layer, manual transaction management), follow it in that module
- Do not mix new patterns with old patterns in the same class
- Document the target pattern in DECISIONS.md when starting a migration

## Refactoring safety
- Refactoring must be in a separate commit from behavior changes
- Extract method/class refactorings must have passing tests before and after
- Never refactor code that has no test coverage without adding tests first

## Common risks
- Shared mutable state in singletons
- Thread safety in legacy service classes
- Implicit dependencies via static methods or service locators
- Database schema changes that break existing queries

## Review checklist for legacy changes
- [ ] Does this change touch shared code? If yes, what else depends on it?
- [ ] Is there a test that covers the changed behavior?
- [ ] Does this change require a database migration?
- [ ] Is the rollback strategy documented?
- [ ] Were any deprecated APIs introduced or consumed?
