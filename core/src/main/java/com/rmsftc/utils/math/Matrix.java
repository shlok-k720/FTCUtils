package com.rmsftc.utils.math;

/**
 * A small dense matrix of doubles, written for this library instead of
 * depending on an external linear-algebra library. It covers what the
 * filters need: arithmetic, transpose, inverse, and determinant.
 *
 * <p>Implements {@link MathOperand}: add/subtract are element-wise,
 * multiply is matrix multiplication, divide is right division
 * ({@code A / B = A * B^-1}), and remainder is element-wise
 * ({@code A % B} is {@code a[i][j] % b[i][j]} — there is no matrix
 * remainder, so the element-wise one is the only sensible definition).
 * In Kotlin the operators {@code + - * / %} work on matrices.
 */
public class Matrix implements MathOperand<Matrix> {
    private static final double SINGULAR_EPSILON = 1e-12;

    private final int rows;
    private final int cols;
    private final double[][] data;

    /** A zero matrix of the given size. */
    public Matrix(int rows, int cols) {
        if (rows < 1 || cols < 1) {
            throw new IllegalArgumentException("Matrix dimensions must be at least 1");
        }
        this.rows = rows;
        this.cols = cols;
        this.data = new double[rows][cols];
    }

    /** A matrix copying {@code data}. All rows must have the same length. */
    public Matrix(double[][] data) {
        if (data.length == 0 || data[0].length == 0) {
            throw new IllegalArgumentException("Matrix dimensions must be at least 1");
        }
        int cols = data[0].length;
        for (double[] row : data) {
            if (row.length != cols) {
                throw new IllegalArgumentException("All rows must have the same length");
            }
        }
        this.rows = data.length;
        this.cols = cols;
        this.data = new double[rows][cols];
        for (int r = 0; r < rows; r++) {
            System.arraycopy(data[r], 0, this.data[r], 0, cols);
        }
    }

    /** An n-by-n identity matrix. */
    public static Matrix identity(int n) {
        Matrix result = new Matrix(n, n);
        for (int i = 0; i < n; i++) {
            result.data[i][i] = 1.0;
        }
        return result;
    }

    /** A zero matrix of the given size. */
    public static Matrix zeros(int rows, int cols) {
        return new Matrix(rows, cols);
    }

    /** A diagonal matrix with the given values on the diagonal. */
    public static Matrix diagonal(double... values) {
        Matrix result = new Matrix(values.length, values.length);
        for (int i = 0; i < values.length; i++) {
            result.data[i][i] = values[i];
        }
        return result;
    }

    /** A column vector holding the given values. */
    public static Matrix columnVector(double... values) {
        Matrix result = new Matrix(values.length, 1);
        for (int i = 0; i < values.length; i++) {
            result.data[i][0] = values[i];
        }
        return result;
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    public double get(int row, int col) {
        return data[row][col];
    }

    /** A deep copy of this matrix as a 2D array. */
    public double[][] toArray() {
        double[][] copy = new double[rows][cols];
        for (int r = 0; r < rows; r++) {
            System.arraycopy(data[r], 0, copy[r], 0, cols);
        }
        return copy;
    }

    /** A copy of this matrix. */
    public Matrix copy() {
        return new Matrix(data);
    }

    @Override
    public Matrix add(Matrix other) {
        requireSameSize(other, "add");
        double[][] result = new double[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                result[r][c] = data[r][c] + other.data[r][c];
            }
        }
        return new Matrix(result);
    }

    @Override
    public Matrix subtract(Matrix other) {
        requireSameSize(other, "subtract");
        double[][] result = new double[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                result[r][c] = data[r][c] - other.data[r][c];
            }
        }
        return new Matrix(result);
    }

    @Override
    public Matrix multiply(Matrix other) {
        if (cols != other.rows) {
            throw new IllegalArgumentException(
                    "Cannot multiply a " + rows + "x" + cols + " matrix by a "
                            + other.rows + "x" + other.cols + " matrix");
        }
        double[][] result = new double[rows][other.cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < other.cols; c++) {
                double sum = 0.0;
                for (int k = 0; k < cols; k++) {
                    sum += data[r][k] * other.data[k][c];
                }
                result[r][c] = sum;
            }
        }
        return new Matrix(result);
    }

    /** Multiplies this matrix by a vector; the vector length must equal the column count. */
    public double[] multiply(double[] vector) {
        if (vector.length != cols) {
            throw new IllegalArgumentException(
                    "Vector length " + vector.length + " does not match matrix columns " + cols);
        }
        double[] result = new double[rows];
        for (int r = 0; r < rows; r++) {
            double sum = 0.0;
            for (int c = 0; c < cols; c++) {
                sum += data[r][c] * vector[c];
            }
            result[r] = sum;
        }
        return result;
    }

    public Matrix multiply(double scalar) {
        double[][] result = new double[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                result[r][c] = data[r][c] * scalar;
            }
        }
        return new Matrix(result);
    }

    /**
     * Right division: {@code this * other^-1}. {@code other} must be
     * square and invertible.
     */
    @Override
    public Matrix divide(Matrix other) {
        return multiply(other.inverse());
    }

    /**
     * Element-wise remainder: {@code result[i][j] = data[i][j] % other[i][j]}.
     * There is no matrix remainder, so the element-wise one is the definition.
     */
    @Override
    public Matrix remainder(Matrix other) {
        requireSameSize(other, "remainder");
        double[][] result = new double[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                result[r][c] = data[r][c] % other.data[r][c];
            }
        }
        return new Matrix(result);
    }

    public Matrix transpose() {
        double[][] result = new double[cols][rows];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                result[c][r] = data[r][c];
            }
        }
        return new Matrix(result);
    }

    /**
     * The inverse, by Gauss-Jordan elimination with partial pivoting.
     * Throws {@link IllegalStateException} if the matrix is singular.
     */
    public Matrix inverse() {
        if (rows != cols) {
            throw new IllegalStateException("Only square matrices can be inverted");
        }
        int n = rows;
        double[][] a = toArray();
        double[][] inv = identity(n).toArray();

        for (int col = 0; col < n; col++) {
            int pivot = col;
            for (int r = col + 1; r < n; r++) {
                if (Math.abs(a[r][col]) > Math.abs(a[pivot][col])) {
                    pivot = r;
                }
            }
            if (Math.abs(a[pivot][col]) < SINGULAR_EPSILON) {
                throw new IllegalStateException("Matrix is singular and cannot be inverted");
            }
            if (pivot != col) {
                swapRows(a, col, pivot);
                swapRows(inv, col, pivot);
            }
            double divisor = a[col][col];
            scaleRow(a, col, 1.0 / divisor);
            scaleRow(inv, col, 1.0 / divisor);
            for (int r = 0; r < n; r++) {
                if (r != col && a[r][col] != 0.0) {
                    double factor = a[r][col];
                    addScaledRow(a, r, col, -factor);
                    addScaledRow(inv, r, col, -factor);
                }
            }
        }
        return new Matrix(inv);
    }

    /** The determinant, by Gaussian elimination. Only defined for square matrices. */
    public double determinant() {
        if (rows != cols) {
            throw new IllegalStateException("Only square matrices have a determinant");
        }
        int n = rows;
        double[][] a = toArray();
        double det = 1.0;
        for (int col = 0; col < n; col++) {
            int pivot = col;
            for (int r = col + 1; r < n; r++) {
                if (Math.abs(a[r][col]) > Math.abs(a[pivot][col])) {
                    pivot = r;
                }
            }
            if (Math.abs(a[pivot][col]) < SINGULAR_EPSILON) {
                return 0.0;
            }
            if (pivot != col) {
                swapRows(a, col, pivot);
                det = -det;
            }
            det *= a[col][col];
            for (int r = col + 1; r < n; r++) {
                double factor = a[r][col] / a[col][col];
                addScaledRow(a, r, col, -factor);
            }
        }
        return det;
    }

    /** True when every entry differs from {@code other} by at most {@code epsilon}. */
    public boolean equals(Matrix other, double epsilon) {
        if (other == null || rows != other.rows || cols != other.cols) {
            return false;
        }
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (Math.abs(data[r][c] - other.data[r][c]) > epsilon) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int r = 0; r < rows; r++) {
            if (r > 0) {
                sb.append(", ");
            }
            sb.append("[");
            for (int c = 0; c < cols; c++) {
                if (c > 0) {
                    sb.append(", ");
                }
                sb.append(data[r][c]);
            }
            sb.append("]");
        }
        sb.append("]");
        return sb.toString();
    }

    private void requireSameSize(Matrix other, String operation) {
        if (rows != other.rows || cols != other.cols) {
            throw new IllegalArgumentException(
                    "Cannot " + operation + " a " + rows + "x" + cols + " matrix with a "
                            + other.rows + "x" + other.cols + " matrix");
        }
    }

    private static void swapRows(double[][] m, int a, int b) {
        double[] temp = m[a];
        m[a] = m[b];
        m[b] = temp;
    }

    private static void scaleRow(double[][] m, int row, double scale) {
        for (int c = 0; c < m[row].length; c++) {
            m[row][c] *= scale;
        }
    }

    private static void addScaledRow(double[][] m, int target, int source, double scale) {
        for (int c = 0; c < m[target].length; c++) {
            m[target][c] += scale * m[source][c];
        }
    }
}
