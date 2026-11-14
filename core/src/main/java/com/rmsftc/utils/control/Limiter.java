package com.rmsftc.utils.control;

import com.rmsftc.utils.math.MathUtils;

/**
 * A stateful output limiter: rate-limits how fast the output can move
 * toward the target, then clamps it to the configured output range.
 * Reads its limits live from a {@link LimiterConfig}, so tuning the config
 * at runtime retunes the limiter.
 *
 * <p>The time step is passed in explicitly ({@link #limit(double, double)})
 * so the limiter stays deterministic and testable — pass the loop's dt
 * (seconds) from the OpMode.
 *
 * <pre>
 * LimiterConfig config = new LimiterConfig(c -&gt; c.maxRate.set(2.0));
 * Limiter limiter = config.limiter();
 * // in the loop:
 * double power = limiter.limit(targetPower, dtSeconds);
 * </pre>
 */
public class Limiter {
    private final LimiterConfig config;
    private double previousOutput;

    public Limiter(LimiterConfig config) {
        this(config, 0.0);
    }

    public Limiter(LimiterConfig config, double initialOutput) {
        this.config = config;
        this.previousOutput = initialOutput;
    }

    /**
     * Limits {@code target} for this step: moves from the previous output
     * toward it by at most {@code maxRate * dt}, then clamps to
     * [{@code minOutput}, {@code maxOutput}].
     */
    public double limit(double target, double dt) {
        double rate = config.maxRate.get();
        // An infinite rate means "no rate limit": it allows any change,
        // whatever dt is. Without this, rate * 0.0 is NaN when dt is 0
        // (e.g. the first loop tick), and the output comes out NaN.
        double maxChange = Double.isInfinite(rate)
                ? Double.POSITIVE_INFINITY : rate * dt;
        double stepped = MathUtils.rateLimit(previousOutput, target, maxChange);
        double limited = MathUtils.clamp(stepped, config.minOutput.get(), config.maxOutput.get());
        previousOutput = limited;
        return limited;
    }

    /** The most recent limited output. */
    public double get() {
        return previousOutput;
    }

    /** Resets the output to 0. */
    public void reset() {
        reset(0.0);
    }

    /** Resets the output to {@code output}. */
    public void reset(double output) {
        previousOutput = output;
    }
}
