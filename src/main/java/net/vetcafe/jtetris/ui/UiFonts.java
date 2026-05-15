package net.vetcafe.jtetris.ui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

public final class UiFonts {
    private static final String REGULAR_PATH = "/fonts/Inter-Regular.ttf";
    private static final String SEMIBOLD_PATH = "/fonts/Inter-SemiBold.ttf";

    private static volatile boolean loaded;
    private static volatile Font regularBase = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private static volatile Font semiboldBase = new Font(Font.SANS_SERIF, Font.BOLD, 14);

    private UiFonts() {
    }

    public static Font regular(float size) {
        ensureLoaded();
        return regularBase.deriveFont(Font.PLAIN, size);
    }

    public static Font semibold(float size) {
        ensureLoaded();
        return semiboldBase.deriveFont(Font.BOLD, size);
    }

    public static Font mono(float size) {
        return new Font(Font.MONOSPACED, Font.PLAIN, Math.round(size));
    }

    private static void ensureLoaded() {
        if (loaded) {
            return;
        }
        synchronized (UiFonts.class) {
            if (loaded) {
                return;
            }
            Font regular = loadFont(REGULAR_PATH);
            Font semibold = loadFont(SEMIBOLD_PATH);
            if (regular != null) {
                regularBase = regular;
            }
            if (semibold != null) {
                semiboldBase = semibold;
            }
            loaded = true;
        }
    }

    private static Font loadFont(String path) {
        try (InputStream input = UiFonts.class.getResourceAsStream(path)) {
            if (input == null) {
                return null;
            }
            return Font.createFont(Font.TRUETYPE_FONT, input);
        } catch (FontFormatException | IOException ignored) {
            return null;
        }
    }
}

