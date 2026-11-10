package com.rmsftc.utils.config.vars;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The global registry of variables: every named {@link Var} registers
 * itself here on construction, and {@link Var#at(Object)} registers its
 * {@link Modifier}. Unnamed vars are not registered. Ported from the
 * FTChimera team's vars system (without the dashboard registration, which
 * lives in the ftc module's {@code DashboardVars} because it needs the FTC
 * Dashboard classes).
 *
 * <p>The sets are synchronized: variables are usually created on one
 * thread and read from another (an OpMode, a dashboard callback).
 */
public class Variables {
    public static GarbageCollector garbageCollector = new GarbageCollector();
    public static Set<Var<?>> variables =
            Collections.synchronizedSet(new HashSet<Var<?>>());
    public static Set<Modifier<?>> modifiers =
            Collections.synchronizedSet(new HashSet<Modifier<?>>());

    /** Registers a variable (called by every named {@link Var} constructor). */
    public static void add(Var<?> var) {
        variables.add(var);
    }

    /** Registers a modifier (called by {@link Var#at(Object)}). */
    public static void addModifier(Modifier<?> modifier) {
        modifiers.add(modifier);
    }

    /**
     * Disposes of a variable: clears its modifiers, nulls and locks it,
     * and removes it from the registry.
     */
    public static void finalizeVariable(Var<?> var) {
        garbageCollector.clearModifiers(modifiers, var);
        garbageCollector.collect(var);
        variables.remove(var);
    }
}
