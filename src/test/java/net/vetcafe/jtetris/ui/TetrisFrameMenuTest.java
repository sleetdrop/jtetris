package net.vetcafe.jtetris.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TetrisFrameMenuTest {
    @Test
    void themeMenuTitleDoesNotIncludeCurrentSelection() {
        assertEquals("Theme", TetrisFrame.themeMenuTitle());
    }
}
