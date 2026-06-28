# Tasks

- [x] Confirm local Homebrew OpenJDK 25 tools are usable.
- [x] Verify current Java 17 build baseline under Java 25 before editing.
- [x] Identify the formatter/tooling blocker and validate a Java 25-compatible formatter path.
- [x] Update Maven Java/tooling versions and apply required formatting.
- [x] Update developer, packaging, and workflow documentation.
- [x] Record Java 25 release-runtime and modernization policy in spec deltas.
- [x] Run final Java 25 quality gates and record evidence.

## Verification Notes

- `brew --prefix openjdk@25`: `/opt/homebrew/opt/openjdk@25`
- `/opt/homebrew/opt/openjdk@25/bin/java -version`: `openjdk version "25.0.3" 2026-04-21`
- `/opt/homebrew/opt/openjdk@25/bin/javac -version`: `javac 25.0.3`
- `/opt/homebrew/opt/openjdk@25/bin/jpackage --version`: `25.0.3`
- `/usr/libexec/java_home -V`: does not list the Homebrew `openjdk@25` keg on this machine, so local docs should not assume `java_home -v 25` works without extra setup.
- `env JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw -v`: Maven 3.9.16 runs on Java 25.0.3.
- `env JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw -q -Djava.awt.headless=true test`: passed; logging test emitted an expected sandbox-denied temporary log directory message.
- `env JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw checkstyle:check`: passed with 0 Checkstyle violations.
- `env JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw spotless:check`: failed with `NoSuchMethodError` from `palantir-java-format` 2.50.0 against Java 25 javac internals.
- `env JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw -Dpalantir.java.format.version=2.94.0 spotless:check`: formatter no longer crashes; reports 4 existing files needing updated formatting.
- `env JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw -Dspotless.version=3.7.0 -Dpalantir.java.format.version=2.94.0 spotless:check`: same non-crashing result with 4 files needing formatting.
- `env JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw spotless:check`: passed after upgrading Spotless and Palantir Java Format; Spotless reported 69 clean Java files.
- `env JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw checkstyle:check`: passed with 0 Checkstyle violations.
- `env JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw -q -Djava.awt.headless=true clean test`: passed.
- `env JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw -Djava.awt.headless=true -Pmac clean package`: passed; Surefire ran 149 tests with 0 failures/errors/skips, and `jpackage` used `/opt/homebrew/Cellar/openjdk@25/25.0.3/libexec/openjdk.jdk/Contents/Home/bin/jpackage` to produce `target/dist/JTetris.app`.
- `env JAVA_HOME=/opt/homebrew/opt/openjdk@25/libexec/openjdk.jdk/Contents/Home ./mvnw -Pmac clean package`: failed before packaging because non-headless Swing tests triggered the known local AWT `Abort trap: 6` in `LeaderboardContentTest`; project docs now recommend the headless packaging command for deterministic local validation.
