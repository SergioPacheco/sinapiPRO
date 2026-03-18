# Tasks — [Feature Name]

## Feedback loops

Run these after every task. Do NOT commit if any fail.

```bash
# Replace with your project's actual commands
mvn test                    # tests must pass
mvn compile                 # types/compilation must pass
mvn checkstyle:check        # lint must pass
```

## Task list

### Task 1: [Title]
- **Priority:** <!-- high/medium/low — high = risky, do first -->
- **Description:** <!-- What to do — one thing only -->
- **Files:** <!-- Expected files to create/modify -->
- **Done criteria:**
  - [ ] <!-- e.g., Unit test passes -->
  - [ ] <!-- e.g., Migration runs without error -->
  - [ ] <!-- Feedback loops pass -->
- **Commit:** <!-- e.g., feat(auth): add login endpoint -->
- **Parallel:** <!-- yes/no — can this run alongside other tasks? -->
- **Risks:** <!-- Any specific risk for this task -->
- **Dependencies:** <!-- Other tasks that must be done first -->

### Task 2: [Title]
- **Priority:**
- **Description:**
- **Files:**
- **Done criteria:**
  - [ ]
  - [ ] Feedback loops pass
- **Commit:**
- **Parallel:**
- **Risks:**
- **Dependencies:**

<!-- Add more tasks as needed -->

## Execution order

Tasks ordered by risk (highest first):
1. Task 1 — [why this is first: architectural decision / integration point / spike]
2. Task 2

## Notes
- Each task = one Ralph iteration = one atomic commit
- If a task touches > 5 files, split it
- Never outrun your feedback loops
