# Reader-First Java Style Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a deterministic Java style and lint baseline that keeps JTetris readable as it grows.

**Architecture:** Document the style in `doc/java-style.md`, wire formatting and linting through Maven, then apply only mechanical Java formatting and low-risk lint fixes. Larger readability refactors remain separate OpenSpec changes.

**Tech Stack:** Java 17, Maven, Spotless Maven Plugin, Palantir Java Format, Maven Checkstyle Plugin.

---

## File Structure

- `openspec/changes/add-reader-first-java-style/proposal.md`: explains why the style baseline is needed.
- `openspec/changes/add-reader-first-java-style/design.md`: records the chosen formatter, lint strategy, and staged migration.
- `openspec/changes/add-reader-first-java-style/tasks.md`: tracks implementation and verification.
- `openspec/changes/add-reader-first-java-style/specs/project-workflow/spec.md`: adds quality-gate requirements.
- `doc/java-style.md`: stable reader-first Java style guide.
- `doc/quality-gates.md`: adds formatter and lint commands to local gates.
- `config/checkstyle/checkstyle.xml`: project-owned Checkstyle rule set.
- `pom.xml`: Maven plugin configuration.
- `src/main/java/**/*.java` and `src/test/java/**/*.java`: mechanical formatting and low-risk import/name fixes only.

## Tasks

### Task 1: Record The OpenSpec Change

**Files:**
- Create: `openspec/changes/add-reader-first-java-style/proposal.md`
- Create: `openspec/changes/add-reader-first-java-style/design.md`
- Create: `openspec/changes/add-reader-first-java-style/tasks.md`
- Create: `openspec/changes/add-reader-first-java-style/specs/project-workflow/spec.md`

- [ ] Write the proposal, design, tasks, and spec delta.
- [ ] Verify the change has no placeholder text with `rg -n "TBD|TODO|implement later" openspec/changes/add-reader-first-java-style`.
- [ ] Commit with `git add openspec/changes/add-reader-first-java-style && git commit -m "spec: add reader-first java style change"`.

### Task 2: Add Style Documentation

**Files:**
- Create: `doc/java-style.md`
- Modify: `doc/quality-gates.md`

- [ ] Write `doc/java-style.md` with reader-first Java rules, formatter policy, and staged refactoring guidance.
- [ ] Add format and lint commands to `doc/quality-gates.md`.
- [ ] Commit with `git add doc/java-style.md doc/quality-gates.md && git commit -m "docs: define reader-first java style"`.

### Task 3: Add Maven Tooling

**Files:**
- Modify: `pom.xml`
- Create: `config/checkstyle/checkstyle.xml`

- [ ] Add Maven properties for Spotless, Palantir Java Format, Maven Checkstyle Plugin, and Checkstyle.
- [ ] Configure Spotless to format Java source and tests.
- [ ] Configure Maven Checkstyle to use `config/checkstyle/checkstyle.xml`.
- [ ] Create the Checkstyle configuration with low-risk style checks.
- [ ] Run `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./mvnw spotless:check` and expect formatting failures before applying format.
- [ ] Run `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./mvnw checkstyle:check` and inspect any rule failures.
- [ ] Commit with `git add pom.xml config/checkstyle/checkstyle.xml && git commit -m "build: add java format and lint gates"`.

### Task 4: Apply Mechanical Formatting

**Files:**
- Modify: `src/main/java/**/*.java`
- Modify: `src/test/java/**/*.java`

- [ ] Run `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./mvnw spotless:apply`.
- [ ] Review `git diff --stat` to confirm this is formatting-only.
- [ ] Run `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./mvnw spotless:check` and expect success.
- [ ] Commit with `git add src/main/java src/test/java && git commit -m "style: format java sources"`.

### Task 5: Fix Low-Risk Lint Failures

**Files:**
- Modify only Java files reported by Checkstyle.

- [ ] Run `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./mvnw checkstyle:check`.
- [ ] Fix only unused imports, import order, wildcard imports, missing braces, or naming issues that do not change behavior.
- [ ] Run `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./mvnw checkstyle:check` again and expect success.
- [ ] Commit with `git add src/main/java src/test/java && git commit -m "style: satisfy java lint baseline"`.

### Task 6: Verify And Record Evidence

**Files:**
- Modify: `openspec/changes/add-reader-first-java-style/tasks.md`

- [ ] Run `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./mvnw spotless:check`.
- [ ] Run `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./mvnw checkstyle:check`.
- [ ] Run `./mvnw -Djava.awt.headless=true clean test`.
- [ ] Record command results in `tasks.md`.
- [ ] Commit with `git add openspec/changes/add-reader-first-java-style/tasks.md && git commit -m "docs: record java style verification"`.

## Self-Review

- Spec coverage: style documentation, formatter, lint, formatting, and verification are all covered.
- Placeholder scan: no placeholders are intended.
- Type consistency: no production API changes are part of this plan.
