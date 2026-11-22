package com.rmsftc.utils.telemetry.html;

import com.rmsftc.utils.telemetry.Logger;

/**
 * A {@link Logger} that can also emit raw HTML — for sinks that render it,
 * like FTC Dashboard telemetry. Plain {@link #log(String)} messages are
 * treated as text (implementations escape them); {@link #logHTML(String)}
 * passes markup through as-is.
 */
public interface AdvancedLogger extends Logger {
    void logHTML(String html);
}
