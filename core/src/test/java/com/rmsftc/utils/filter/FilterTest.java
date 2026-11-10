package com.rmsftc.utils.filter;

import com.rmsftc.utils.math.Matrix;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void defaultPredictUpdateAndResetAreNoOpsForStatelessFilters() {
        Filter filter = Filter.lowPassFilter(0.5);
        filter.predict();
        filter.update(new double[]{1.0});
        filter.reset();
        assertEquals(0.5, filter.update(1.0, 0.0, 0.0));
    }

    @Test
    void filterStateResetResetsAWrappedKalmanFilter() {
        FilterState state = new FilterState(new KalmanFilter(
                new Matrix(new double[][]{{1}}),
                new Matrix(new double[][]{{1}}),
                Matrix.diagonal(0.001),
                Matrix.diagonal(0.1),
                new double[]{0},
                Matrix.identity(1)));
        for (int i = 0; i < 5; i++) {
            state.update(5.0);
        }
        assertTrue(state.get() > 4.0, "output should converge toward 5, was " + state.get());
        state.reset();
        assertEquals(0.0, state.get());
        FilterState fresh = new FilterState(new KalmanFilter(
                new Matrix(new double[][]{{1}}),
                new Matrix(new double[][]{{1}}),
                Matrix.diagonal(0.001),
                Matrix.diagonal(0.1),
                new double[]{0},
                Matrix.identity(1)));
        assertEquals(fresh.update(5.0), state.update(5.0), 1e-9);
    }

    @Test
    void kalmanFilterCanBeUsedAsAFilter() {
        KalmanFilter kalman = new KalmanFilter(
                new Matrix(new double[][]{{1}}),
                new Matrix(new double[][]{{1}}),
                Matrix.diagonal(0.001),
                Matrix.diagonal(0.1),
                new double[]{0},
                Matrix.identity(1));
        Filter filter = kalman;
        filter.predict();
        filter.update(new double[]{5.0});
        assertEquals(4.545, kalman.getState()[0], 0.01);
        filter.reset();
        assertEquals(0.0, kalman.getState()[0]);
    }
}
