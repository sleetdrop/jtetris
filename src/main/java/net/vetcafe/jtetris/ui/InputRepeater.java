package net.vetcafe.jtetris.ui;

import net.vetcafe.jtetris.logging.InputLog;

/**
 * Handles deterministic horizontal key repeat (DAS/ARR) independent from OS keyboard repeat settings.
 */
public class InputRepeater {
    private final long dasMs;
    private final long arrMs;

    private boolean leftHeld;
    private boolean rightHeld;
    private long pressSequence;
    private long leftPressOrder;
    private long rightPressOrder;

    private int activeDirection; // -1 left, 0 idle, 1 right
    private long nextRepeatAt;

    public InputRepeater(long dasMs, long arrMs) {
        this.dasMs = dasMs;
        this.arrMs = arrMs;
    }

    public int pressLeft(long nowMs) {
        boolean duplicate = leftHeld;
        if (!leftHeld) {
            leftHeld = true;
            leftPressOrder = ++pressSequence;
        }
        int step = syncDirectionAndMaybeStep(nowMs);
        trace("pressLeft", nowMs, step, duplicate ? "duplicate-press" : "press");
        return step;
    }

    public int releaseLeft(long nowMs) {
        leftHeld = false;
        int step = syncDirectionAndMaybeStep(nowMs);
        trace("releaseLeft", nowMs, step, "release");
        return step;
    }

    public int pressRight(long nowMs) {
        boolean duplicate = rightHeld;
        if (!rightHeld) {
            rightHeld = true;
            rightPressOrder = ++pressSequence;
        }
        int step = syncDirectionAndMaybeStep(nowMs);
        trace("pressRight", nowMs, step, duplicate ? "duplicate-press" : "press");
        return step;
    }

    public int releaseRight(long nowMs) {
        rightHeld = false;
        int step = syncDirectionAndMaybeStep(nowMs);
        trace("releaseRight", nowMs, step, "release");
        return step;
    }

    public int poll(long nowMs) {
        int desired = desiredDirection();
        if (desired != activeDirection) {
            activeDirection = desired;
            if (activeDirection == 0) {
                trace("poll", nowMs, 0, "idle");
                return 0;
            }
            nextRepeatAt = nowMs + dasMs;
            trace("poll", nowMs, activeDirection, "direction-change");
            return activeDirection;
        }

        if (activeDirection == 0) {
            trace("poll", nowMs, 0, "idle");
            return 0;
        }

        if (nowMs < nextRepeatAt) {
            trace("poll", nowMs, 0, "before-deadline");
            return 0;
        }
        nextRepeatAt = nowMs + arrMs;
        trace("poll", nowMs, activeDirection, "repeat");
        return activeDirection;
    }

    public void reset() {
        leftHeld = false;
        rightHeld = false;
        activeDirection = 0;
        nextRepeatAt = 0;
        pressSequence = 0;
        leftPressOrder = 0;
        rightPressOrder = 0;
        trace("reset", -1, 0, "reset");
    }

    private int syncDirectionAndMaybeStep(long nowMs) {
        int desired = desiredDirection();
        if (desired == activeDirection) {
            return 0;
        }
        activeDirection = desired;
        if (activeDirection == 0) {
            return 0;
        }
        nextRepeatAt = nowMs + dasMs;
        return activeDirection;
    }

    private int desiredDirection() {
        if (leftHeld && rightHeld) {
            return leftPressOrder > rightPressOrder ? -1 : 1;
        }
        if (leftHeld) {
            return -1;
        }
        if (rightHeld) {
            return 1;
        }
        return 0;
    }

    private void trace(String event, long nowMs, int step, String reason) {
        InputLog.repeaterDecision(
                "horizontal",
                event,
                nowMs,
                leftHeld,
                rightHeld,
                activeDirection,
                nextRepeatAt,
                step,
                reason,
                leftPressOrder,
                rightPressOrder);
    }
}
