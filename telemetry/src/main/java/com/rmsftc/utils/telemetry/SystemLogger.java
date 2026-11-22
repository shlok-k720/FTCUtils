package com.rmsftc.utils.telemetry;

/** A {@link Logger} that prints each message to standard output. */
public class SystemLogger implements Logger {

    @Override
    public void log(String message) {
        System.out.println(message);
    }
}
