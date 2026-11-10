package com.rmsftc.utils.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatrixTest {

    private static final double EPS = 1e-9;

    @Test
    void constructorCopiesItsInput() {
        double[][] data = {{1.0, 2.0}, {3.0, 4.0}};
        Matrix m = new Matrix(data);
        data[0][0] = 99.0;
        assertEquals(1.0, m.get(0, 0));
    }

    @Test
    void constructorRejectsRaggedInput() {
        assertThrows(IllegalArgumentException.class,
                () -> new Matrix(new double[][]{{1.0, 2.0}, {3.0}}));
    }

    @Test
    void factories() {
        assertTrue(Matrix.identity(3).equals(
                new Matrix(new double[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}), EPS));
        assertTrue(Matrix.diagonal(2.0, 3.0).equals(
                new Matrix(new double[][]{{2, 0}, {0, 3}}), EPS));
        assertTrue(Matrix.columnVector(1.0, 2.0).equals(
                new Matrix(new double[][]{{1}, {2}}), EPS));
        assertEquals(0.0, Matrix.zeros(2, 2).get(1, 1));
    }

    @Test
    void addAndSubtract() {
        Matrix a = new Matrix(new double[][]{{1, 2}, {3, 4}});
        Matrix b = new Matrix(new double[][]{{5, 6}, {7, 8}});
        assertTrue(a.add(b).equals(new Matrix(new double[][]{{6, 8}, {10, 12}}), EPS));
        assertTrue(b.subtract(a).equals(new Matrix(new double[][]{{4, 4}, {4, 4}}), EPS));
    }

    @Test
    void addRejectsMismatchedSizes() {
        Matrix a = new Matrix(2, 2);
        Matrix b = new Matrix(3, 3);
        assertThrows(IllegalArgumentException.class, () -> a.add(b));
    }

    @Test
    void multiplyMatrixAndVector() {
        Matrix a = new Matrix(new double[][]{{1, 2}, {3, 4}});
        Matrix b = new Matrix(new double[][]{{2, 0}, {1, 2}});
        assertTrue(a.multiply(b).equals(new Matrix(new double[][]{{4, 4}, {10, 8}}), EPS));
        assertArrayEquals(new double[]{5.0, 11.0}, a.multiply(new double[]{1.0, 2.0}), EPS);
        assertTrue(a.multiply(2.0).equals(new Matrix(new double[][]{{2, 4}, {6, 8}}), EPS));
    }

    @Test
    void multiplyRejectsMismatchedDimensions() {
        Matrix a = new Matrix(2, 3);
        Matrix b = new Matrix(2, 2);
        assertThrows(IllegalArgumentException.class, () -> a.multiply(b));
        assertThrows(IllegalArgumentException.class, () -> a.multiply(new double[]{1.0, 2.0}));
    }

    @Test
    void transpose() {
        Matrix a = new Matrix(new double[][]{{1, 2, 3}, {4, 5, 6}});
        assertTrue(a.transpose().equals(
                new Matrix(new double[][]{{1, 4}, {2, 5}, {3, 6}}), EPS));
    }

    @Test
    void inverseOfTwoByTwo() {
        Matrix a = new Matrix(new double[][]{{4, 7}, {2, 6}});
        // det = 10, inverse = (1/10) * [[6, -7], [-2, 4]]
        assertTrue(a.inverse().equals(
                new Matrix(new double[][]{{0.6, -0.7}, {-0.2, 0.4}}), EPS));
    }

    @Test
    void inverseTimesOriginalIsIdentity() {
        Matrix a = new Matrix(new double[][]{{2, 1, 0}, {1, 3, 1}, {0, 1, 2}});
        assertTrue(a.multiply(a.inverse()).equals(Matrix.identity(3), EPS));
        assertTrue(a.inverse().multiply(a).equals(Matrix.identity(3), EPS));
    }

    @Test
    void inverseRejectsSingularMatrices() {
        Matrix singular = new Matrix(new double[][]{{1, 2}, {2, 4}});
        assertThrows(IllegalStateException.class, singular::inverse);
        Matrix nonSquare = new Matrix(2, 3);
        assertThrows(IllegalStateException.class, nonSquare::inverse);
    }

    @Test
    void determinant() {
        assertEquals(10.0, new Matrix(new double[][]{{4, 7}, {2, 6}}).determinant(), EPS);
        assertEquals(0.0, new Matrix(new double[][]{{1, 2}, {2, 4}}).determinant(), EPS);
        assertEquals(1.0, new Matrix(new double[][]{{1, 2, 3}, {0, 1, 4}, {5, 6, 0}}).determinant(), EPS);
    }

    @Test
    void equalsWithEpsilon() {
        Matrix a = new Matrix(new double[][]{{1.0, 2.0}});
        Matrix b = new Matrix(new double[][]{{1.0 + 1e-10, 2.0}});
        assertTrue(a.equals(b, 1e-9));
        assertFalse(a.equals(b, 1e-11));
        assertFalse(a.equals(new Matrix(1, 3), 1e-9));
    }

    @Test
    void operationsDoNotMutateTheirInputs() {
        Matrix a = new Matrix(new double[][]{{1, 2}, {3, 4}});
        Matrix b = new Matrix(new double[][]{{5, 6}, {7, 8}});
        a.add(b);
        a.multiply(b);
        a.inverse();
        assertTrue(a.equals(new Matrix(new double[][]{{1, 2}, {3, 4}}), EPS));
        assertTrue(b.equals(new Matrix(new double[][]{{5, 6}, {7, 8}}), EPS));
    }
}
