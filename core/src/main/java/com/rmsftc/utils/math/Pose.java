package com.rmsftc.utils.math;

import com.pedropathing.math.Matrix;
import com.pedropathing.math.Twist;
import com.pedropathing.math.Velocity;
import com.pedropathing.utils.Angle;

/**
 * A mutable, Pedro-compatible pose: extends {@link com.pedropathing.math.Pose}
 * but keeps its own {@code x}, {@code y}, and {@code heading}, and overrides
 * every operation to use them. Pedro's pose is immutable (private final
 * fields read directly by its methods), so nothing here delegates to
 * {@code super} for state — the superclass fields are set once by the
 * constructor and never read again.
 *
 * <p>Mutate in place with {@link #setX(double)}, {@link #setY(double)},
 * {@link #setHeading(double)} (normalized, like the constructor), or
 * {@link #set(double, double, double)}. The {@code withX}/{@code withY}/
 * {@code withHeading} methods keep Pedro's semantics and return new poses.
 *
 * <p>Because this <em>is</em> a Pedro {@code Pose}, it can be passed anywhere
 * Pedro expects one (paths, followers, localizers) with no conversion, and
 * Pedro code reading it through {@code x()}/{@code y()}/{@code heading()}
 * sees the current (possibly mutated) values. Pedro Pathing core is a
 * {@code compileOnly} dependency of this module: consumers using the geometry
 * family declare Pedro themselves, as they already do.
 */
public class Pose extends com.pedropathing.math.Pose {

    private double x;
    private double y;
    private double heading;

    public Pose(double x, double y, double heading) {
        super(x, y, heading);
        this.x = x;
        this.y = y;
        this.heading = Angle.normalize(heading);
    }

    public Pose(double x, double y) {
        this(x, y, 0);
    }

    /** Wraps any Pedro pose in this type (copies x, y, heading). */
    public static Pose fromPedro(com.pedropathing.math.Pose pose) {
        return new Pose(pose.x(), pose.y(), pose.heading());
    }

    public static Pose zero() {
        return new Pose(0, 0, 0);
    }

    public static Pose interpolate(com.pedropathing.math.Pose lowerPose,
                                   com.pedropathing.math.Pose upperPose, double ratio) {
        double x = lowerPose.x() + ratio * (upperPose.x() - lowerPose.x());
        double y = lowerPose.y() + ratio * (upperPose.y() - lowerPose.y());
        double headingDiff = Angle.normalizeSigned(upperPose.heading() - lowerPose.heading());
        double heading = Angle.normalize(lowerPose.heading() + ratio * headingDiff);
        return new Pose(x, y, heading);
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double heading() {
        return heading;
    }

    /** Sets x in place. */
    public void setX(double x) {
        this.x = x;
    }

    /** Sets y in place. */
    public void setY(double y) {
        this.y = y;
    }

    /** Sets the heading in place (normalized, like the constructor). */
    public void setHeading(double heading) {
        this.heading = Angle.normalize(heading);
    }

    /** Sets x, y, and heading in place (heading normalized). */
    public void set(double x, double y, double heading) {
        this.x = x;
        this.y = y;
        this.heading = Angle.normalize(heading);
    }

    @Override
    public Pose withX(double x) {
        return new Pose(x, y, heading);
    }

    @Override
    public Pose withY(double y) {
        return new Pose(x, y, heading);
    }

    @Override
    public Pose withHeading(double heading) {
        return new Pose(x, y, heading);
    }

    @Override
    public com.pedropathing.math.Vector2D toVector2D() {
        return com.pedropathing.math.Vector2D.cartesian(x, y);
    }

    @Override
    public Matrix toMatrix() {
        return Matrix.createTransformation(x, y, heading);
    }

    @Override
    public Pose exp(Velocity velocity, double time) {
        return new Pose(x + velocity.vx * time, y + velocity.vy * time,
                heading + velocity.omega * time);
    }

    @Override
    public Pose exp(Velocity velocity) {
        return exp(velocity, 1.0);
    }

    @Override
    public Pose exp(Twist twist, double time) {
        if (Math.abs(twist.omega) < 1e-9) return exp(twist.toVelocity(heading), time);
        double theta = twist.omega * time;
        double sin = Math.sin(theta);
        double cos = Math.cos(theta);
        com.pedropathing.math.Vector2D localDeltas = com.pedropathing.math.Vector2D.cartesian(
                (sin * twist.vx - (1 - cos) * twist.vy) / twist.omega,
                ((1 - cos) * twist.vx + sin * twist.vy) / twist.omega);
        com.pedropathing.math.Vector2D globalDeltas = localDeltas.rotate(heading);
        return new Pose(x + globalDeltas.x(), y + globalDeltas.y(), heading + theta);
    }

    @Override
    public Pose exp(Twist twist) {
        return exp(twist, 1.0);
    }

    @Override
    public Pose compose(com.pedropathing.math.Pose other) {
        com.pedropathing.math.Vector2D translationDeltas = other.toVector2D().rotate(heading);
        return new Pose(x + translationDeltas.x(), y + translationDeltas.y(),
                heading + other.heading());
    }

    @Override
    public Pose plus(com.pedropathing.math.Pose other) {
        return new Pose(x + other.x(), y + other.y(), heading + other.heading());
    }

    @Override
    public Pose minus(com.pedropathing.math.Pose other) {
        return new Pose(x - other.x(), y - other.y(), heading - other.heading());
    }

    @Override
    public Pose times(double scalar) {
        return new Pose(x * scalar, y * scalar, heading * scalar);
    }

    @Override
    public Pose div(double scalar) {
        return new Pose(x / scalar, y / scalar, heading / scalar);
    }

    @Override
    public double distance(com.pedropathing.math.Pose other) {
        return Math.hypot(x - other.x(), y - other.y());
    }

    @Override
    public Pose invert() {
        com.pedropathing.math.Vector2D invTrans =
                com.pedropathing.math.Vector2D.cartesian(-x, -y).rotate(-heading);
        return new Pose(invTrans.x(), invTrans.y(), -heading);
    }

    @Override
    public Twist log() {
        double h = Angle.normalizeSigned(heading);
        double eps = 1e-6;
        if (Math.abs(h) < eps) {
            // Small-angle: Jacobian ≈ I
            return new Twist(x, y, h);
        }

        double A = Math.sin(h) / h;
        double B = (1.0 - Math.cos(h)) / h;
        double denom = A * A + B * B;
        if (denom < 1e-12) {
            return new Twist(x, y, h);
        }

        double vx = (A * x + B * y) / denom;
        double vy = (-B * x + A * y) / denom;

        return new Twist(vx, vy, h);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + heading + ")";
    }

    /** Heading in degrees, in the same range as {@link #heading()}. */
    public double headingDegrees() {
        return Math.toDegrees(heading);
    }

    /** A copy of this pose with the heading set from degrees. */
    public Pose withHeadingDegrees(double degrees) {
        return withHeading(Math.toRadians(degrees));
    }

    /** Squared distance to {@code other} (no square root). */
    public double distanceSquared(com.pedropathing.math.Pose other) {
        double dx = x - other.x();
        double dy = y - other.y();
        return dx * dx + dy * dy;
    }

    /** Field-frame heading (radians) from this pose toward {@code other}. */
    public double headingTo(com.pedropathing.math.Pose other) {
        return Math.atan2(other.y() - y, other.x() - x);
    }
}
