package com.rmsftc.utils.logger;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * A static log-event bus: code anywhere calls {@link #log(String, String)}
 * with a caption and details, and every attached {@link LogSystem} receives
 * the event with a timestamp. When nothing is attached, logging is a no-op,
 * so hot paths (e.g. behavior-tree ticks) can log unconditionally.
 *
 * <p>Generic equivalent of BehaviorArchitectures' {@code util} Logger, kept
 * API-compatible (minus that library's own caption constants, which stay
 * with it) so that library can swap its {@code util} module for this one
 * later.
 */
public class Logger {
    private static final Set<LogSystem> loggers = new CopyOnWriteArraySet<>();

    public Logger() {}

    public static void reset() {
        loggers.clear();
    }

    public static void attachLogger(LogSystem... loggers) {
        Objects.requireNonNull(loggers, "loggers");
        for (LogSystem logger : loggers) {
            attachLogger(logger);
        }
    }

    public static void attachLogger(LogSystem logger) {
        loggers.add(Objects.requireNonNull(logger, "logger"));
    }

    public static void removeLogger(LogSystem logSystem) {
        loggers.remove(Objects.requireNonNull(logSystem, "logSystem"));
    }

    public static void log(String caption, String data) {
        Objects.requireNonNull(caption, "caption");
        Objects.requireNonNull(data, "data");
        // Nothing is listening: skip the timestamp and the iteration.
        // Every node tick logs, so this is on the hot path.
        if (loggers.isEmpty()) return;

        double timestampMs = System.currentTimeMillis();
        loggers.forEach(logger -> logger.log(caption, data, timestampMs));
    }
}
