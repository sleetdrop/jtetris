# Tasks

- [x] Create the OpenSpec proposal, design, task list, and spec delta.
- [x] Add the reader-first Java style guide.
- [x] Add Spotless and Checkstyle Maven configuration.
- [x] Update quality gate documentation.
- [x] Run automatic formatting across Java source and tests.
- [x] Fix low-risk lint failures without changing behavior.
- [x] Run formatter, lint, and test verification.
- [x] Record verification evidence and handoff notes.

## Verification Notes

2026-06-26:
- `./mvnw spotless:check` under the default JDK 26 failed before formatting because
  Palantir Java Format hit a changed `javac` internal API:
  `NoSuchMethodError: Log$DeferredDiagnosticHandler.getDiagnostics()`.
- `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./mvnw spotless:check` initially
  reported 54 Java files needing formatting.
- `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./mvnw spotless:apply` formatted
  Java source and tests.
- `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./mvnw checkstyle:check` initially
  reported 41 low-risk style violations after formatting; these were fixed
  with braces, uppercase static-final names, and wrapped long strings.
- `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./mvnw spotless:check` passed.
- `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./mvnw checkstyle:check` passed
  with 0 Checkstyle violations.
- `./mvnw -Djava.awt.headless=true clean test` passed with 135 tests, 0
  failures, 0 errors, 0 skipped.

Handoff note: formatter and lint commands should run with JDK 17 to match the
project runtime. The default local JDK 26 is currently too new for the selected
Palantir Java Format stack.
