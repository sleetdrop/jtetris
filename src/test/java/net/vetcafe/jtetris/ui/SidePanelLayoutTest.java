package net.vetcafe.jtetris.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import net.vetcafe.jtetris.model.Board;
import org.junit.jupiter.api.Test;

class SidePanelLayoutTest {

    @Test
    void sidePanelOmitsPersistentControlsTextArea() throws Exception {
        SidePanel panel = onEdt(() -> new SidePanel(new Board(7L)));

        assertFalse(descendants(panel).stream().anyMatch(JTextArea.class::isInstance));
    }

    @Test
    void sidePanelKeepsExistingPreferredSize() throws Exception {
        SidePanel panel = onEdt(() -> new SidePanel(new Board(7L)));

        assertEquals(new Dimension(200, 520), panel.getPreferredSize());
    }

    @Test
    void sidePanelPlacesElapsedTimeAfterLines() throws Exception {
        SidePanel panel = onEdt(() -> new SidePanel(new Board(7L), () -> 3_600_000L));

        List<String> coreStats = descendants(panel).stream()
                .filter(JLabel.class::isInstance)
                .map(JLabel.class::cast)
                .map(JLabel::getText)
                .filter(text -> text.startsWith("Score:")
                        || text.startsWith("Level:")
                        || text.startsWith("Lines:")
                        || text.startsWith("Time:"))
                .toList();

        assertEquals(List.of("Score: 0", "Level: 1", "Lines: 0", "Time: 1:00:00"), coreStats);
    }

    @Test
    void sidePanelDisplaysAllUpcomingPiecesInQueueOrder() throws Exception {
        Board board = new Board(7L);
        SidePanel panel = onEdt(() -> new SidePanel(board));

        assertEquals(board.getNextQueue(), panel.displayedNextTypes());
        assertEquals(3, panel.displayedNextTypes().size());
    }

    @Test
    void sectionTitlesUsePrimaryTextColor() {
        assertEquals(UiTheme.active().textPrimary(), SidePanel.sectionTitleColor());
    }

    private static List<Component> descendants(Container root) {
        List<Component> result = new ArrayList<>();
        for (Component child : root.getComponents()) {
            result.add(child);
            if (child instanceof Container container) {
                result.addAll(descendants(container));
            }
        }
        return result;
    }

    private static <T> T onEdt(Supplier<T> supplier) throws Exception {
        FutureTask<T> task = new FutureTask<>(supplier::get);
        SwingUtilities.invokeAndWait(task);
        return task.get();
    }
}
