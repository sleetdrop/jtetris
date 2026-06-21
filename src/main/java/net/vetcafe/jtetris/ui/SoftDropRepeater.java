package net.vetcafe.jtetris.ui;

import net.vetcafe.jtetris.logging.InputLog;

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
            trace("press", nowMs, 0, "duplicate-press");
            return 0;
        }
        held = true;
        nextRepeatAt = nowMs + repeatMs;
        trace("press", nowMs, 1, "press");
        return 1;
    }

    public void release() {
        held = false;
        trace("release", -1, 0, "release");
    }

    public int poll(long nowMs) {
        if (!held) {
            trace("poll", nowMs, 0, "idle");
            return 0;
        }
        if (nowMs < nextRepeatAt) {
            trace("poll", nowMs, 0, "before-deadline");
            return 0;
        }
        nextRepeatAt = nowMs + repeatMs;
        trace("poll", nowMs, 1, "repeat");
        return 1;
    }

    public void reset() {
        held = false;
        nextRepeatAt = 0;
        trace("reset", -1, 0, "reset");
    }

    private void trace(String event, long nowMs, int step, String reason) {
        InputLog.repeaterDecision(
                "softDrop",
                event,
                nowMs,
                held,
                false,
                held ? 1 : 0,
                nextRepeatAt,
                step,
                reason,
                0,
                0
        );
    }
}

