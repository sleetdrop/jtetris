# Tasks

- [x] Inspect the current ghost rendering and prior theme design constraints.
- [x] Define the unfilled double-outline visual treatment.
- [x] Add focused rendering tests for light/dark ghost cells and active-piece
  overlap.
- [x] Implement theme-aware double-outline ghost rendering in `GamePanel`.
- [x] Run focused tests and `./mvnw -Djava.awt.headless=true clean test`.
- [ ] Review user-supplied light/dark screenshots.
- [ ] Update the canonical UI theme specification and archive this change.

## Verification Notes

- Design-only step completed on 2026-06-21 before implementation began.
- RED on 2026-06-21:
  `./mvnw -Djava.awt.headless=true -Dtest=GamePanelGhostRenderingTest test`
  failed during test compilation with 5 expected errors because the new ghost
  color, outline, and overlap helpers did not yet exist.
- GREEN on 2026-06-21:
  `./mvnw -Djava.awt.headless=true -Dtest=GamePanelGhostRenderingTest test`
  passed with 4 tests, 0 failures, and 0 errors.
- Focused regression on 2026-06-21:
  `./mvnw -Djava.awt.headless=true
  -Dtest=GamePanelGhostRenderingTest,ThemeVisualsTest,GhostPieceTest test`
  passed with 9 tests, 0 failures, and 0 errors.
- Full verification on 2026-06-21:
  `./mvnw -Djava.awt.headless=true clean test` passed with 96 tests,
  0 failures, and 0 errors.
- Runnable artifact verification on 2026-06-21:
  `./mvnw -Djava.awt.headless=true clean package` passed with 96 tests and
  produced `target/jtetris-1.0.0.jar`.
