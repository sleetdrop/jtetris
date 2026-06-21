# Tasks

- [x] Add SLF4J 2 and Logback dependencies as a standalone dependency change.
- [x] Add shared platform application-data path resolution for score and log directories.
- [x] Add tested logging property parsing, defaults, precedence, and validation.
- [x] Add bundled rolling Logback configuration and stderr fallback bootstrap.
- [x] Add uncaught-exception capture and debug-only EDT watchdog.
- [x] Add Swing input-boundary `DEBUG` diagnostics.
- [x] Add controller `DEBUG` diagnostics and stable movement fields.
- [x] Add repeater `TRACE` diagnostics for DAS/ARR and soft-drop decisions.
- [x] Document normal, debug, input-trace, custom-directory, and external-config launches.
- [x] Run focused logging/input tests, packaging verification, and `./mvnw -Djava.awt.headless=true clean test`.
- [ ] Collect a real player reproduction log before changing input behavior again.

## Verification Notes
- Dependency verification on 2026-06-21: compile passed with SLF4J 2.0.18 and Logback 1.5.33; dependency tree contained one SLF4J API and one Logback provider.
- Path RED: `ApplicationDataPathsTest` failed at compilation because the shared resolver did not exist.
- Path GREEN: application-data, score-path, and score-manager tests passed 20 tests.
- Settings RED: `LoggingSettingsTest` failed at compilation because logging settings did not exist.
- Settings GREEN: 6 configuration tests passed.
- Bootstrap/package verification: logging tests passed; the standalone jar contains `logback.xml`, `logback-stderr.xml`, and `META-INF/services/org.slf4j.spi.SLF4JServiceProvider`.
- Watchdog RED: `EdtWatchdogTest` failed at compilation because the watchdog did not exist.
- Watchdog GREEN: watchdog and bootstrap tests passed 7 tests.
- Input diagnostics RED: `InputLogTest` failed at compilation because the input logger did not exist.
- Input diagnostics GREEN: logger, repeater, controller, and lifecycle tests passed 29 tests.
- Full verification on 2026-06-21: `./mvnw -Djava.awt.headless=true clean test` passed 134 tests with 0 failures and 0 errors.
- Packaging verification on 2026-06-21: `./mvnw -Djava.awt.headless=true package` succeeded and produced the standalone jar.
- Packaged bootstrap probe: launching the standalone jar headlessly with `jtetris.log.dir` set to a temporary directory created `jtetris.log` and recorded the expected uncaught `HeadlessException` stack.
- Remaining evidence gate: run a real GUI session with input `TRACE`, reproduce the multi-cell tap, and inspect the resulting log before modifying input behavior.
