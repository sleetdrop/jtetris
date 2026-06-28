# Tasks

- [x] Create release CI and packaging spec deltas.
- [x] Add GitHub Actions CI for Java 25 formatting, linting, and tests.
- [x] Add release-build workflow with standalone jar, macOS arm64, macOS x64, Windows 11 x64 installer, and draft GitHub Release publishing.
- [x] Add Windows jpackage profile and keep macOS packaging behavior intact.
- [x] Update Maven release version and versioned artifact documentation to `1.1.0`.
- [x] Start `CHANGELOG.md`.
- [x] Double-check score and preference compatibility with focused tests.
- [x] Refresh README screenshots after user-provided image replacements.
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
- Replaced `doc/images/jtetris-light.png` and `doc/images/jtetris-dark.png` with user-provided 1.1.0 screenshots on 2026-06-28.
- Final screenshot-refresh verification: `env JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw -Djava.awt.headless=true clean test` passed on 2026-06-28 after replacing screenshots: 151 tests, 0 failures, 0 errors, 0 skipped.
- First `v1.1.0` release-build run `28320597421` found two CI-only issues: Windows x64 tests exposed Unix-only absolute path assumptions in path/logging tests, and Windows arm64 could not provision Temurin Java 25 arm64. The release matrix was narrowed to stable native targets, with Windows arm64 documented as a standalone-jar fallback.
- `env JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw -Djava.awt.headless=true -Dtest=ApplicationDataPathsTest,ScoreDataPathsTest,LoggingSettingsTest test` passed on 2026-06-28 after the path-test fix: 18 tests, 0 failures, 0 errors, 0 skipped.
- Post-CI-fix verification on 2026-06-28: `spotless:check` passed, `checkstyle:check` passed with 0 violations, and `./mvnw -Djava.awt.headless=true clean test` passed with 151 tests, 0 failures, 0 errors, 0 skipped.
- Windows distribution was changed from an app-image zip to a Windows 11 x64 `.exe` installer, and release-tag runs now create a draft GitHub Release with assets attached.
- Installer workflow verification on 2026-06-28: `./mvnw -Djava.awt.headless=true -Pwindows help:effective-pom -Doutput=/tmp/jtetris-windows-installer-effective-pom.xml` passed locally; the actual `.exe` installer must be verified on the Windows GitHub-hosted runner.
- Post-installer-change local verification on 2026-06-28: `spotless:check` passed, `checkstyle:check` passed with 0 violations, and `./mvnw -Djava.awt.headless=true clean test` passed with 151 tests, 0 failures, 0 errors, 0 skipped.
- `env JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw -Djava.awt.headless=true -Pmac clean package` passed after the installer workflow change on 2026-06-28: 151 tests, 0 failures, 0 errors, 0 skipped, and macOS `jpackage` still used `--type app-image`.
