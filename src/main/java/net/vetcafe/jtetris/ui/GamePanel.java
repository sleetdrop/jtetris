package net.vetcafe.jtetris.ui;

import net.vetcafe.jtetris.model.Board;
import net.vetcafe.jtetris.model.Tetromino;
import net.vetcafe.jtetris.model.TetrominoType;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.util.Arrays;

public class GamePanel extends JPanel {
    private static final int CLEAR_FLASH_TOTAL_MS = 180;
    private static final int CLEAR_FLASH_STEP_MS = 45;

    private final Board board;
    private final Timer clearFlashTimer;
    private int cellSize = 24;
    private int lastSeenLineClearEffectVersion = -1;
    private int[] flashingRows = new int[0];
    private long flashStartAtMs;
    private long flashEndAtMs;

    public GamePanel(Board board) {
        this.board = board;
        this.clearFlashTimer = new Timer(CLEAR_FLASH_STEP_MS, e -> {
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
                    fillCell(g2d, x, y - 2, ColorPalette.colorFor(type));
                }
            }
        }
    }

    private void drawCurrentPiece(Graphics2D g2d) {
        Tetromino current = board.getCurrent();
        if (current == null) return;
        Color color = ColorPalette.colorFor(current.getType());
        for (var cell : current.getCells()) {
            int px = (current.getX() + cell.x) * cellSize;
            int py = (current.getY() + cell.y - 2) * cellSize;
            if (py + cellSize <= 0) continue; // skip hidden rows
            fillCell(g2d, px / cellSize, py / cellSize, color);
        }
    }

    private void drawGhostPiece(Graphics2D g2d) {
        Tetromino ghost = board.getGhost();
        if (ghost == null) return;

        UiTheme theme = UiTheme.active();
        Color shadowBase = theme.isDark() ? new Color(203, 210, 230) : new Color(92, 99, 118);
        int fillAlpha = theme.isDark() ? 38 : 34;
        int strokeAlpha = theme.isDark() ? 88 : 80;
        Color fill = new Color(shadowBase.getRed(), shadowBase.getGreen(), shadowBase.getBlue(), fillAlpha);
        Color stroke = new Color(shadowBase.getRed(), shadowBase.getGreen(), shadowBase.getBlue(), strokeAlpha);
        for (var cell : ghost.getCells()) {
            int gx = ghost.getX() + cell.x;
            int gy = ghost.getY() + cell.y - 2;
            if (gy < 0) continue; // skip hidden rows
            drawGhostCell(g2d, gx, gy, fill, stroke);
        }
    }

    private void drawGhostCell(Graphics2D g2d, int gridX, int gridY, Color fill, Color stroke) {
        int x = gridX * cellSize;
        int y = gridY * cellSize;
        g2d.setColor(fill);
        g2d.fillRect(x + 1, y + 1, cellSize - 2, cellSize - 2);
        g2d.setColor(stroke);
        g2d.drawRect(x + 1, y + 1, cellSize - 3, cellSize - 3);
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
        flashEndAtMs = flashStartAtMs + CLEAR_FLASH_TOTAL_MS;
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
        long phase = (now - flashStartAtMs) / CLEAR_FLASH_STEP_MS;
        if ((phase & 1L) == 1L) return;

        UiTheme theme = UiTheme.active();
        Color fill = theme.isDark()
                ? new Color(246, 248, 255, 132)
                : new Color(255, 255, 255, 154);
        Color edge = theme.isDark()
                ? new Color(255, 255, 255, 178)
                : new Color(255, 255, 255, 196);
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

    private void fillCell(Graphics2D g2d, int gridX, int gridY, Color color) {
        int x = gridX * cellSize;
        int y = gridY * cellSize;
        g2d.setColor(color);
        g2d.fillRect(x + 1, y + 1, cellSize - 2, cellSize - 2);
        Color edge = UiTheme.active().isDark() ? color.darker() : color.darker().darker();
        g2d.setColor(edge);
        g2d.drawRect(x, y, cellSize, cellSize);
    }
}
