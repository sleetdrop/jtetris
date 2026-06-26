package net.vetcafe.jtetris.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import net.vetcafe.jtetris.model.Tetromino;
import net.vetcafe.jtetris.model.TetrominoType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class GamePanelGhostRenderingTest {
    private final UiTheme.Mode originalMode = UiTheme.activeMode();

    @AfterEach
    void restoreThemeMode() {
        UiTheme.setActiveMode(originalMode);
    }

    @Test
    void normalGhostCellUsesTwoOutlinesAndLeavesCenterTransparent() {
        for (UiTheme.Mode mode : new UiTheme.Mode[] {UiTheme.Mode.LIGHT, UiTheme.Mode.DARK}) {
            UiTheme.setActiveMode(mode);
            BufferedImage image = renderGhostCell(24);
            Color board = UiTheme.active().boardBackground();

            assertEquals(board.getRGB(), image.getRGB(12, 12));
            assertNotEquals(board.getRGB(), image.getRGB(1, 1));
            assertNotEquals(board.getRGB(), image.getRGB(3, 3));
            assertNotEquals(image.getRGB(1, 1), image.getRGB(3, 3));
        }
    }

    @Test
    void ghostOutlinesStayBelowSolidPieceContrast() {
        for (UiTheme.Mode mode : new UiTheme.Mode[] {UiTheme.Mode.LIGHT, UiTheme.Mode.DARK}) {
            UiTheme.setActiveMode(mode);
            Color board = UiTheme.active().boardBackground();
            Color outer = GamePanel.ghostOuterColor();
            Color inner = GamePanel.ghostInnerColor();
            double minimumPieceDistance = Double.MAX_VALUE;
            for (TetrominoType type : TetrominoType.values()) {
                minimumPieceDistance =
                        Math.min(minimumPieceDistance, colorDistance(ColorPalette.colorFor(type), board));
            }

            assertTrue(colorDistance(outer, board) < minimumPieceDistance);
            assertTrue(colorDistance(inner, board) < colorDistance(outer, board));
        }
    }

    @Test
    void smallGhostCellFallsBackToOneValidOutline() {
        BufferedImage image = renderGhostCell(6);
        Color board = UiTheme.active().boardBackground();

        assertNotEquals(board.getRGB(), image.getRGB(1, 1));
        assertEquals(board.getRGB(), image.getRGB(3, 3));
    }

    @Test
    void overlappingCurrentCellIsDetectedBeforeGhostDrawing() {
        Tetromino current = new Tetromino(TetrominoType.O, 4, 18);

        assertTrue(GamePanel.overlapsCurrentCell(current, 5, 19));
        assertFalse(GamePanel.overlapsCurrentCell(current, 3, 19));
    }

    private static BufferedImage renderGhostCell(int cellSize) {
        BufferedImage image = new BufferedImage(cellSize, cellSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(UiTheme.active().boardBackground());
        graphics.fillRect(0, 0, cellSize, cellSize);
        GamePanel.drawGhostCell(graphics, 0, 0, cellSize);
        graphics.dispose();
        return image;
    }

    private static double colorDistance(Color first, Color second) {
        int red = first.getRed() - second.getRed();
        int green = first.getGreen() - second.getGreen();
        int blue = first.getBlue() - second.getBlue();
        return Math.sqrt((red * red) + (green * green) + (blue * blue));
    }
}
