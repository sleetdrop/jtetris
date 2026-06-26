package net.vetcafe.jtetris.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.util.Arrays;
import javax.swing.JPanel;
import javax.swing.Timer;
import net.vetcafe.jtetris.model.Board;
import net.vetcafe.jtetris.model.Tetromino;
import net.vetcafe.jtetris.model.TetrominoType;

public class GamePanel extends JPanel {
    private static final int DEFAULT_CLEAR_FLASH_TOTAL_MS = 180;
    private static final int DEFAULT_CLEAR_FLASH_STEP_MS = 45;
    private static final int DEFAULT_CLEAR_FLASH_DARK_FILL_ALPHA = 132;
    private static final int DEFAULT_CLEAR_FLASH_LIGHT_FILL_ALPHA = 154;
    private static final int DEFAULT_CLEAR_FLASH_DARK_EDGE_ALPHA = 178;
    private static final int DEFAULT_CLEAR_FLASH_LIGHT_EDGE_ALPHA = 196;

    private static final int clearFlashTotalMs =
            boundedIntProperty("jtetris.flash.duration.ms", DEFAULT_CLEAR_FLASH_TOTAL_MS, 60, 1000);
    private static final int clearFlashStepMs =
            boundedIntProperty("jtetris.flash.step.ms", DEFAULT_CLEAR_FLASH_STEP_MS, 15, 250);
    private static final int clearFlashDarkFillAlpha =
            boundedIntProperty("jtetris.flash.dark.fill.alpha", DEFAULT_CLEAR_FLASH_DARK_FILL_ALPHA, 20, 255);
    private static final int clearFlashLightFillAlpha =
            boundedIntProperty("jtetris.flash.light.fill.alpha", DEFAULT_CLEAR_FLASH_LIGHT_FILL_ALPHA, 20, 255);
    private static final int clearFlashDarkEdgeAlpha =
            boundedIntProperty("jtetris.flash.dark.edge.alpha", DEFAULT_CLEAR_FLASH_DARK_EDGE_ALPHA, 20, 255);
    private static final int clearFlashLightEdgeAlpha =
            boundedIntProperty("jtetris.flash.light.edge.alpha", DEFAULT_CLEAR_FLASH_LIGHT_EDGE_ALPHA, 20, 255);

    private final Board board;
    private final Timer clearFlashTimer;
    private int cellSize = 24;
    private int lastSeenLineClearEffectVersion = -1;
    private int[] flashingRows = new int[0];
    private long flashStartAtMs;
    private long flashEndAtMs;

    public GamePanel(Board board) {
        this.board = board;
        this.clearFlashTimer = new Timer(clearFlashStepMs, e -> {
            if (isLineClearFlashActive()) {
                repaint();
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        setPreferredSize(new Dimension(Board.WIDTH * cellSize, (Board.HEIGHT - 2) * cellSize));
        setBackground(UiTheme.active().boardBackground());
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                requestFocusInWindow();
            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                cellSize = Math.min(getWidth() / Board.WIDTH, getHeight() / (Board.HEIGHT - 2));
            }
        });
    }

    public void applyTheme() {
        setBackground(UiTheme.active().boardBackground());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        syncLineClearFlash();
        drawGrid(g2d);
        drawLockedBlocks(g2d);
        drawLineClearFlash(g2d);
        drawGhostPiece(g2d);
        drawCurrentPiece(g2d);
        g2d.dispose();
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(UiTheme.active().boardGrid());
        for (int x = 0; x <= Board.WIDTH; x++) {
            g2d.drawLine(x * cellSize, 0, x * cellSize, (Board.HEIGHT - 2) * cellSize);
        }
        for (int y = 0; y <= Board.HEIGHT - 2; y++) {
            g2d.drawLine(0, y * cellSize, Board.WIDTH * cellSize, y * cellSize);
        }
    }

    private void drawLockedBlocks(Graphics2D g2d) {
        TetrominoType[][] grid = board.snapshot();
        for (int y = 2; y < Board.HEIGHT; y++) { // hide top buffer rows
            for (int x = 0; x < Board.WIDTH; x++) {
                TetrominoType type = grid[y][x];
                if (type != null) {
                    fillCell(g2d, x, y - 2, type);
                }
            }
        }
    }

    private void drawCurrentPiece(Graphics2D g2d) {
        Tetromino current = board.getCurrent();
        if (current == null) return;
        for (var cell : current.getCells()) {
            int px = (current.getX() + cell.x) * cellSize;
            int py = (current.getY() + cell.y - 2) * cellSize;
            if (py + cellSize <= 0) continue; // skip hidden rows
            fillCell(g2d, px / cellSize, py / cellSize, current.getType());
        }
    }

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

    static Color ghostOuterColor() {
        return UiTheme.active().isDark() ? new Color(118, 128, 150) : new Color(155, 164, 178);
    }

    static Color ghostInnerColor() {
        return UiTheme.active().isDark() ? new Color(68, 78, 99) : new Color(200, 205, 214);
    }

    static boolean overlapsCurrentCell(Tetromino current, int gridX, int modelY) {
        if (current == null) return false;
        for (var cell : current.getCells()) {
            if (current.getX() + cell.x == gridX && current.getY() + cell.y == modelY) {
                return true;
            }
        }
        return false;
    }

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

    private void syncLineClearFlash() {
        int version = board.getLineClearEffectVersion();
        if (version == lastSeenLineClearEffectVersion) {
            return;
        }
        lastSeenLineClearEffectVersion = version;
        int[] visibleRows = Arrays.stream(board.getLastClearedRows())
                .map(row -> row - 2)
                .filter(row -> row >= 0 && row < (Board.HEIGHT - 2))
                .toArray();
        if (visibleRows.length == 0) {
            flashingRows = new int[0];
            return;
        }
        flashingRows = visibleRows;
        flashStartAtMs = System.currentTimeMillis();
        flashEndAtMs = flashStartAtMs + clearFlashTotalMs;
        if (!clearFlashTimer.isRunning()) {
            clearFlashTimer.start();
        }
    }

    private boolean isLineClearFlashActive() {
        return flashingRows.length > 0 && System.currentTimeMillis() < flashEndAtMs;
    }

    private void drawLineClearFlash(Graphics2D g2d) {
        if (flashingRows.length == 0) return;
        long now = System.currentTimeMillis();
        if (now >= flashEndAtMs) {
            flashingRows = new int[0];
            return;
        }
        long phase = (now - flashStartAtMs) / clearFlashStepMs;
        if ((phase & 1L) == 1L) return;

        UiTheme theme = UiTheme.active();
        int boost = cellSize <= 16 ? 28 : (cellSize <= 20 ? 14 : 0);
        Color fill = theme.isDark()
                ? new Color(246, 248, 255, boostedAlpha(clearFlashDarkFillAlpha, boost))
                : new Color(255, 255, 255, boostedAlpha(clearFlashLightFillAlpha, boost));
        Color edge = theme.isDark()
                ? new Color(255, 255, 255, boostedAlpha(clearFlashDarkEdgeAlpha, boost / 2))
                : new Color(255, 255, 255, boostedAlpha(clearFlashLightEdgeAlpha, boost / 2));
        int width = Board.WIDTH * cellSize;

        for (int row : flashingRows) {
            int y = row * cellSize;
            g2d.setColor(fill);
            g2d.fillRect(0, y, width, cellSize);
            g2d.setColor(edge);
            g2d.drawLine(0, y, width, y);
            g2d.drawLine(0, y + cellSize - 1, width, y + cellSize - 1);
        }
    }

    private void fillCell(Graphics2D g2d, int gridX, int gridY, TetrominoType type) {
        int x = gridX * cellSize;
        int y = gridY * cellSize;
        Color color = ColorPalette.colorFor(type);
        Color edge = ColorPalette.outlineFor(type);
        int inset = cellSize <= 18 ? 1 : 2;
        int size = Math.max(1, cellSize - (inset * 2));
        g2d.setColor(color);
        g2d.fillRect(x + inset, y + inset, size, size);
        g2d.setColor(edge);
        g2d.drawRect(x + inset, y + inset, size - 1, size - 1);
    }

    private static int boostedAlpha(int base, int boost) {
        return Math.min(255, Math.max(0, base + boost));
    }

    private static int boundedIntProperty(String key, int fallback, int min, int max) {
        String raw = System.getProperty(key);
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            int parsed = Integer.parseInt(raw.trim());
            if (parsed < min || parsed > max) {
                return fallback;
            }
            return parsed;
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
}
