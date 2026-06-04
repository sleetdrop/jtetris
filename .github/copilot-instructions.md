# GitHub Copilot Project Instructions

This file defines project-specific behavior for GitHub Copilot in this repository.

## Priority
- Follow `AGENTS.md` first for workflow and stability rules.
- For non-trivial changes, use spec workflow under `doc/specs`.

## Stability Mode
- Default mode is strict and scope-limited.
- Before edits, restate:
  - current goal
  - file allowlist
  - out-of-scope items
- Do not modify files outside the allowlist unless user explicitly re-approves.

## Mandatory Flow For Non-trivial Changes
1. Read `AGENTS.md`.
2. Read active spec and latest `doc/specs/context-pack.md` entry.
3. Implement only one small checklist step per iteration.
4. Commit the completed checklist step with a focused git commit before starting the next step.
5. Run `mvn clean test` before finishing.
6. Update spec `Verification` and append handoff notes.

## Avoid
- Opportunistic refactors.
- Unplanned package/structure/dependency changes.
- Mixing unrelated fixes into the same task.

## Preferred Prompt Files
- `.github/prompts/session-start.prompt.md`
- `.github/prompts/feature-task.prompt.md`
- `.github/prompts/session-handoff.prompt.md`

