package tetris.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LockDelayTest {

    @Test
    void groundedPieceDoesNotLockOnFirstBlockedTick() {
        Board board = new Board(7L);
        Tetromino grounded = dropToGround(board);

        assertTrue(board.tick(), "Tick should progress lock-delay state");

        Tetromino afterFirstBlockedTick = board.getCurrent();
        assertEquals(grounded.getType(), afterFirstBlockedTick.getType());
        assertEquals(grounded.getX(), afterFirstBlockedTick.getX());
        assertEquals(grounded.getY(), afterFirstBlockedTick.getY());

        TetrominoType[][] grid = board.snapshot();
        for (var cell : grounded.getCells()) {
            int x = grounded.getX() + cell.x;
            int y = grounded.getY() + cell.y;
            assertNull(grid[y][x], "Piece should not be written to grid during lock delay");
        }
    }

    @Test
    void groundedPieceLocksAfterDelayExpires() {
        Board board = new Board(7L);
        Tetromino grounded = dropToGround(board);

        board.tick();
        board.tick();

        TetrominoType[][] grid = board.snapshot();
        for (var cell : grounded.getCells()) {
            int x = grounded.getX() + cell.x;
            int y = grounded.getY() + cell.y;
            assertEquals(grounded.getType(), grid[y][x], "Piece should lock into grid after lock delay");
        }
    }

    @Test
    void successfulMoveResetsLockDelayWindow() {
        Board board = new Board(7L);
        Tetromino grounded = dropToGround(board);

        board.tick();
        boolean moved = board.move(-1, 0) || board.move(1, 0);
        assertTrue(moved, "Piece should be able to move horizontally on floor");

        board.tick();
        Tetromino afterResetTick = board.getCurrent();
        assertEquals(grounded.getType(), afterResetTick.getType(), "Lock should not happen immediately after reset action");
        TetrominoType[][] gridAfterResetTick = board.snapshot();
        assertNoCellsLocked(gridAfterResetTick, grounded);

        board.tick();
        TetrominoType[][] gridAfterFinalTick = board.snapshot();
        assertTrue(containsType(gridAfterFinalTick, grounded.getType()));
    }

    @Test
    void hardDropStillLocksImmediately() {
        Board board = new Board(7L);
        Tetromino falling = board.getCurrent().copy();
        Tetromino ghostBeforeDrop = board.getGhost();

        assertTrue(board.hardDrop());

        Tetromino spawnedAfterDrop = board.getCurrent();
        assertNotEquals(falling.getType(), spawnedAfterDrop.getType());

        TetrominoType[][] grid = board.snapshot();
        for (var cell : ghostBeforeDrop.getCells()) {
            int x = ghostBeforeDrop.getX() + cell.x;
            int y = ghostBeforeDrop.getY() + cell.y;
            assertEquals(ghostBeforeDrop.getType(), grid[y][x]);
        }
    }

    private static Tetromino dropToGround(Board board) {
        while (board.move(0, 1)) {
            // move to floor
        }
        return board.getCurrent().copy();
    }

    private static void assertNoCellsLocked(TetrominoType[][] grid, Tetromino tetromino) {
        for (var cell : tetromino.getCells()) {
            int x = tetromino.getX() + cell.x;
            int y = tetromino.getY() + cell.y;
            assertNull(grid[y][x], "Piece should still be active during lock-delay reset");
        }
    }

    private static boolean containsType(TetrominoType[][] grid, TetrominoType type) {
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                if (grid[y][x] == type) {
                    return true;
                }
            }
        }
        return false;
    }
}

