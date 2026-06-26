# OpenSpec Agent Instructions

## Scope
- This file applies to the `openspec/` tree and defines how agents should create, update, validate, and archive JTetris specs.
- Root `AGENTS.md` still applies to the whole repository. When instructions conflict, follow the more specific instruction for the files being edited.

## Start Routine
1. Read root `AGENTS.md`.
2. Read `openspec/project.md`.
3. Inspect active changes with `find openspec/changes -maxdepth 2 -type f | sort`.
4. Read the active change's `proposal.md`, `tasks.md`, any `design.md`, and affected spec deltas when the task is inside an active OpenSpec change.
5. Restate the goal, exact file allowlist, and out-of-scope items before editing.

## When To Use OpenSpec
- OpenSpec is for durable decisions, not routine development overhead.
- Use OpenSpec when a change alters long-lived behavior contracts, product decisions, architecture boundaries, project workflow, build/release/dependency policy, or compatibility expectations.
- Do not create OpenSpec changes for routine bug fixes, localized refactors, tests, small documentation edits, or implementation details that are clear from code and commits.

## Change Structure
Each OpenSpec-backed change should use:
- `openspec/changes/<change-id>/proposal.md`
- `openspec/changes/<change-id>/tasks.md`
- `openspec/changes/<change-id>/design.md` when the change is cross-cutting, risky, or architectural.
- `openspec/changes/<change-id>/specs/<capability>/spec.md` for requirement deltas.

Use lower-kebab-case change IDs, such as `add-replay-export-ui` or `tune-lock-delay`.

## Spec Delta Style
- Use `## ADDED Requirements` for new behavior.
- Use `## MODIFIED Requirements` for behavior changes.
- Use `## REMOVED Requirements` for intentionally retired behavior.
- Every requirement must include at least one `#### Scenario:` block with concrete Given/When/Then behavior.

## Execution Rules
- Default mode is strict: implement only the active change and only the approved task.
- Do not edit outside the active file allowlist unless the user approves an updated allowlist.
- Do not change Java source, tests, build files, dependencies, package names, or public behavior from a workflow-only change.
- For gameplay timing changes, validate model and UI together.
- For model behavior changes, keep replay hooks and deterministic tests aligned.
- For UI changes, preserve keyboard focus and repeaters.

## Verification
- Minimum command before completion:
  ```bash
  ./mvnw clean test
  ```
- Record manual checks when UI or workflow behavior changes.
- Keep verification notes in the active change's `tasks.md` until the change is archived.

## Archiving
- When a change is complete, update the canonical spec under `openspec/specs/`.
- Move the completed change directory to `openspec/changes/archive/`.
- Preserve historical `doc/specs` files; do not rewrite them during archive unless the active change explicitly calls for a historical-doc correction.
