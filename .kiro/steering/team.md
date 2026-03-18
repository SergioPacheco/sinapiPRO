---
description: Team conventions — spec ownership, parallel work, branch strategy, PR rules
inclusion: manual
---

# Team Mode Steering

Conventions for multiple people and/or AI agents working on the same project simultaneously.

## Spec ownership

Every active spec has exactly one owner at a time. The owner is recorded in the spec's `requirements.md`:

```markdown
**Owner:** @username or "AI (Ralph loop)"
**Status:** in-progress | review | done
```

Rules:
- Only the owner modifies files within a spec folder
- To take over a spec, update the owner field and notify the previous owner
- AI-owned specs (Ralph loop) should be reviewed by a human before archiving

## Parallel specs

Multiple specs can be in progress simultaneously if they don't touch the same files.

Before starting a new spec in parallel:
1. Check `PROGRESS.md` of all active specs for file overlap
2. If overlap exists, either wait or coordinate with the other spec's owner
3. Document the parallel execution in STATE.md

### Conflict resolution
- If two specs modify the same file, the one that committed first wins
- The second spec must rebase and resolve conflicts before committing
- If the conflict is architectural, escalate to DECISIONS.md and discuss before proceeding

## Branch strategy

| Scenario | Branch |
|----------|--------|
| Single spec, single developer | Work on `main` or a feature branch |
| Multiple specs in parallel | Each spec gets its own branch: `spec/<spec-name>` |
| AI (Ralph loop) | Dedicated branch: `ai/<spec-name>`, human reviews before merge |
| Hotfix | `hotfix/<description>`, merged to main immediately |

## PR conventions

Every spec that runs on a branch should be merged via PR:

```markdown
## PR Title
feat: [spec-name] — [one-line summary]

## Description
Spec: .kiro/specs/<spec-name>/
Tasks completed: N/N
Verification: PASS | PASS WITH NOTES | FAIL

## Checklist
- [ ] All tasks complete (tasks.md checkboxes)
- [ ] Verification report attached (VERIFICATION.md)
- [ ] Feedback loops pass on this branch
- [ ] STATE.md and CHANGELOG_AI.md updated
- [ ] No unrelated changes included
```

## Communication

- Use DECISIONS.md for architectural discussions — it persists across sessions
- Use RISKS.md to flag concerns for other team members
- Use STATE.md session summaries to communicate what happened
- Tag spec owners in PR reviews when their area is affected

## AI + Human collaboration

| Task type | Who | Mode |
|-----------|-----|------|
| Architectural decisions | Human | HITL |
| New feature planning | Human + AI planner | HITL |
| Implementation of well-defined tasks | AI | AFK (Ralph loop) |
| Code review of AI work | Human | Review PR |
| Bug investigation | AI investigator | HITL (human confirms root cause) |
| Verification | AI verifier + human spot-check | Mixed |

## Anti-patterns

- Two people editing the same spec simultaneously — use ownership
- AI running Ralph loop on main without review — use a branch
- Merging without verification report — always run verifier first
- Ignoring DECISIONS.md — if you disagree with a decision, update it, don't silently override
