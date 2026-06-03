package net.vetcafe.jtetris.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

class BoardRegressionGateTest {

    @Test
    void cannotMoveOutsideLeftBoundary() {
        Board board = new Board(7L);
        while (board.move(-1, 0)) {
            // slide to wall
        }
        assertFalse(board.move(-1, 0));
    }

    @Test
    void cannotMoveIntoOccupiedCell() throws Exception {
        Board board = new Board(7L);
        Tetromino moved = board.getCurrent().copy();
        moved.move(1, 0);

        TetrominoType[][] grid = getGrid(board);
        boolean blocked = false;
        for (var cell : moved.getCells()) {
            int x = moved.getX() + cell.x;
            int y = moved.getY() + cell.y;
            if (x >= 0 && x < Board.WIDTH && y >= 0 && y < Board.HEIGHT) {
                grid[y][x] = TetrominoType.I;
                blocked = true;
                break;
            }
        }

        assertTrue(blocked, "Test setup should place at least one blocking cell");
        assertFalse(board.move(1, 0));
    }

    @Test
    void rotationFailsWhenBoardIsFullyOccupied() throws Exception {
        Board board = new Board(7L);
        TetrominoType[][] grid = getGrid(board);
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                grid[y][x] = TetrominoType.I;
            }
        }
        setCurrent(board, new Tetromino(TetrominoType.T, 4, 5));

        assertFalse(board.rotateCW());
    }

    @Test
    void clearLinesUpdatesStateConsistently() throws Exception {
        Board board = new Board(7L);
        TetrominoType[][] grid = getGrid(board);
        fillRow(grid, Board.HEIGHT - 1, TetrominoType.I);
        fillRow(grid, Board.HEIGHT - 2, TetrominoType.J);

        invokeClearLines(board);

        assertEquals(2, board.getLinesCleared());
        assertEquals(300, board.getScore());
        assertEquals("LINE CLEAR 2", board.getLastScoreEvent().replace('_', ' '));

        for (int x = 0; x < Board.WIDTH; x++) {
            assertEquals(null, grid[Board.HEIGHT - 1][x]);
            assertEquals(null, grid[Board.HEIGHT - 2][x]);
        }
    }

    @Test
    void clearLinesPublishesRowsForUiFlashEffect() throws Exception {
        Board board = new Board(7L);
        TetrominoType[][] grid = getGrid(board);
        fillRow(grid, Board.HEIGHT - 1, TetrominoType.I);
        fillRow(grid, Board.HEIGHT - 2, TetrominoType.J);

        invokeClearLines(board);

        assertArrayEquals(new int[]{Board.HEIGHT - 1, Board.HEIGHT - 2}, board.getLastClearedRows());
        assertEquals(1, board.getLineClearEffectVersion());

        invokeClearLines(board);

        assertArrayEquals(new int[0], board.getLastClearedRows());
        assertEquals(1, board.getLineClearEffectVersion());
    }

    @Test
    void topOutWhenSpawnAreaIsBlocked() throws Exception {
        Board board = new Board(7L);
        TetrominoType[][] grid = getGrid(board);
        setNext(board, new Tetromino(TetrominoType.O, Board.WIDTH / 2 - 2, 0));

        // O piece spawn cells at x=4,5 and y=0,1 for current spawn offset.
        grid[0][4] = TetrominoType.I;
        grid[0][5] = TetrominoType.I;

        invokeSpawnNext(board);

        assertTrue(board.isGameOver());
        assertNotNull(board.getCurrent());
    }

    private static void fillRow(TetrominoType[][] grid, int y, TetrominoType type) {
        for (int x = 0; x < Board.WIDTH; x++) {
            grid[y][x] = type;
        }
    }

    private static TetrominoType[][] getGrid(Board board) throws Exception {
        Field grid = Board.class.getDeclaredField("grid");
        grid.setAccessible(true);
        return (TetrominoType[][]) grid.get(board);
    }

    private static void setCurrent(Board board, Tetromino current) throws Exception {
        Field field = Board.class.getDeclaredField("current");
        field.setAccessible(true);
        field.set(board, current);
    }

    private static void setNext(Board board, Tetromino next) throws Exception {
        Field field = Board.class.getDeclaredField("next");
        field.setAccessible(true);
        field.set(board, next);
    }

    private static void invokeClearLines(Board board) throws Exception {
        Method method = Board.class.getDeclaredMethod("clearLines");
        method.setAccessible(true);
        method.invoke(board);
    }

    private static void invokeSpawnNext(Board board) throws Exception {
        Method method = Board.class.getDeclaredMethod("spawnNext");
        method.setAccessible(true);
        method.invoke(board);
    }
}

