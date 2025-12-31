package tetris.model;

import java.util.Arrays;
import java.util.Random;

public class Board {
    public static final int WIDTH = 10;
    public static final int HEIGHT = 22; // includes hidden rows at top

    private final TetrominoType[][] grid = new TetrominoType[HEIGHT][WIDTH];
    private final Random random = new Random();
    private Tetromino current;
    private Tetromino next;
    private boolean gameOver;
    private int score;
    private int linesCleared;
    private int level = 1;

    public Board() {
        spawnInitial();
    }

    public void reset() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                grid[y][x] = null;
            }
        }
        gameOver = false;
        score = 0;
        linesCleared = 0;
        level = 1;
        spawnInitial();
    }

    private void spawnInitial() {
        current = new Tetromino(randomType(), WIDTH / 2 - 2, 0);
        next = new Tetromino(randomType(), WIDTH / 2 - 2, 0);
        if (!isPositionValid(current)) {
            gameOver = true;
        }
    }

    private TetrominoType randomType() {
        TetrominoType[] values = TetrominoType.values();
        return values[random.nextInt(values.length)];
    }

    public Tetromino getCurrent() {
        return current;
    }

    public Tetromino getNext() {
        return next;
    }

    public int getScore() {
        return score;
    }

    public int getLinesCleared() {
        return linesCleared;
    }

    public int getLevel() {
        return level;
    }

    public TetrominoType[][] snapshot() {
        TetrominoType[][] copy = new TetrominoType[HEIGHT][WIDTH];
        for (int r = 0; r < HEIGHT; r++) {
            copy[r] = Arrays.copyOf(grid[r], WIDTH);
        }
        return copy;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean move(int dx, int dy) {
        if (gameOver) return false;
        Tetromino moved = current.copy();
        moved.move(dx, dy);
        if (isPositionValid(moved)) {
            current = moved;
            return true;
        }
        return false;
    }

    public boolean rotateCW() {
        return rotate(true);
    }

    public boolean rotateCCW() {
        return rotate(false);
    }

    private boolean rotate(boolean cw) {
        if (gameOver) return false;
        Tetromino rotated = current.copy();
        if (cw) rotated.rotateCW(); else rotated.rotateCCW();
        if (isPositionValid(rotated)) {
            current = rotated;
            return true;
        }
        return false;
    }

    public boolean hardDrop() {
        if (gameOver) return false;
        boolean moved = false;
        while (move(0, 1)) {
            moved = true;
        }
        if (!moved) return false;
        lockCurrent();
        return true;
    }

    public boolean tick() {
        if (gameOver) return false;
        if (!move(0, 1)) {
            lockCurrent();
        }
        return true;
    }

    private void lockCurrent() {
        for (var cell : current.getCells()) {
            int x = current.getX() + cell.x;
            int y = current.getY() + cell.y;
            if (y < 0 || y >= HEIGHT || x < 0 || x >= WIDTH) {
                gameOver = true;
                return;
            }
            grid[y][x] = current.getType();
        }
        clearLines();
        spawnNext();
    }

    private void spawnNext() {
        current = new Tetromino(next.getType(), WIDTH / 2 - 2, 0);
        next = new Tetromino(randomType(), WIDTH / 2 - 2, 0);
        if (!isPositionValid(current)) {
            gameOver = true;
        }
    }

    private void clearLines() {
        int cleared = 0;
        for (int y = HEIGHT - 1; y >= 0; y--) {
            boolean full = true;
            for (int x = 0; x < WIDTH; x++) {
                if (grid[y][x] == null) {
                    full = false;
                    break;
                }
            }
            if (full) {
                cleared++;
                for (int ty = y; ty > 0; ty--) {
                    grid[ty] = Arrays.copyOf(grid[ty - 1], WIDTH);
                }
                grid[0] = new TetrominoType[WIDTH];
                y++; // recheck this row after shift
            }
        }
        if (cleared > 0) {
            linesCleared += cleared;
            score += switch (cleared) {
                case 1 -> 100 * level;
                case 2 -> 300 * level;
                case 3 -> 500 * level;
                case 4 -> 800 * level;
                default -> 0;
            };
            level = 1 + linesCleared / 10;
        }
    }

    private boolean isPositionValid(Tetromino tetromino) {
        for (var cell : tetromino.getCells()) {
            int x = tetromino.getX() + cell.x;
            int y = tetromino.getY() + cell.y;
            if (x < 0 || x >= WIDTH || y >= HEIGHT) return false;
            if (y >= 0 && grid[y][x] != null) return false;
        }
        return true;
    }
}
