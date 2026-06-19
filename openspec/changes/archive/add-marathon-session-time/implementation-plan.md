# Endless Marathon Session Time Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add an active-play timer to the current Endless Marathon mode and show it below Lines without changing deterministic board or replay behavior.

**Architecture:** `GameSessionTimer` owns monotonic active-duration accumulation, while `ElapsedTimeFormatter` owns display formatting. `TetrisFrame` derives whether the timer should run from pause, overlay, and game-over state; `StageOverlayHost` reports blocking visibility changes; `SidePanel` only reads elapsed milliseconds.

**Tech Stack:** Java 17, Swing, JUnit 5, Maven, OpenSpec.

---

### Task 1: Deterministic Session Timer

**Files:**
- Create: `src/main/java/net/vetcafe/jtetris/ui/GameSessionTimer.java`
- Create: `src/test/java/net/vetcafe/jtetris/ui/GameSessionTimerTest.java`
- Modify: `openspec/changes/add-marathon-session-time/tasks.md`

- [ ] **Step 1: Write failing timer tests**

Test a mutable `LongSupplier` clock against these operations:

```java
GameSessionTimer timer = new GameSessionTimer(clock);
timer.start();
clock.advanceMillis(1_500);
assertEquals(1_500, timer.elapsedMillis());
timer.pause();
clock.advanceMillis(2_000);
assertEquals(1_500, timer.elapsedMillis());
timer.start();
clock.advanceMillis(500);
assertEquals(2_000, timer.elapsedMillis());
```

Also verify `resetAndStart()`, repeated `start()`/`pause()`, and a clock value
moving backward never reduce elapsed duration.

- [ ] **Step 2: Verify RED**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=GameSessionTimerTest test
```

Expected: compilation failure because `GameSessionTimer` does not exist.

- [ ] **Step 3: Implement the minimal timer**

Use an injected `LongSupplier` returning nanoseconds:

```java
final class GameSessionTimer {
    private final LongSupplier nanoTime;
    private long accumulatedNanos;
    private long startedAtNanos;
    private boolean running;

    GameSessionTimer() {
        this(System::nanoTime);
    }

    GameSessionTimer(LongSupplier nanoTime) {
        this.nanoTime = Objects.requireNonNull(nanoTime);
    }

    void start() { /* idempotent start */ }
    void pause() { /* accumulate non-negative delta */ }
    void resetAndStart() { /* clear and start */ }
    void syncRunning(boolean shouldRun) { /* start or pause */ }
    long elapsedMillis() { /* accumulated plus current non-negative delta */ }
}
```

- [ ] **Step 4: Verify GREEN**

Run the focused test command and confirm all timer tests pass.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/net/vetcafe/jtetris/ui/GameSessionTimer.java \
  src/test/java/net/vetcafe/jtetris/ui/GameSessionTimerTest.java \
  openspec/changes/add-marathon-session-time/tasks.md
git commit -m "feat: add marathon session timer"
```

### Task 2: Elapsed-Time Formatting And Side Panel

**Files:**
- Create: `src/main/java/net/vetcafe/jtetris/ui/ElapsedTimeFormatter.java`
- Create: `src/test/java/net/vetcafe/jtetris/ui/ElapsedTimeFormatterTest.java`
- Modify: `src/main/java/net/vetcafe/jtetris/ui/SidePanel.java`
- Modify: `src/test/java/net/vetcafe/jtetris/ui/SidePanelLayoutTest.java`
- Modify: `openspec/changes/add-marathon-session-time/tasks.md`

- [ ] **Step 1: Write failing formatter and layout tests**

Verify:

```java
assertEquals("00:00", ElapsedTimeFormatter.format(0));
assertEquals("59:59", ElapsedTimeFormatter.format(3_599_000));
assertEquals("1:00:00", ElapsedTimeFormatter.format(3_600_000));
```

Construct `SidePanel` with `() -> 3_600_000L`, find labels in component order,
and assert `Score`, `Level`, `Lines`, `Time`. Retain the established
`new Dimension(200, 520)` assertion.

- [ ] **Step 2: Verify RED**

Run:

```bash
./mvnw -Djava.awt.headless=true \
  -Dtest=ElapsedTimeFormatterTest,SidePanelLayoutTest test
```

Expected: compilation failure because the formatter and elapsed-time constructor
do not exist.

- [ ] **Step 3: Implement formatting and Layout A**

Add:

```java
static String format(long elapsedMillis) {
    long totalSeconds = Math.max(0, elapsedMillis) / 1000;
    long hours = totalSeconds / 3600;
    long minutes = (totalSeconds % 3600) / 60;
    long seconds = totalSeconds % 60;
    return hours > 0
            ? "%d:%02d:%02d".formatted(hours, minutes, seconds)
            : "%02d:%02d".formatted(minutes, seconds);
}
```

Keep `SidePanel(Board)` delegating to `SidePanel(Board, LongSupplier)` with a
zero-value supplier for compatibility. Add `timeLabel` after `linesLabel`, and
refresh it through the existing 200 ms timer. Do not change preferred size.

- [ ] **Step 4: Verify GREEN**

Run the focused formatter/layout tests and confirm they pass.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/net/vetcafe/jtetris/ui/ElapsedTimeFormatter.java \
  src/main/java/net/vetcafe/jtetris/ui/SidePanel.java \
  src/test/java/net/vetcafe/jtetris/ui/ElapsedTimeFormatterTest.java \
  src/test/java/net/vetcafe/jtetris/ui/SidePanelLayoutTest.java \
  openspec/changes/add-marathon-session-time/tasks.md
git commit -m "feat: display marathon session time"
```

### Task 3: Frame And Overlay Lifecycle

**Files:**
- Modify: `src/main/java/net/vetcafe/jtetris/ui/StageOverlayHost.java`
- Modify: `src/test/java/net/vetcafe/jtetris/ui/StageOverlayHostLayoutTest.java`
- Modify: `src/main/java/net/vetcafe/jtetris/ui/TetrisFrame.java`
- Create: `src/test/java/net/vetcafe/jtetris/ui/GameSessionLifecycleTest.java`
- Modify: `openspec/changes/add-marathon-session-time/tasks.md`

- [ ] **Step 1: Write failing lifecycle tests**

Verify `StageOverlayHost` invokes a visibility callback with `true` immediately
when any overlay begins entering and with `false` when dismissal completes.
Verify a package-visible frame policy:

```java
assertTrue(TetrisFrame.shouldRunSessionTimer(false, false, false));
assertFalse(TetrisFrame.shouldRunSessionTimer(true, false, false));
assertFalse(TetrisFrame.shouldRunSessionTimer(false, true, false));
assertFalse(TetrisFrame.shouldRunSessionTimer(false, false, true));
```

- [ ] **Step 2: Verify RED**

Run:

```bash
./mvnw -Djava.awt.headless=true \
  -Dtest=StageOverlayHostLayoutTest,GameSessionLifecycleTest test
```

Expected: compilation failures for the callback and lifecycle policy.

- [ ] **Step 3: Implement lifecycle synchronization**

Add `setBlockingVisibilityListener(Consumer<Boolean>)` to `StageOverlayHost`;
notify `true` from `showOverlay()` and `false` only after state reaches
`HIDDEN`.

In `TetrisFrame`:

```java
private final GameSessionTimer sessionTimer = new GameSessionTimer();
private final SidePanel sidePanel = new SidePanel(board, sessionTimer::elapsedMillis);

static boolean shouldRunSessionTimer(boolean paused, boolean overlayVisible, boolean gameOver) {
    return !paused && !overlayVisible && !gameOver;
}

private void syncSessionTimer() {
    sessionTimer.syncRunning(shouldRunSessionTimer(
            paused, overlayHost.isOverlayVisible(), board.isGameOver()));
}
```

Register the overlay listener in the constructor. Call synchronization after
pause/resume, overlay visibility changes, gravity ticks, hold/hard drop, and
game-over detection. The gravity timer uses the same pause/overlay/game-over
policy so pieces do not fall behind a blocking overlay. Restart uses
`resetAndStart()` after `board.reset()`. Remove Help's private pause-state
mutation because every overlay now blocks time and gameplay through the common
overlay state; retain manual pause state.

- [ ] **Step 4: Verify GREEN**

Run focused lifecycle tests, then:

```bash
./mvnw -Djava.awt.headless=true clean test
```

- [ ] **Step 5: Commit**

```bash
git add src/main/java/net/vetcafe/jtetris/ui/StageOverlayHost.java \
  src/main/java/net/vetcafe/jtetris/ui/TetrisFrame.java \
  src/test/java/net/vetcafe/jtetris/ui/StageOverlayHostLayoutTest.java \
  src/test/java/net/vetcafe/jtetris/ui/GameSessionLifecycleTest.java \
  openspec/changes/add-marathon-session-time/tasks.md
git commit -m "feat: integrate marathon timer lifecycle"
```

### Task 4: Player And Developer Documentation

**Files:**
- Modify: `src/main/java/net/vetcafe/jtetris/ui/HelpContent.java`
- Modify: `src/test/java/net/vetcafe/jtetris/ui/HelpContentTest.java`
- Modify: `README.md`
- Modify: `doc/overview.md`
- Modify: `openspec/project.md`
- Modify: `openspec/changes/add-marathon-session-time/tasks.md`

- [ ] **Step 1: Write a failing Help content test**

Assert Help contains `Endless Marathon`, `top-out`, and an explanation that
Time excludes pauses and blocking prompts.

- [ ] **Step 2: Verify RED**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=HelpContentTest test
```

Expected: assertion failure because the new player explanation is absent.

- [ ] **Step 3: Update documentation**

Add a concise `Endless Marathon` Help section and README feature text. Update
the overview's side-panel and lifecycle descriptions. Record Sprint / 40 Lines,
Ultra / Time Trial, and Score Attack / Versus as future directions in
`openspec/project.md`, explicitly outside the current implementation.

- [ ] **Step 4: Verify GREEN**

Run Help tests and `git diff --check`.

- [ ] **Step 5: Commit**

```bash
git add src/main/java/net/vetcafe/jtetris/ui/HelpContent.java \
  src/test/java/net/vetcafe/jtetris/ui/HelpContentTest.java \
  README.md doc/overview.md openspec/project.md \
  openspec/changes/add-marathon-session-time/tasks.md
git commit -m "docs: define endless marathon mode"
```

### Task 5: Canonical Spec, Verification, And Archive

**Files:**
- Create: `openspec/specs/game-session/spec.md`
- Modify: `openspec/specs/ui-theme/spec.md`
- Modify: `openspec/changes/add-marathon-session-time/tasks.md`
- Move: `openspec/changes/add-marathon-session-time/` to `openspec/changes/archive/add-marathon-session-time/`

- [ ] **Step 1: Run final verification**

```bash
./mvnw -Djava.awt.headless=true clean test
git diff --check
git status --short
```

Expected: all tests pass and only intended files are modified.

- [ ] **Step 2: Review the complete diff**

Confirm timer state is UI-only, overlays all pause time through one callback,
manual pause is preserved, preferred panel size remains fixed, and no score or
replay format changed.

- [ ] **Step 3: Update canonical specs and archive**

Promote game-session requirements to `openspec/specs/game-session/spec.md`.
Extend the UI theme spec with Layout A. Mark all tasks complete, record test
counts and manual screenshot status, then archive the change.

- [ ] **Step 4: Commit**

```bash
git add openspec/specs openspec/changes
git commit -m "docs: archive marathon session time spec"
```
