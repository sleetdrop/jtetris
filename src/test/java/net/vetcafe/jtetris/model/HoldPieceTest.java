package net.vetcafe.jtetris.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class HoldPieceTest {

    @Test
    void firstHoldStoresCurrentAndPromotesNext() {
        Board board = new Board(7L);
        TetrominoType firstCurrent = board.getCurrent().getType();
        TetrominoType firstNext = board.getNext().getType();

        assertTrue(board.isHoldAvailable());
        assertTrue(board.hold());
        assertNotNull(board.getHold());
        assertEquals(firstCurrent, board.getHold().getType());
        assertEquals(firstNext, board.getCurrent().getType());
        assertFalse(board.isHoldAvailable());
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

        assertTrue(board.isHoldAvailable());
        assertTrue(board.hold());
    }

    @Test
    void firstHoldAdvancesAndRefillsUpcomingQueue() {
        Board board = new Board(7L);
        List<TetrominoType> before = board.getNextQueue();

        assertTrue(board.hold());

        assertEquals(before.get(0), board.getCurrent().getType());
        assertEquals(before.get(1), board.getNextQueue().get(0));
        assertEquals(before.get(2), board.getNextQueue().get(1));
        assertEquals(before.get(3), board.getNextQueue().get(2));
        assertEquals(before.get(4), board.getNextQueue().get(3));
        assertEquals(5, board.getNextQueue().size());
    }

    @Test
    void populatedHoldSwapLeavesUpcomingQueueUnchanged() {
        Board board = new Board(7L);
        assertTrue(board.hold());
        lockCurrent(board);
        List<TetrominoType> beforeSwap = board.getNextQueue();

        assertTrue(board.hold());

        assertEquals(beforeSwap, board.getNextQueue());
    }

    private static void lockCurrent(Board board) {
        while (board.move(0, 1)) {
            // descend until touching stack/floor
        }
        board.tick();
        board.tick();
    }
}
