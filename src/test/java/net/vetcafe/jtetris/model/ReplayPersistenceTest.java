package net.vetcafe.jtetris.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ReplayPersistenceTest {

    @TempDir
    Path tempDir;

    @Test
    void saveAndLoadRoundTripsSeedAndActionOrder() throws IOException {
        Path replayFile = tempDir.resolve("sample.jtr");
        List<ReplayAction> actions = List.of(
                ReplayAction.LEFT,
                ReplayAction.ROTATE_CW,
                ReplayAction.SOFT_DROP,
                ReplayAction.HARD_DROP,
                ReplayAction.TICK
        );

        ReplayPersistence.save(replayFile, 42L, actions);
        ReplayPersistence.LoadedReplay loaded = ReplayPersistence.load(replayFile);

        assertEquals(42L, loaded.seed());
        assertEquals(actions, loaded.actions());
    }

    @Test
    void loadedReplayCanRebuildBoardDeterministically() throws IOException {
        Path replayFile = tempDir.resolve("deterministic.jtr");
        Board source = new Board(123L);
        List<ReplayAction> actions = List.of(
                ReplayAction.RIGHT,
                ReplayAction.ROTATE_CCW,
                ReplayAction.SOFT_DROP,
                ReplayAction.SOFT_DROP,
                ReplayAction.HARD_DROP,
                ReplayAction.HOLD,
                ReplayAction.TICK,
                ReplayAction.TICK,
                ReplayAction.LEFT,
                ReplayAction.HARD_DROP
        );
        for (ReplayAction action : actions) {
            source.applyReplayAction(action);
        }

        ReplayPersistence.save(replayFile, 123L, source.getReplayActions());
        ReplayPersistence.LoadedReplay loaded = ReplayPersistence.load(replayFile);
        Board replay = Board.replayFromSeed(loaded.seed(), loaded.actions());

        assertEquals(source.getScore(), replay.getScore());
        assertEquals(source.getLinesCleared(), replay.getLinesCleared());
        assertEquals(source.getLevel(), replay.getLevel());
        assertEquals(source.getLastScoreEvent(), replay.getLastScoreEvent());

        TetrominoType[][] sourceGrid = source.snapshot();
        TetrominoType[][] replayGrid = replay.snapshot();
        for (int y = 0; y < Board.HEIGHT; y++) {
            for (int x = 0; x < Board.WIDTH; x++) {
                assertEquals(sourceGrid[y][x], replayGrid[y][x]);
            }
        }
    }

    @Test
    void loadRejectsMalformedFile() throws IOException {
        Path replayFile = tempDir.resolve("broken.jtr");
        Files.writeString(replayFile, "not-a-replay", StandardCharsets.UTF_8);

        IOException ex = assertThrows(IOException.class, () -> ReplayPersistence.load(replayFile));
        assertTrue(ex.getMessage().contains("Invalid replay file"));
    }
}

