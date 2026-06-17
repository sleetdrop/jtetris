package net.vetcafe.jtetris.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ScoreFeedbackFormatterTest {

    @Test
    void inactiveEventsRenderAsEmptyFeedback() {
        assertEquals("", ScoreFeedbackFormatter.eventText("NONE"));
        assertEquals("", ScoreFeedbackFormatter.eventText("NO_CLEAR"));
    }

    @Test
    void lineClearEventsRenderForPlayers() {
        assertEquals("Single", ScoreFeedbackFormatter.eventText("LINE_CLEAR_1"));
        assertEquals("Double + Combo x2", ScoreFeedbackFormatter.eventText("LINE_CLEAR_2_COMBO_2"));
        assertEquals("Tetris + Back-to-Back + Combo x3", ScoreFeedbackFormatter.eventText("TETRIS_B2B_COMBO_3"));
    }

    @Test
    void tSpinEventsRenderForPlayers() {
        assertEquals("T-Spin Single", ScoreFeedbackFormatter.eventText("TSPIN_SINGLE"));
        assertEquals("T-Spin Double + Back-to-Back", ScoreFeedbackFormatter.eventText("TSPIN_DOUBLE_B2B"));
    }

    @Test
    void statusLabelsUseInactiveLanguage() {
        assertEquals("Combo -", ScoreFeedbackFormatter.comboText(0));
        assertEquals("Combo x4", ScoreFeedbackFormatter.comboText(4));
        assertEquals("B2B Ready", ScoreFeedbackFormatter.backToBackText(false));
        assertEquals("B2B On", ScoreFeedbackFormatter.backToBackText(true));
    }
}
