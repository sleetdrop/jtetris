# Tasks

- [x] Update side panel hierarchy and status labels.
- [x] Add event feedback formatting for line clears, T-Spins, combo, and B2B.
- [x] Expose hold availability to the UI without changing hold rules.
- [x] Add Swing Help page and menu/key binding entry points.
- [x] Update documentation/backlog notes for future Tetris polish.
- [x] Add/update focused tests.
- [x] Verify with `./mvnw clean test`.

## Future Polish Backlog
- Multi-piece next queue display backed by a model queue instead of a single `next` piece.
- Perfect Clear detection and scoring feedback.
- Soft drop and hard drop scoring.
- T-Spin Mini detection and scoring distinction.
- Optional advanced in-game help details for scoring formulas and modern guideline-style terminology.
- Optional player-facing onboarding prompt for first launch.

## Verification Notes
- `./mvnw -Djava.awt.headless=true clean test` passed on 2026-06-18: 52 tests, 0 failures, 0 errors, 0 skipped.
