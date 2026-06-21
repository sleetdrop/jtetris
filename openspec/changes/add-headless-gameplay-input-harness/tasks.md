# Tasks

- [x] Add failing controller integration tests for horizontal taps, DAS/ARR, delayed polling, and dual-direction priority.
- [x] Implement `GameplayInputController` with injected board, repeaters, and monotonic clock.
- [x] Add failing controller integration tests for soft drop, rotation, hard drop, hold, and mixed operation scenarios.
- [x] Complete controller discrete-operation delegation and repaint-result semantics.
- [ ] Replace `TetrisFrame` core gameplay input orchestration with controller delegation while preserving eligibility and repaint policy.
- [ ] Update input architecture and quality-gate documentation.
- [ ] Run focused controller/repeater/model tests and `./mvnw -Djava.awt.headless=true clean test`.
- [ ] Record player-owned subjective verification items without claiming automated GUI coverage.
