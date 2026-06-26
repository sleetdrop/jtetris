# Quality Gates

This document defines practical quality gates for local development, OpenSpec changes, and optional PR validation.

## Gate levels

- `Gate 0` (fast local): compile + unit tests.
- `Gate 1` (local completion): formatter + lint + full test suite +
  deterministic replay check.
- `Gate 2` (review/CI): same as Gate 1 plus OpenSpec and documentation sanity checklist.

## Java style checks

JTetris uses the reader-first Java style in `doc/java-style.md`.

On macOS, run formatter and lint commands with the project JDK:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

Check formatting:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 17) ./mvnw spotless:check
```

Apply formatting:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 17) ./mvnw spotless:apply
```

Check the low-risk lint baseline:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 17) ./mvnw checkstyle:check
```

## Gate 0: Fast local check

```bash
./mvnw -q -Djava.awt.headless=true test
```

Pass condition:
- Build succeeds.
- All tests pass.

## Gate 1: Local completion check

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 17) ./mvnw spotless:check
JAVA_HOME=$(/usr/libexec/java_home -v 17) ./mvnw checkstyle:check
./mvnw -q -Djava.awt.headless=true clean test
```

Recommended focus after run:
- `LoggingSettingsTest`, `LoggingBootstrapTest`, and `EdtWatchdogTest` for diagnostic infrastructure.
- `InputLogTest` for stable input fields and DEBUG/TRACE gating.
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
JAVA_HOME=$(/usr/libexec/java_home -v 17) ./mvnw spotless:check
JAVA_HOME=$(/usr/libexec/java_home -v 17) ./mvnw checkstyle:check
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

## Real input reproduction flow

For a problem that occurs only during real Swing interaction:

1. Build with `./mvnw -Djava.awt.headless=true clean package`.
2. Launch with:

   ```bash
   java -Djtetris.debug=true \
     -Djtetris.log.input.level=TRACE \
     -jar target/jtetris-1.0.0-standalone.jar
   ```

3. Reproduce the issue once, then exit normally.
4. Preserve `jtetris.log` and relevant rolled files from the platform `logs`
   directory.
5. Compare Swing action count/timestamps, controller `holdMs`, repeater decision
   reasons and emitted steps, coordinate changes, and any `event=edt_delay`
   warnings.

Do not change DAS/ARR behavior until this evidence identifies whether the extra
movement came from actual hold duration, duplicate Swing actions, repeater
output, controller application, or EDT delay.

## Failure triage order

1. Input trace boundary comparison for real-interaction-only defects.
2. `GameplayInputControllerTest` for input and operation sequencing.
3. `BoardRegressionGateTest` for model invariants.
4. `ReplayHooksTest` for deterministic reconstruction.
5. Mechanic-focused tests (`LockDelayTest`, `SrsRotationTest`, `HoldPieceTest`, `GhostPieceTest`).
6. Scoring tests (`ScoringRulesTest`, `TSpinDetectorTest`).

This order usually narrows root cause fastest.
