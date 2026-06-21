# Add Configurable Logging

## Summary
Add a long-lived, configurable logging foundation for JTetris using SLF4J as the application logging API and Logback as the default runtime provider.

## Motivation
The remaining intermittent horizontal-input issue cannot be diagnosed from headless tests alone because the missing evidence is at the native/Swing event boundary and in real elapsed key timing. A one-off trace file would solve only the immediate incident and create another maintenance path.

JTetris instead needs a small conventional logging system that is quiet during normal play, can be enabled when diagnosing problems, writes to a predictable platform directory, and keeps application code independent from the concrete logging backend.

## Scope
- Add SLF4J 2 API and Logback Classic runtime dependencies.
- Add a JTetris logging configuration layer for global level, input logger level, log directory, rolling limits, and debug mode.
- Use standard levels: `ERROR`, `WARN`, `INFO`, `DEBUG`, and `TRACE`.
- Default to `ERROR` file logging for serious failures only.
- Provide `DEBUG` diagnostic mode and separately configurable input `TRACE`.
- Write rolling logs under the platform application-data directory by default.
- Permit an explicit absolute log-directory override.
- Permit a complete external Logback configuration override.
- Add structured input diagnostics at the Swing action, controller, and repeater boundaries.
- Add an EDT responsiveness watchdog only when debug diagnostics are enabled.
- Capture uncaught exceptions and serious startup failures without preventing fallback startup when logging initialization fails.
- Document commands for normal, debug, and deep input-diagnostic launches.

## Out Of Scope
- No network log shipping, syslog appender, telemetry service, crash-upload service, or user-data upload.
- No in-game log viewer or settings screen.
- No always-on high-frequency input logging.
- No change to DAS, ARR, input behavior, gameplay rules, replay format, or score storage.
- No logging of usernames, home-directory contents, or other unnecessary personal data.

## File Allowlist
- `pom.xml`
- `src/main/java/net/vetcafe/jtetris/logging/**`
- `src/main/java/net/vetcafe/jtetris/platform/**`
- `src/main/java/net/vetcafe/jtetris/score/ScoreDataPaths.java`
- `src/main/java/net/vetcafe/jtetris/score/ScoreManager.java`
- `src/main/java/net/vetcafe/jtetris/ui/TetrisFrame.java`
- `src/main/java/net/vetcafe/jtetris/ui/GameplayInputController.java`
- `src/main/java/net/vetcafe/jtetris/ui/InputRepeater.java`
- `src/main/java/net/vetcafe/jtetris/ui/SoftDropRepeater.java`
- `src/main/resources/logback*.xml`
- `src/test/resources/logback-test.xml`
- `src/test/java/net/vetcafe/jtetris/logging/**`
- `src/test/java/net/vetcafe/jtetris/platform/**`
- `src/test/java/net/vetcafe/jtetris/score/ScoreDataPathsTest.java`
- `src/test/java/net/vetcafe/jtetris/score/ScoreManagerTest.java`
- `src/test/java/net/vetcafe/jtetris/ui/GameplayInputControllerTest.java`
- `src/test/java/net/vetcafe/jtetris/ui/InputRepeaterTest.java`
- `src/test/java/net/vetcafe/jtetris/ui/SoftDropRepeaterTest.java`
- `README.md`
- `doc/overview.md`
- `doc/algorithms.md`
- `doc/quality-gates.md`
- `openspec/changes/add-configurable-logging/**`
