---
description: Check preconditions before starting a spec task
event: on_task_start
---

## Conditions

- A task from a spec in `.kiro/specs/` is about to be executed
- The task file (tasks.md) exists and has structured task entries

## Instructions

- Read the current task entry from tasks.md
- Verify the task has done criteria defined — if missing, ask the user to define them before proceeding
- Check if the task has dependencies on other tasks — if those tasks are not marked complete, warn the user
- If the task mentions database changes (migration, ALTER, new table), verify the rollback strategy is documented in design.md
- If the task touches files in shared code paths (service/, model/, repository/), flag it as higher risk and remind the user to check what else depends on those files
- Read `.kiro/state/RISKS.md` and display any open risks relevant to this task's scope
