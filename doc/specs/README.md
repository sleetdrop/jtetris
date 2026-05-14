# Spec Workflow

This folder defines the project spec mechanism used to drive changes safely.

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
- PRs without acceptance evidence cannot be merged.

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
1. Create or update a spec in this folder.
2. Open a GitHub issue and reference the `Spec ID`.
3. Implement one small checklist item per PR when possible.
4. Fill verification results directly in the spec and PR.
5. Mark spec status as `Done` only after merge + verification.
6. Run context compression using `m2-context-compression.md` and append one entry to `context-pack.md`.

