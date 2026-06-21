# Tasks

- [x] Add SLF4J 2 and Logback dependencies as a standalone dependency change.
- [x] Add shared platform application-data path resolution for score and log directories.
- [x] Add tested logging property parsing, defaults, precedence, and validation.
- [ ] Add bundled rolling Logback configuration and stderr fallback bootstrap.
- [ ] Add uncaught-exception capture and debug-only EDT watchdog.
- [ ] Add Swing input-boundary `DEBUG` diagnostics.
- [ ] Add controller `DEBUG` diagnostics and stable movement fields.
- [ ] Add repeater `TRACE` diagnostics for DAS/ARR and soft-drop decisions.
- [ ] Document normal, debug, input-trace, custom-directory, and external-config launches.
- [ ] Run focused logging/input tests, packaging verification, and `./mvnw -Djava.awt.headless=true clean test`.
- [ ] Collect a real player reproduction log before changing input behavior again.
