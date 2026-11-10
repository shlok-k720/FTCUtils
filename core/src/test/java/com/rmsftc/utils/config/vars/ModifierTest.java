package com.rmsftc.utils.config.vars;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModifierTest {

    @Test
    void applyAndRevert() {
        Var<Double> var = new Var<>("Speed", 0.8);
        Modifier<Double> modifier = var.at(0.4);
        assertEquals(0.8, modifier.getOriginal());
        assertEquals(0.4, modifier.getValue());
        assertFalse(modifier.applied());
        assertEquals(var, modifier.getVar());

        modifier.apply();
        assertEquals(0.4, var.get());
        assertTrue(modifier.applied());

        modifier.revert();
        assertEquals(0.8, var.get());
        assertFalse(modifier.applied());
    }

    @Test
    void applyOnALockedVarThrows() {
        Var<Double> var = new Var<>("Speed", 0.8);
        Modifier<Double> modifier = var.at(0.4);
        var.makeFinal();
        assertThrows(IllegalStateException.class, modifier::apply);
    }

    @Test
    void revertRestoresTheOriginalEvenAfterOtherSets() {
        Var<Double> var = new Var<>("Speed", 0.8);
        Modifier<Double> modifier = var.at(0.4);
        var.set(0.6);
        modifier.revert();
        assertEquals(0.8, var.get());
    }
}
