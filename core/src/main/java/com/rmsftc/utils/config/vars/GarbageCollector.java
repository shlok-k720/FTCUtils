package com.rmsftc.utils.config.vars;

import java.util.Set;

/**
 * Disposal for {@link Variables}: nulls and locks a variable, and drops
 * its modifiers. Ported from the FTChimera team's vars system, with two
 * fixes: {@link #clearModifiers(Set, Var)} uses {@code removeIf} (the
 * original removed from the set while streaming over it), and
 * {@link #collect(Var)} unlocks the var first so collecting an already
 * final var works.
 *
 * <p>Note: {@link #collect(Var)} still sets the value to null, so a var
 * with a non-null validator refuses collection (that is the validator
 * doing its job).
 */
public class GarbageCollector {
    public GarbageCollector() {}

    public void collect(Var<?> var) {
        var.makeMutable();
        var.set(null);
        var.makeFinal();
    }

    public void clearModifiers(Set<Modifier<?>> modifiers, Var<?> var) {
        modifiers.removeIf(modifier -> modifier.getVar() == var);
    }
}
