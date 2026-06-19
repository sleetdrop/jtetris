# Tasks

- [x] Add a failing regression test for `light system -> dark -> auto`.
- [x] Preserve the pre-FlatLaf system appearance when resolving Auto.
- [x] Run focused and full automated verification.
- [ ] Update the canonical UI theme specification and archive this change.

## Verification Notes
- RED: `./mvnw -Djava.awt.headless=true -Dtest=ThemeVisualsTest test`
  failed because Auto remained dark after FlatDarkLaf was installed.
- GREEN: the same focused command passed with 3 tests after preserving the
  pre-FlatLaf system appearance.
- Full: `./mvnw -Djava.awt.headless=true clean test` passed with 82 tests,
  0 failures, 0 errors, and 0 skipped.
- `git diff --check` passed.
