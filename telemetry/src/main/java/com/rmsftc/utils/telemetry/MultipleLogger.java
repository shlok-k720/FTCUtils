package com.rmsftc.utils.telemetry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/** A {@link Logger} that forwards every message to several loggers. */
public class MultipleLogger implements Logger {
    private final List<Logger> loggers = new ArrayList<>();

    public MultipleLogger(Logger... loggers) {
        for (Logger logger : loggers) {
            add(logger);
        }
    }

    public MultipleLogger add(Logger logger) {
        loggers.add(Objects.requireNonNull(logger, "logger"));
        return this;
    }

    @Override
    public void log(String message) {
        for (Logger logger : loggers) {
            logger.log(message);
        }
    }

    /** Convenience factory, mirroring the constructor. */
    public static MultipleLogger of(Logger... loggers) {
        return new MultipleLogger(loggers);
    }

    @Override
    public String toString() {
        return "MultipleLogger" + Arrays.toString(loggers.toArray());
    }
}
