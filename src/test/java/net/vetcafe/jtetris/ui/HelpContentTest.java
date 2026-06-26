package net.vetcafe.jtetris.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HelpContentTest {

    @Test
    void helpContentExplainsSurfacedMechanics() {
        String html = HelpContent.helpHtml(UiTheme.active());

        assertTrue(html.contains("Controls"));
        assertTrue(html.contains("Hold"));
        assertTrue(html.contains("Next"));
        assertTrue(html.contains("next three pieces"));
        assertTrue(html.contains("ghost piece"));
        assertTrue(html.contains("Combo"));
        assertTrue(html.contains("Back-to-Back"));
        assertTrue(html.contains("T-Spin"));
        assertTrue(html.contains("Endless Marathon"));
        assertTrue(html.contains("top-out"));
        assertTrue(html.contains("excludes pauses and blocking prompts"));
        assertFalse(html.contains("Future polish targets"));
        assertFalse(html.contains("Perfect Clear"));
    }
}
