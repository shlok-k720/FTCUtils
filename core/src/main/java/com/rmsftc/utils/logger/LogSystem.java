package com.rmsftc.utils.logger;

/**
 * A sink for log events dispatched by {@link Logger}. Attach one or more
 * systems (telemetry, file, dashboard, ...) and every
 * {@link Logger#log(String, String)} call reaches all of them.
 */
@FunctionalInterface
public interface LogSystem {
    /**
     * Receives a log event.
     *
     * @param caption the event caption (a category string chosen by the caller)
     * @param data event-specific details
     * @param timeStampMs Unix epoch timestamp in milliseconds
     */
    void log(String caption, String data, double timeStampMs);
}
