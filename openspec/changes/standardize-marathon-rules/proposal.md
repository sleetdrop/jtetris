# Standardize Marathon Rules

## Why
Endless Marathon currently exposes guideline-style mechanics, but natural gravity
does not speed up as level increases. This makes later levels feel too similar to
the opening level and weakens the Marathon progression.

## What Changes
- Add a level-based gravity curve for Endless Marathon.
- Use a fixed UI-layer lock-delay duration for normal gravity locking.
- Expand the upcoming-piece queue and side-panel Next preview from three to five.
- Add soft-drop and hard-drop scoring.
- Record a concise local note summarizing stable guideline-style references and
  the rule that notes older than three months should be refreshed from the web.

## Scope
- In scope: Endless Marathon rules, side-panel Next layout, tests, and docs.
- Out of scope: multiple game modes, versus rules, configurable handling UI,
  dependencies, package moves, and release packaging.

## File Allowlist
- `src/main/java/net/vetcafe/jtetris/model/**`
- `src/main/java/net/vetcafe/jtetris/ui/**`
- `src/test/java/net/vetcafe/jtetris/model/**`
- `src/test/java/net/vetcafe/jtetris/ui/**`
- `README.md`
- `doc/overview.md`
- `doc/algorithms.md`
- `doc/tetris-guideline-notes.md`
- `openspec/changes/standardize-marathon-rules/**`
