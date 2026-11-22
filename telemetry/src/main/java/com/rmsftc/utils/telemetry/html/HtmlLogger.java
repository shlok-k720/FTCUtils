package com.rmsftc.utils.telemetry.html;

/**
 * An {@link AdvancedLogger} that accumulates HTML in a buffer: plain
 * {@link #log(String)} messages are escaped and followed by a line break,
 * {@link #logHTML(String)} markup is appended as-is. Read the result with
 * {@link #toString()} (e.g. to hand to FTC Dashboard telemetry) and reset
 * it with {@link #clear()}.
 */
public class HtmlLogger implements AdvancedLogger {
    private final StringBuilder buffer = new StringBuilder();

    @Override
    public void log(String message) {
        buffer.append(Html.line(message));
    }

    @Override
    public void logHTML(String html) {
        buffer.append(html);
    }

    public void clear() {
        buffer.setLength(0);
    }

    @Override
    public String toString() {
        return buffer.toString();
    }
}
