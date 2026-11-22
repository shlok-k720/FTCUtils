package com.rmsftc.utils.ftc.telemetry;

import com.rmsftc.utils.telemetry.html.AdvancedLogger;
import com.rmsftc.utils.telemetry.html.Html;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.Objects;

/**
 * A telemetry-module {@link AdvancedLogger} backed by an FTC
 * {@link Telemetry}: plain messages become telemetry lines, and HTML is
 * passed through as-is (FTC Dashboard renders it).
 *
 * <p>For FTC Dashboard, construct it with the dashboard's telemetry from
 * robot code (this library does not depend on the Dashboard itself):
 * <pre>
 * Logger logger = new TelemetryLogger(FtcDashboard.getInstance().getTelemetry());
 * </pre>
 */
public class TelemetryLogger implements AdvancedLogger {
    private final Telemetry telemetry;

    public TelemetryLogger(Telemetry telemetry) {
        this.telemetry = Objects.requireNonNull(telemetry, "telemetry");
    }

    @Override
    public void log(String message) {
        telemetry.addLine(message);
    }

    @Override
    public void logHTML(String html) {
        telemetry.addLine(html);
    }

    /** Convenience: log a bold HTML line. */
    public void logBold(String message) {
        logHTML(Html.bold(message));
    }
}
