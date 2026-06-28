# Tasks

- [x] Create release CI and packaging spec deltas.
- [x] Add GitHub Actions CI for Java 25 formatting, linting, and tests.
- [x] Add release-build workflow with standalone jar, macOS arm64, macOS x64, Windows x64, and best-effort Windows arm64 artifacts.
- [x] Add Windows jpackage profile and keep macOS packaging behavior intact.
- [x] Update Maven release version and versioned artifact documentation to `1.1.0`.
- [x] Start `CHANGELOG.md`.
- [x] Double-check score and preference compatibility with focused tests.
- [ ] Refresh README screenshots after user-provided image replacements.
- [x] Run documentation impact check for `README.md`, `doc/overview.md`, `doc/algorithms.md`, `doc/quality-gates.md`, and `AGENTS.md`.
- [x] Verify with `./mvnw -Djava.awt.headless=true clean test`.

## Verification Notes

- `env JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw -Djava.awt.headless=true -Dtest=ScoreDataPathsTest,ScoreManagerTest,ApplicationDataPathsTest,UserPreferencesTest test` passed on 2026-06-28: 24 tests, 0 failures, 0 errors, 0 skipped.
- `env JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw -Djava.awt.headless=true -Pwindows help:effective-pom -Doutput=/tmp/jtetris-windows-effective-pom.xml` passed on 2026-06-28 and confirmed Maven can parse the Windows packaging profile on this macOS workstation.
- `env JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw -Djava.awt.headless=true -Pmac clean package` passed on 2026-06-28: 151 tests, 0 failures, 0 errors, 0 skipped, and `jpackage` produced `target/dist/JTetris.app` with version `1.1.0`.
- Documentation impact check completed: updated `README.md`, `doc/overview.md`, `doc/quality-gates.md`, `AGENTS.md`, and `openspec/project.md`; `doc/algorithms.md` did not need changes because no mechanics or persistence algorithms changed.
- `env JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw spotless:check` passed on 2026-06-28: 69 Java files clean.
- `env JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw checkstyle:check` passed on 2026-06-28: 0 Checkstyle violations.
- `env JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw -Djava.awt.headless=true clean test` passed on 2026-06-28: 151 tests, 0 failures, 0 errors, 0 skipped.
- Screenshot refresh remains pending user-provided replacements for `doc/images/jtetris-light.png` and `doc/images/jtetris-dark.png`.
