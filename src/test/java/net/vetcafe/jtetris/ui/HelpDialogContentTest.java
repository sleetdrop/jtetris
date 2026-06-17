package net.vetcafe.jtetris.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HelpDialogContentTest {

    @Test
    void helpContentExplainsSurfacedMechanics() {
        String html = HelpDialog.helpHtml(UiTheme.active());

        assertTrue(html.contains("Controls"));
        assertTrue(html.contains("Hold"));
        assertTrue(html.contains("Next"));
        assertTrue(html.contains("ghost piece"));
        assertTrue(html.contains("Combo"));
        assertTrue(html.contains("Back-to-Back"));
        assertTrue(html.contains("T-Spin"));
        assertTrue(html.contains("Perfect Clear"));
    }
}
