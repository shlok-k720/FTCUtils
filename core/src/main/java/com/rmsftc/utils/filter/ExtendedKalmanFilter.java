package com.rmsftc.utils.filter;

import com.rmsftc.utils.math.Matrix;

/**
 * An extended Kalman filter for nonlinear models. The caller supplies the
 * process and measurement functions plus their Jacobians, evaluated at the
 * current state; the filter linearizes around the estimate each step.
 *
 * <p>Model:
 * <pre>
 *   x' = f(x, u)             (predict, with Jacobian F = df/dx)
 *   P' = F P F^T + Q
 *   z  = h(x)                (measurement, with Jacobian H = dh/dx)
 * </pre>
 * The update uses the Joseph form of the covariance update, which stays
 * symmetric and positive-definite even with rounding error.
 *
 * <p>Implements {@link Filter}: the scalar {@code update} is a 1-D
 * convenience (predict + correct with the scalar measurement, returning
 * the first state component), and the {@code Filter} defaults
 * {@code predict()}, {@code update(double[])} and {@code reset()} are
 * overridden with the real EKF steps.
 *
 * <pre>
 * // 1-D state, nonlinear measurement z = x^2.
 * ExtendedKalmanFilter ekf = new ExtendedKalmanFilter(
 *         (state, control) -&gt; new double[]{state[0] + control[0]},
 *         state -&gt; new double[]{state[0] * state[0]},
 *         (state, control) -&gt; Matrix.identity(1),
 *         state -&gt; new Matrix(new double[][]{{2 * state[0]}}),
 *         Matrix.diagonal(0.0), Matrix.diagonal(0.1),
 *         new double[]{1.5}, Matrix.identity(1));
 * // in the loop:
 * ekf.predict(new double[]{0.0});
 * ekf.update(new double[]{measuredSquared});
 * </pre>
 */
public class ExtendedKalmanFilter implements Filter {

    /** The process model: next state from the current state and control. */
    @FunctionalInterface
    public interface ProcessModel {
        double[] apply(double[] state, double[] control);
    }

    /** The measurement model: expected measurement from the state. */
    @FunctionalInterface
    public interface MeasurementModel {
        double[] apply(double[] state);
    }

    /** The Jacobian of the process model, evaluated at (state, control). */
    @FunctionalInterface
    public interface ProcessJacobian {
        Matrix apply(double[] state, double[] control);
    }

    /** The Jacobian of the measurement model, evaluated at the state. */
    @FunctionalInterface
    public interface MeasurementJacobian {
        Matrix apply(double[] state);
    }

    private final ProcessModel processModel;
    private final MeasurementModel measurementModel;
    private final ProcessJacobian processJacobian;
    private final MeasurementJacobian measurementJacobian;
    private final Matrix q;
    private final Matrix r;
    private final double[] initialState;
    private final Matrix initialCovariance;
    private Matrix x;
    private Matrix p;

    /**
     * @param processModel f(x, u)
     * @param measurementModel h(x)
     * @param processJacobian df/dx at (x, u)
     * @param measurementJacobian dh/dx at x
     * @param q process-noise covariance (n-by-n)
     * @param r measurement-noise covariance (m-by-m)
     * @param initialState the initial state estimate (length n)
     * @param initialCovariance the initial state covariance (n-by-n)
     */
    public ExtendedKalmanFilter(ProcessModel processModel,
                                MeasurementModel measurementModel,
                                ProcessJacobian processJacobian,
                                MeasurementJacobian measurementJacobian,
                                Matrix q, Matrix r,
                                double[] initialState, Matrix initialCovariance) {
        int n = initialState.length;
        if (q.rows() != n || q.cols() != n) {
            throw new IllegalArgumentException("Q must be n-by-n");
        }
        if (initialCovariance.rows() != n || initialCovariance.cols() != n) {
            throw new IllegalArgumentException("initialCovariance must be n-by-n");
        }
        this.processModel = processModel;
        this.measurementModel = measurementModel;
        this.processJacobian = processJacobian;
        this.measurementJacobian = measurementJacobian;
        this.q = q;
        this.r = r;
        this.initialState = initialState.clone();
        this.initialCovariance = initialCovariance.copy();
        this.x = Matrix.columnVector(initialState);
        this.p = initialCovariance.copy();
    }

    /**
     * Predicts the next state with no control input. Passes an empty
     * control array to the process model, so models that read the control
     * must use {@link #predict(double[])} instead.
     */
    @Override
    public void predict() {
        predict(new double[0]);
    }

    /** Predicts the next state with the given control input. */
    public void predict(double[] control) {
        double[] current = getState();
        double[] next = processModel.apply(current, control);
        Matrix f = processJacobian.apply(current, control);
        if (next.length != current.length) {
            throw new IllegalStateException("Process model changed the state size");
        }
        if (f.rows() != current.length || f.cols() != current.length) {
            throw new IllegalStateException("Process Jacobian must be n-by-n");
        }
        x = Matrix.columnVector(next);
        p = f.multiply(p).multiply(f.transpose()).add(q);
    }

    /** Corrects the estimate with a measurement. */
    @Override
    public void update(double[] measurement) {
        double[] current = getState();
        Matrix h = measurementJacobian.apply(current);
        if (h.cols() != current.length) {
            throw new IllegalStateException(
                    "Measurement Jacobian must have as many columns as the state size");
        }
        if (measurement.length != h.rows()) {
            throw new IllegalArgumentException(
                    "Measurement must have length " + h.rows());
        }
        Matrix z = Matrix.columnVector(measurement);
        Matrix predicted = Matrix.columnVector(measurementModel.apply(current));
        if (predicted.rows() != h.rows()) {
            throw new IllegalStateException(
                    "Measurement model must return as many values as the Jacobian has rows");
        }
        Matrix y = z.subtract(predicted);
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
        double[] current = getState();
        if (current.length != 1
                || measurementModel.apply(current).length != 1
                || measurementJacobian.apply(current).rows() != 1) {
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
        double[] state = new double[x.rows()];
        for (int i = 0; i < state.length; i++) {
            state[i] = x.get(i, 0);
        }
        return state;
    }

    /** A copy of the current state covariance. */
    public Matrix getCovariance() {
        return p.copy();
    }
}
