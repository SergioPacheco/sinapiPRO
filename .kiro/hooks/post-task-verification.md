---
description: Verify task completion after execution
event: on_task_complete
---

## Conditions

- A task from a spec in `.kiro/specs/` has just been completed
- The task file (tasks.md) exists with structured task entries

## Instructions

- Read the completed task entry from tasks.md
- Check each done criterion — verify it was actually satisfied
- Compare the files listed in the task with the files actually modified in the working tree — flag unexpected changes or missing expected changes
- If the task changed behavior (not just refactoring), verify a test exists that covers the change
- Verify `.kiro/state/CHANGELOG_AI.md` has an entry for this task — if not, remind the user to add one
- If the task was the last one in the spec, suggest running the verifier agent for a full delivery check
