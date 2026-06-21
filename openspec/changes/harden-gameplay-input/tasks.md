# Tasks

- [x] Add failing horizontal-input regression tests for delayed polling, deadline rebasing, duplicate press events, and equal-timestamp direction priority.
- [x] Harden `InputRepeater` to emit at most one step per poll and use explicit press-order priority.
- [x] Commit the horizontal repeater test and implementation step.
- [x] Add failing soft-drop regression tests for delayed polling and deadline rebasing.
- [x] Harden `SoftDropRepeater` to emit at most one step per poll.
- [x] Commit the soft-drop repeater test and implementation step.
- [x] Switch `TetrisFrame` input scheduling to monotonic elapsed time and update concise algorithm documentation.
- [x] Commit the clock and documentation step.
- [x] Run targeted input tests and `./mvnw clean test`.
- [ ] Add a failing regression test for the application default DAS boundary observed in the player trace.
- [ ] Tune default horizontal DAS from 130ms to 180ms while preserving 35ms ARR.
- [ ] Update algorithm documentation and run focused plus full verification.
- [ ] Complete the manual verification checklist and record results below.

## Verification Notes
- Horizontal RED on 2026-06-21: `InputRepeaterTest` failed as expected because a delayed poll returned `-4` and equal-timestamp opposite presses kept the wrong active direction.
- Horizontal GREEN on 2026-06-21: `InputRepeaterTest` passed 7 tests.
- Soft-drop RED on 2026-06-21: `SoftDropRepeaterTest` failed as expected because a delayed poll returned `3`.
- Soft-drop GREEN on 2026-06-21: `SoftDropRepeaterTest` passed 5 tests.
- Combined focused verification on 2026-06-21: 12 input tests passed with 0 failures and 0 errors.
- Full headless verification on 2026-06-21: `./mvnw -Djava.awt.headless=true clean test` passed 100 tests with 0 failures and 0 errors.
- Running `./mvnw clean test` without headless mode under Homebrew OpenJDK 26.0.1 caused an AWT process abort while starting `LeaderboardContentTest`; no test assertion failed before the process crash.
- Automated GUI interaction was not accepted as manual evidence because the available screenshot/computer-control path cannot reliably target and exercise real-time Swing gameplay. Player experience verification remains pending.
- Player trace on 2026-06-21: 48 presses and 48 releases paired exactly; no duplicate presses or EDT delays occurred. Hold duration was median 121ms, p75 135ms, and maximum 176ms. The 130ms DAS caused 13 taps to emit one repeat step.
