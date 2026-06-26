package net.vetcafe.jtetris.ui;

import java.awt.Color;
import java.util.Locale;
import javax.swing.LookAndFeel;
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
            new Color(238, 241, 244),
            new Color(247, 248, 250),
            new Color(224, 229, 235),
            new Color(252, 252, 253),
            new Color(36, 42, 52),
            new Color(96, 105, 117),
            new Color(60, 111, 182),
            new Color(242, 244, 247),
            new Color(255, 255, 255),
            new Color(198, 206, 216),
            new Color(231, 235, 240),
            new Color(50, 58, 70),
            new Color(213, 219, 227));

    private static final UiTheme DARK = new UiTheme(
            Mode.DARK,
            true,
            new Color(17, 21, 29),
            new Color(23, 27, 38),
            new Color(36, 42, 56),
            new Color(28, 33, 46),
            new Color(241, 243, 247),
            new Color(166, 174, 188),
            new Color(137, 167, 232),
            new Color(29, 35, 49),
            new Color(21, 26, 37),
            new Color(73, 84, 108),
            new Color(34, 40, 55),
            new Color(214, 219, 229),
            new Color(51, 59, 78));

    private static volatile Mode activeMode = modeOverride();
    private static volatile boolean systemDark = isLikelyDarkSystemTheme();
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
            Color tableGrid) {
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

    public static Mode activeMode() {
        return activeMode;
    }

    public static void setActiveMode(Mode mode) {
        activeMode = mode == null ? Mode.AUTO : mode;
        active = themeFor(activeMode);
    }

    public static void refreshFromSystem() {
        if (!isFlatLafActive()) {
            systemDark = isLikelyDarkSystemTheme();
        }
        active = themeFor(activeMode);
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
        return themeFor(activeMode);
    }

    private static UiTheme themeFor(Mode mode) {
        return switch (mode) {
            case LIGHT -> LIGHT;
            case DARK -> DARK;
            case AUTO -> systemDark ? DARK : LIGHT;
        };
    }

    private static boolean isFlatLafActive() {
        LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
        return lookAndFeel != null && lookAndFeel.getClass().getName().startsWith("com.formdev.flatlaf.");
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

    public Color overlaySurface() {
        return dark ? mix(dialogSurface, frameBackground, 0.35) : mix(dialogSurface, frameBackground, 0.25);
    }

    public Color dialogBorder() {
        return dialogBorder;
    }

    public Color overlayBorder() {
        return dark ? mix(dialogBorder, accent, 0.45) : mix(dialogBorder, accent, 0.28);
    }

    public Color overlayBackground() {
        return dark ? mix(frameBackground, boardBackground, 0.25) : mix(frameBackground, boardBackground, 0.30);
    }

    public Color overlayText() {
        return textPrimary;
    }

    public Color overlayAccent() {
        return dark ? mix(accent, textPrimary, 0.18) : mix(accent, textPrimary, 0.10);
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
