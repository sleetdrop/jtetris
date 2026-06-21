package net.vetcafe.jtetris.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.EventQueue;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.LongSupplier;

public final class EdtWatchdog implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger("net.vetcafe.jtetris.edt");

    private final long thresholdMs;
    private final LongSupplier nowMs;
    private final Consumer<Runnable> edtDispatcher;
    private final WarningSink warningSink;
    private final ScheduledExecutorService scheduler;

    private volatile long pendingSinceMs = -1;
    private volatile boolean warned;
    private volatile boolean running = true;
    private volatile Thread edtThread;

    EdtWatchdog(
            long thresholdMs,
            LongSupplier nowMs,
            Consumer<Runnable> edtDispatcher,
            WarningSink warningSink
    ) {
        this(thresholdMs, nowMs, edtDispatcher, warningSink, null);
    }

    private EdtWatchdog(
            long thresholdMs,
            LongSupplier nowMs,
            Consumer<Runnable> edtDispatcher,
            WarningSink warningSink,
            ScheduledExecutorService scheduler
    ) {
        this.thresholdMs = thresholdMs;
        this.nowMs = nowMs;
        this.edtDispatcher = edtDispatcher;
        this.warningSink = warningSink;
        this.scheduler = scheduler;
    }

    public static EdtWatchdog start(long thresholdMs) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "jtetris-edt-watchdog");
            thread.setDaemon(true);
            return thread;
        });
        EdtWatchdog watchdog = new EdtWatchdog(
                thresholdMs,
                () -> System.nanoTime() / 1_000_000L,
                EventQueue::invokeLater,
                (delay, stack) -> LOGGER.warn(
                        "event=edt_delay delayMs={} thresholdMs={} edtStack={}",
                        delay,
                        thresholdMs,
                        Arrays.toString(stack)
                ),
                scheduler
        );
        long interval = Math.max(100, thresholdMs / 2);
        scheduler.scheduleAtFixedRate(watchdog::probe, interval, interval, TimeUnit.MILLISECONDS);
        return watchdog;
    }

    void probe() {
        if (!running) {
            return;
        }
        long now = nowMs.getAsLong();
        long pending = pendingSinceMs;
        if (pending >= 0) {
            long delay = Math.max(0, now - pending);
            if (delay >= thresholdMs && !warned) {
                warned = true;
                Thread thread = edtThread;
                warningSink.warn(
                        delay,
                        thread == null ? new StackTraceElement[0] : thread.getStackTrace()
                );
            }
            return;
        }

        pendingSinceMs = now;
        edtDispatcher.accept(() -> {
            edtThread = Thread.currentThread();
            pendingSinceMs = -1;
            warned = false;
        });
    }

    boolean isRunning() {
        return running;
    }

    @Override
    public void close() {
        running = false;
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    @FunctionalInterface
    interface WarningSink {
        void warn(long delayMs, StackTraceElement[] stack);
    }
}
