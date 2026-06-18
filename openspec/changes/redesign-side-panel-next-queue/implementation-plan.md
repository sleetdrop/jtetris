# Side Panel and Next Queue Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the single stored next piece with an immutable three-piece Board queue and render the approved option A side panel without a persistent controls list.

**Architecture:** `Board` owns an `ArrayDeque<TetrominoType>` and centralizes queue promotion/refill in one helper used by normal spawning and empty Hold. `SidePanel` remains a themed Swing component but uses stable painted sections for Hold and three vertically ordered upcoming previews. Help and project documentation become the authoritative controls and queue explanation.

**Tech Stack:** Java 17, Swing, Maven, JUnit 5, OpenSpec.

---

## File Map

- Modify `src/main/java/net/vetcafe/jtetris/model/Board.java`: own and advance the three-piece queue.
- Create `src/test/java/net/vetcafe/jtetris/model/NextQueueTest.java`: specify queue lifecycle and immutable API.
- Modify `src/test/java/net/vetcafe/jtetris/model/HoldPieceTest.java`: verify empty and populated Hold queue behavior.
- Modify `src/test/java/net/vetcafe/jtetris/model/ReplayHooksTest.java`: compare the complete upcoming queue.
- Modify `src/test/java/net/vetcafe/jtetris/model/BoardRegressionGateTest.java`: replace reflection against the removed `next` field.
- Modify `src/main/java/net/vetcafe/jtetris/ui/SidePanel.java`: implement option A and remove controls.
- Create `src/test/java/net/vetcafe/jtetris/ui/SidePanelLayoutTest.java`: verify component structure, preferred size, and preview order.
- Modify `src/main/java/net/vetcafe/jtetris/ui/HelpContent.java`: explain three upcoming pieces.
- Modify `src/test/java/net/vetcafe/jtetris/ui/HelpContentTest.java`: lock player-facing queue wording.
- Modify `doc/overview.md` and `doc/algorithms.md`: document queue ownership and new side panel.
- Modify and archive `openspec/changes/redesign-side-panel-next-queue/**`: track verification and merge spec deltas.
- Modify `openspec/specs/ui-theme/spec.md`: merge the approved side-panel requirements.
- Create `openspec/specs/next-queue/spec.md`: establish the canonical queue capability.

### Task 1: Model-backed Three-piece Queue

**Files:**
- Create: `src/test/java/net/vetcafe/jtetris/model/NextQueueTest.java`
- Modify: `src/test/java/net/vetcafe/jtetris/model/HoldPieceTest.java`
- Modify: `src/test/java/net/vetcafe/jtetris/model/ReplayHooksTest.java`
- Modify: `src/test/java/net/vetcafe/jtetris/model/BoardRegressionGateTest.java`
- Modify: `src/main/java/net/vetcafe/jtetris/model/Board.java`
- Modify: `openspec/changes/redesign-side-panel-next-queue/tasks.md`

- [ ] **Step 1: Write failing construction and immutability tests**

Create `NextQueueTest` with assertions equivalent to:

```java
@Test
void boardStartsWithThreeUpcomingPieces() {
    Board board = new Board(7L);

    assertEquals(3, board.getNextQueue().size());
    assertEquals(board.getNext().getType(), board.getNextQueue().get(0));
}

@Test
void upcomingQueueSnapshotIsImmutable() {
    Board board = new Board(7L);

    assertThrows(UnsupportedOperationException.class,
            () -> board.getNextQueue().add(TetrominoType.I));
}
```

- [ ] **Step 2: Run the new test and verify RED**

Run:

```bash
./mvnw -Dtest=NextQueueTest test
```

Expected: compilation fails because `Board.getNextQueue()` does not exist.

- [ ] **Step 3: Add minimal queue storage and read API**

In `Board`:

```java
private static final int NEXT_QUEUE_SIZE = 3;
private final ArrayDeque<TetrominoType> nextQueue = new ArrayDeque<>(NEXT_QUEUE_SIZE);

public List<TetrominoType> getNextQueue() {
    return List.copyOf(nextQueue);
}

public Tetromino getNext() {
    TetrominoType type = nextQueue.peekFirst();
    if (type == null) {
        throw new IllegalStateException("next queue must not be empty");
    }
    return new Tetromino(type, WIDTH / 2 - 2, 0);
}
```

Add a `fillNextQueue()` helper, clear/fill it in `spawnInitial()`, and remove the old stored `next` field.

- [ ] **Step 4: Run construction tests and verify GREEN**

Run:

```bash
./mvnw -Dtest=NextQueueTest test
```

Expected: both tests pass.

- [ ] **Step 5: Write failing promotion and reset tests**

Add tests that:
- capture the initial three entries;
- lock the current piece;
- assert the former head became current;
- assert prior positions 2 and 3 moved to positions 1 and 2;
- assert queue size remains three;
- reset after gameplay and assert queue size is three and replay actions are empty.

- [ ] **Step 6: Run promotion tests and verify RED**

Run:

```bash
./mvnw -Dtest=NextQueueTest test
```

Expected: promotion assertions fail because `spawnNext()` still uses the removed single-next flow or does not refill the queue.

- [ ] **Step 7: Centralize promotion and refill**

Add:

```java
private TetrominoType takeNextType() {
    TetrominoType type = nextQueue.removeFirst();
    fillNextQueue();
    return type;
}

private void fillNextQueue() {
    while (nextQueue.size() < NEXT_QUEUE_SIZE) {
        nextQueue.addLast(pieceBag.next());
    }
}
```

Use `takeNextType()` from `spawnNext()` and empty-Hold promotion. Clear `nextQueue` before initial refill and reset refill. Do not consume the queue during a populated-Hold swap.

- [ ] **Step 8: Add Hold queue tests**

Extend `HoldPieceTest`:

```java
@Test
void firstHoldAdvancesAndRefillsUpcomingQueue() {
    Board board = new Board(7L);
    List<TetrominoType> before = board.getNextQueue();

    assertTrue(board.hold());

    assertEquals(before.get(0), board.getCurrent().getType());
    assertEquals(before.get(1), board.getNextQueue().get(0));
    assertEquals(before.get(2), board.getNextQueue().get(1));
    assertEquals(3, board.getNextQueue().size());
}
```

After locking once, capture the queue, perform a populated-Hold swap, and assert the queue equals the captured value.

- [ ] **Step 9: Update replay and blocked-spawn regression tests**

Replace the single-next replay assertion with:

```java
assertEquals(source.getNextQueue(), replay.getNextQueue());
```

In `BoardRegressionGateTest`, replace reflection against `next` with a helper that accesses `nextQueue`, clears it, and inserts the desired head plus two valid filler types before invoking `spawnNext()`.

- [ ] **Step 10: Run focused and full model tests**

Run:

```bash
./mvnw -Dtest=NextQueueTest,HoldPieceTest,PieceBagTest,ReplayHooksTest,BoardRegressionGateTest test
./mvnw -Djava.awt.headless=true test
```

Expected: all focused tests and the complete test suite pass.

- [ ] **Step 11: Update task state and commit**

Mark model queue tasks complete and record the focused test command in `tasks.md`.

```bash
git add src/main/java/net/vetcafe/jtetris/model/Board.java \
  src/test/java/net/vetcafe/jtetris/model/NextQueueTest.java \
  src/test/java/net/vetcafe/jtetris/model/HoldPieceTest.java \
  src/test/java/net/vetcafe/jtetris/model/ReplayHooksTest.java \
  src/test/java/net/vetcafe/jtetris/model/BoardRegressionGateTest.java \
  openspec/changes/redesign-side-panel-next-queue/tasks.md
git commit -m "feat: add three-piece next queue"
```

### Task 2: Option A Side Panel

**Files:**
- Create: `src/test/java/net/vetcafe/jtetris/ui/SidePanelLayoutTest.java`
- Modify: `src/main/java/net/vetcafe/jtetris/ui/SidePanel.java`
- Modify: `openspec/changes/redesign-side-panel-next-queue/tasks.md`

- [ ] **Step 1: Write failing structural tests**

Create `SidePanelLayoutTest` that constructs a seeded Board and SidePanel on the EDT, then recursively inspects components:

```java
@Test
void sidePanelOmitsPersistentControlsTextArea() throws Exception {
    SidePanel panel = onEdt(() -> new SidePanel(new Board(7L)));

    assertFalse(descendants(panel).stream().anyMatch(JTextArea.class::isInstance));
}

@Test
void sidePanelKeepsExistingPreferredSize() throws Exception {
    SidePanel panel = onEdt(() -> new SidePanel(new Board(7L)));

    assertEquals(new Dimension(200, 520), panel.getPreferredSize());
}
```

Add a package-private preview-order accessor to the planned preview component API only if a render-order assertion cannot be expressed through existing Board state. Do not add a test-only Board API.

- [ ] **Step 2: Run the layout test and verify RED**

Run:

```bash
./mvnw -Dtest=SidePanelLayoutTest test
```

Expected: the controls-area assertion fails because `SidePanel` contains a `JTextArea`.

- [ ] **Step 3: Remove the controls section**

Remove `controlsArea`, `createControlsPanel()`, the south component, and its theme update path. Preserve the 200 by 520 preferred size.

- [ ] **Step 4: Run structural tests and verify GREEN**

Run:

```bash
./mvnw -Dtest=SidePanelLayoutTest test
```

Expected: structural tests pass.

- [ ] **Step 5: Write failing three-preview rendering test**

Add a package-private method on the custom preview panel:

```java
List<TetrominoType> displayedNextTypes() {
    return board.getNextQueue();
}
```

The test locates the preview panel and asserts that its displayed types equal all three queue entries in order. The method reports production render input and does not mutate state.

- [ ] **Step 6: Run preview test and verify RED**

Run:

```bash
./mvnw -Dtest=SidePanelLayoutTest test
```

Expected: compilation or assertion failure because the existing panel paints only `getNext()`.

- [ ] **Step 7: Implement option A hierarchy**

Refactor `SidePanel` so:
- score uses the strongest label style;
- level and lines remain compact;
- scoring feedback, combo, and B2B retain existing formatter semantics;
- the center preview component paints a performance divider, Hold section, second divider, and three Next pieces;
- first Next cells use 18 px and subsequent cells use 15 px;
- all vertical offsets are constants sized to fit 520 px;
- preview colors continue through `ColorPalette`.

Use `board.getNextQueue()` as the only multi-preview data source.

- [ ] **Step 8: Verify UI tests**

Run:

```bash
./mvnw -Dtest=SidePanelLayoutTest,ThemeVisualsTest,ScoreFeedbackFormatterTest test
./mvnw -Djava.awt.headless=true test
```

Expected: all tests pass without Swing exceptions.

- [ ] **Step 9: Update task state and commit**

```bash
git add src/main/java/net/vetcafe/jtetris/ui/SidePanel.java \
  src/test/java/net/vetcafe/jtetris/ui/SidePanelLayoutTest.java \
  openspec/changes/redesign-side-panel-next-queue/tasks.md
git commit -m "feat: redesign side panel previews"
```

### Task 3: Help and Stable Documentation

**Files:**
- Modify: `src/test/java/net/vetcafe/jtetris/ui/HelpContentTest.java`
- Modify: `src/main/java/net/vetcafe/jtetris/ui/HelpContent.java`
- Modify: `doc/overview.md`
- Modify: `doc/algorithms.md`
- Modify: `openspec/changes/redesign-side-panel-next-queue/tasks.md`

- [ ] **Step 1: Write failing Help wording test**

Add:

```java
assertTrue(html.contains("next three pieces"));
```

- [ ] **Step 2: Run Help test and verify RED**

Run:

```bash
./mvnw -Dtest=HelpContentTest test
```

Expected: assertion fails because Help describes only one upcoming piece.

- [ ] **Step 3: Update Help wording**

Change the Playfield paragraph to state that Next shows the next three pieces in order. Keep the controls table and all existing scoring explanations.

- [ ] **Step 4: Run Help test and verify GREEN**

Run:

```bash
./mvnw -Dtest=HelpContentTest test
```

Expected: test passes.

- [ ] **Step 5: Update stable documentation**

In `doc/overview.md`:
- describe `Board` as owning Hold and a three-piece upcoming queue;
- update the class diagram from `Tetromino next` to `Deque<TetrominoType> nextQueue`;
- remove the obsolete `setControls` representation;
- describe the option A side-panel groups.

In `doc/algorithms.md`:
- document queue promotion/refill and Hold interaction;
- remove multi-piece queue from extension ideas;
- remove the persistent controls-list description.

- [ ] **Step 6: Run documentation-adjacent tests**

Run:

```bash
./mvnw -Dtest=HelpContentTest,SidePanelLayoutTest,ReplayHooksTest test
```

Expected: all tests pass.

- [ ] **Step 7: Update task state and commit**

```bash
git add src/main/java/net/vetcafe/jtetris/ui/HelpContent.java \
  src/test/java/net/vetcafe/jtetris/ui/HelpContentTest.java \
  doc/overview.md doc/algorithms.md \
  openspec/changes/redesign-side-panel-next-queue/tasks.md
git commit -m "docs: explain next queue interface"
```

### Task 4: Verification and OpenSpec Archive

**Files:**
- Modify: `openspec/changes/redesign-side-panel-next-queue/tasks.md`
- Modify: `openspec/specs/ui-theme/spec.md`
- Create: `openspec/specs/next-queue/spec.md`
- Move: `openspec/changes/redesign-side-panel-next-queue/` to `openspec/changes/archive/redesign-side-panel-next-queue/`

- [ ] **Step 1: Run final automated verification**

Run:

```bash
./mvnw clean test
git diff --check
```

Expected: all tests pass and `git diff --check` prints no errors.

- [ ] **Step 2: Build the macOS package for manual inspection**

Run:

```bash
./mvnw -Pmac -Djava.awt.headless=true clean package
```

Expected: build succeeds and produces `target/dist/JTetris.app`.

- [ ] **Step 3: Request user visual verification**

Ask the user to run the rebuilt app and provide light/dark screenshots confirming:
- no clipping or overlap;
- Hold unavailable state remains understandable;
- three Next pieces appear in correct order;
- option A hierarchy matches the approved mockup.

- [ ] **Step 4: Record evidence**

Add exact automated command results and the user's visual result to `tasks.md`. Mark all implementation and verification tasks complete.

- [ ] **Step 5: Merge canonical specs**

Create `openspec/specs/next-queue/spec.md` from the approved delta. Update the side-panel requirement in `openspec/specs/ui-theme/spec.md` so it no longer requires controls and now requires Hold plus three Next previews.

- [ ] **Step 6: Archive the change**

Move the complete change directory:

```bash
mkdir -p openspec/changes/archive
mv openspec/changes/redesign-side-panel-next-queue \
  openspec/changes/archive/redesign-side-panel-next-queue
```

- [ ] **Step 7: Commit the archive**

```bash
git add openspec/specs/ui-theme/spec.md \
  openspec/specs/next-queue/spec.md \
  openspec/changes/archive/redesign-side-panel-next-queue
git add -u openspec/changes/redesign-side-panel-next-queue
git commit -m "docs: archive side panel next queue spec"
```
