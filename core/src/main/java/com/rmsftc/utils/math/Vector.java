package com.rmsftc.utils.math;

import com.pedropathing.math.Matrix;

/**
 * A mutable, Pedro-compatible n-dimensional vector: extends
 * {@link com.pedropathing.math.Vector} and overrides every vector-returning
 * operation so results stay in this type. Mutate in place with
 * {@link #set(int, double)} / {@link #setAll(double...)} (Pedro's
 * {@code elements} array is public, so in-place writes are visible to every
 * operation).
 *
 * <p>Because this <em>is</em> a Pedro {@code Vector}, it can be passed
 * anywhere Pedro expects one with no conversion. Pedro Pathing core is a
 * {@code compileOnly} dependency of this module.
 */
public class Vector extends com.pedropathing.math.Vector {

    public Vector(double... elements) {
        super(elements);
    }

    /** Sets one element in place. */
    public void set(int i, double value) {
        elements[i] = value;
    }

    /** Replaces every element in place (must match this vector's size). */
    public void setAll(double... values) {
        if (values.length != size()) {
            throw new IllegalArgumentException("Vector sizes must match.");
        }
        System.arraycopy(values, 0, elements, 0, values.length);
    }

    /** Wraps any Pedro vector in this type (copies its elements). */
    public static Vector fromPedro(com.pedropathing.math.Vector vector) {
        return new Vector(vector.elements());
    }

    public static Vector e(int i, int dim) {
        return fromPedro(com.pedropathing.math.Vector.e(i, dim));
    }

    public static Vector zero(int dim) {
        return fromPedro(com.pedropathing.math.Vector.zero(dim));
    }

    public static Vector[] gramSchmidt(com.pedropathing.math.Vector... vectors) {
        com.pedropathing.math.Vector[] result =
                com.pedropathing.math.Vector.gramSchmidt(vectors);
        Vector[] wrapped = new Vector[result.length];
        for (int i = 0; i < result.length; i++) {
            wrapped[i] = fromPedro(result[i]);
        }
        return wrapped;
    }

    @Override
    public Vector normalized() {
        return fromPedro(super.normalized());
    }

    @Override
    public Vector times(double scalar) {
        return fromPedro(super.times(scalar));
    }

    @Override
    public Vector div(double scalar) {
        return fromPedro(super.div(scalar));
    }

    @Override
    public Vector plus(com.pedropathing.math.Vector other) {
        return fromPedro(super.plus(other));
    }

    @Override
    public Vector minus(com.pedropathing.math.Vector other) {
        return fromPedro(super.minus(other));
    }

    @Override
    public Vector hadamard(com.pedropathing.math.Vector other) {
        return fromPedro(super.hadamard(other));
    }

    @Override
    public Vector transform(Matrix m) {
        return fromPedro(super.transform(m));
    }

    @Override
    public Vector abs() {
        return fromPedro(super.abs());
    }

    @Override
    public Vector projectOnto(com.pedropathing.math.Vector other) {
        return fromPedro(super.projectOnto(other));
    }

    @Override
    public Vector cross(com.pedropathing.math.Vector other) {
        return fromPedro(super.cross(other));
    }
}
