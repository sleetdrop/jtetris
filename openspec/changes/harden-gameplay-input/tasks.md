# Tasks

- [ ] Add failing horizontal-input regression tests for delayed polling, deadline rebasing, duplicate press events, and equal-timestamp direction priority.
- [ ] Harden `InputRepeater` to emit at most one step per poll and use explicit press-order priority.
- [ ] Commit the horizontal repeater test and implementation step.
- [ ] Add failing soft-drop regression tests for delayed polling and deadline rebasing.
- [ ] Harden `SoftDropRepeater` to emit at most one step per poll.
- [ ] Commit the soft-drop repeater test and implementation step.
- [ ] Switch `TetrisFrame` input scheduling to monotonic elapsed time and update concise algorithm documentation.
- [ ] Commit the clock and documentation step.
- [ ] Run targeted input tests and `./mvnw clean test`.
- [ ] Complete the manual verification checklist and record results below.

## Verification Notes
- Pending implementation.
