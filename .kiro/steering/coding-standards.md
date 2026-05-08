---
inclusion: auto
name: coding-standards
description: Coding standards and feedback loops. Use when writing, reviewing, or committing code.
---

# Coding Standards Steering

## General rules
- No unused imports
- No commented-out code in production
- No magic numbers — use named constants
- Methods should do one thing and be under 30 lines when possible
- Prefer composition over inheritance

## Error handling
- Never swallow exceptions silently
- Use specific exception types, not generic catch-all
- Log errors with context (what was being done, with what input)
- Return meaningful error responses to callers

## Feedback loops

Never commit without running feedback loops.

### Required loops (run after every task)
<!-- Customize for your stack -->
```bash
# Compilation / type checking
# mvn compile | npm run typecheck | mypy src/

# Tests
# mvn test | npm test | pytest

# Linting
# mvn checkstyle:check | npm run lint | ruff check src/
```

### Rules
- Do NOT commit if any feedback loop fails. Fix issues first.
- Run loops after each task, not at the end of a batch.

## Explicit quality

This is production code. Follow existing patterns. Add tests. Handle errors. Log meaningfully. The AI amplifies what it sees in the codebase — keep it clean.

## Atomic commits

One task = one commit. Format: `type(scope): description`

Types: feat, fix, refactor, test, docs, chore, migration

### Clean state rule
Every commit must leave the codebase in a mergeable state. No half-implemented features, no broken builds.

### Markdown tampering protection
- Do NOT edit tasks.md except to mark checkboxes as `[x]`
- Do NOT remove, reorder, or rewrite tasks
- State files (PROGRESS.md, CHANGELOG_AI.md) are append-only during execution
