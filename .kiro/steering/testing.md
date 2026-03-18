---
description: Testing strategy — what to test, how to test, coverage expectations
inclusion: auto
---

# Testing Steering

## Test strategy
- Unit tests for all service-layer business logic
- Integration tests for data access with real database (Testcontainers or equivalent)
- API tests for endpoints (request/response validation)

## Coverage expectations
- New service methods: 80%+ branch coverage
- Bug fixes: must include a regression test that fails without the fix
- Refactoring: all existing tests must pass without modification
- Legacy code with no tests: add characterization test before modifying

## Test structure
Use Arrange-Act-Assert pattern. Name tests descriptively: `should_expectedResult_when_condition`.

## Test data
- Use builders/factories for test entities
- Never share mutable state between tests
- Never depend on data from other tests

## What NOT to test
- Framework internals (ORM cascade, DI wiring)
- Getters/setters
- Third-party library behavior
