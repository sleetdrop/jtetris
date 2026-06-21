package net.vetcafe.jtetris.ui;

import net.vetcafe.jtetris.logging.InputLog;
import net.vetcafe.jtetris.model.Board;

import java.util.Objects;
import java.util.function.LongSupplier;

final class GameplayInputController {
    private final Board board;
    private final InputRepeater horizontalRepeater;
    private final SoftDropRepeater softDropRepeater;
    private final LongSupplier nowMs;
    private boolean leftHeld;
    private boolean rightHeld;
    private boolean softDropHeld;
    private long leftPressedAt;
    private long rightPressedAt;
    private long softDropPressedAt;

    GameplayInputController(
            Board board,
            long dasMs,
            long arrMs,
            long softDropRepeatMs,
            LongSupplier nowMs
    ) {
        if (dasMs <= 0 || arrMs <= 0 || softDropRepeatMs <= 0) {
            throw new IllegalArgumentException("input timing values must be positive");
        }
        this.board = Objects.requireNonNull(board, "board");
        this.horizontalRepeater = new InputRepeater(dasMs, arrMs);
        this.softDropRepeater = new SoftDropRepeater(softDropRepeatMs);
        this.nowMs = Objects.requireNonNull(nowMs, "nowMs");
    }

    boolean pressLeft() {
        long now = nowMs.getAsLong();
        if (!leftHeld) {
            leftHeld = true;
            leftPressedAt = now;
        }
        return applyAndLogHorizontal("pressLeft", now, horizontalRepeater.pressLeft(now), -1);
    }

    boolean releaseLeft() {
        long now = nowMs.getAsLong();
        long holdMs = leftHeld ? Math.max(0, now - leftPressedAt) : -1;
        leftHeld = false;
        return applyAndLogHorizontal("releaseLeft", now, horizontalRepeater.releaseLeft(now), holdMs);
    }

    boolean pressRight() {
        long now = nowMs.getAsLong();
        if (!rightHeld) {
            rightHeld = true;
            rightPressedAt = now;
        }
        return applyAndLogHorizontal("pressRight", now, horizontalRepeater.pressRight(now), -1);
    }

    boolean releaseRight() {
        long now = nowMs.getAsLong();
        long holdMs = rightHeld ? Math.max(0, now - rightPressedAt) : -1;
        rightHeld = false;
        return applyAndLogHorizontal("releaseRight", now, horizontalRepeater.releaseRight(now), holdMs);
    }

    boolean pressSoftDrop() {
        long now = nowMs.getAsLong();
        if (!softDropHeld) {
            softDropHeld = true;
            softDropPressedAt = now;
        }
        return applyAndLogSoftDrop("pressSoftDrop", now, softDropRepeater.press(now), -1);
    }

    void releaseSoftDrop() {
        long now = nowMs.getAsLong();
        long holdMs = softDropHeld ? Math.max(0, now - softDropPressedAt) : -1;
        int beforeX = currentX();
        int beforeY = currentY();
        softDropHeld = false;
        softDropRepeater.release();
        InputLog.controllerOperation(
                "releaseSoftDrop",
                now,
                0,
                0,
                beforeX,
                beforeY,
                currentX(),
                currentY(),
                false,
                holdMs
        );
    }

    boolean poll() {
        long now = nowMs.getAsLong();
        int horizontalSteps = horizontalRepeater.poll(now);
        int softDropSteps = softDropRepeater.poll(now);
        int beforeX = currentX();
        int beforeY = currentY();
        boolean movedHorizontally = applyHorizontalSteps(horizontalSteps);
        boolean movedDown = applySoftDropSteps(softDropSteps);
        boolean changed = movedHorizontally || movedDown;
        if (horizontalSteps != 0 || softDropSteps != 0 || changed) {
            InputLog.controllerOperation(
                    "poll",
                    now,
                    horizontalSteps,
                    softDropSteps,
                    beforeX,
                    beforeY,
                    currentX(),
                    currentY(),
                    changed,
                    -1
            );
        }
        return changed;
    }

    boolean rotateClockwise() {
        return applyAndLogDiscrete("rotateClockwise", board::rotateCW);
    }

    boolean rotateCounterclockwise() {
        return applyAndLogDiscrete("rotateCounterclockwise", board::rotateCCW);
    }

    boolean hardDrop() {
        return applyAndLogDiscrete("hardDrop", board::hardDrop);
    }

    boolean hold() {
        return applyAndLogDiscrete("hold", board::hold);
    }

    void reset() {
        horizontalRepeater.reset();
        softDropRepeater.reset();
        leftHeld = false;
        rightHeld = false;
        softDropHeld = false;
        InputLog.controllerOperation(
                "reset",
                nowMs.getAsLong(),
                0,
                0,
                currentX(),
                currentY(),
                currentX(),
                currentY(),
                false,
                -1
        );
    }

    private boolean applyAndLogHorizontal(String operation, long now, int steps, long holdMs) {
        int beforeX = currentX();
        int beforeY = currentY();
        boolean changed = applyHorizontalSteps(steps);
        InputLog.controllerOperation(
                operation,
                now,
                steps,
                0,
                beforeX,
                beforeY,
                currentX(),
                currentY(),
                changed,
                holdMs
        );
        return changed;
    }

    private boolean applyAndLogSoftDrop(String operation, long now, int steps, long holdMs) {
        int beforeX = currentX();
        int beforeY = currentY();
        boolean changed = applySoftDropSteps(steps);
        InputLog.controllerOperation(
                operation,
                now,
                0,
                steps,
                beforeX,
                beforeY,
                currentX(),
                currentY(),
                changed,
                holdMs
        );
        return changed;
    }

    private boolean applyAndLogDiscrete(String operation, BooleanOperation action) {
        long now = nowMs.getAsLong();
        int beforeX = currentX();
        int beforeY = currentY();
        boolean changed = action.run();
        InputLog.controllerOperation(
                operation,
                now,
                0,
                0,
                beforeX,
                beforeY,
                currentX(),
                currentY(),
                changed,
                -1
        );
        return changed;
    }

    private boolean applyHorizontalSteps(int signedSteps) {
        if (signedSteps == 0) {
            return false;
        }
        int direction = signedSteps > 0 ? 1 : -1;
        int steps = Math.abs(signedSteps);
        boolean moved = false;
        for (int i = 0; i < steps; i++) {
            if (!board.move(direction, 0)) {
                break;
            }
            moved = true;
        }
        return moved;
    }

    private boolean applySoftDropSteps(int steps) {
        boolean moved = false;
        for (int i = 0; i < steps; i++) {
            if (!board.move(0, 1)) {
                break;
            }
            moved = true;
        }
        return moved;
    }

    private int currentX() {
        return board.getCurrent() == null ? -1 : board.getCurrent().getX();
    }

    private int currentY() {
        return board.getCurrent() == null ? -1 : board.getCurrent().getY();
    }

    @FunctionalInterface
    private interface BooleanOperation {
        boolean run();
    }
}
