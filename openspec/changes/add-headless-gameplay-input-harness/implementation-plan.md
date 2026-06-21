# Headless Gameplay Input Harness Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make production gameplay input deterministic and directly executable in headless integration tests.

**Architecture:** Add a package-private controller that owns repeaters, reads an injected monotonic clock, and applies operations to a real `Board`. Keep Swing lifecycle policy and repaint scheduling in `TetrisFrame`, which delegates only eligible gameplay operations.

**Tech Stack:** Java 17, Swing, JUnit 5, Maven

---

### Task 1: Timed movement controller

**Files:**
- Create: `src/main/java/net/vetcafe/jtetris/ui/GameplayInputController.java`
- Create: `src/test/java/net/vetcafe/jtetris/ui/GameplayInputControllerTest.java`
- Modify: `openspec/changes/add-headless-gameplay-input-harness/tasks.md`

- [ ] **Step 1: Write failing horizontal and soft-drop integration tests**

Create a fake millisecond clock and tests that construct:

```java
Board board = new Board(42L);
FakeClock clock = new FakeClock();
GameplayInputController controller =
        new GameplayInputController(board, 120, 40, 40, clock::nowMs);
```

Cover:

```java
assertTrue(controller.pressLeft());
assertTrue(controller.releaseLeft() == false);
assertEquals(startX - 1, board.getCurrent().getX());

clock.advance(119);
assertFalse(controller.poll());
clock.advance(1);
assertTrue(controller.poll());

assertTrue(controller.pressSoftDrop());
clock.advance(120);
assertTrue(controller.poll());
assertEquals(startY + 2, board.getCurrent().getY());
```

Also assert delayed polling moves at most one column/row, latest direction wins, release fallback moves immediately, and reset stops repeats.

- [ ] **Step 2: Run the controller test and verify RED**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=GameplayInputControllerTest test
```

Expected: test compilation fails because `GameplayInputController` does not exist.

- [ ] **Step 3: Implement the minimal timed controller**

Implement constructor validation, press/release methods, `poll()`, and `reset()`.
Use one clock sample in `poll()` and return true when either horizontal or
soft-drop movement succeeds.

- [ ] **Step 4: Run controller and repeater tests and verify GREEN**

Run:

```bash
./mvnw -Djava.awt.headless=true \
  -Dtest=GameplayInputControllerTest,InputRepeaterTest,SoftDropRepeaterTest test
```

Expected: all timed input tests pass.

- [ ] **Step 5: Update tasks and commit**

```bash
git add src/main/java/net/vetcafe/jtetris/ui/GameplayInputController.java \
  src/test/java/net/vetcafe/jtetris/ui/GameplayInputControllerTest.java \
  openspec/changes/add-headless-gameplay-input-harness
git commit -m "test: add headless timed input controller"
```

### Task 2: Discrete and mixed operations

**Files:**
- Modify: `src/test/java/net/vetcafe/jtetris/ui/GameplayInputControllerTest.java`
- Modify: `src/main/java/net/vetcafe/jtetris/ui/GameplayInputController.java`
- Modify: `openspec/changes/add-headless-gameplay-input-harness/tasks.md`

- [ ] **Step 1: Add failing operation scenarios**

Add deterministic tests asserting:

```java
assertTrue(controller.rotateClockwise());
assertEquals((startRotation + 1) % 4, board.getCurrent().getRotation());

TetrominoType nextType = board.getNext().getType();
assertTrue(controller.hardDrop());
assertEquals(nextType, board.getCurrent().getType());

TetrominoType heldType = board.getCurrent().getType();
assertTrue(controller.hold());
assertEquals(heldType, board.getHold().getType());
assertFalse(controller.hold());
```

Add a mixed scenario where horizontal and soft drop are held during one poll,
plus a scenario showing blocked horizontal movement returns false.

- [ ] **Step 2: Run the new tests and verify RED**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=GameplayInputControllerTest test
```

Expected: compilation fails because the discrete operation methods are absent.

- [ ] **Step 3: Add minimal operation delegation**

Implement:

```java
boolean rotateClockwise() { return board.rotateCW(); }
boolean rotateCounterclockwise() { return board.rotateCCW(); }
boolean hardDrop() { return board.hardDrop(); }
boolean hold() { return board.hold(); }
```

- [ ] **Step 4: Run focused tests and verify GREEN**

Run:

```bash
./mvnw -Djava.awt.headless=true \
  -Dtest=GameplayInputControllerTest,BoardRegressionGateTest,HoldPieceTest,SrsRotationTest test
```

Expected: all controller and affected model tests pass.

- [ ] **Step 5: Update tasks and commit**

```bash
git add src/main/java/net/vetcafe/jtetris/ui/GameplayInputController.java \
  src/test/java/net/vetcafe/jtetris/ui/GameplayInputControllerTest.java \
  openspec/changes/add-headless-gameplay-input-harness/tasks.md
git commit -m "test: cover headless gameplay operations"
```

### Task 3: Swing production wiring

**Files:**
- Modify: `src/main/java/net/vetcafe/jtetris/ui/TetrisFrame.java`
- Modify: `src/test/java/net/vetcafe/jtetris/ui/GameplayInputControllerTest.java`
- Modify: `openspec/changes/add-headless-gameplay-input-harness/tasks.md`

- [ ] **Step 1: Add controller eligibility contract coverage**

Keep native-window construction out of tests. Add controller tests proving that
calling `reset()` clears held input without resetting the board, so existing
pause/overlay/focus handlers can safely preserve their current policy.

- [ ] **Step 2: Run the added test and verify RED if behavior is missing**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=GameplayInputControllerTest test
```

Expected: the new reset contract passes only when controller reset affects
repeaters and not board state.

- [ ] **Step 3: Replace frame orchestration with delegation**

Construct one controller from the frame board and existing timing constants.
For each gameplay callback:

```java
if (!isGameplayInputEnabled()) return;
if (inputController.pressLeft()) {
    gamePanel.repaint();
}
```

Delegate release, soft drop, polling, rotation, hard drop, and hold. Preserve
session timer synchronization after hard drop and hold. Replace
`clearHeldInputs()` internals with `inputController.reset()`. Remove duplicate
repeater fields, clock helper, and movement loops from the frame.

- [ ] **Step 4: Run focused tests and compile production wiring**

Run:

```bash
./mvnw -Djava.awt.headless=true \
  -Dtest=GameplayInputControllerTest,InputRepeaterTest,SoftDropRepeaterTest,GameSessionLifecycleTest test
```

Expected: tests pass and `TetrisFrame` compiles.

- [ ] **Step 5: Update tasks and commit**

```bash
git add src/main/java/net/vetcafe/jtetris/ui/TetrisFrame.java \
  src/test/java/net/vetcafe/jtetris/ui/GameplayInputControllerTest.java \
  openspec/changes/add-headless-gameplay-input-harness/tasks.md
git commit -m "refactor: route Swing gameplay through input controller"
```

### Task 4: Documentation and verification

**Files:**
- Modify: `doc/overview.md`
- Modify: `doc/algorithms.md`
- Modify: `doc/quality-gates.md`
- Modify: `openspec/changes/add-headless-gameplay-input-harness/tasks.md`

- [ ] **Step 1: Document the new verification boundary**

Document the shared controller, fake-clock scenario tests, headless verification
command, and the explicit separation between automated state assertions and
player-owned feel testing.

- [ ] **Step 2: Run final verification**

Run:

```bash
git diff --check
./mvnw -Djava.awt.headless=true clean test
```

Expected: no whitespace errors; Maven reports zero failures and zero errors.

- [ ] **Step 3: Record exact evidence**

Mark completed tasks and add exact test counts and commands. Keep subjective
game feel listed as player-owned verification, not as automated evidence.

- [ ] **Step 4: Commit documentation**

```bash
git add doc/overview.md doc/algorithms.md doc/quality-gates.md \
  openspec/changes/add-headless-gameplay-input-harness
git commit -m "docs: define headless gameplay verification workflow"
```
