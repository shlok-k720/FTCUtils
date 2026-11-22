package com.rmsftc.utils.telemetry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link Logger} that collects messages in memory — for tests, and for
 * code that wants to render its log somewhere else later.
 */
public class StringLogger implements Logger {
    private final List<String> lines = new ArrayList<>();

    @Override
    public void log(String message) {
        lines.add(message);
    }

    /** Every message logged so far, in order. */
    public List<String> getLines() {
        return Collections.unmodifiableList(new ArrayList<>(lines));
    }

    public void clear() {
        lines.clear();
    }

    /** All messages joined with newlines. */
    @Override
    public String toString() {
        return String.join(Strings.newline(), lines);
    }
}
