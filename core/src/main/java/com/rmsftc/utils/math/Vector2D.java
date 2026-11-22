package com.rmsftc.utils.math;

import com.pedropathing.math.Matrix;
import com.pedropathing.math.Twist;
import com.pedropathing.math.Velocity;

import java.util.Objects;

/**
 * A Pedro-compatible 2D vector. Pedro's {@link com.pedropathing.math.Vector2D}
 * has a private constructor, so it cannot be extended; this class wraps one
 * instead, mirroring its full API with results in this type, and converts
 * back with {@link #toPedro()} wherever Pedro needs its own type.
 *
 * <p>Pedro Pathing core is a {@code compileOnly} dependency of this module.
 */
public class Vector2D {
    private final com.pedropathing.math.Vector2D delegate;

    private Vector2D(com.pedropathing.math.Vector2D delegate) {
        this.delegate = delegate;
    }

    public static Vector2D cartesian(double x, double y) {
        return new Vector2D(com.pedropathing.math.Vector2D.cartesian(x, y));
    }

    public static Vector2D polar(double magnitude, double theta) {
        return new Vector2D(com.pedropathing.math.Vector2D.polar(magnitude, theta));
    }

    public static Vector2D unit(double angle) {
        return new Vector2D(com.pedropathing.math.Vector2D.unit(angle));
    }

    public static Vector2D zero() {
        return new Vector2D(com.pedropathing.math.Vector2D.zero());
    }

    public static Vector2D iHat() {
        return new Vector2D(com.pedropathing.math.Vector2D.iHat());
    }

    public static Vector2D jHat() {
        return new Vector2D(com.pedropathing.math.Vector2D.jHat());
    }

    /** Wraps a Pedro vector in this type. */
    public static Vector2D fromPedro(com.pedropathing.math.Vector2D vector) {
        return new Vector2D(Objects.requireNonNull(vector, "vector"));
    }

    /** The wrapped Pedro vector, for APIs that need Pedro's own type. */
    public com.pedropathing.math.Vector2D toPedro() {
        return delegate;
    }

    public double x() {
        return delegate.x();
    }

    public double y() {
        return delegate.y();
    }

    public double magnitude() {
        return delegate.magnitude();
    }

    public double magnitudeSquared() {
        return delegate.magnitudeSquared();
    }

    public Vector2D normalized() {
        return new Vector2D(delegate.normalized());
    }

    public Vector2D plus(Vector2D other) {
        return new Vector2D(delegate.plus(other.delegate));
    }

    public Vector2D minus(Vector2D other) {
        return new Vector2D(delegate.minus(other.delegate));
    }

    public Vector2D times(double scalar) {
        return new Vector2D(delegate.times(scalar));
    }

    public Vector2D div(double scalar) {
        return new Vector2D(delegate.div(scalar));
    }

    public Vector2D rotate(double angle) {
        return new Vector2D(delegate.rotate(angle));
    }

    public double dot(Vector2D other) {
        return delegate.dot(other.delegate);
    }

    public double det(Vector2D other) {
        return delegate.det(other.delegate);
    }

    public Vector2D projectOnto(Vector2D other) {
        return new Vector2D(delegate.projectOnto(other.delegate));
    }

    public double theta() {
        return delegate.theta();
    }

    public Vector toVector() {
        return Vector.fromPedro(delegate.toVector());
    }

    public Vector2D transform(Matrix m) {
        return new Vector2D(delegate.transform(m));
    }

    public boolean isZero() {
        return delegate.isZero();
    }

    public double quadraticForm(Matrix m) {
        return delegate.quadraticForm(m);
    }

    public double angleTo(Vector2D other) {
        return delegate.angleTo(other.delegate);
    }

    public double distance(Vector2D other) {
        return delegate.distance(other.delegate);
    }

    public double distanceSquared(Vector2D other) {
        return delegate.distanceSquared(other.delegate);
    }

    public Vector2D hadamardProduct(Vector2D other) {
        return new Vector2D(delegate.hadamardProduct(other.delegate));
    }

    public Vector2D elementDivision(Vector2D other) {
        return new Vector2D(delegate.elementDivision(other.delegate));
    }

    public Vector2D perpendicularLeft() {
        return new Vector2D(delegate.perpendicularLeft());
    }

    public Pose toPose(double heading) {
        return Pose.fromPedro(delegate.toPose(heading));
    }

    public Pose toPose() {
        return Pose.fromPedro(delegate.toPose());
    }

    public Vector2D abs() {
        return new Vector2D(delegate.abs());
    }

    public Vector2D toBodyFrame(double heading) {
        return new Vector2D(delegate.toBodyFrame(heading));
    }

    public Vector2D toWorldFrame(double heading) {
        return new Vector2D(delegate.toWorldFrame(heading));
    }

    public Velocity toVelocity(double angularVelocity) {
        return delegate.toVelocity(angularVelocity);
    }

    public Velocity toVelocity() {
        return delegate.toVelocity();
    }

    public Twist toTwist(double angularVelocity) {
        return delegate.toTwist(angularVelocity);
    }

    public Twist toTwist() {
        return delegate.toTwist();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector2D)) return false;
        Vector2D that = (Vector2D) o;
        return Double.compare(x(), that.x()) == 0 && Double.compare(y(), that.y()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x(), y());
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
