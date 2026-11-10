package com.rmsftc.utils.control;

import com.rmsftc.utils.config.Configuration;
import com.rmsftc.utils.config.Configurator;
import com.rmsftc.utils.config.vars.Var;

/**
 * Configuration for a {@link Limiter}: how fast the output may move
 * ({@code maxRate}, units per second) and the allowed output range
 * ([{@code minOutput}, {@code maxOutput}]).
 *
 * <p>Defaults: unlimited rate, output in [-1, 1] (motor-power range).
 *
 * <pre>
 * LimiterConfig config = new LimiterConfig(c -&gt; {
 *     c.maxRate.set(2.0);
 *     c.maxOutput.set(0.8);
 * });
 * Limiter limiter = config.limiter();
 * </pre>
 *
 * <p>Note: the fields are assigned in {@link #defaults()}, not at
 * declaration — defaults() runs from the superclass constructor, before
 * field initializers run, so declaring them with initializers would leave
 * them null when defaults() executes.
 */
public class LimiterConfig extends Configuration<LimiterConfig> {
    public Var<Double> maxRate;
    public Var<Double> minOutput;
    public Var<Double> maxOutput;

    public LimiterConfig() {
        this(config -> {});
    }

    public LimiterConfig(Configurator<LimiterConfig> configurator) {
        super(configurator);
    }

    @Override
    public void defaults() {
        maxRate = new Var<>(Double.POSITIVE_INFINITY);
        minOutput = new Var<>(-1.0);
        maxOutput = new Var<>(1.0);
    }

    /** A limiter using this config. */
    public Limiter limiter() {
        return new Limiter(this);
    }
}
