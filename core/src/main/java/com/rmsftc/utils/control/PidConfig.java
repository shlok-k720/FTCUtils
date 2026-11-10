package com.rmsftc.utils.control;

import com.rmsftc.utils.config.Configuration;
import com.rmsftc.utils.config.Configurator;
import com.rmsftc.utils.config.vars.Var;

/**
 * Configuration for a PIDF controller: the four gains as tunable
 * {@link Var}s. The controller built from this config reads the gains
 * live, so changing a gain at runtime retunes the controller.
 *
 * <p>Defaults: all gains 0.
 *
 * <pre>
 * PidConfig gains = new PidConfig(c -&gt; {
 *     c.p.set(0.1);
 *     c.d.set(0.01);
 * });
 * Controller pid = gains.controller();
 * </pre>
 *
 * <p>Note: the fields are assigned in {@link #defaults()}, not at
 * declaration — defaults() runs from the superclass constructor, before
 * field initializers run, so declaring them with initializers would leave
 * them null when defaults() executes.
 */
public class PidConfig extends Configuration<PidConfig> {
    public Var<Double> p;
    public Var<Double> i;
    public Var<Double> d;
    public Var<Double> f;

    public PidConfig() {
        this(config -> {});
    }

    public PidConfig(Configurator<PidConfig> configurator) {
        super(configurator);
    }

    @Override
    public void defaults() {
        p = new Var<>(0.0);
        i = new Var<>(0.0);
        d = new Var<>(0.0);
        f = new Var<>(0.0);
    }

    /** A PIDF controller using these gains (read live). */
    public Controller controller() {
        return Controller.pidf(p::get, i::get, d::get, f::get);
    }
}
