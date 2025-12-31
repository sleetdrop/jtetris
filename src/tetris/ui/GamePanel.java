package tetris.ui;

import tetris.model.Board;
import tetris.model.Tetromino;
import tetris.model.TetrominoType;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;

public class GamePanel extends JPanel {
    private final Board board;
    private int cellSize = 24;

    public GamePanel(Board board) {
        this.board = board;
        setPreferredSize(new Dimension(Board.WIDTH * cellSize, (Board.HEIGHT - 2) * cellSize));
        setBackground(new Color(18, 18, 24));
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

        drawGrid(g2d);
        drawLockedBlocks(g2d);
        drawCurrentPiece(g2d);
        g2d.dispose();
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(new Color(50, 50, 60));
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

    private void fillCell(Graphics2D g2d, int gridX, int gridY, Color color) {
        int x = gridX * cellSize;
        int y = gridY * cellSize;
        g2d.setColor(color);
        g2d.fillRect(x + 1, y + 1, cellSize - 2, cellSize - 2);
        g2d.setColor(color.darker());
        g2d.drawRect(x, y, cellSize, cellSize);
    }
}
