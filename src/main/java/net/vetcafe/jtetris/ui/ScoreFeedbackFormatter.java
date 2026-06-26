package net.vetcafe.jtetris.ui;

final class ScoreFeedbackFormatter {
    private ScoreFeedbackFormatter() {}

    static String eventText(String raw) {
        if (raw == null || raw.isBlank() || "NONE".equals(raw) || "NO_CLEAR".equals(raw)) {
            return "";
        }
        String[] parts = raw.split("_");
        StringBuilder text = new StringBuilder();
        int index = 0;
        if (parts.length >= 2 && "LINE".equals(parts[0]) && "CLEAR".equals(parts[1])) {
            index = 3;
            text.append(lineName(parseInt(parts, 2)));
        } else if (parts.length >= 2 && "TSPIN".equals(parts[0])) {
            index = 2;
            text.append("T-Spin");
            String tspinLines = lineName(lineCountFor(parts[1]));
            if (!tspinLines.isEmpty()) {
                text.append(' ').append(tspinLines);
            }
        } else if ("TETRIS".equals(parts[0])) {
            index = 1;
            text.append("Tetris");
        } else {
            text.append(titleCase(raw.replace('_', ' ')));
            index = parts.length;
        }

        while (index < parts.length) {
            if ("B2B".equals(parts[index])) {
                text.append(" + Back-to-Back");
                index++;
            } else if ("COMBO".equals(parts[index]) && index + 1 < parts.length) {
                text.append(" + Combo x").append(parts[index + 1]);
                index += 2;
            } else {
                index++;
            }
        }
        return text.toString();
    }

    static String comboText(int comboStreak) {
        return comboStreak > 0 ? "Combo x" + comboStreak : "Combo -";
    }

    static String backToBackText(boolean active) {
        return active ? "B2B On" : "B2B Ready";
    }

    static boolean activeCombo(int comboStreak) {
        return comboStreak > 0;
    }

    static boolean activeBackToBack(boolean active) {
        return active;
    }

    private static int parseInt(String[] parts, int index) {
        if (index < 0 || index >= parts.length) {
            return 0;
        }
        try {
            return Integer.parseInt(parts[index]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static int lineCountFor(String word) {
        return switch (word) {
            case "SINGLE" -> 1;
            case "DOUBLE" -> 2;
            case "TRIPLE" -> 3;
            default -> 0;
        };
    }

    private static String lineName(int lines) {
        return switch (lines) {
            case 1 -> "Single";
            case 2 -> "Double";
            case 3 -> "Triple";
            case 4 -> "Tetris";
            default -> "";
        };
    }

    private static String titleCase(String value) {
        StringBuilder result = new StringBuilder(value.length());
        boolean nextUpper = true;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.isWhitespace(c)) {
                result.append(c);
                nextUpper = true;
            } else if (nextUpper) {
                result.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }
        return result.toString();
    }
}
