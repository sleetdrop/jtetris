package net.vetcafe.jtetris.ui;

import net.vetcafe.jtetris.model.Board;

import java.util.Objects;
import java.util.function.LongSupplier;

final class GameplayInputController {
    private final Board board;
    private final InputRepeater horizontalRepeater;
    private final SoftDropRepeater softDropRepeater;
    private final LongSupplier nowMs;

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
        return applyHorizontalSteps(horizontalRepeater.pressLeft(nowMs.getAsLong()));
    }

    boolean releaseLeft() {
        return applyHorizontalSteps(horizontalRepeater.releaseLeft(nowMs.getAsLong()));
    }

    boolean pressRight() {
        return applyHorizontalSteps(horizontalRepeater.pressRight(nowMs.getAsLong()));
    }

    boolean releaseRight() {
        return applyHorizontalSteps(horizontalRepeater.releaseRight(nowMs.getAsLong()));
    }

    boolean pressSoftDrop() {
        return applySoftDropSteps(softDropRepeater.press(nowMs.getAsLong()));
    }

    void releaseSoftDrop() {
        softDropRepeater.release();
    }

    boolean poll() {
        long now = nowMs.getAsLong();
        boolean movedHorizontally = applyHorizontalSteps(horizontalRepeater.poll(now));
        boolean movedDown = applySoftDropSteps(softDropRepeater.poll(now));
        return movedHorizontally || movedDown;
    }

    boolean rotateClockwise() {
        return board.rotateCW();
    }

    boolean rotateCounterclockwise() {
        return board.rotateCCW();
    }

    boolean hardDrop() {
        return board.hardDrop();
    }

    boolean hold() {
        return board.hold();
    }

    void reset() {
        horizontalRepeater.reset();
        softDropRepeater.reset();
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
}
