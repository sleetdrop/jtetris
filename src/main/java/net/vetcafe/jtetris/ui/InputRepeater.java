package net.vetcafe.jtetris.ui;

/**
 * Handles deterministic horizontal key repeat (DAS/ARR) independent from OS keyboard repeat settings.
 */
public class InputRepeater {
    private final long dasMs;
    private final long arrMs;

    private boolean leftHeld;
    private boolean rightHeld;
    private long leftPressedAt;
    private long rightPressedAt;

    private int activeDirection; // -1 left, 0 idle, 1 right
    private long nextRepeatAt;

    public InputRepeater(long dasMs, long arrMs) {
        this.dasMs = dasMs;
        this.arrMs = arrMs;
    }

    public int pressLeft(long nowMs) {
        if (!leftHeld) {
            leftHeld = true;
            leftPressedAt = nowMs;
        }
        return syncDirectionAndMaybeStep(nowMs);
    }

    public int releaseLeft(long nowMs) {
        leftHeld = false;
        return syncDirectionAndMaybeStep(nowMs);
    }

    public int pressRight(long nowMs) {
        if (!rightHeld) {
            rightHeld = true;
            rightPressedAt = nowMs;
        }
        return syncDirectionAndMaybeStep(nowMs);
    }

    public int releaseRight(long nowMs) {
        rightHeld = false;
        return syncDirectionAndMaybeStep(nowMs);
    }

    public int poll(long nowMs) {
        int desired = desiredDirection();
        if (desired != activeDirection) {
            activeDirection = desired;
            if (activeDirection == 0) {
                return 0;
            }
            nextRepeatAt = nowMs + dasMs;
            return activeDirection;
        }

        if (activeDirection == 0) {
            return 0;
        }

        int steps = 0;
        while (nowMs >= nextRepeatAt) {
            steps += activeDirection;
            nextRepeatAt += arrMs;
        }
        return steps;
    }

    public void reset() {
        leftHeld = false;
        rightHeld = false;
        activeDirection = 0;
        nextRepeatAt = 0;
        leftPressedAt = 0;
        rightPressedAt = 0;
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
            return leftPressedAt >= rightPressedAt ? -1 : 1;
        }
        if (leftHeld) {
            return -1;
        }
        if (rightHeld) {
            return 1;
        }
        return 0;
    }
}



