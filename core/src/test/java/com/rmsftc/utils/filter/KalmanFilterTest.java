package com.rmsftc.utils.filter;

import com.rmsftc.utils.math.Matrix;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KalmanFilterTest {

    private static final double EPS = 1e-9;

    @Test
    void tracksAConstantValue() {
        KalmanFilter filter = new KalmanFilter(
                new Matrix(new double[][]{{1}}),
                new Matrix(new double[][]{{1}}),
                Matrix.diagonal(0.001),
                Matrix.diagonal(0.1),
                new double[]{0},
                Matrix.identity(1));
        for (int i = 0; i < 10; i++) {
            filter.predict();
            filter.update(new double[]{5.0});
        }
        double[] state = filter.getState();
        assertTrue(Math.abs(state[0] - 5.0) < 0.05,
                "estimate should converge to 5, was " + state[0]);
        assertTrue(filter.getCovariance().get(0, 0) < 0.1,
                "covariance should shrink");
    }

    @Test
    void tracksConstantVelocityFromPositionMeasurements() {
        double dt = 1.0;
        KalmanFilter filter = new KalmanFilter(
                new Matrix(new double[][]{{1, dt}, {0, 1}}),
                new Matrix(new double[][]{{1, 0}}),
                Matrix.diagonal(0.001, 0.001),
                Matrix.diagonal(0.1),
                new double[]{0, 0},
                Matrix.identity(2));
        // True motion: position = t, velocity = 1, measured exactly.
        for (int t = 1; t <= 10; t++) {
            filter.predict();
            filter.update(new double[]{t});
        }
        double[] state = filter.getState();
        assertTrue(Math.abs(state[0] - 10.0) < 0.5,
                "position should track 10, was " + state[0]);
        assertTrue(Math.abs(state[1] - 1.0) < 0.2,
                "velocity should track 1, was " + state[1]);
    }

    @Test
    void predictPropagatesStateAndCovariance() {
        KalmanFilter filter = new KalmanFilter(
                new Matrix(new double[][]{{1, 1}, {0, 1}}),
                new Matrix(new double[][]{{1, 0}}),
                Matrix.zeros(2, 2),
                Matrix.diagonal(0.1),
                new double[]{2, 3},
                Matrix.identity(2));
        filter.predict();
        assertArrayEquals(new double[]{5.0, 3.0}, filter.getState(), EPS);
        // P' = F * I * F^T = [[2, 1], [1, 1]]
        assertTrue(filter.getCovariance().equals(
                new Matrix(new double[][]{{2, 1}, {1, 1}}), EPS));
    }

    @Test
    void controlInputMovesTheState() {
        KalmanFilter filter = new KalmanFilter(
                new Matrix(new double[][]{{1}}),
                new Matrix(new double[][]{{1}}),
                new Matrix(new double[][]{{1}}),
                Matrix.zeros(1, 1),
                Matrix.diagonal(1.0),
                new double[]{0},
                Matrix.identity(1));
        filter.predict(new double[]{2.0});
        assertArrayEquals(new double[]{2.0}, filter.getState(), EPS);
        filter.update(new double[]{2.0});
        assertArrayEquals(new double[]{2.0}, filter.getState(), EPS);
    }

    @Test
    void rejectsMismatchedDimensions() {
        Matrix f = Matrix.identity(2);
        Matrix h = new Matrix(new double[][]{{1, 0}});
        Matrix q = Matrix.identity(2);
        Matrix r = Matrix.diagonal(0.1);
        assertThrows(IllegalArgumentException.class, () -> new KalmanFilter(
                f, new Matrix(new double[][]{{1, 0, 0}}), q, r,
                new double[]{0, 0}, Matrix.identity(2)));
        KalmanFilter filter = new KalmanFilter(f, h, q, r,
                new double[]{0, 0}, Matrix.identity(2));
        assertThrows(IllegalArgumentException.class,
                () -> filter.update(new double[]{1.0, 2.0}));
    }
}
