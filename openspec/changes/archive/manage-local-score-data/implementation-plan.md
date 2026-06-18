# Local Score Data Management Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Store local scores in platform-standard application data directories, safely migrate the legacy home-directory file, and let users delete individual leaderboard records after confirmation.

**Architecture:** Add a pure `ScoreDataPaths` resolver for platform paths and make `ScoreManager` accept explicit target and legacy paths through a package-private test seam. Keep Java properties as the storage format. Extract leaderboard content construction into a focused Swing component so selection and delete-button behavior can be tested headlessly, while `TetrisFrame` coordinates the existing stage-overlay transitions.

**Tech Stack:** Java 17, `java.nio.file`, Swing, JUnit 5, Maven, OpenSpec.

---

## File Structure

- Create `src/main/java/net/vetcafe/jtetris/score/ScoreDataPaths.java`: resolve the current and legacy score paths without reading or writing score data.
- Modify `src/main/java/net/vetcafe/jtetris/score/ScoreManager.java`: use `Path`, migrate legacy data, create parent directories, and expose durable player deletion.
- Create `src/main/java/net/vetcafe/jtetris/ui/LeaderboardContent.java`: own leaderboard table, empty state, selection, and action buttons.
- Modify `src/main/java/net/vetcafe/jtetris/ui/TetrisFrame.java`: coordinate leaderboard, deletion confirmation, failure feedback, and focus restoration.
- Create `src/test/java/net/vetcafe/jtetris/score/ScoreDataPathsTest.java`: cover platform path rules.
- Create `src/test/java/net/vetcafe/jtetris/score/ScoreManagerTest.java`: cover migration and deletion persistence.
- Create `src/test/java/net/vetcafe/jtetris/ui/LeaderboardContentTest.java`: cover single selection and delete action state.
- Modify `doc/overview.md`, `doc/algorithms.md`, and `openspec/project.md`: document public storage and deletion behavior.
- Modify `openspec/changes/manage-local-score-data/tasks.md`: record progress and verification.

### Task 1: Platform Score Paths

**Files:**
- Create: `src/test/java/net/vetcafe/jtetris/score/ScoreDataPathsTest.java`
- Create: `src/main/java/net/vetcafe/jtetris/score/ScoreDataPaths.java`
- Modify: `openspec/changes/manage-local-score-data/tasks.md`

- [ ] **Step 1: Write failing path-resolution tests**

Create tests that call a package-private pure resolver:

```java
assertEquals(
        home.resolve("Library/Application Support/net.vetcafe.jtetris/scores.properties"),
        ScoreDataPaths.resolve("Mac OS X", home, null, null)
);
assertEquals(
        Path.of("/data").resolve("net.vetcafe.jtetris/scores.properties"),
        ScoreDataPaths.resolve("Linux", home, "/data", null)
);
assertEquals(
        home.resolve(".local/share/net.vetcafe.jtetris/scores.properties"),
        ScoreDataPaths.resolve("Linux", home, "relative/data", null)
);
assertEquals(
        Path.of("C:/Users/test/AppData/Local").resolve("net.vetcafe.jtetris/scores.properties"),
        ScoreDataPaths.resolve("Windows 11", home, null, "C:/Users/test/AppData/Local")
);
assertEquals(
        home.resolve("AppData/Local/net.vetcafe.jtetris/scores.properties"),
        ScoreDataPaths.resolve("Windows 11", home, null, null)
);
```

Also assert that an unknown OS uses the XDG-style `~/.local/share` fallback and that `legacy(home)` resolves `~/.tetris_scores.properties`.

- [ ] **Step 2: Run the focused tests and verify RED**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=ScoreDataPathsTest test
```

Expected: compilation fails because `ScoreDataPaths` does not exist.

- [ ] **Step 3: Implement the minimal pure resolver**

Implement:

```java
final class ScoreDataPaths {
    static final String APP_DIRECTORY = "net.vetcafe.jtetris";
    static final String SCORE_FILE = "scores.properties";

    static Path current() {
        return resolve(
                System.getProperty("os.name", ""),
                Path.of(System.getProperty("user.home", ".")),
                System.getenv("XDG_DATA_HOME"),
                System.getenv("LOCALAPPDATA")
        );
    }

    static Path legacy(Path home) {
        return home.resolve(".tetris_scores.properties");
    }

    static Path resolve(String osName, Path home, String xdgDataHome, String localAppData) {
        String normalizedOs = osName == null ? "" : osName.toLowerCase(Locale.ROOT);
        Path dataRoot;
        if (normalizedOs.contains("mac")) {
            dataRoot = home.resolve("Library").resolve("Application Support");
        } else if (normalizedOs.contains("win")) {
            dataRoot = nonBlankPath(localAppData).orElse(home.resolve("AppData").resolve("Local"));
        } else {
            dataRoot = absolutePath(xdgDataHome).orElse(home.resolve(".local").resolve("share"));
        }
        return dataRoot.resolve(APP_DIRECTORY).resolve(SCORE_FILE);
    }
}
```

Keep blank and relative environment handling in private helpers.

- [ ] **Step 4: Run the focused tests and verify GREEN**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=ScoreDataPathsTest test
```

Expected: all `ScoreDataPathsTest` tests pass.

- [ ] **Step 5: Update the task checklist and commit**

Mark the first two tasks complete, then:

```bash
git add src/main/java/net/vetcafe/jtetris/score/ScoreDataPaths.java \
  src/test/java/net/vetcafe/jtetris/score/ScoreDataPathsTest.java \
  openspec/changes/manage-local-score-data/tasks.md
git commit -m "feat: resolve platform score data paths"
```

### Task 2: Migration and Durable Deletion

**Files:**
- Create: `src/test/java/net/vetcafe/jtetris/score/ScoreManagerTest.java`
- Modify: `src/main/java/net/vetcafe/jtetris/score/ScoreManager.java`
- Modify: `openspec/changes/manage-local-score-data/tasks.md`

- [ ] **Step 1: Write failing migration tests**

Use `@TempDir Path tempDir` and an explicit constructor:

```java
Path store = tempDir.resolve("data/net.vetcafe.jtetris/scores.properties");
Path legacy = tempDir.resolve(".tetris_scores.properties");
Files.writeString(legacy, "alice=1200\n");

ScoreManager manager = new ScoreManager(store, legacy);

assertEquals(1200, manager.getBest("Alice"));
assertTrue(Files.exists(store));
assertFalse(Files.exists(legacy));
```

Add tests proving:

- a pre-existing new store takes precedence and leaves the legacy file untouched;
- parent directories are created;
- a migration write failure leaves the legacy file in place while loaded scores remain readable.

For deterministic write failures, add a package-private persistence seam:

```java
interface Persistence {
    boolean exists(Path path);
    Properties load(Path path);
    boolean save(Path path, Properties properties);
    boolean delete(Path path);
}
```

- [ ] **Step 2: Run migration tests and verify RED**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=ScoreManagerTest test
```

Expected: compilation fails because the explicit constructor and persistence seam do not exist.

- [ ] **Step 3: Implement migration with the new path**

Change the default constructor to:

```java
public ScoreManager() {
    this(
            ScoreDataPaths.current(),
            ScoreDataPaths.legacy(Path.of(System.getProperty("user.home", ".")))
    );
}
```

The package-private constructor loads the new store when present. Otherwise it loads the legacy store, attempts to save to the new path, and deletes the legacy file only after save success. Saving creates the parent directory with `Files.createDirectories(...)`.

- [ ] **Step 4: Run migration tests and verify GREEN**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=ScoreManagerTest test
```

Expected: all migration tests pass.

- [ ] **Step 5: Write failing deletion tests**

Add:

```java
manager.updateIfHigher("Alice", 1200);
assertTrue(manager.deleteUser("aLiCe"));
assertEquals(0, manager.getBest("Alice"));
assertTrue(manager.getUsers().isEmpty());

ScoreManager reloaded = new ScoreManager(store, legacy);
assertTrue(reloaded.getLeaderboard().isEmpty());
```

Also test:

- deleting a missing user returns `false` without changing data;
- simulated save failure returns `false` and restores the property and display name in memory.

- [ ] **Step 6: Run deletion tests and verify RED**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=ScoreManagerTest test
```

Expected: compilation fails because `deleteUser(String)` does not exist.

- [ ] **Step 7: Implement deletion with rollback**

Implement:

```java
public synchronized boolean deleteUser(String user) {
    String normalized = key(user);
    if (!props.containsKey(normalized)) {
        return false;
    }
    String previousScore = props.getProperty(normalized);
    String previousName = userNames.get(normalized);
    props.remove(normalized);
    userNames.remove(normalized);
    if (save()) {
        return true;
    }
    props.setProperty(normalized, previousScore);
    if (previousName != null) {
        userNames.put(normalized, previousName);
    }
    return false;
}
```

Make `save()` return persistence success. Preserve the public behavior of `updateIfHigher`.

- [ ] **Step 8: Run score tests and full regression tests**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=ScoreManagerTest test
./mvnw -Djava.awt.headless=true clean test
```

Expected: focused tests and the complete suite pass.

- [ ] **Step 9: Update the task checklist and commit**

Mark migration and deletion model tasks complete, then:

```bash
git add src/main/java/net/vetcafe/jtetris/score/ScoreManager.java \
  src/test/java/net/vetcafe/jtetris/score/ScoreManagerTest.java \
  openspec/changes/manage-local-score-data/tasks.md
git commit -m "feat: migrate and manage local scores"
```

### Task 3: Leaderboard Selection and Delete Flow

**Files:**
- Create: `src/test/java/net/vetcafe/jtetris/ui/LeaderboardContentTest.java`
- Create: `src/main/java/net/vetcafe/jtetris/ui/LeaderboardContent.java`
- Modify: `src/main/java/net/vetcafe/jtetris/ui/TetrisFrame.java`
- Modify: `openspec/changes/manage-local-score-data/tasks.md`

- [ ] **Step 1: Write failing leaderboard-content tests**

Construct the content with score entries and callbacks:

```java
AtomicReference<String> requestedDelete = new AtomicReference<>();
LeaderboardContent content = new LeaderboardContent(
        List.of(new ScoreManager.ScoreEntry("Alice", 1200)),
        requestedDelete::set,
        () -> { }
);

assertEquals(ListSelectionModel.SINGLE_SELECTION, content.table().getSelectionModel().getSelectionMode());
assertFalse(content.deleteButton().isEnabled());
content.table().setRowSelectionInterval(0, 0);
assertTrue(content.deleteButton().isEnabled());
content.deleteButton().doClick();
assertEquals("Alice", requestedDelete.get());
```

Add an empty-state test asserting `No scores yet` and a disabled delete button.

- [ ] **Step 2: Run focused UI tests and verify RED**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=LeaderboardContentTest test
```

Expected: compilation fails because `LeaderboardContent` does not exist.

- [ ] **Step 3: Implement the focused Swing content component**

`LeaderboardContent` extends `JPanel`, receives entries plus delete/close callbacks, and:

- applies the existing leaderboard theme and font styling;
- uses `ListSelectionModel.SINGLE_SELECTION`;
- updates `Delete` enabled state from the selection model;
- passes the selected model-row username to the delete callback;
- exposes package-private `table()` and `deleteButton()` accessors for UI tests;
- renders the existing empty-state label when there are no entries.

- [ ] **Step 4: Run focused UI tests and verify GREEN**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=LeaderboardContentTest test
```

Expected: all `LeaderboardContentTest` tests pass.

- [ ] **Step 5: Integrate the delete confirmation overlay**

Replace the inline table construction in `showLeaderboard()` with:

```java
LeaderboardContent content = new LeaderboardContent(
        scoreManager.getLeaderboard(),
        this::requestLeaderboardDelete,
        this::dismissOverlayIfVisible
);
```

Add a small leaderboard flow state in `TetrisFrame`:

```java
private String pendingLeaderboardDeleteUser;
private LeaderboardReturn leaderboardReturn = LeaderboardReturn.NONE;
```

`requestLeaderboardDelete(user)` records the name, sets the next state to confirmation, and dismisses the leaderboard. The leaderboard lifecycle opens a `Delete score` confirmation overlay instead of restoring focus when deletion was requested.

The confirmation message is:

```text
Delete all score data for "Alice"?
```

Buttons are `Delete` and `Cancel`. Confirm calls `scoreManager.deleteUser(...)`; cancel does not modify storage. Both return to a freshly constructed leaderboard. A failed delete first shows an in-window `Score data could not be deleted.` feedback overlay whose close action returns to the unchanged leaderboard.

Extend `confirmOverlayIfVisible()` so Enter/Space confirms `score-delete-confirm`, and preserve Esc cancellation.

- [ ] **Step 6: Run focused and complete UI regression tests**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=LeaderboardContentTest,StageOverlayHostLayoutTest test
./mvnw -Djava.awt.headless=true clean test
```

Expected: focused UI tests and the complete suite pass with no failures.

- [ ] **Step 7: Update the task checklist and commit**

Mark leaderboard implementation tasks complete, then:

```bash
git add src/main/java/net/vetcafe/jtetris/ui/LeaderboardContent.java \
  src/main/java/net/vetcafe/jtetris/ui/TetrisFrame.java \
  src/test/java/net/vetcafe/jtetris/ui/LeaderboardContentTest.java \
  openspec/changes/manage-local-score-data/tasks.md
git commit -m "feat: delete leaderboard players"
```

### Task 4: Documentation, Review, and Archive

**Files:**
- Modify: `doc/overview.md`
- Modify: `doc/algorithms.md`
- Modify: `openspec/project.md`
- Create or modify: `openspec/specs/score-storage/spec.md`
- Create or modify: `openspec/specs/leaderboard-management/spec.md`
- Modify: `openspec/changes/manage-local-score-data/tasks.md`
- Move: `openspec/changes/manage-local-score-data` to `openspec/changes/archive/manage-local-score-data`

- [ ] **Step 1: Update public documentation**

Document:

- the exact macOS, Linux/XDG, and Windows paths;
- successful one-time migration and deletion of `~/.tetris_scores.properties`;
- the new leaderboard delete and confirmation interaction;
- unchanged properties format and best-score semantics.

- [ ] **Step 2: Run documentation and full verification checks**

Run:

```bash
git diff --check
./mvnw -Djava.awt.headless=true clean test
```

Expected: no whitespace errors and the complete test suite passes.

- [ ] **Step 3: Review the complete diff against the design**

Check:

- no `pom.xml` or dependency changes;
- no score calculation or replay changes;
- no merge when both stores exist;
- legacy deletion occurs only after successful migration save;
- deletion failure rolls back in-memory data;
- leaderboard selection is single-row and requires confirmation;
- overlay focus and gameplay-input blocking remain intact.

- [ ] **Step 4: Record verification and update canonical specs**

Copy the completed requirements into:

```text
openspec/specs/score-storage/spec.md
openspec/specs/leaderboard-management/spec.md
```

Mark all tasks complete and record command output counts in `tasks.md`.

- [ ] **Step 5: Archive and commit**

Move the active change to:

```text
openspec/changes/archive/manage-local-score-data
```

Then:

```bash
git add doc/overview.md doc/algorithms.md openspec/project.md \
  openspec/specs/score-storage/spec.md \
  openspec/specs/leaderboard-management/spec.md \
  openspec/changes/archive/manage-local-score-data
git commit -m "docs: archive local score data spec"
```

