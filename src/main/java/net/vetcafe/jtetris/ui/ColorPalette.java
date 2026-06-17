package net.vetcafe.jtetris.ui;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;
import net.vetcafe.jtetris.model.TetrominoType;

public final class ColorPalette {
    private static final Map<TetrominoType, Color> DARK_COLORS = new EnumMap<>(TetrominoType.class);
    private static final Map<TetrominoType, Color> LIGHT_COLORS = new EnumMap<>(TetrominoType.class);

    static {
        DARK_COLORS.put(TetrominoType.I, new Color(79, 190, 203));
        DARK_COLORS.put(TetrominoType.O, new Color(226, 184, 82));
        DARK_COLORS.put(TetrominoType.T, new Color(169, 136, 210));
        DARK_COLORS.put(TetrominoType.S, new Color(98, 181, 128));
        DARK_COLORS.put(TetrominoType.Z, new Color(221, 105, 106));
        DARK_COLORS.put(TetrominoType.J, new Color(103, 135, 218));
        DARK_COLORS.put(TetrominoType.L, new Color(226, 145, 82));

        LIGHT_COLORS.put(TetrominoType.I, new Color(43, 157, 174));
        LIGHT_COLORS.put(TetrominoType.O, new Color(204, 155, 48));
        LIGHT_COLORS.put(TetrominoType.T, new Color(135, 96, 185));
        LIGHT_COLORS.put(TetrominoType.S, new Color(58, 152, 96));
        LIGHT_COLORS.put(TetrominoType.Z, new Color(195, 80, 82));
        LIGHT_COLORS.put(TetrominoType.J, new Color(74, 107, 184));
        LIGHT_COLORS.put(TetrominoType.L, new Color(204, 118, 50));
    }

    private ColorPalette() {}

    public static Color colorFor(TetrominoType type) {
        Map<TetrominoType, Color> palette = UiTheme.active().isDark() ? DARK_COLORS : LIGHT_COLORS;
        return palette.getOrDefault(type, Color.GRAY);
    }

    public static Color outlineFor(TetrominoType type) {
        return outlineFor(colorFor(type));
    }

    static Color outlineFor(Color color) {
        UiTheme theme = UiTheme.active();
        Color anchor = theme.isDark() ? theme.boardBackground() : theme.boardGrid();
        double anchorWeight = theme.isDark() ? 0.34 : 0.32;
        return mix(color, anchor, anchorWeight);
    }

    private static Color mix(Color a, Color b, double bWeight) {
        double aWeight = 1.0 - bWeight;
        int red = clamp((int) Math.round((a.getRed() * aWeight) + (b.getRed() * bWeight)));
        int green = clamp((int) Math.round((a.getGreen() * aWeight) + (b.getGreen() * bWeight)));
        int blue = clamp((int) Math.round((a.getBlue() * aWeight) + (b.getBlue() * bWeight)));
        return new Color(red, green, blue);
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
