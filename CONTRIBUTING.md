# Contributing to JTetris

Thanks for contributing.

## Prerequisites
- JDK 17
- Maven 3.9+

## Project layout
- Main source: `src/main/java/net/vetcafe/jtetris`
- Tests: `src/test/java/net/vetcafe/jtetris`
- Docs/specs: `doc/`

## Local validation
Run before opening a PR:

```bash
mvn clean test
```

## Pull request checklist
- Keep changes focused and small.
- Add or update tests for behavior changes.
- Update docs when user-facing behavior or structure changes.
- Ensure `mvn clean test` passes locally.
- Fill verification notes in related spec files under `doc/specs` when applicable.

