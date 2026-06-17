# Tasks

- [x] Update license, Maven metadata, notices, and community files.
- [x] Add Maven Wrapper and document wrapper usage.
- [x] Remove obsolete GitHub/Copilot artifacts.
- [x] Remove or correct malformed font resources and align docs with reality.
- [x] Archive completed OpenSpec changes into canonical specs.
- [x] Update README up to the screenshot placeholder, then pause for user-provided screenshots.
- [x] Verify with repository checks and `./mvnw clean test`.

## Verification Notes
- `mvn -N wrapper:wrapper -Dmaven=3.9.16` initially failed in the sandbox because Maven could not write under `~/.m2`; rerunning with approval succeeded and generated the official only-script Maven Wrapper.
- `./mvnw -version` initially failed in the sandbox because the wrapper could not create `~/.m2/wrapper/dists`; rerunning with approval succeeded and reported Apache Maven 3.9.16.
- `./mvnw -Djava.awt.headless=true clean test` passed locally on 2026-06-17: 47 tests, 0 failures, 0 errors, 0 skipped.
- `./mvnw -Djava.awt.headless=true package` passed locally on 2026-06-17: 47 tests, 0 failures, 0 errors, 0 skipped, and produced `target/jtetris-1.0-SNAPSHOT.jar`.
- README screenshot embedding completed with user-provided light and dark theme screenshots copied to `doc/images/`.
- Final pre-commit verification on 2026-06-17: `git diff --check` passed, and `./mvnw -Djava.awt.headless=true clean test` passed with 47 tests, 0 failures, 0 errors, 0 skipped.
