package net.vetcafe.jtetris.ui;

import java.awt.Color;
import java.util.Locale;
import javax.swing.UIManager;

public final class UiTheme {
    public enum Mode {
        AUTO,
        LIGHT,
        DARK
    }

    private static final UiTheme LIGHT = new UiTheme(
            Mode.LIGHT,
            false,
            new Color(243, 239, 232),
            new Color(231, 224, 213),
            new Color(132, 116, 89),
            new Color(250, 248, 243),
            new Color(44, 39, 31),
            new Color(104, 94, 78),
            new Color(146, 114, 59),
            new Color(236, 229, 217),
            new Color(230, 221, 206),
            new Color(142, 126, 99),
            new Color(230, 224, 216),
            new Color(78, 71, 58),
            new Color(204, 193, 174)
    );

    private static final UiTheme DARK = new UiTheme(
            Mode.DARK,
            true,
            new Color(15, 17, 30),
            new Color(22, 25, 40),
            new Color(42, 47, 66),
            new Color(26, 30, 48),
            new Color(235, 233, 227),
            new Color(180, 177, 168),
            new Color(185, 151, 92),
            new Color(31, 35, 55),
            new Color(23, 26, 42),
            new Color(76, 84, 112),
            new Color(35, 39, 59),
            new Color(189, 184, 171),
            new Color(50, 56, 80)
    );

    private static volatile UiTheme active = detectInitialTheme();

    private final Mode mode;
    private final boolean dark;
    private final Color frameBackground;
    private final Color boardBackground;
    private final Color boardGrid;
    private final Color sidePanelBackground;
    private final Color textPrimary;
    private final Color textMuted;
    private final Color accent;
    private final Color dialogBackground;
    private final Color dialogSurface;
    private final Color dialogBorder;
    private final Color tableHeaderBackground;
    private final Color tableHeaderText;
    private final Color tableGrid;

    private UiTheme(
            Mode mode,
            boolean dark,
            Color frameBackground,
            Color boardBackground,
            Color boardGrid,
            Color sidePanelBackground,
            Color textPrimary,
            Color textMuted,
            Color accent,
            Color dialogBackground,
            Color dialogSurface,
            Color dialogBorder,
            Color tableHeaderBackground,
            Color tableHeaderText,
            Color tableGrid
    ) {
        this.mode = mode;
        this.dark = dark;
        this.frameBackground = frameBackground;
        this.boardBackground = boardBackground;
        this.boardGrid = boardGrid;
        this.sidePanelBackground = sidePanelBackground;
        this.textPrimary = textPrimary;
        this.textMuted = textMuted;
        this.accent = accent;
        this.dialogBackground = dialogBackground;
        this.dialogSurface = dialogSurface;
        this.dialogBorder = dialogBorder;
        this.tableHeaderBackground = tableHeaderBackground;
        this.tableHeaderText = tableHeaderText;
        this.tableGrid = tableGrid;
    }

    public static UiTheme active() {
        return active;
    }

    public static void refreshFromSystem() {
        active = themeFor(modeOverride());
    }

    public static Mode modeOverride() {
        String value = System.getProperty("jtetris.theme", "auto").trim().toLowerCase(Locale.ROOT);
        return switch (value) {
            case "light" -> Mode.LIGHT;
            case "dark" -> Mode.DARK;
            default -> Mode.AUTO;
        };
    }

    private static UiTheme detectInitialTheme() {
        return themeFor(modeOverride());
    }

    private static UiTheme themeFor(Mode mode) {
        return switch (mode) {
            case LIGHT -> LIGHT;
            case DARK -> DARK;
            case AUTO -> isLikelyDarkSystemTheme() ? DARK : LIGHT;
        };
    }

    private static boolean isLikelyDarkSystemTheme() {
        Color panelColor = UIManager.getColor("Panel.background");
        if (panelColor == null) {
            panelColor = UIManager.getColor("control");
        }
        if (panelColor == null) {
            return false;
        }
        return relativeLuminance(panelColor) < 0.5;
    }

    private static double relativeLuminance(Color color) {
        return (0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue()) / 255.0;
    }

    public Mode mode() {
        return mode;
    }

    public boolean isDark() {
        return dark;
    }

    public Color frameBackground() {
        return frameBackground;
    }

    public Color boardBackground() {
        return boardBackground;
    }

    public Color boardGrid() {
        return boardGrid;
    }

    public Color sidePanelBackground() {
        return sidePanelBackground;
    }

    public Color textPrimary() {
        return textPrimary;
    }

    public Color textMuted() {
        return textMuted;
    }

    public Color accent() {
        return accent;
    }

    public Color dialogBackground() {
        return dialogBackground;
    }

    public Color dialogSurface() {
        return dialogSurface;
    }

    public Color dialogBorder() {
        return dialogBorder;
    }

    public Color tableHeaderBackground() {
        return tableHeaderBackground;
    }

    public Color tableHeaderText() {
        return tableHeaderText;
    }

    public Color tableGrid() {
        return tableGrid;
    }
}

