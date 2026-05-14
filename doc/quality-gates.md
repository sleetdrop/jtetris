# Quality Gates

This document defines practical quality gates for local development and PR validation.

## Gate levels

- `Gate 0` (fast local): compile + unit tests.
- `Gate 1` (pre-PR): full test suite + deterministic replay check.
- `Gate 2` (PR/CI): same as Gate 1 plus documentation sanity checklist.

## Gate 0: Fast local check

```bash
mvn -q test
```

Pass condition:
- Build succeeds.
- All tests pass.

## Gate 1: Pre-PR check

```bash
mvn -q clean test
```

Recommended focus after run:
- `BoardRegressionGateTest` for model invariants.
- `ReplayHooksTest` for deterministic replay reconstruction.
- `ScoringRulesTest` and `TSpinDetectorTest` for scoring semantics.

Pass condition:
- Full suite passes from clean state.
- No unexpected test flakes.

## Gate 2: PR/CI check

For this project, CI should execute:

```bash
mvn -q clean test
```

And enforce a lightweight docs checklist:
- Behavior change has corresponding spec update in `doc/specs/`.
- `doc/specs/context-pack.md` includes the new handoff entry.
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

## Failure triage order

1. `BoardRegressionGateTest`
2. `ReplayHooksTest`
3. Mechanic-focused tests (`LockDelayTest`, `SrsRotationTest`, `HoldPieceTest`, `GhostPieceTest`)
4. Scoring tests (`ScoringRulesTest`, `TSpinDetectorTest`)

This order usually narrows root cause fastest.

