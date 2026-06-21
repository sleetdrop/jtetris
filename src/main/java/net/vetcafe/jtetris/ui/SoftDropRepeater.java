package net.vetcafe.jtetris.ui;

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
        if (nowMs < nextRepeatAt) {
            return 0;
        }
        nextRepeatAt = nowMs + repeatMs;
        return 1;
    }

    public void reset() {
        held = false;
        nextRepeatAt = 0;
    }
}


