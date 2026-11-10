package com.rmsftc.utils.filter;

import com.rmsftc.utils.math.Matrix;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtendedKalmanFilterTest {

    private static final double EPS = 1e-9;

    @Test
    void withLinearModelsMatchesTheLinearKalmanFilter() {
        Matrix f = new Matrix(new double[][]{{1, 1}, {0, 1}});
        Matrix h = new Matrix(new double[][]{{1, 0}});
        Matrix q = Matrix.diagonal(0.001, 0.001);
        Matrix r = Matrix.diagonal(0.1);
        double[] initialState = {0, 0};
        Matrix initialCovariance = Matrix.identity(2);

        KalmanFilter kf = new KalmanFilter(f, h, q, r, initialState, initialCovariance);
        ExtendedKalmanFilter ekf = new ExtendedKalmanFilter(
                (state, control) -> new double[]{state[0] + state[1], state[1]},
                state -> new double[]{state[0]},
                (state, control) -> f,
                state -> h,
                q, r, initialState, initialCovariance);

        for (int t = 1; t <= 10; t++) {
            kf.predict();
            ekf.predict();
            kf.update(new double[]{t});
            ekf.update(new double[]{t});
        }
        assertArrayEquals(kf.getState(), ekf.getState(), EPS);
        assertTrue(kf.getCovariance().equals(ekf.getCovariance(), EPS));
    }

    @Test
    void tracksANonlinearMeasurement() {
        // 1-D state, measurement z = x^2. True state is 2, so z = 4.
        ExtendedKalmanFilter ekf = new ExtendedKalmanFilter(
                (state, control) -> new double[]{state[0] + control[0]},
                state -> new double[]{state[0] * state[0]},
                (state, control) -> Matrix.identity(1),
                state -> new Matrix(new double[][]{{2 * state[0]}}),
                Matrix.diagonal(0.0),
                Matrix.diagonal(0.1),
                new double[]{1.5},
                Matrix.identity(1));
        for (int i = 0; i < 10; i++) {
            ekf.predict(new double[]{0.0});
            ekf.update(new double[]{4.0});
        }
        double[] state = ekf.getState();
        assertTrue(Math.abs(state[0] - 2.0) < 0.1,
                "estimate should converge to 2, was " + state[0]);
    }

    @Test
    void scalarUpdateIsAPredictPlusCorrectStep() {
        ExtendedKalmanFilter ekf = new ExtendedKalmanFilter(
                (state, control) -> new double[]{state[0]},
                state -> new double[]{state[0] * state[0]},
                (state, control) -> Matrix.identity(1),
                state -> new Matrix(new double[][]{{2 * state[0]}}),
                Matrix.diagonal(0.0),
                Matrix.diagonal(0.1),
                new double[]{1.5},
                Matrix.identity(1));
        for (int i = 0; i < 10; i++) {
            double output = ekf.update(4.0, 0.0, 0.0);
            assertEquals(ekf.getState()[0], output, EPS);
        }
        assertTrue(Math.abs(ekf.getState()[0] - 2.0) < 0.1,
                "estimate should converge to 2, was " + ekf.getState()[0]);
    }

    @Test
    void resetRestoresTheInitialState() {
        ExtendedKalmanFilter ekf = new ExtendedKalmanFilter(
                (state, control) -> new double[]{state[0] + control[0]},
                state -> new double[]{state[0] * state[0]},
                (state, control) -> Matrix.identity(1),
                state -> new Matrix(new double[][]{{2 * state[0]}}),
                Matrix.diagonal(0.0),
                Matrix.diagonal(0.1),
                new double[]{1.5},
                Matrix.identity(1));
        ekf.predict(new double[]{0.0});
        ekf.update(new double[]{4.0});
        ekf.reset();
        assertArrayEquals(new double[]{1.5}, ekf.getState(), EPS);
        assertTrue(ekf.getCovariance().equals(Matrix.identity(1), EPS));
    }

    @Test
    void rejectsMismatchedDimensions() {
        assertThrows(IllegalArgumentException.class, () -> new ExtendedKalmanFilter(
                (state, control) -> state,
                state -> new double[]{state[0]},
                (state, control) -> Matrix.identity(2),
                state -> new Matrix(new double[][]{{1, 0}}),
                Matrix.identity(3),
                Matrix.diagonal(0.1),
                new double[]{0, 0},
                Matrix.identity(2)));
        ExtendedKalmanFilter ekf = new ExtendedKalmanFilter(
                (state, control) -> state,
                state -> new double[]{state[0]},
                (state, control) -> Matrix.identity(2),
                state -> new Matrix(new double[][]{{1, 0}}),
                Matrix.identity(2),
                Matrix.diagonal(0.1),
                new double[]{0, 0},
                Matrix.identity(2));
        assertThrows(IllegalArgumentException.class,
                () -> ekf.update(new double[]{1.0, 2.0}));
    }
}
