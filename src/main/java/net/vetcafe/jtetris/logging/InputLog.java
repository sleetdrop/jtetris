package net.vetcafe.jtetris.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InputLog {
    public static final String LOGGER_NAME = "net.vetcafe.jtetris.input";
    private static final Logger LOGGER = LoggerFactory.getLogger(LOGGER_NAME);

    private InputLog() {}

    public static void swingAction(
            String action,
            long nowMs,
            boolean eligible,
            int beforeX,
            int beforeY,
            int afterX,
            int afterY,
            boolean changed) {
        if (!LOGGER.isDebugEnabled()) {
            return;
        }
        LOGGER.debug(
                "boundary=swing action={} nowMs={} eligible={} beforeX={} beforeY={} "
                        + "afterX={} afterY={} changed={} thread={}",
                action,
                nowMs,
                eligible,
                beforeX,
                beforeY,
                afterX,
                afterY,
                changed,
                Thread.currentThread().getName());
    }

    public static void controllerOperation(
            String operation,
            long nowMs,
            int stepX,
            int stepY,
            int beforeX,
            int beforeY,
            int afterX,
            int afterY,
            boolean changed,
            long holdMs) {
        if (!LOGGER.isDebugEnabled()) {
            return;
        }
        LOGGER.debug(
                "boundary=controller operation={} nowMs={} stepX={} stepY={} beforeX={} beforeY={} "
                        + "afterX={} afterY={} changed={} holdMs={}",
                operation,
                nowMs,
                stepX,
                stepY,
                beforeX,
                beforeY,
                afterX,
                afterY,
                changed,
                holdMs);
    }

    public static void repeaterDecision(
            String component,
            String event,
            long nowMs,
            boolean leftHeld,
            boolean rightHeld,
            int activeDirection,
            long nextRepeatAt,
            int step,
            String reason,
            long leftPressOrder,
            long rightPressOrder) {
        if (!LOGGER.isTraceEnabled()) {
            return;
        }
        LOGGER.trace(
                "boundary=repeater component={} event={} nowMs={} leftHeld={} rightHeld={} "
                        + "activeDirection={} nextRepeatAt={} step={} reason={} leftPressOrder={} rightPressOrder={}",
                component,
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
