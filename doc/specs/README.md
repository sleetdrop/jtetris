# Spec Workflow

This folder defines the project spec mechanism used to drive changes safely.

## Source path note
- Current source layout is Maven standard: `src/main/java/net/vetcafe/jtetris` and `src/test/java/net/vetcafe/jtetris`.
- Older specs may still mention historical paths under `src/tetris`; map them to the current layout when implementing.

## Goals
- Keep optimization work incremental and reviewable.
- Bind every non-trivial change to an explicit spec.
- Require clear acceptance checks before merge.

## Status model
- `Draft`: idea captured, not approved yet.
- `Approved`: scope accepted, ready for implementation.
- `In-Progress`: implementation branch opened.
- `Done`: merged and validated.
- `Deferred`: intentionally paused.

## Spec granularity (selected)
We use **hybrid granularity**:
- One milestone spec (for context and boundaries).
- Multiple small implementation specs under that milestone.

This matches option **C** from the decision set.

## Acceptance policy (selected)
We use **strict gate acceptance**:
- Manual verification notes are required.
- Automated checks are required when practical (`mvn clean test` at minimum once tests exist).
- Changes are not considered complete without acceptance evidence.

This matches option **C** from the decision set.

## File naming
- Milestones: `mX-<topic>.md` (for example `m1-core-rules.md`)
- Task specs: `mX.Y-<topic>.md` (for example `m1.1-7bag-randomizer.md`)

## Required sections for each spec
1. `Spec ID`
2. `Status`
3. `Scope`
4. `Out of scope`
5. `Design notes`
6. `Implementation checklist`
7. `Acceptance criteria`
8. `Verification`
9. `Rollback plan`

## Working routine
1. Create or update a spec in this folder (start from `doc/specs/_feature-spec-template.md` when applicable).
2. Define an implementation file allowlist in the spec before coding.
3. Implement one small checklist item per session/commit when possible.
4. Fill verification results directly in the spec.
5. End each step by preparing/updating a handoff note for the next step (from `doc/specs/_session-handoff-template.md`), saved near the active spec (for example `doc/specs/m8.1-handoff.md`).
6. Before context handoff, append one entry to `context-pack.md` when a sub-spec is completed.
7. Mark spec status as `Done` only after verification is complete.

## Solo-dev phase note
- During early development, GitHub issue/PR linkage is optional.
- Once collaboration starts, add issue/PR references back as required metadata.

