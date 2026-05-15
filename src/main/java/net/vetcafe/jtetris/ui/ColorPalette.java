package net.vetcafe.jtetris.ui;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;
import net.vetcafe.jtetris.model.TetrominoType;

public final class ColorPalette {
    private static final Map<TetrominoType, Color> DARK_COLORS = new EnumMap<>(TetrominoType.class);
    private static final Map<TetrominoType, Color> LIGHT_COLORS = new EnumMap<>(TetrominoType.class);

    static {
        DARK_COLORS.put(TetrominoType.I, new Color(107, 206, 216));
        DARK_COLORS.put(TetrominoType.O, new Color(236, 196, 109));
        DARK_COLORS.put(TetrominoType.T, new Color(178, 146, 213));
        DARK_COLORS.put(TetrominoType.S, new Color(126, 191, 150));
        DARK_COLORS.put(TetrominoType.Z, new Color(212, 117, 116));
        DARK_COLORS.put(TetrominoType.J, new Color(121, 142, 210));
        DARK_COLORS.put(TetrominoType.L, new Color(228, 155, 107));

        LIGHT_COLORS.put(TetrominoType.I, new Color(71, 148, 165));
        LIGHT_COLORS.put(TetrominoType.O, new Color(182, 144, 60));
        LIGHT_COLORS.put(TetrominoType.T, new Color(121, 94, 165));
        LIGHT_COLORS.put(TetrominoType.S, new Color(80, 137, 95));
        LIGHT_COLORS.put(TetrominoType.Z, new Color(161, 84, 82));
        LIGHT_COLORS.put(TetrominoType.J, new Color(78, 96, 152));
        LIGHT_COLORS.put(TetrominoType.L, new Color(169, 108, 71));
    }

    private ColorPalette() {}

    public static Color colorFor(TetrominoType type) {
        Map<TetrominoType, Color> palette = UiTheme.active().isDark() ? DARK_COLORS : LIGHT_COLORS;
        return palette.getOrDefault(type, Color.GRAY);
    }
}


