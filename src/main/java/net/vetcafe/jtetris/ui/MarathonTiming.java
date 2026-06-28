package net.vetcafe.jtetris.ui;

final class MarathonTiming {
    private static final int LOCK_DELAY_MS = 500;
    private static final int[] GRAVITY_DELAYS_MS = {
        700, 630, 560, 490, 420, 360, 300, 250, 210, 180,
        150, 130, 110, 95, 80, 70, 60, 55, 50, 50
    };

    private MarathonTiming() {}

    static int gravityDelayMs(int level) {
        int index = Math.max(1, level) - 1;
        if (index >= GRAVITY_DELAYS_MS.length) {
            return GRAVITY_DELAYS_MS[GRAVITY_DELAYS_MS.length - 1];
        }
        return GRAVITY_DELAYS_MS[index];
    }

    static int lockDelayMs() {
        return LOCK_DELAY_MS;
    }
}
