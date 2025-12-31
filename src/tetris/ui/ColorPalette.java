package tetris.ui;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;
import tetris.model.TetrominoType;

public final class ColorPalette {
    private static final Map<TetrominoType, Color> COLORS = new EnumMap<>(TetrominoType.class);

    static {
        COLORS.put(TetrominoType.I, new Color(77, 208, 225));      // cyan teal
        COLORS.put(TetrominoType.O, new Color(255, 209, 102));     // warm amber
        COLORS.put(TetrominoType.T, new Color(199, 146, 234));     // soft purple
        COLORS.put(TetrominoType.S, new Color(127, 209, 185));     // mint green
        COLORS.put(TetrominoType.Z, new Color(239, 83, 80));       // modern red
        COLORS.put(TetrominoType.J, new Color(92, 107, 192));      // indigo
        COLORS.put(TetrominoType.L, new Color(255, 138, 101));     // coral
    }

    private ColorPalette() {}

    public static Color colorFor(TetrominoType type) {
        return COLORS.getOrDefault(type, Color.GRAY);
    }
}
