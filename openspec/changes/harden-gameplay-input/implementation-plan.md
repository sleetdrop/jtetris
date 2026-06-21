# Gameplay Input Hardening Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Prevent delayed Swing input polling from causing burst movement while preserving existing DAS/ARR controls and transition clearing.

**Architecture:** Keep key bindings and Swing timer polling in `TetrisFrame`. Harden the two small repeater state machines so a poll emits at most one current-intent step, use explicit horizontal press order, and supply monotonic elapsed time from the frame.

**Tech Stack:** Java 17, Swing, JUnit 5, Maven

---

### Task 1: Harden horizontal DAS/ARR state

**Files:**
- Modify: `src/test/java/net/vetcafe/jtetris/ui/InputRepeaterTest.java`
- Modify: `src/main/java/net/vetcafe/jtetris/ui/InputRepeater.java`
- Modify: `openspec/changes/harden-gameplay-input/tasks.md`

- [ ] **Step 1: Add failing regression tests**

Add tests equivalent to:

```java
@Test
void delayedPollEmitsOneStepAndRebasesArrDeadline() {
    InputRepeater repeater = new InputRepeater(120, 40);
    assertEquals(-1, repeater.pressLeft(0));
    assertEquals(-1, repeater.poll(240));
    assertEquals(0, repeater.poll(279));
    assertEquals(-1, repeater.poll(280));
}

@Test
void duplicatePressDoesNotMoveOrStealPriority() {
    InputRepeater repeater = new InputRepeater(120, 40);
    assertEquals(-1, repeater.pressLeft(0));
    assertEquals(1, repeater.pressRight(10));
    assertEquals(0, repeater.pressLeft(20));
    assertEquals(0, repeater.poll(129));
    assertEquals(1, repeater.poll(130));
}

@Test
void equalTimestampOppositePressUsesEventOrder() {
    InputRepeater repeater = new InputRepeater(120, 40);
    assertEquals(-1, repeater.pressLeft(50));
    assertEquals(1, repeater.pressRight(50));
}
```

- [ ] **Step 2: Run the horizontal tests and verify RED**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=InputRepeaterTest test
```

Expected: the delayed poll returns multiple steps, and the equal-timestamp direction test reports left instead of right.

- [ ] **Step 3: Implement explicit press order and one-step polling**

Replace timestamp priority fields with sequence fields:

```java
private long pressSequence;
private long leftPressOrder;
private long rightPressOrder;
```

Only update order on a genuine unheld-to-held transition:

```java
if (!leftHeld) {
    leftHeld = true;
    leftPressOrder = ++pressSequence;
}
```

When a repeat is due, emit one active-direction step and rebase:

```java
if (nowMs < nextRepeatAt) {
    return 0;
}
nextRepeatAt = nowMs + arrMs;
return activeDirection;
```

- [ ] **Step 4: Run the horizontal tests and verify GREEN**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=InputRepeaterTest test
```

Expected: all `InputRepeaterTest` tests pass.

- [ ] **Step 5: Update the checklist and commit**

Mark the first two tasks complete in `tasks.md`, then commit:

```bash
git add src/main/java/net/vetcafe/jtetris/ui/InputRepeater.java \
  src/test/java/net/vetcafe/jtetris/ui/InputRepeaterTest.java \
  openspec/changes/harden-gameplay-input/tasks.md
git commit -m "fix: stabilize horizontal input repeat"
```

### Task 2: Harden soft-drop repeat state

**Files:**
- Modify: `src/test/java/net/vetcafe/jtetris/ui/SoftDropRepeaterTest.java`
- Modify: `src/main/java/net/vetcafe/jtetris/ui/SoftDropRepeater.java`
- Modify: `openspec/changes/harden-gameplay-input/tasks.md`

- [ ] **Step 1: Replace backlog expectations with failing controllability tests**

Replace the multiple-step test with:

```java
@Test
void delayedPollEmitsOneStepAndRebasesDeadline() {
    SoftDropRepeater repeater = new SoftDropRepeater(40);
    repeater.press(0);
    assertEquals(1, repeater.poll(120));
    assertEquals(0, repeater.poll(159));
    assertEquals(1, repeater.poll(160));
}

@Test
void duplicatePressDoesNotEmitAnotherImmediateStep() {
    SoftDropRepeater repeater = new SoftDropRepeater(40);
    assertEquals(1, repeater.press(0));
    assertEquals(0, repeater.press(10));
}
```

- [ ] **Step 2: Run the soft-drop tests and verify RED**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=SoftDropRepeaterTest test
```

Expected: the delayed poll returns `3` instead of `1`.

- [ ] **Step 3: Implement one-step polling**

Replace backlog accumulation with:

```java
if (nowMs < nextRepeatAt) {
    return 0;
}
nextRepeatAt = nowMs + repeatMs;
return 1;
```

- [ ] **Step 4: Run the soft-drop tests and verify GREEN**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=SoftDropRepeaterTest test
```

Expected: all `SoftDropRepeaterTest` tests pass.

- [ ] **Step 5: Update the checklist and commit**

Mark the soft-drop test and implementation tasks complete, then commit:

```bash
git add src/main/java/net/vetcafe/jtetris/ui/SoftDropRepeater.java \
  src/test/java/net/vetcafe/jtetris/ui/SoftDropRepeaterTest.java \
  openspec/changes/harden-gameplay-input/tasks.md
git commit -m "fix: prevent soft drop repeat bursts"
```

### Task 3: Use monotonic input time and document semantics

**Files:**
- Modify: `src/main/java/net/vetcafe/jtetris/ui/TetrisFrame.java`
- Modify: `doc/algorithms.md`
- Modify: `doc/overview.md`
- Modify: `openspec/changes/harden-gameplay-input/tasks.md`

- [ ] **Step 1: Switch the input clock**

Change `nowMs()` to:

```java
private long nowMs() {
    return System.nanoTime() / 1_000_000L;
}
```

- [ ] **Step 2: Update concise documentation**

Document that horizontal and soft-drop repeaters use monotonic elapsed time, emit at most one step per poll, and discard stale missed intervals after event-dispatch delays.

- [ ] **Step 3: Run focused input tests**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=InputRepeaterTest,SoftDropRepeaterTest test
```

Expected: all input repeater tests pass.

- [ ] **Step 4: Update the checklist and commit**

Mark the clock/documentation task complete, then commit:

```bash
git add src/main/java/net/vetcafe/jtetris/ui/TetrisFrame.java \
  doc/algorithms.md doc/overview.md \
  openspec/changes/harden-gameplay-input/tasks.md
git commit -m "fix: use monotonic gameplay input timing"
```

### Task 4: Verify the complete change

**Files:**
- Modify: `openspec/changes/harden-gameplay-input/tasks.md`

- [ ] **Step 1: Run full automated verification**

Run:

```bash
git diff --check
./mvnw clean test
```

Expected: no whitespace errors; Maven reports zero failures and zero errors.

- [ ] **Step 2: Perform the manual input checklist**

Launch:

```bash
./mvnw clean package
java -jar target/jtetris-1.0.0.jar
```

Verify taps, holds, rapid opposite-direction changes, overlays, pause/resume, focus loss, and soft drop as listed in `design.md`.

- [ ] **Step 3: Record evidence**

Check all tasks and replace `Pending implementation` in `tasks.md` with exact automated and manual verification results.

- [ ] **Step 4: Commit verification notes**

```bash
git add openspec/changes/harden-gameplay-input/tasks.md
git commit -m "docs: record gameplay input verification"
```
