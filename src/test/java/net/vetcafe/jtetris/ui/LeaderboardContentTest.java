package net.vetcafe.jtetris.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.ListSelectionModel;
import net.vetcafe.jtetris.score.ScoreManager;
import org.junit.jupiter.api.Test;

class LeaderboardContentTest {
    @Test
    void enablesDeleteOnlyForSingleSelectedPlayer() {
        AtomicReference<String> requestedDelete = new AtomicReference<>();
        LeaderboardContent content = new LeaderboardContent(
                List.of(new ScoreManager.ScoreEntry("Alice", 1200), new ScoreManager.ScoreEntry("Bob", 800)),
                requestedDelete::set,
                () -> {});

        assertEquals(
                ListSelectionModel.SINGLE_SELECTION,
                content.table().getSelectionModel().getSelectionMode());
        assertFalse(content.deleteButton().isEnabled());

        content.table().setRowSelectionInterval(1, 1);

        assertTrue(content.deleteButton().isEnabled());
        content.deleteButton().doClick();
        assertEquals("Bob", requestedDelete.get());
    }

    @Test
    void emptyLeaderboardShowsEmptyStateAndCannotDelete() {
        LeaderboardContent content = new LeaderboardContent(List.of(), user -> {}, () -> {});

        assertNull(content.table());
        assertEquals("No scores yet", content.emptyLabel().getText());
        assertFalse(content.deleteButton().isEnabled());
    }

    @Test
    void closeButtonRunsCloseAction() {
        AtomicBoolean closed = new AtomicBoolean();
        LeaderboardContent content = new LeaderboardContent(List.of(), user -> {}, () -> closed.set(true));

        content.closeButton().doClick();

        assertTrue(closed.get());
    }
}
