package tetris.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HoldPieceTest {

    @Test
    void firstHoldStoresCurrentAndPromotesNext() {
        Board board = new Board(7L);
        TetrominoType firstCurrent = board.getCurrent().getType();
        TetrominoType firstNext = board.getNext().getType();

        assertTrue(board.hold());
        assertNotNull(board.getHold());
        assertEquals(firstCurrent, board.getHold().getType());
        assertEquals(firstNext, board.getCurrent().getType());
    }

    @Test
    void secondHoldInSameTurnIsIgnored() {
        Board board = new Board(7L);
        assertTrue(board.hold());
        TetrominoType currentAfterFirstHold = board.getCurrent().getType();

        assertFalse(board.hold());
        assertEquals(currentAfterFirstHold, board.getCurrent().getType());
    }

    @Test
    void holdBecomesAvailableAgainAfterLock() {
        Board board = new Board(7L);
        assertTrue(board.hold());
        assertFalse(board.hold());

        lockCurrent(board);

        assertTrue(board.hold());
    }

    private static void lockCurrent(Board board) {
        while (board.move(0, 1)) {
            // descend until touching stack/floor
        }
        board.tick();
        board.tick();
    }
}

