package com.rmsftc.utils.filter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilterTest {

    @Test
    void lowPassFilterSmoothsTowardTheInput() {
        Filter filter = Filter.lowPassFilter(0.5);
        double output = 0.0;
        output = filter.update(1.0, 0.0, output);
        assertEquals(0.5, output);
        output = filter.update(1.0, 0.0, output);
        assertEquals(0.75, output);
        output = filter.update(1.0, 0.0, output);
        assertEquals(0.875, output);
    }

    @Test
    void highPassFilterPassesChangesAndBlocksSteadyValues() {
        Filter filter = Filter.highPassFilter(0.5);
        double output = 0.0;
        output = filter.update(1.0, 0.0, output);
        assertEquals(0.5, output);
        output = filter.update(1.0, 0.0, output);
        assertEquals(0.25, output);
        output = filter.update(1.0, 0.0, output);
        assertEquals(0.125, output);
        // Falling edge: a negative spike, then it decays back to zero.
        output = filter.update(0.0, 0.0, output);
        assertEquals(-0.4375, output);
    }

    @Test
    void highPassFilterRejectsAlphaOutsideZeroToOne() {
        assertThrows(IllegalArgumentException.class, () -> Filter.highPassFilter(-0.1));
        assertThrows(IllegalArgumentException.class, () -> Filter.highPassFilter(1.1));
    }

    @Test
    void movingAverageFilterAveragesTheWindow() {
        Filter filter = Filter.movingAverageFilter(3);
        assertEquals(1.0, filter.update(1.0, 0.0, 0.0));
        assertEquals(1.5, filter.update(2.0, 0.0, 0.0));
        assertEquals(2.0, filter.update(3.0, 0.0, 0.0));
        assertEquals(3.0, filter.update(4.0, 0.0, 0.0));
        assertEquals(4.0, filter.update(5.0, 0.0, 0.0));
    }

    @Test
    void movingAverageFilterRejectsWindowBelowOne() {
        assertThrows(IllegalArgumentException.class, () -> Filter.movingAverageFilter(0));
    }

    @Test
    void filtersCombineWithMathOperandOperations() {
        Filter sum = Filter.lowPassFilter(0.5).add(Filter.lowPassFilter(0.5));
        assertEquals(1.0, sum.update(1.0, 0.0, 0.0));
        assertEquals(1.5, sum.update(1.0, 0.0, 0.5));
    }

    @Test
    void filterStateHoldsThePreviousOutput() {
        FilterState state = new FilterState(Filter.lowPassFilter(0.5));
        assertEquals(0.5, state.update(1.0));
        assertEquals(0.75, state.update(1.0));
        assertEquals(0.75, state.get());

        state.reset();
        assertEquals(0.0, state.get());
        assertEquals(0.5, state.update(1.0));

        state.reset(1.0);
        assertEquals(1.0, state.get());
    }

    @Test
    void filterStateRemembersCovariance() {
        FilterState state = new FilterState(Filter.lowPassFilter(0.5));
        state.update(1.0, 2.0);
        assertEquals(2.0, state.getCovariance());
        state.setCovariance(3.0);
        assertEquals(3.0, state.getCovariance());
    }
}
