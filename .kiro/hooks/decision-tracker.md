---
description: Detect and record architectural decisions in DECISIONS.md
event: manual
---

# Decision Tracker Hook

## Conditions
- After a planning session where technology choices or architectural patterns were decided
- After a task implementation that introduced a new pattern or dependency
- When the user explicitly says "record this decision" or "log decision"
- After the planner agent produces a design.md with architectural choices

## Instructions

Scan the current session for architectural decisions. Look for:
1. Technology choices (library A over library B, pattern X over pattern Y)
2. Structural decisions (where to put code, how to organize modules)
3. Trade-offs discussed (performance vs simplicity, consistency vs flexibility)
4. Rejected alternatives (what was considered but not chosen, and why)

For each decision found, append an entry to `.kiro/state/DECISIONS.md`:

```markdown
### YYYY-MM-DD — [Short decision title]

**Context:** Why this decision was needed.

**Decision:** What was decided.

**Alternatives considered:**
- Alternative A — rejected because...
- Alternative B — rejected because...

**Consequences:** What this means going forward.

**Spec:** [spec-name] (if related to a specific spec)
```

Rules:
- Append only — never modify or delete previous decisions
- Be specific — "chose CDI events over direct method call" not "chose an approach"
- Include the rejected alternatives — future sessions need to know what was already considered
- If a decision contradicts a previous one, note it explicitly and explain why
- One entry per decision — don't bundle unrelated decisions
