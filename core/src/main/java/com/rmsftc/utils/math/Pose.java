package com.rmsftc.utils.math;

import com.pedropathing.math.Twist;
import com.pedropathing.math.Velocity;

/**
 * A Pedro-compatible pose: extends {@link com.pedropathing.math.Pose} and
 * overrides every pose-returning operation so results stay in this type,
 * plus a few convenience helpers (degrees, squared distance, heading-to).
 *
 * <p>Because this <em>is</em> a Pedro {@code Pose}, it can be passed
 * anywhere Pedro expects one (paths, followers, localizers) with no
 * conversion. Pedro Pathing core is a {@code compileOnly} dependency of
 * this module: consumers using the geometry family declare Pedro
 * themselves, as they already do.
 */
public class Pose extends com.pedropathing.math.Pose {

    public Pose(double x, double y, double heading) {
        super(x, y, heading);
    }

    public Pose(double x, double y) {
        super(x, y);
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
        return fromPedro(com.pedropathing.math.Pose.interpolate(lowerPose, upperPose, ratio));
    }

    @Override
    public Pose withX(double x) {
        return fromPedro(super.withX(x));
    }

    @Override
    public Pose withY(double y) {
        return fromPedro(super.withY(y));
    }

    @Override
    public Pose withHeading(double heading) {
        return fromPedro(super.withHeading(heading));
    }

    @Override
    public Pose exp(Velocity velocity, double time) {
        return fromPedro(super.exp(velocity, time));
    }

    @Override
    public Pose exp(Velocity velocity) {
        return fromPedro(super.exp(velocity));
    }

    @Override
    public Pose exp(Twist twist, double time) {
        return fromPedro(super.exp(twist, time));
    }

    @Override
    public Pose exp(Twist twist) {
        return fromPedro(super.exp(twist));
    }

    @Override
    public Pose compose(com.pedropathing.math.Pose other) {
        return fromPedro(super.compose(other));
    }

    @Override
    public Pose plus(com.pedropathing.math.Pose other) {
        return fromPedro(super.plus(other));
    }

    @Override
    public Pose minus(com.pedropathing.math.Pose other) {
        return fromPedro(super.minus(other));
    }

    @Override
    public Pose times(double scalar) {
        return fromPedro(super.times(scalar));
    }

    @Override
    public Pose div(double scalar) {
        return fromPedro(super.div(scalar));
    }

    @Override
    public Pose invert() {
        return fromPedro(super.invert());
    }

    /** Heading in degrees, in the same range as {@link #heading()}. */
    public double headingDegrees() {
        return Math.toDegrees(heading());
    }

    /** A copy of this pose with the heading set from degrees. */
    public Pose withHeadingDegrees(double degrees) {
        return withHeading(Math.toRadians(degrees));
    }

    /** Squared distance to {@code other} (no square root). */
    public double distanceSquared(com.pedropathing.math.Pose other) {
        double dx = x() - other.x();
        double dy = y() - other.y();
        return dx * dx + dy * dy;
    }

    /** Field-frame heading (radians) from this pose toward {@code other}. */
    public double headingTo(com.pedropathing.math.Pose other) {
        return Math.atan2(other.y() - y(), other.x() - x());
    }
}
