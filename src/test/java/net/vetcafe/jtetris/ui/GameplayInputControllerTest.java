package net.vetcafe.jtetris.ui;

import net.vetcafe.jtetris.model.Board;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameplayInputControllerTest {

    @Test
    void horizontalTapMovesExactlyOneColumn() {
        TestRig rig = new TestRig();
        int startX = rig.board.getCurrent().getX();

        assertTrue(rig.controller.pressLeft());
        assertFalse(rig.controller.releaseLeft());

        assertEquals(startX - 1, rig.board.getCurrent().getX());
    }

    @Test
    void heldHorizontalInputWaitsForDasThenRepeatsAtArr() {
        TestRig rig = new TestRig();
        int startX = rig.board.getCurrent().getX();

        assertTrue(rig.controller.pressRight());
        rig.clock.advance(119);
        assertFalse(rig.controller.poll());
        rig.clock.advance(1);
        assertTrue(rig.controller.poll());
        rig.clock.advance(39);
        assertFalse(rig.controller.poll());
        rig.clock.advance(1);
        assertTrue(rig.controller.poll());

        assertEquals(startX + 3, rig.board.getCurrent().getX());
    }

    @Test
    void delayedHorizontalPollAppliesOnlyOneStep() {
        TestRig rig = new TestRig();
        int startX = rig.board.getCurrent().getX();

        assertTrue(rig.controller.pressLeft());
        rig.clock.advance(400);
        assertTrue(rig.controller.poll());

        assertEquals(startX - 2, rig.board.getCurrent().getX());
    }

    @Test
    void latestDirectionWinsAndReleaseFallsBackImmediately() {
        TestRig rig = new TestRig();
        int startX = rig.board.getCurrent().getX();

        assertTrue(rig.controller.pressLeft());
        assertTrue(rig.controller.pressRight());
        assertTrue(rig.controller.releaseRight());

        assertEquals(startX - 1, rig.board.getCurrent().getX());
    }

    @Test
    void softDropPressRepeatsWithoutBurstingAfterDelay() {
        TestRig rig = new TestRig();
        int startY = rig.board.getCurrent().getY();

        assertTrue(rig.controller.pressSoftDrop());
        rig.clock.advance(120);
        assertTrue(rig.controller.poll());

        assertEquals(startY + 2, rig.board.getCurrent().getY());
    }

    @Test
    void resetStopsHeldInputWithoutMovingBoard() {
        TestRig rig = new TestRig();

        assertTrue(rig.controller.pressRight());
        assertTrue(rig.controller.pressSoftDrop());
        int xAfterPress = rig.board.getCurrent().getX();
        int yAfterPress = rig.board.getCurrent().getY();

        rig.controller.reset();
        rig.clock.advance(1_000);

        assertFalse(rig.controller.poll());
        assertEquals(xAfterPress, rig.board.getCurrent().getX());
        assertEquals(yAfterPress, rig.board.getCurrent().getY());
    }

    private static final class TestRig {
        private final Board board = new Board(42L);
        private final FakeClock clock = new FakeClock();
        private final GameplayInputController controller =
                new GameplayInputController(board, 120, 40, 40, clock::nowMs);
    }

    private static final class FakeClock {
        private long nowMs;

        long nowMs() {
            return nowMs;
        }

        void advance(long millis) {
            nowMs += millis;
        }
    }
}
