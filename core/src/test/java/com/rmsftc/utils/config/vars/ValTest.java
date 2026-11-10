package com.rmsftc.utils.config.vars;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValTest {

    @Test
    void constantValAlwaysReturnsItsValue() {
        Val<Double> val = new Val<>(0.8);
        assertEquals(0.8, val.get());
        assertEquals(0.8, val.get());
    }

    @Test
    void supplierBackedValReflectsTheCurrentValue() {
        AtomicReference<Double> source = new AtomicReference<>(1.0);
        Val<Double> val = new Val<>(source::get);

        assertEquals(1.0, val.get());
        source.set(2.5);
        assertEquals(2.5, val.get());
    }

    @Test
    void valIsAValGetter() {
        Val<String> val = new Val<>("go");
        Supplier<String> supplier = val;
        assertEquals("go", supplier.get());
        assertTrue(val instanceof ValGetter);
    }

    @Test
    void toStringShowsTheValue() {
        assertEquals("Val(0.8)", new Val<>(0.8).toString());
    }
}
