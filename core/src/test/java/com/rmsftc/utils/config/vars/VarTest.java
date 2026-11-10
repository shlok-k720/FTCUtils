package com.rmsftc.utils.config.vars;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VarTest {

    @Test
    void fieldBackedVarReadsAndWritesItsValue() {
        Var<Double> var = new Var<>(0.1);
        assertEquals(0.1, var.get());
        var.set(0.15);
        assertEquals(0.15, var.get());
    }

    @Test
    void noArgVarStartsNull() {
        Var<Double> var = new Var<>();
        assertNull(var.get());
        var.set(1.0);
        assertEquals(1.0, var.get());
    }

    @Test
    void providerBackedVarDelegatesToTheProvider() {
        Var<Double> backing = new Var<>(1.0);
        Var<Double> alias = new Var<>(backing);

        assertEquals(1.0, alias.get());
        alias.set(2.0);
        assertEquals(2.0, backing.get());
        backing.set(3.0);
        assertEquals(3.0, alias.get());
    }

    @Test
    void varIsAGetterAndASetter() {
        Var<Double> var = new Var<>(0.5);

        Supplier<Double> supplier = var;
        assertEquals(0.5, supplier.get());

        Consumer<Double> consumer = var;
        consumer.accept(0.9);
        assertEquals(0.9, var.get());

        assertTrue(var instanceof VarProvider);
        assertTrue(var instanceof ValGetter);
        assertTrue(var instanceof ValSetter);
    }

    @Test
    void toStringShowsTheValue() {
        assertEquals("Var(0.1)", new Var<>(0.1).toString());
    }

    @Test
    void namedVarHasASectionAndRegistersItself() {
        Var<Double> var = new Var<>("Max speed", 0.8, Validator.inRange(0.0, 1.0));
        assertEquals("Max speed", var.getName());
        assertEquals(Section.VARIABLES_CATEGORY, var.section.category);
        assertEquals("Max speed", var.section.name);
        assertFalse(var.section.isModifier);
        assertTrue(Variables.variables.contains(var));
    }

    @Test
    void unnamedVarHasNoSectionAndIsNotRegistered() {
        Var<Double> var = new Var<>(0.8);
        assertNull(var.getName());
        assertNull(var.section);
        assertFalse(Variables.variables.contains(var));
    }

    @Test
    void setEnforcesValidators() {
        Var<Double> var = new Var<>("Max speed", 0.8, Validator.inRange(0.0, 1.0));
        var.set(0.5);
        assertEquals(0.5, var.get());
        assertThrows(IllegalArgumentException.class, () -> var.set(1.5));
        assertEquals(0.5, var.get());
    }

    @Test
    void makeFinalLocksTheVar() {
        Var<Double> var = new Var<>(0.1);
        assertTrue(var.isMutable());
        var.makeFinal();
        assertFalse(var.isMutable());
        assertThrows(IllegalStateException.class, () -> var.set(0.2));
        var.makeMutable();
        assertTrue(var.isMutable());
        var.set(0.2);
        assertEquals(0.2, var.get());
    }

    @Test
    void atCreatesARegisteredModifier() {
        Var<Double> var = new Var<>("Speed", 0.8);
        Modifier<Double> modifier = var.at(0.4);
        assertEquals(0.8, modifier.getOriginal());
        assertTrue(Variables.modifiers.contains(modifier));
    }

    @Test
    void namedProviderBackedVarValidatesAndDelegates() {
        Var<Double> backing = new Var<>(1.0);
        Var<Double> alias = new Var<>("Alias", backing, Validator.nonnegative());
        assertEquals(1.0, alias.get());
        alias.set(2.0);
        assertEquals(2.0, backing.get());
        assertThrows(IllegalArgumentException.class, () -> alias.set(-1.0));
        assertEquals(2.0, backing.get());
    }
}
