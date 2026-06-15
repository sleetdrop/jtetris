# Migration Tasks

## Checklist
- [x] Capture the migration in `doc/specs/m9.1-openspec-migration.md`.
- [x] Create `openspec/project.md`.
- [x] Create `openspec/AGENTS.md`.
- [x] Create canonical `openspec/specs/project-workflow/spec.md`.
- [x] Create migration proposal, design, tasks, and spec delta.
- [x] Update root `AGENTS.md` for OpenSpec-first agent maintenance.
- [x] Update README, CONTRIBUTING, quality gates, and legacy spec README.
- [x] Remove or neutralize Copilot/GitHub-specific workflow entry points.
- [x] Run `mvn clean test`.
- [x] Record final verification and handoff notes.

## Verification
- `mvn clean test`: pass on 2026-06-15; 42 tests, 0 failures, 0 errors, 0 skipped.
- Manual documentation check: pass. Root docs point to OpenSpec; Copilot prompts and old GitHub issue template are removed; PR template is a generic OpenSpec wrapper.

## Notes
- `RTK.md` is referenced by the user-provided instruction preamble but is not present in the repository root during this migration.
- Per-step commits were required by the legacy workflow, but early commit attempts could not write `.git/index.lock` and two approval attempts timed out. The reviewed migration is being committed as one focused workflow migration change.

## Handoff
- Migration files are complete and validated.
- New non-trivial work should start from `openspec/changes/<change-id>/`.
- `doc/specs` is historical context after this migration.
