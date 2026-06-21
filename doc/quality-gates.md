# Quality Gates

This document defines practical quality gates for local development, OpenSpec changes, and optional PR validation.

## Gate levels

- `Gate 0` (fast local): compile + unit tests.
- `Gate 1` (local completion): full test suite + deterministic replay check.
- `Gate 2` (review/CI): same as Gate 1 plus OpenSpec and documentation sanity checklist.

## Gate 0: Fast local check

```bash
./mvnw -q -Djava.awt.headless=true test
```

Pass condition:
- Build succeeds.
- All tests pass.

## Gate 1: Local completion check

```bash
./mvnw -q -Djava.awt.headless=true clean test
```

Recommended focus after run:
- `GameplayInputControllerTest` for production input orchestration against a real seeded board.
- `BoardRegressionGateTest` for model invariants.
- `ReplayHooksTest` for deterministic replay reconstruction.
- `ScoringRulesTest` and `TSpinDetectorTest` for scoring semantics.

Pass condition:
- Full suite passes from clean state.
- No unexpected test flakes.

## Gate 2: Review/CI check

For this project, CI should execute:

```bash
./mvnw -q -Djava.awt.headless=true clean test
```

And enforce a lightweight docs checklist:
- Behavior change has corresponding OpenSpec change/spec update under `openspec/`.
- The active OpenSpec change records verification evidence and any needed handoff notes.
- User-facing behavior changes are reflected in `README.md` or `doc/overview.md`.

## Deterministic replay debug flow

When a gameplay issue is hard to reproduce:

1. Use a seeded board (`new Board(seed)`).
2. Capture action stream via `board.getReplayActions()`.
3. Rebuild with `Board.replayFromSeed(seed, actions)`.
4. Compare state snapshots and score fields.

Minimal sketch:

```java
Board original = new Board(42L);
original.applyReplayAction(ReplayAction.LEFT);
original.applyReplayAction(ReplayAction.ROTATE_CW);
// ...
Board replay = Board.replayFromSeed(42L, original.getReplayActions());
```

## Headless gameplay input flow

Use `GameplayInputControllerTest` for operation-level bugs that involve both
timing and board transitions:

1. Construct a seeded `Board`.
2. Inject a mutable fake millisecond clock into `GameplayInputController`.
3. Call press/release, polling, rotation, drop, and hold methods directly.
4. Assert controller results and the active piece, hold, queue, or board snapshot.
5. Do not sleep, create a `JFrame`, synthesize native input, or inspect screenshots.

This gate verifies objective state transitions. Player testing remains responsible
for subjective DAS/ARR feel, visual responsiveness, and presentation quality.

## Failure triage order

1. `GameplayInputControllerTest` for input and operation sequencing.
2. `BoardRegressionGateTest` for model invariants.
3. `ReplayHooksTest` for deterministic reconstruction.
4. Mechanic-focused tests (`LockDelayTest`, `SrsRotationTest`, `HoldPieceTest`, `GhostPieceTest`).
5. Scoring tests (`ScoringRulesTest`, `TSpinDetectorTest`).

This order usually narrows root cause fastest.
