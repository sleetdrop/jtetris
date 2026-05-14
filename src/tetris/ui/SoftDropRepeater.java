package tetris.ui;

/**
 * Deterministic soft-drop repeater, independent from OS key repeat settings.
 */
public class SoftDropRepeater {
    private final long repeatMs;
    private boolean held;
    private long nextRepeatAt;

    public SoftDropRepeater(long repeatMs) {
        this.repeatMs = repeatMs;
    }

    public int press(long nowMs) {
        if (held) {
            return 0;
        }
        held = true;
        nextRepeatAt = nowMs + repeatMs;
        return 1;
    }

    public void release() {
        held = false;
    }

    public int poll(long nowMs) {
        if (!held) {
            return 0;
        }
        int steps = 0;
        while (nowMs >= nextRepeatAt) {
            steps++;
            nextRepeatAt += repeatMs;
        }
        return steps;
    }

    public void reset() {
        held = false;
        nextRepeatAt = 0;
    }
}

