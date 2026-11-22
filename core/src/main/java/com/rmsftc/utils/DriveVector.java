package com.rmsftc.utils;

import com.pedropathing.drivetrain.DrivePowers;
import com.rmsftc.utils.math.MathUtils;

import java.util.Locale;
import java.util.function.DoubleUnaryOperator;

/**
 * A mutable drive command (forward, strafe, turn) that extends Pedro's
 * {@link DrivePowers}, so it can be handed to Pedro drivetrains and to
 * {@code MecanumDrive} alike. Unlike Pedro's immutable powers, the
 * components can be adjusted in place (deadband, scaling, field-centric
 * rotation) before being applied.
 *
 * <p>Pedro Pathing core is a {@code compileOnly} dependency of this module.
 */
public class DriveVector extends DrivePowers {

    /** The zero drive command. Mutable like every DriveVector — copy before editing. */
    public static final DriveVector ZERO = new DriveVector();

    private double forward;
    private double strafe;
    private double turn;

    public DriveVector(double forward, double strafe, double turn) {
        super(forward, strafe, turn);
        this.forward = forward;
        this.strafe = strafe;
        this.turn = turn;
    }

    public DriveVector(DrivePowers powers) {
        this(powers.forward(), powers.strafe(), powers.turn());
    }

    public DriveVector() {
        this(0.0, 0.0, 0.0);
    }

    /**
     * A field-centric drive command: {@code forward}/{@code strafe} are in
     * field frame and are rotated into robot frame using
     * {@code headingRadians} (the robot's field heading).
     */
    public DriveVector(double forward, double strafe, double turn, double headingRadians) {
        this(
                forward * Math.cos(headingRadians) + strafe * Math.sin(headingRadians),
                -forward * Math.sin(headingRadians) + strafe * Math.cos(headingRadians),
                turn);
    }

    /** A turn-only drive command. */
    public DriveVector(double turn) {
        this(0.0, 0.0, turn);
    }

    @Override
    public double forward() {
        return forward;
    }

    @Override
    public double strafe() {
        return strafe;
    }

    @Override
    public double turn() {
        return turn;
    }

    public void forward(double forward) {
        this.forward = forward;
    }

    public void strafe(double strafe) {
        this.strafe = strafe;
    }

    public void turn(double turn) {
        this.turn = turn;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "(%.2f, %.2f, %.2f)", forward, strafe, turn);
    }

    /** Applies {@code op} to every component in place. Returns {@code this}. */
    public DriveVector doubleUnaryOperator(DoubleUnaryOperator op) {
        forward = op.applyAsDouble(forward);
        strafe = op.applyAsDouble(strafe);
        turn = op.applyAsDouble(turn);
        return this;
    }

    /**
     * Rotates this command from field frame into robot frame using the
     * robot's field heading (radians). Mutates and returns {@code this}.
     */
    public DriveVector fieldCentric(double headingRadians) {
        double cosA = Math.cos(headingRadians);
        double sinA = Math.sin(headingRadians);
        double newForward = forward * cosA + strafe * sinA;
        double newStrafe = -forward * sinA + strafe * cosA;
        forward = newForward;
        strafe = newStrafe;
        return this;
    }

    /**
     * Like {@link #fieldCentric(double)}, but first offsets the heading by
     * the alliance's field-centric offset (0° red, 180° blue), so driver
     * inputs stay field-relative on either alliance.
     */
    public DriveVector fieldCentric(double headingRadians, AllianceColor color) {
        return fieldCentric(MathUtils.wrapAngleRadians(
                headingRadians - Math.toRadians(color.fieldCentricOffset())));
    }

    public DriveVector copy() {
        return new DriveVector(forward, strafe, turn);
    }

    /** Multiplies every component by {@code v} in place. */
    public void scale(double v) {
        doubleUnaryOperator(op -> op * v);
    }
}
