package net.vetcafe.jtetris.ui;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import net.vetcafe.jtetris.model.TetrominoType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ThemeVisualsTest {
    private final UiTheme.Mode originalMode = UiTheme.activeMode();

    @AfterEach
    void restoreThemeMode() {
        UiTheme.setActiveMode(originalMode);
    }

    @Test
    void themeSurfacesKeepReadableTextAndQuietGrid() {
        for (UiTheme.Mode mode : new UiTheme.Mode[] { UiTheme.Mode.LIGHT, UiTheme.Mode.DARK }) {
            UiTheme.setActiveMode(mode);
            UiTheme theme = UiTheme.active();

            assertTrue(contrastRatio(theme.textPrimary(), theme.sidePanelBackground()) >= 4.5);
            assertTrue(luminanceDistance(theme.boardBackground(), theme.boardGrid()) <= 0.18);
            assertTrue(luminanceDistance(theme.boardBackground(), theme.sidePanelBackground()) <= 0.08);
        }
    }

    @Test
    void paletteOutlinesStayFlatInsteadOfShadowHeavy() {
        for (UiTheme.Mode mode : new UiTheme.Mode[] { UiTheme.Mode.LIGHT, UiTheme.Mode.DARK }) {
            UiTheme.setActiveMode(mode);
            for (TetrominoType type : TetrominoType.values()) {
                Color fill = ColorPalette.colorFor(type);
                Color outline = ColorPalette.outlineFor(type);

                assertNotEquals(fill, outline);
                assertTrue(luminanceDistance(fill, outline) <= 0.22);
            }
        }
    }

    private static double luminanceDistance(Color a, Color b) {
        return Math.abs(relativeLuminance(a) - relativeLuminance(b));
    }

    private static double contrastRatio(Color a, Color b) {
        double first = relativeLuminance(a);
        double second = relativeLuminance(b);
        double lighter = Math.max(first, second);
        double darker = Math.min(first, second);
        return (lighter + 0.05) / (darker + 0.05);
    }

    private static double relativeLuminance(Color color) {
        return (0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue()) / 255.0;
    }
}
