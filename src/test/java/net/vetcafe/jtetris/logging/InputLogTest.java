package net.vetcafe.jtetris.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class InputLogTest {
    private final Logger logger = (Logger) LoggerFactory.getLogger(InputLog.LOGGER_NAME);
    private final Level originalLevel = logger.getLevel();
    private ListAppender<ILoggingEvent> attachedAppender;

    @AfterEach
    void restoreLevel() {
        if (attachedAppender != null) {
            logger.detachAppender(attachedAppender);
            attachedAppender.stop();
        }
        logger.setLevel(originalLevel);
    }

    @Test
    void debugEventContainsStableSwingAndControllerFields() {
        logger.setLevel(Level.DEBUG);
        ListAppender<ILoggingEvent> appender = attach();

        InputLog.swingAction("rightPressed", 100, true, 3, 0, 4, 0, true);
        InputLog.controllerOperation("pressRight", 100, 1, 0, 3, 0, 4, 0, true, -1);

        assertEquals(2, appender.list.size());
        assertTrue(appender.list.get(0).getFormattedMessage().contains("boundary=swing"));
        assertTrue(appender.list.get(0).getFormattedMessage().contains("action=rightPressed"));
        assertTrue(appender.list.get(0).getFormattedMessage().contains("beforeX=3"));
        assertTrue(appender.list.get(0).getFormattedMessage().contains("afterX=4"));
        assertTrue(appender.list.get(1).getFormattedMessage().contains("boundary=controller"));
        assertTrue(appender.list.get(1).getFormattedMessage().contains("stepX=1"));
    }

    @Test
    void traceRepeaterDetailsAreSuppressedAtDebug() {
        logger.setLevel(Level.DEBUG);
        ListAppender<ILoggingEvent> appender = attach();

        InputLog.repeaterDecision("horizontal", "poll", 100, true, false, -1, 130, 0, "before-das", 1, 0);

        assertTrue(appender.list.isEmpty());
    }

    @Test
    void traceRepeaterDetailsContainDecisionState() {
        logger.setLevel(Level.TRACE);
        ListAppender<ILoggingEvent> appender = attach();

        InputLog.repeaterDecision("horizontal", "poll", 140, true, false, -1, 180, -1, "repeat", 1, 0);

        assertEquals(1, appender.list.size());
        String message = appender.list.get(0).getFormattedMessage();
        assertTrue(message.contains("boundary=repeater"));
        assertTrue(message.contains("reason=repeat"));
        assertTrue(message.contains("step=-1"));
        assertTrue(message.contains("nextRepeatAt=180"));
    }

    private ListAppender<ILoggingEvent> attach() {
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        attachedAppender = appender;
        return appender;
    }
}
