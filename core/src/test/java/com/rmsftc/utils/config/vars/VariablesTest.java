package com.rmsftc.utils.config.vars;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VariablesTest {

    @Test
    void sectionCategories() {
        assertEquals("Variables", Section.VARIABLES_CATEGORY);
        assertEquals("Modifiers", Section.MODIFIERS_CATEGORY);
    }

    @Test
    void finalizeVariableRemovesItAndItsModifiers() {
        Var<Double> var = new Var<>("Temp", 1.0);
        Modifier<Double> modifier = var.at(2.0);
        assertTrue(Variables.variables.contains(var));
        assertTrue(Variables.modifiers.contains(modifier));

        Variables.finalizeVariable(var);

        assertFalse(Variables.variables.contains(var));
        assertFalse(Variables.modifiers.contains(modifier));
        assertNull(var.get());
        assertFalse(var.isMutable());
    }

    @Test
    void finalizeVariableOnAnUnnamedVar() {
        Var<Double> var = new Var<>(1.0);
        Variables.finalizeVariable(var);
        assertNull(var.get());
        assertFalse(var.isMutable());
    }
}
