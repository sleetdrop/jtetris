package net.vetcafe.jtetris.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class NextQueueTest {

    @Test
    void boardStartsWithThreeUpcomingPieces() {
        Board board = new Board(7L);

        assertEquals(3, board.getNextQueue().size());
        assertEquals(board.getNext().getType(), board.getNextQueue().get(0));
    }

    @Test
    void upcomingQueueSnapshotIsImmutable() {
        Board board = new Board(7L);

        assertThrows(
                UnsupportedOperationException.class, () -> board.getNextQueue().add(TetrominoType.I));
    }

    @Test
    void lockingPromotesQueueHeadAndRefillsTail() {
        Board board = new Board(7L);
        List<TetrominoType> before = board.getNextQueue();

        lockCurrent(board);

        assertEquals(before.get(0), board.getCurrent().getType());
        assertEquals(before.get(1), board.getNextQueue().get(0));
        assertEquals(before.get(2), board.getNextQueue().get(1));
        assertEquals(3, board.getNextQueue().size());
    }

    @Test
    void resetRebuildsThreeUpcomingPieces() {
        Board board = new Board(7L);
        board.applyReplayAction(ReplayAction.LEFT);
        lockCurrent(board);

        board.reset();

        assertEquals(3, board.getNextQueue().size());
        assertEquals(0, board.getReplayActions().size());
    }

    private static void lockCurrent(Board board) {
        while (board.move(0, 1)) {
            // descend until touching stack/floor
        }
        board.tick();
        board.tick();
    }
}
