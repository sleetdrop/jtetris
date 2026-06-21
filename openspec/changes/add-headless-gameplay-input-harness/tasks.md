# Tasks

- [x] Add failing controller integration tests for horizontal taps, DAS/ARR, delayed polling, and dual-direction priority.
- [x] Implement `GameplayInputController` with injected board, repeaters, and monotonic clock.
- [x] Add failing controller integration tests for soft drop, rotation, hard drop, hold, and mixed operation scenarios.
- [x] Complete controller discrete-operation delegation and repaint-result semantics.
- [x] Replace `TetrisFrame` core gameplay input orchestration with controller delegation while preserving eligibility and repaint policy.
- [x] Update input architecture and quality-gate documentation.
- [x] Run focused controller/repeater/model tests and `./mvnw -Djava.awt.headless=true clean test`.
- [x] Record player-owned subjective verification items without claiming automated GUI coverage.

## Verification Notes
- Controller RED on 2026-06-21: `GameplayInputControllerTest` failed at test compilation because `GameplayInputController` did not exist.
- Timed-input GREEN on 2026-06-21: controller and repeater tests passed 18 tests with 0 failures and 0 errors.
- Discrete-operation RED on 2026-06-21: controller tests failed at test compilation because rotation, hard-drop, and hold methods did not exist.
- Discrete-operation GREEN on 2026-06-21: controller plus affected model tests passed 25 tests with 0 failures and 0 errors.
- Swing wiring verification on 2026-06-21: controller, repeater, and session lifecycle tests passed 24 tests with 0 failures and 0 errors.
- Final focused verification on 2026-06-21: controller, repeater, movement, hold, and rotation tests passed 39 tests with 0 failures and 0 errors.
- Full verification on 2026-06-21: `./mvnw -Djava.awt.headless=true clean test` passed 113 tests with 0 failures and 0 errors.
- Automated coverage now owns objective gameplay state transitions and timing boundaries. The player owns subjective checks for DAS/ARR feel, perceived responsiveness, and visual feedback.
