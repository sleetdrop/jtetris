# FlatLaf Shade Manifest Filter Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Remove the harmless FlatLaf manifest overlap warning without changing standalone JAR behavior.

**Architecture:** Keep the existing manifest transformer as the authoritative output manifest and exclude only FlatLaf's input manifest through a dependency-specific Shade filter.

**Tech Stack:** Maven, maven-shade-plugin 3.5.1, Java 17.

---

### Task 1: Filter and Verify

**Files:**
- Modify: `pom.xml`
- Modify: `openspec/changes/filter-flatlaf-shade-manifest/tasks.md`

- [ ] Add a `com.formdev:flatlaf` filter excluding `META-INF/MANIFEST.MF`.
- [ ] Run `./mvnw -Djava.awt.headless=true clean package` and capture the output.
- [ ] Assert the output does not contain `overlapping resource`.
- [ ] Run `jar tf target/jtetris-1.0.0-standalone.jar` and assert exactly one `META-INF/MANIFEST.MF`.
- [ ] Run `unzip -p target/jtetris-1.0.0-standalone.jar META-INF/MANIFEST.MF` and confirm `Main-Class: net.vetcafe.jtetris.ui.TetrisFrame`.
- [ ] Run `./mvnw -Djava.awt.headless=true clean test`.
- [ ] Record verification and commit.

### Task 2: Archive

**Files:**
- Modify: `openspec/specs/release-distribution/spec.md`
- Move: `openspec/changes/filter-flatlaf-shade-manifest/` to `openspec/changes/archive/filter-flatlaf-shade-manifest/`

- [ ] Merge the clean standalone packaging scenario into the canonical spec.
- [ ] Archive the completed change.
- [ ] Commit the archive.
