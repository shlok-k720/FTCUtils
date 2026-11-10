package com.rmsftc.utils.config.vars;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GarbageCollectorTest {

    @Test
    void collectNullsAndLocksTheVar() {
        Var<Double> var = new Var<>("X", 5.0);
        new GarbageCollector().collect(var);
        assertNull(var.get());
        assertFalse(var.isMutable());
    }

    @Test
    void collectWorksOnAnAlreadyFinalVar() {
        Var<Double> var = new Var<>("X", 5.0);
        var.makeFinal();
        new GarbageCollector().collect(var);
        assertNull(var.get());
        assertFalse(var.isMutable());
    }

    @Test
    void collectRefusesANonnullValidatedVar() {
        Var<Double> var = new Var<>("Y", 1.0, Validator.nonnull());
        assertThrows(IllegalArgumentException.class, () -> new GarbageCollector().collect(var));
        assertEquals(1.0, var.get());
    }

    @Test
    void clearModifiersRemovesOnlyThatVarsModifiers() {
        Var<Double> var = new Var<>("X", 1.0);
        Var<Double> other = new Var<>("Y", 2.0);
        Modifier<Double> first = new Modifier<>(var, 1.5);
        Modifier<Double> second = new Modifier<>(var, 1.6);
        Modifier<Double> others = new Modifier<>(other, 2.5);
        Set<Modifier<?>> modifiers = new HashSet<>();
        modifiers.add(first);
        modifiers.add(second);
        modifiers.add(others);

        new GarbageCollector().clearModifiers(modifiers, var);

        assertEquals(1, modifiers.size());
        assertTrue(modifiers.contains(others));
    }
}
