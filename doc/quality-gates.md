# Quality Gates

This document defines practical quality gates for local development, lightweight
Superpowers-guided work, selective OpenSpec changes, and optional PR validation.

## Gate levels

- `Gate 0` (fast local): compile + unit tests.
- `Gate 1` (local completion): formatter + lint + full test suite +
  deterministic replay check.
- `Gate 2` (review/CI): same as Gate 1 plus documentation impact and
  selective OpenSpec checks.

## Java style checks

JTetris uses the reader-first Java style in `doc/java-style.md`.

Formatter and lint commands should run on JDK 25. Use whatever environment
manager fits your platform as long as `java -version` reports 25 before running
the quality gates. Do not commit machine-specific JDK paths; prefer discovery
through `JAVA_HOME`, `direnv`, SDKMAN!, asdf, jenv, `/usr/libexec/java_home -v
25`, or package-manager metadata.

On macOS with Homebrew `openjdk@25`, this form selects JDK 25 for the current
shell:

```bash
export JAVA_HOME="$(brew --prefix openjdk@25)/libexec/openjdk.jdk/Contents/Home"
```

If you use direnv, keep a local `.envrc` in your checkout. The repository
ignores this file because the correct JDK path is platform- and machine-specific.
One Homebrew-backed macOS example:

```bash
export JAVA_HOME="$(brew --prefix openjdk@25)/libexec/openjdk.jdk/Contents/Home"
PATH_add "$JAVA_HOME/bin"
```

Then run `direnv allow` once from the repository root.

Check formatting:

```bash
./mvnw spotless:check
```

Apply formatting:

```bash
./mvnw spotless:apply
```

Check the low-risk lint baseline:

```bash
./mvnw checkstyle:check
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
./mvnw spotless:check
./mvnw checkstyle:check
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
./mvnw spotless:check
./mvnw checkstyle:check
./mvnw -q -Djava.awt.headless=true clean test
```

And enforce a lightweight docs impact checklist:
- Check whether `README.md`, `doc/overview.md`, `doc/algorithms.md`,
  `doc/quality-gates.md`, or `AGENTS.md` needs an update.
- Update user-facing docs for visible behavior, controls, storage paths, launch
  options, or workflow changes.
- Update technical docs when algorithms, replay/determinism contracts, scoring,
  input timing, quality gates, or agent workflow rules change.
- Use OpenSpec only for durable behavior contracts, product decisions,
  architecture boundaries, project workflow, build/release/dependency policy, or
  compatibility expectations; routine bug fixes, localized refactors, tests, and
  small docs edits do not need an OpenSpec change.
- When an OpenSpec change is active, record verification evidence and any needed
  handoff notes there as well as in the final response.

When Superpowers guidance is used during development, follow its verification
discipline before claiming completion: identify the command that proves the
claim, run it fresh, read the output, and report the evidence.

## GitHub Actions

The repository uses two workflow levels:

- `CI`: runs on pushes to `main` and pull requests. It provisions Java 25 with
  Temurin, then runs Spotless, Checkstyle, and the headless clean test suite.
- `Release Build`: runs on release tags and manual dispatch. It builds the
  standalone jar, macOS Apple Silicon app image, macOS Intel app image, and
  Windows 11 x64 and Windows 11 arm64 installers. On release tags, it creates
  a draft GitHub Release and uploads those assets for maintainer review.

Release builds created from manual dispatch upload workflow artifacts only.

## Java modernization policy

JTetris targets Java 25 LTS but does not require immediate source rewrites to
newer language features. Prefer modern Java features when they make code shorter
and easier to read without changing behavior. Good candidates are records for
small immutable data carriers and pattern matching where it simplifies branching.
Avoid broad `var` churn, clever stream rewrites, or sealed hierarchies unless a
focused change demonstrates a clear readability or model-boundary benefit.

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
     -jar target/jtetris-1.1.0-standalone.jar
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
