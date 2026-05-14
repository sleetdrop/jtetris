package tetris.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GhostPieceTest {

    @Test
    void ghostMatchesHardDropLandingCells() {
        Board board = new Board(7L);
        Tetromino currentBefore = board.getCurrent().copy();

        Tetromino ghost = board.getGhost();
        assertNotNull(ghost);

        Tetromino currentAfterGhostQuery = board.getCurrent();
        assertEquals(currentBefore.getType(), currentAfterGhostQuery.getType());
        assertEquals(currentBefore.getRotation(), currentAfterGhostQuery.getRotation());
        assertEquals(currentBefore.getX(), currentAfterGhostQuery.getX());
        assertEquals(currentBefore.getY(), currentAfterGhostQuery.getY());

        assertTrue(board.hardDrop());
        TetrominoType[][] grid = board.snapshot();
        for (var cell : ghost.getCells()) {
            int x = ghost.getX() + cell.x;
            int y = ghost.getY() + cell.y;
            assertEquals(ghost.getType(), grid[y][x], "Hard-drop lock should match ghost projection");
        }
    }

    @Test
    void ghostIsHiddenWhenGameOver() {
        Board board = new Board(7L);
        for (int i = 0; i < 2000 && !board.isGameOver(); i++) {
            board.hardDrop();
        }
        assertTrue(board.isGameOver());
        assertNull(board.getGhost());
    }
}

