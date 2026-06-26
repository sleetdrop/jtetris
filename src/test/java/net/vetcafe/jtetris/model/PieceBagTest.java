package net.vetcafe.jtetris.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.EnumSet;
import java.util.Random;
import org.junit.jupiter.api.Test;

class PieceBagTest {

    @Test
    void everyBagContainsAllSevenUniqueTetrominoes() {
        PieceBag bag = new PieceBag(new Random(42L));

        for (int round = 0; round < 3; round++) {
            EnumSet<TetrominoType> seen = EnumSet.noneOf(TetrominoType.class);
            for (int i = 0; i < TetrominoType.size(); i++) {
                seen.add(bag.next());
            }
            assertEquals(EnumSet.allOf(TetrominoType.class), seen);
        }
    }

    @Test
    void boardCurrentAndNextAdvanceAfterHardDrop() {
        Board board = new Board(7L);
        TetrominoType firstCurrent = board.getCurrent().getType();
        TetrominoType firstNext = board.getNext().getType();

        // Ensure we can lock current and promote next.
        while (board.move(0, 1)) {
            // keep descending
        }
        board.tick();
        board.tick();

        assertEquals(firstNext, board.getCurrent().getType());
        assertNotEquals(firstCurrent, board.getCurrent().getType());
    }
}
