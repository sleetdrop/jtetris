# Distinct Ghost Piece Rendering Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the translucent filled ghost piece with a neutral, unfilled double outline that remains distinguishable from real blocks in light and dark themes.

**Architecture:** Keep projection state and gameplay behavior unchanged in `Board`. Add small package-private rendering helpers to `GamePanel` for theme-aware ghost colors, outline geometry, and active-cell overlap detection; exercise those helpers with pixel-level headless tests before integrating them into the existing stage paint order.

**Tech Stack:** Java 17, Swing/AWT `Graphics2D`, `BufferedImage`, JUnit 5, Maven, OpenSpec.

---

## File Structure

- Modify `src/main/java/net/vetcafe/jtetris/ui/GamePanel.java`
  - Own neutral ghost color tokens, double-outline geometry, and active-cell overlap filtering.
- Create `src/test/java/net/vetcafe/jtetris/ui/GamePanelGhostRenderingTest.java`
  - Verify transparent interiors, two visible outline bands, theme contrast, small-cell fallback, and overlap detection.
- Modify `openspec/changes/refine-ghost-piece-visibility/tasks.md`
  - Track red/green verification and manual screenshot status.
- Modify `openspec/specs/ui-theme/spec.md`
  - Merge the approved ghost rendering requirement after implementation passes.
- Move `openspec/changes/refine-ghost-piece-visibility/` to
  `openspec/changes/archive/refine-ghost-piece-visibility/`
  - Preserve the completed proposal, design, plan, spec delta, and verification notes.

### Task 1: Lock Ghost Rendering Behavior With Focused Tests

**Files:**
- Create: `src/test/java/net/vetcafe/jtetris/ui/GamePanelGhostRenderingTest.java`

- [x] **Step 1: Write tests for double-outline pixels and transparent interior**

Create the test file with:

```java
package net.vetcafe.jtetris.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import net.vetcafe.jtetris.model.Tetromino;
import net.vetcafe.jtetris.model.TetrominoType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class GamePanelGhostRenderingTest {
    private final UiTheme.Mode originalMode = UiTheme.activeMode();

    @AfterEach
    void restoreThemeMode() {
        UiTheme.setActiveMode(originalMode);
    }

    @Test
    void normalGhostCellUsesTwoOutlinesAndLeavesCenterTransparent() {
        for (UiTheme.Mode mode : new UiTheme.Mode[] {UiTheme.Mode.LIGHT, UiTheme.Mode.DARK}) {
            UiTheme.setActiveMode(mode);
            BufferedImage image = renderGhostCell(24);
            Color board = UiTheme.active().boardBackground();

            assertEquals(board.getRGB(), image.getRGB(12, 12));
            assertNotEquals(board.getRGB(), image.getRGB(1, 1));
            assertNotEquals(board.getRGB(), image.getRGB(3, 3));
            assertNotEquals(image.getRGB(1, 1), image.getRGB(3, 3));
        }
    }

    @Test
    void ghostOutlinesStayBelowSolidPieceContrast() {
        for (UiTheme.Mode mode : new UiTheme.Mode[] {UiTheme.Mode.LIGHT, UiTheme.Mode.DARK}) {
            UiTheme.setActiveMode(mode);
            Color board = UiTheme.active().boardBackground();
            Color outer = GamePanel.ghostOuterColor();
            Color inner = GamePanel.ghostInnerColor();
            double minimumPieceDistance = Double.MAX_VALUE;
            for (TetrominoType type : TetrominoType.values()) {
                minimumPieceDistance = Math.min(
                        minimumPieceDistance,
                        colorDistance(ColorPalette.colorFor(type), board)
                );
            }

            assertTrue(colorDistance(outer, board) < minimumPieceDistance);
            assertTrue(colorDistance(inner, board) < colorDistance(outer, board));
        }
    }

    @Test
    void smallGhostCellFallsBackToOneValidOutline() {
        BufferedImage image = renderGhostCell(6);
        Color board = UiTheme.active().boardBackground();

        assertNotEquals(board.getRGB(), image.getRGB(1, 1));
        assertEquals(board.getRGB(), image.getRGB(3, 3));
    }

    @Test
    void overlappingCurrentCellIsDetectedBeforeGhostDrawing() {
        Tetromino current = new Tetromino(TetrominoType.O, 4, 18);

        assertTrue(GamePanel.overlapsCurrentCell(current, 5, 19));
        assertFalse(GamePanel.overlapsCurrentCell(current, 3, 19));
    }

    private static BufferedImage renderGhostCell(int cellSize) {
        BufferedImage image = new BufferedImage(cellSize, cellSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(UiTheme.active().boardBackground());
        graphics.fillRect(0, 0, cellSize, cellSize);
        GamePanel.drawGhostCell(graphics, 0, 0, cellSize);
        graphics.dispose();
        return image;
    }

    private static double colorDistance(Color first, Color second) {
        int red = first.getRed() - second.getRed();
        int green = first.getGreen() - second.getGreen();
        int blue = first.getBlue() - second.getBlue();
        return Math.sqrt((red * red) + (green * green) + (blue * blue));
    }
}
```

- [x] **Step 2: Run the focused test and verify the RED state**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=GamePanelGhostRenderingTest test
```

Expected: compilation fails because `GamePanel.ghostOuterColor()`,
`ghostInnerColor()`, `drawGhostCell(Graphics2D, int, int, int)`, and
`overlapsCurrentCell(...)` do not exist.

- [x] **Step 3: Record the RED result**

Append the exact failure summary and date to
`openspec/changes/refine-ghost-piece-visibility/tasks.md` under
`Verification Notes`.

### Task 2: Implement Double-Outline Ghost Rendering

**Files:**
- Modify: `src/main/java/net/vetcafe/jtetris/ui/GamePanel.java:145-178`
- Test: `src/test/java/net/vetcafe/jtetris/ui/GamePanelGhostRenderingTest.java`
- Modify: `openspec/changes/refine-ghost-piece-visibility/tasks.md`

- [x] **Step 1: Replace translucent fill setup with overlap-aware outline drawing**

Replace `drawGhostPiece(...)` with:

```java
private void drawGhostPiece(Graphics2D g2d) {
    Tetromino ghost = board.getGhost();
    if (ghost == null) return;

    Tetromino current = board.getCurrent();
    for (var cell : ghost.getCells()) {
        int gx = ghost.getX() + cell.x;
        int modelY = ghost.getY() + cell.y;
        int gy = modelY - 2;
        if (gy < 0 || overlapsCurrentCell(current, gx, modelY)) continue;
        drawGhostCell(g2d, gx, gy, cellSize);
    }
}
```

- [x] **Step 2: Add package-private theme color helpers**

Add these methods near `drawGhostPiece(...)`:

```java
static Color ghostOuterColor() {
    return UiTheme.active().isDark()
            ? new Color(118, 128, 150)
            : new Color(155, 164, 178);
}

static Color ghostInnerColor() {
    return UiTheme.active().isDark()
            ? new Color(68, 78, 99)
            : new Color(200, 205, 214);
}
```

These values are intentionally neutral and opaque. Their hierarchy comes from
color distance to the board rather than alpha blending, keeping rendered
pixels deterministic for tests and screenshots.

- [x] **Step 3: Add active-cell overlap detection**

Add:

```java
static boolean overlapsCurrentCell(Tetromino current, int gridX, int modelY) {
    if (current == null) return false;
    for (var cell : current.getCells()) {
        if (current.getX() + cell.x == gridX && current.getY() + cell.y == modelY) {
            return true;
        }
    }
    return false;
}
```

- [x] **Step 4: Replace filled ghost cells with double-outline geometry**

Replace the existing instance `drawGhostCell(...)` with:

```java
static void drawGhostCell(Graphics2D g2d, int gridX, int gridY, int cellSize) {
    int x = gridX * cellSize;
    int y = gridY * cellSize;
    int outerInset = 1;
    int outerSize = cellSize - (outerInset * 2);
    if (outerSize <= 1) return;

    g2d.setColor(ghostOuterColor());
    g2d.drawRect(x + outerInset, y + outerInset, outerSize - 1, outerSize - 1);

    int innerInset = cellSize >= 10 ? 3 : -1;
    int innerSize = cellSize - (innerInset * 2);
    if (innerInset > outerInset && innerSize > 1) {
        g2d.setColor(ghostInnerColor());
        g2d.drawRect(x + innerInset, y + innerInset, innerSize - 1, innerSize - 1);
    }
}
```

- [x] **Step 5: Run the focused test and verify the GREEN state**

Run:

```bash
./mvnw -Djava.awt.headless=true -Dtest=GamePanelGhostRenderingTest test
```

Expected: 4 tests pass with 0 failures and 0 errors.

- [x] **Step 6: Run existing theme and ghost model regression tests**

Run:

```bash
./mvnw -Djava.awt.headless=true \
  -Dtest=GamePanelGhostRenderingTest,ThemeVisualsTest,GhostPieceTest test
```

Expected: all selected tests pass; model landing projection behavior remains
unchanged.

- [x] **Step 7: Update task evidence and checklist**

In `openspec/changes/refine-ghost-piece-visibility/tasks.md`:

- mark focused rendering tests and implementation complete;
- record the focused and regression command results;
- leave manual screenshot review and archive unchecked.

- [x] **Step 8: Commit the implementation**

```bash
git add src/main/java/net/vetcafe/jtetris/ui/GamePanel.java \
  src/test/java/net/vetcafe/jtetris/ui/GamePanelGhostRenderingTest.java \
  openspec/changes/refine-ghost-piece-visibility/tasks.md
git commit -m "fix: distinguish ghost piece from solid blocks"
```

### Task 3: Verify, Review, and Archive

**Files:**
- Modify: `openspec/changes/refine-ghost-piece-visibility/tasks.md`
- Modify: `openspec/specs/ui-theme/spec.md`
- Move: `openspec/changes/refine-ghost-piece-visibility/` to
  `openspec/changes/archive/refine-ghost-piece-visibility/`

- [x] **Step 1: Run the full automated quality gate**

Run:

```bash
./mvnw -Djava.awt.headless=true clean test
```

Expected: all project tests pass with 0 failures and 0 errors.

- [x] **Step 2: Check patch hygiene**

Run:

```bash
git diff --check
git status --short
```

Expected: `git diff --check` produces no output; status contains only files
belonging to this OpenSpec change and its implementation.

- [ ] **Step 3: Request user screenshots**

Build the runnable artifact:

```bash
./mvnw -Djava.awt.headless=true clean package
```

Ask the user to run the game and provide:

- one light-theme screenshot with a sparse stack;
- one light-theme screenshot with a high stack;
- one dark-theme screenshot showing the ghost near locked blocks.

Review for transparent interiors, unambiguous distinction from real blocks,
continuous outlines, and lower visual priority than the active piece. Do not
take screenshots automatically.

- [ ] **Step 4: Merge the approved requirement into the canonical spec**

Add this scenario under
`Requirement: Theme visuals use deliberate flat contrast` in
`openspec/specs/ui-theme/spec.md`:

```markdown
#### Scenario: Ghost projection is structurally distinct from real blocks
- **Given** either the light or dark theme is active
- **And** the active tetromino has a visible landing projection
- **When** the ghost cells are rendered
- **Then** each ghost cell has a transparent interior
- **And** each normally sized ghost cell uses nested neutral outlines
- **And** the outlines do not inherit the active tetromino color
- **And** ghost outlines are not drawn over overlapping active cells
- **And** the ghost remains lower in visual priority than active and locked pieces
```

- [ ] **Step 5: Complete verification notes and archive the change**

Mark every task complete and record:

- focused RED and GREEN evidence;
- selected regression results;
- full test count and result;
- package result;
- user screenshot review date and conclusion.

Then run:

```bash
mkdir -p openspec/changes/archive
mv openspec/changes/refine-ghost-piece-visibility \
  openspec/changes/archive/refine-ghost-piece-visibility
```

- [ ] **Step 6: Commit canonical spec and archive**

```bash
git add openspec/specs/ui-theme/spec.md \
  openspec/changes/archive/refine-ghost-piece-visibility
git commit -m "docs: archive ghost piece visibility spec"
```

- [ ] **Step 7: Confirm final repository state**

Run:

```bash
git status --short --branch
git log -3 --oneline
```

Expected: working tree is clean; the latest commits are the implementation and
archive commits for this change.
