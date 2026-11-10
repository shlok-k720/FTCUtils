package com.rmsftc.utils.filter;

import com.rmsftc.utils.math.Matrix;

/**
 * A linear Kalman filter over a matrix state. It holds the state estimate
 * and its covariance, so callers just call {@link #predict()} and
 * {@link #update(double[])} each loop.
 *
 * <p>The model is the standard linear one:
 * <pre>
 *   x' = F x + B u          (predict)
 *   P' = F P F^T + Q
 *   z  = H x                (measurement)
 * </pre>
 * The update uses the Joseph form of the covariance update, which stays
 * symmetric and positive-definite even with rounding error.
 *
 * <p>Implements {@link Filter}: the scalar {@code update} is a 1-D
 * convenience (predict + correct with the scalar measurement, returning
 * the first state component), and the {@code Filter} defaults
 * {@code predict()}, {@code update(double[])} and {@code reset()} are
 * overridden with the real Kalman steps. The covariance lives inside the
 * filter — that is why the scalar update ignores its covariance argument.
 *
 * <pre>
 * // Track a position moving at constant velocity, measuring position only.
 * double dt = 0.02;
 * Matrix f = new Matrix(new double[][]{{1, dt}, {0, 1}});
 * Matrix h = new Matrix(new double[][]{{1, 0}});
 * Matrix q = Matrix.diagonal(0.001, 0.001);
 * Matrix r = Matrix.diagonal(0.05);
 * KalmanFilter filter = new KalmanFilter(f, h, q, r,
 *         new double[]{0, 0}, Matrix.identity(2));
 * // in the loop:
 * filter.predict();
 * filter.update(new double[]{measuredPosition});
 * double position = filter.getState()[0];
 * </pre>
 */
public class KalmanFilter implements Filter {
    private final Matrix f;
    private final Matrix b;
    private final Matrix h;
    private final Matrix q;
    private final Matrix r;
    private final double[] initialState;
    private final Matrix initialCovariance;
    private Matrix x;
    private Matrix p;

    /** A filter with no control input (B is absent). */
    public KalmanFilter(Matrix f, Matrix h, Matrix q, Matrix r,
                        double[] initialState, Matrix initialCovariance) {
        this(f, null, h, q, r, initialState, initialCovariance);
    }

    /**
     * @param f state transition matrix (n-by-n)
     * @param b control-input matrix (n-by-k), or null for no control input
     * @param h measurement matrix (m-by-n)
     * @param q process-noise covariance (n-by-n)
     * @param r measurement-noise covariance (m-by-m)
     * @param initialState the initial state estimate (length n)
     * @param initialCovariance the initial state covariance (n-by-n)
     */
    public KalmanFilter(Matrix f, Matrix b, Matrix h, Matrix q, Matrix r,
                        double[] initialState, Matrix initialCovariance) {
        int n = f.rows();
        if (f.cols() != n) {
            throw new IllegalArgumentException("F must be square");
        }
        if (h.cols() != n) {
            throw new IllegalArgumentException("H must have as many columns as F has states");
        }
        if (q.rows() != n || q.cols() != n) {
            throw new IllegalArgumentException("Q must be n-by-n");
        }
        if (r.rows() != h.rows() || r.cols() != h.rows()) {
            throw new IllegalArgumentException("R must be m-by-m, where m is the measurement size");
        }
        if (b != null && b.rows() != n) {
            throw new IllegalArgumentException("B must have as many rows as F has states");
        }
        if (initialState.length != n) {
            throw new IllegalArgumentException("initialState must have length n");
        }
        if (initialCovariance.rows() != n || initialCovariance.cols() != n) {
            throw new IllegalArgumentException("initialCovariance must be n-by-n");
        }
        this.f = f;
        this.b = b;
        this.h = h;
        this.q = q;
        this.r = r;
        this.initialState = initialState.clone();
        this.initialCovariance = initialCovariance.copy();
        this.x = Matrix.columnVector(initialState);
        this.p = initialCovariance.copy();
    }

    /** Predicts the next state with no control input. */
    @Override
    public void predict() {
        predict(null);
    }

    /** Predicts the next state with the given control input (ignored when there is no B). */
    public void predict(double[] control) {
        x = f.multiply(x);
        if (b != null && control != null) {
            x = x.add(b.multiply(Matrix.columnVector(control)));
        }
        p = f.multiply(p).multiply(f.transpose()).add(q);
    }

    /** Corrects the estimate with a measurement. */
    @Override
    public void update(double[] measurement) {
        if (measurement.length != h.rows()) {
            throw new IllegalArgumentException(
                    "Measurement must have length " + h.rows());
        }
        Matrix z = Matrix.columnVector(measurement);
        Matrix y = z.subtract(h.multiply(x));
        Matrix s = h.multiply(p).multiply(h.transpose()).add(r);
        Matrix k = p.multiply(h.transpose()).multiply(s.inverse());
        x = x.add(k.multiply(y));
        Matrix identity = Matrix.identity(x.rows());
        Matrix iMinusKh = identity.subtract(k.multiply(h));
        p = iMinusKh.multiply(p).multiply(iMinusKh.transpose())
                .add(k.multiply(r).multiply(k.transpose()));
    }

    /**
     * The {@link Filter} scalar step: a predict step plus a correction
     * with the scalar measurement, returning the first state component.
     * Only meaningful for 1-D filters (1 state, 1 measurement); the
     * {@code covariance} and {@code oldOutput} arguments are ignored —
     * the filter keeps its own covariance and state.
     */
    @Override
    public double update(double newInput, double covariance, double oldOutput) {
        if (x.rows() != 1 || h.rows() != 1) {
            throw new UnsupportedOperationException(
                    "Scalar update is only supported for 1-D filters");
        }
        predict();
        update(new double[]{newInput});
        return getState()[0];
    }

    /** Restores the initial state estimate and covariance. */
    @Override
    public void reset() {
        x = Matrix.columnVector(initialState);
        p = initialCovariance.copy();
    }

    /** A copy of the current state estimate. */
    public double[] getState() {
        return stateArray();
    }

    /** A copy of the current state covariance. */
    public Matrix getCovariance() {
        return p.copy();
    }

    private double[] stateArray() {
        double[] state = new double[x.rows()];
        for (int i = 0; i < state.length; i++) {
            state[i] = x.get(i, 0);
        }
        return state;
    }
}
