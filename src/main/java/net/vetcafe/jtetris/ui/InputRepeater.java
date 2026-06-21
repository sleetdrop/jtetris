package net.vetcafe.jtetris.ui;

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
        if (!leftHeld) {
            leftHeld = true;
            leftPressOrder = ++pressSequence;
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
            rightPressOrder = ++pressSequence;
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

        if (nowMs < nextRepeatAt) {
            return 0;
        }
        nextRepeatAt = nowMs + arrMs;
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
}


