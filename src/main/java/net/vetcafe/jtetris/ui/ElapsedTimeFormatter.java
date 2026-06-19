package net.vetcafe.jtetris.ui;

final class ElapsedTimeFormatter {
    private static final long MILLIS_PER_SECOND = 1_000L;
    private static final long SECONDS_PER_MINUTE = 60L;
    private static final long SECONDS_PER_HOUR = 3_600L;

    private ElapsedTimeFormatter() {
    }

    static String format(long elapsedMillis) {
        long totalSeconds = Math.max(0, elapsedMillis) / MILLIS_PER_SECOND;
        long hours = totalSeconds / SECONDS_PER_HOUR;
        long minutes = (totalSeconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE;
        long seconds = totalSeconds % SECONDS_PER_MINUTE;
        return hours > 0
                ? "%d:%02d:%02d".formatted(hours, minutes, seconds)
                : "%02d:%02d".formatted(minutes, seconds);
    }
}
