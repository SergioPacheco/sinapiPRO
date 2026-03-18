# Spec Archive

Completed specs are moved here after delivery is verified.

## Convention

When a feature or bugfix is fully delivered and verified:

1. Move the spec folder from `.kiro/specs/<name>/` to `.kiro/specs/archive/<date>-<name>/`
2. The date format is `YYYY-MM-DD`
3. Example: `.kiro/specs/archive/2026-03-17-add-user-auth/`

## Why archive?

- Keeps `.kiro/specs/` clean — only active work is visible
- Preserves history — decisions, context, and design rationale are never lost
- Enables retrospectives — review past deliveries for patterns and improvements

## What gets archived

The entire spec folder, including:
- requirements.md
- design.md
- tasks.md
- CONTEXT.md (if present)

## What stays active

Specs that are:
- In progress
- Planned but not started
- Blocked
