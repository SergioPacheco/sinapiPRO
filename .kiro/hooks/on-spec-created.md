---
description: Validate spec completeness when a new spec is created or modified
event: on_file_save
---

## Conditions

- The saved file is inside `.kiro/specs/` directory
- The file is a markdown file (`.md`)
- The file is not inside `specs/archive/`

## Instructions

- Check if the spec has all required sections based on its type:
  - For feature specs (requirements.md): Summary, Acceptance criteria, Constraints
  - For bugfix specs (bugfix.md): Reproduction steps, Expected behavior, Actual behavior, Root cause
  - For design docs (design.md): Approach, Components affected, Risks, Rollback strategy
  - For task files (tasks.md): At least one task with Description, Files, Done criteria, Commit message
- If acceptance criteria or done criteria exist, verify each one is specific and testable — flag vague criteria like "works correctly" or "is fast"
- If the spec touches database (mentions migration, ALTER, new table/column), verify a rollback strategy is documented
- If the spec touches shared code or public APIs, verify risks are listed
- Report missing sections as warnings, not errors — suggest what to add
