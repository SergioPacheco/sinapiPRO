# Tasks — [Bug Title]

## Feedback loops

Run these after every task. Do NOT commit if any fail.

```bash
# Replace with your project's actual commands
mvn test                    # tests must pass
mvn compile                 # types/compilation must pass
mvn checkstyle:check        # lint must pass
```

## Task list

### Task 1: Write regression test
- **Priority:** high — proves the bug exists
- **Description:** Create a test that reproduces the bug and currently fails
- **Files:**
- **Done criteria:**
  - [ ] Test fails with the current bug present
  - [ ] Test clearly documents the expected behavior
  - [ ] All other tests still pass
- **Commit:** <!-- e.g., test(module): add regression test for #123 -->

### Task 2: Implement fix
- **Priority:** high
- **Description:** Apply the minimal fix for the root cause
- **Files:**
- **Done criteria:**
  - [ ] Regression test now passes
  - [ ] All existing tests still pass
  - [ ] Feedback loops pass (tests, compile, lint)
  - [ ] No unrelated changes included
- **Commit:** <!-- e.g., fix(module): resolve null pointer in user lookup -->
- **Dependencies:** Task 1

### Task 3: Update state
- **Priority:** low
- **Done criteria:**
  - [ ] CHANGELOG_AI.md updated
  - [ ] DECISIONS.md updated if architectural choice was made
  - [ ] RISKS.md updated if new risk was identified
- **Commit:** <!-- e.g., docs(state): update changelog for bugfix #123 -->
- **Dependencies:** Task 2

## Execution order
1. Task 1 — regression test first (proves the bug)
2. Task 2 — fix (makes the test pass)
3. Task 3 — state update

## Notes
- Each task = one Ralph iteration = one atomic commit
- Never skip the regression test — it's the proof the fix works
