---
description: Auto-update STATE.md with session summary at end of work
event: manual
---

# Session Summary Hook

## Conditions
- User says "end session", "wrap up", "done for today", or similar
- OR the Ralph loop completes all tasks for a spec
- OR the user explicitly asks to update state

## Instructions

Read the following to understand what happened this session:
1. `git log --oneline -20` — recent commits
2. `.kiro/state/CHANGELOG_AI.md` — what changed and why
3. Any `PROGRESS.md` files in active specs

Then update `.kiro/state/STATE.md` with a session summary block:

```markdown
## YYYY-MM-DD — Session summary

### What was done
- (list completed tasks, features, fixes)

### What's in progress
- (list partially completed work, if any)

### What's next
- (list the logical next steps)

### Open risks
- (list any new risks identified during this session)
```

Rules:
- Append to STATE.md, never overwrite previous entries
- Be specific — reference spec names, task numbers, file names
- If a spec was completed, note it and mention if it was archived
- If decisions were made, verify they're also in DECISIONS.md
