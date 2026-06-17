# Contributing to JTetris

Thanks for contributing.

## Prerequisites
- JDK 17
- Use the included Maven Wrapper (`./mvnw` on macOS/Linux, `mvnw.cmd` on Windows), or install Maven 3.9+ locally.

## Project layout
- Main source: `src/main/java/net/vetcafe/jtetris`
- Tests: `src/test/java/net/vetcafe/jtetris`
- Current specs: `openspec/`
- Historical specs and design docs: `doc/`

## Workflow
- For non-trivial changes, create or update an OpenSpec change under `openspec/changes/<change-id>/`.
- Include `proposal.md`, `tasks.md`, and spec deltas under `specs/<capability>/spec.md`.
- Keep the change scoped to the file allowlist recorded in the active change.
- Use `doc/specs` as historical context, not as the default location for new specs.

## Local validation
Run before opening a PR:

```bash
./mvnw clean test
```

## Pull request checklist
- Keep changes focused and small.
- Add or update tests for behavior changes.
- Update docs when user-facing behavior or structure changes.
- Ensure `./mvnw clean test` passes locally.
- Fill verification notes in the related OpenSpec change.
