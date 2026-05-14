package tetris.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.Test;

class ReplayHooksTest {

    @Test
    void replayFromSeedReproducesBoardState() {
        Board source = new Board(42L);
        List<ReplayAction> actions = List.of(
                ReplayAction.LEFT,
                ReplayAction.ROTATE_CW,
                ReplayAction.SOFT_DROP,
                ReplayAction.SOFT_DROP,
                ReplayAction.HARD_DROP,
                ReplayAction.HOLD,
                ReplayAction.RIGHT,
                ReplayAction.ROTATE_CCW,
                ReplayAction.TICK,
                ReplayAction.TICK,
                ReplayAction.HARD_DROP
        );

        for (ReplayAction action : actions) {
            source.applyReplayAction(action);
        }

        Board replay = Board.replayFromSeed(42L, actions);

        assertEquals(source.getScore(), replay.getScore());
        assertEquals(source.getLinesCleared(), replay.getLinesCleared());
        assertEquals(source.getLevel(), replay.getLevel());
        assertEquals(source.getComboStreak(), replay.getComboStreak());
        assertEquals(source.isBackToBackActive(), replay.isBackToBackActive());
        assertEquals(source.getLastScoreEvent(), replay.getLastScoreEvent());
        assertEquals(source.isGameOver(), replay.isGameOver());

        assertEquals(source.getCurrent().getType(), replay.getCurrent().getType());
        assertEquals(source.getCurrent().getRotation(), replay.getCurrent().getRotation());
        assertEquals(source.getCurrent().getX(), replay.getCurrent().getX());
        assertEquals(source.getCurrent().getY(), replay.getCurrent().getY());

        assertEquals(source.getNext().getType(), replay.getNext().getType());

        if (source.getHold() == null) {
            assertNull(replay.getHold());
        } else {
            assertEquals(source.getHold().getType(), replay.getHold().getType());
        }

        TetrominoType[][] sourceGrid = source.snapshot();
        TetrominoType[][] replayGrid = replay.snapshot();
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                assertEquals(sourceGrid[y][x], replayGrid[y][x]);
            }
        }
    }

    @Test
    void resetClearsRecordedReplayActions() {
        Board board = new Board(99L);
        board.applyReplayAction(ReplayAction.LEFT);
        board.applyReplayAction(ReplayAction.TICK);
        assertEquals(2, board.getReplayActions().size());

        board.reset();

        assertEquals(0, board.getReplayActions().size());
    }

    @Test
    void replaySeedIsExposedForSeededBoards() {
        Board seeded = new Board(123L);
        Board unseeded = new Board();

        assertEquals(123L, seeded.getReplaySeed());
        assertNull(unseeded.getReplaySeed());
    }
}

