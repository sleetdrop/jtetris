# Session Handoff Template

Use this when context is ending and work will continue in a new session.

## Task
- Active spec: `<spec path>`
- Goal in one line: `<goal>`
- Current status: `Draft | Approved | In-Progress | Done | Deferred`

## Scope Guardrails
- Allowed files (exact paths):
  - `<path>`
- Explicitly out of scope:
  - `<item>`

## Completed This Session
- `<change 1>`
- `<change 2>`

## Validation
- Command(s) run:
  - `mvn clean test`
- Result summary:
  - `<pass/fail + key notes>`

## Remaining Work
- Next smallest step:
  - `<step>`
- Risks/blockers:
  - `<risk>`

## Resume Prompt (copy into next session)
```text
Continue JTetris work in strict mode.
1) Read AGENTS.md, <active spec path>, and the latest entry in doc/specs/context-pack.md.
2) Follow this file allowlist only: <allowed files>.
3) Do not perform unrelated refactors, renames, or dependency/build changes.
4) Execute the next smallest step: <next step>.
5) After changes, run mvn clean test and update spec Verification + context-pack.
```

