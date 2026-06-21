# Configurable Logging Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add quiet-by-default rolling application logs and opt-in diagnostics that can identify whether intermittent movement originates at Swing, controller, repeater, or EDT timing boundaries.

**Architecture:** Application code logs through SLF4J 2. A bootstrap layer validates JTetris system properties and exposes resolved values to the bundled Logback configuration before any logger initializes. Input diagnostics use one functional logger category across Swing, controller, and repeaters; a testable debug-only watchdog observes EDT responsiveness.

**Tech Stack:** Java 17, Swing, SLF4J 2.0.18, Logback 1.5.33, JUnit 5, Maven

---

### Task 1: Logging dependencies

**Files:**
- Modify: `pom.xml`
- Modify: `openspec/changes/add-configurable-logging/tasks.md`

- [ ] Add `slf4j-api:2.0.18` and `logback-classic:1.5.33`.
- [ ] Run `./mvnw -Djava.awt.headless=true -DskipTests compile`.
- [ ] Verify dependency convergence with `./mvnw dependency:tree`.
- [ ] Commit as `build: add application logging dependencies`.

### Task 2: Shared platform data paths

**Files:**
- Create: `src/main/java/net/vetcafe/jtetris/platform/ApplicationDataPaths.java`
- Create: `src/test/java/net/vetcafe/jtetris/platform/ApplicationDataPathsTest.java`
- Modify: `src/main/java/net/vetcafe/jtetris/score/ScoreDataPaths.java`
- Modify: `src/test/java/net/vetcafe/jtetris/score/ScoreDataPathsTest.java`
- Modify: `openspec/changes/add-configurable-logging/tasks.md`

- [ ] Write failing tests for macOS, Linux/XDG, Windows/LOCALAPPDATA, fallbacks, score file, and log directory.
- [ ] Run the path tests and verify RED because `ApplicationDataPaths` does not exist.
- [ ] Implement one shared application directory resolver; retain `ScoreDataPaths` as a score-specific adapter.
- [ ] Run path and score manager tests and verify GREEN.
- [ ] Commit as `refactor: share platform application data paths`.

### Task 3: Logging settings

**Files:**
- Create: `src/main/java/net/vetcafe/jtetris/logging/LogLevel.java`
- Create: `src/main/java/net/vetcafe/jtetris/logging/LoggingSettings.java`
- Create: `src/test/java/net/vetcafe/jtetris/logging/LoggingSettingsTest.java`
- Modify: `openspec/changes/add-configurable-logging/tasks.md`

- [ ] Write failing tests for default ERROR, debug DEBUG, explicit override, input inheritance/TRACE, absolute directory override, invalid fallback, rolling values, watchdog defaults, and external-config detection.
- [ ] Run and verify RED.
- [ ] Implement immutable parsed settings with collected bootstrap warnings.
- [ ] Run and verify GREEN.
- [ ] Commit as `feat: add logging configuration settings`.

### Task 4: Logback bootstrap and rolling files

**Files:**
- Create: `src/main/java/net/vetcafe/jtetris/logging/LoggingBootstrap.java`
- Create: `src/main/resources/logback.xml`
- Create: `src/test/java/net/vetcafe/jtetris/logging/LoggingBootstrapTest.java`
- Modify: `src/main/java/net/vetcafe/jtetris/ui/TetrisFrame.java`
- Modify: `openspec/changes/add-configurable-logging/tasks.md`

- [ ] Write tests for resolved Logback properties, directory creation, external configuration bypass, and uncaught-exception handler installation.
- [ ] Run and verify RED.
- [ ] Implement bootstrap before Swing construction.
- [ ] Configure rolling file plus stderr ERROR fallback, root and input levels, size/history/total cap, and stable key-value-friendly pattern.
- [ ] Run logging tests and package the standalone jar.
- [ ] Inspect the shaded jar for SLF4J provider service metadata and `logback.xml`.
- [ ] Commit as `feat: bootstrap rolling application logs`.

### Task 5: EDT watchdog

**Files:**
- Create: `src/main/java/net/vetcafe/jtetris/logging/EdtWatchdog.java`
- Create: `src/test/java/net/vetcafe/jtetris/logging/EdtWatchdogTest.java`
- Modify: `src/main/java/net/vetcafe/jtetris/ui/TetrisFrame.java`
- Modify: `openspec/changes/add-configurable-logging/tasks.md`

- [ ] Write failing tests for timely acknowledgement, delayed warning, rate limiting, recovery, and disabled mode.
- [ ] Run and verify RED.
- [ ] Implement a daemon scheduler with injectable clock, EDT dispatcher, and warning sink.
- [ ] Start it after frame startup only when enabled; close it on window shutdown.
- [ ] Run and verify GREEN.
- [ ] Commit as `feat: add debug EDT responsiveness watchdog`.

### Task 6: Input diagnostic events

**Files:**
- Create: `src/main/java/net/vetcafe/jtetris/logging/InputLog.java`
- Create: `src/test/java/net/vetcafe/jtetris/logging/InputLogTest.java`
- Modify: `src/main/java/net/vetcafe/jtetris/ui/TetrisFrame.java`
- Modify: `src/main/java/net/vetcafe/jtetris/ui/GameplayInputController.java`
- Modify: `src/main/java/net/vetcafe/jtetris/ui/InputRepeater.java`
- Modify: `src/main/java/net/vetcafe/jtetris/ui/SoftDropRepeater.java`
- Modify: related UI tests
- Modify: `openspec/changes/add-configurable-logging/tasks.md`

- [ ] Write failing tests for stable event fields and level gating.
- [ ] Add Swing DEBUG action events with eligibility and coordinates.
- [ ] Add controller DEBUG events with hold duration, emitted steps, results, and coordinates.
- [ ] Add repeater TRACE decision events with held state, deadlines, emitted step, and reason.
- [ ] Ensure zero-step polls do not build diagnostic payloads below TRACE.
- [ ] Run input/controller/logging tests and verify GREEN.
- [ ] Commit as `feat: add layered gameplay input diagnostics`.

### Task 7: Documentation and final verification

**Files:**
- Modify: `README.md`
- Modify: `doc/overview.md`
- Modify: `doc/algorithms.md`
- Modify: `doc/quality-gates.md`
- Modify: `openspec/changes/add-configurable-logging/tasks.md`

- [ ] Document default, debug, input TRACE, custom directory, and external Logback commands.
- [ ] Document default platform paths, rolling defaults, privacy boundary, and log collection workflow.
- [ ] Run `git diff --check`.
- [ ] Run `./mvnw -Djava.awt.headless=true clean test`.
- [ ] Run `./mvnw -Djava.awt.headless=true package`.
- [ ] Launch a short headless bootstrap probe with a temporary log directory and verify an ERROR log file can be created.
- [ ] Record exact evidence and leave real-player reproduction-log collection pending.
- [ ] Commit as `docs: document configurable logging workflow`.
