package com.rmsftc.utils.ftc.hardware.drivetrain;

import com.rmsftc.utils.ftc.hardware.SmartMotor;

import java.util.Objects;

/**
 * A four-motor mecanum drive built on {@link SmartMotor}.
 *
 * <p>Call {@link #drive(double, double, double)} (robot-centric) or
 * {@link #driveFieldCentric(double, double, double, double)} once per loop
 * to set the wanted motion, then {@link #update(double)} with the loop's
 * dt in seconds to push the powers through each motor's limiter. This
 * mirrors {@link SmartMotor}'s own set-power / update split: {@code drive}
 * only stores targets, {@code update} is what moves the motors.
 *
 * <p>Conventions: {@code forward} is positive forward, {@code strafe} is
 * positive to the robot's right, {@code rotate} is positive
 * counter-clockwise (turning left). Wheel powers are the standard mecanum
 * mix, normalized so no wheel exceeds 1.0:
 *
 * <pre>
 * frontLeft  = forward + strafe + rotate
 * frontRight = forward - strafe - rotate
 * backLeft   = forward - strafe + rotate
 * backRight  = forward + strafe - rotate
 * </pre>
 *
 * <p>That mix assumes each motor is configured so positive power drives
 * its wheel forward — set the motor directions in hardware init
 * accordingly (in the FTC config, or with {@code DcMotor.setDirection}).
 * For field-centric driving, {@code headingRadians} is the robot's
 * heading in the field frame: 0 when the robot faces field forward,
 * increasing counter-clockwise.
 *
 * <pre>
 * MecanumDrive drive = new MecanumDrive(frontLeft, frontRight, backLeft, backRight);
 * // in the loop:
 * drive.drive(gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x);
 * drive.update(dtSeconds);
 * </pre>
 */
public class MecanumDrive {
    private final SmartMotor frontLeft;
    private final SmartMotor frontRight;
    private final SmartMotor backLeft;
    private final SmartMotor backRight;

    /** Last computed wheel powers (after normalization, before limiting): {FL, FR, BL, BR}. */
    private final double[] wheelPowers = new double[4];

    /**
     * Creates a drive from the four wheel motors, in the order
     * front-left, front-right, back-left, back-right.
     */
    public MecanumDrive(SmartMotor frontLeft, SmartMotor frontRight,
                        SmartMotor backLeft, SmartMotor backRight) {
        this.frontLeft = Objects.requireNonNull(frontLeft, "frontLeft");
        this.frontRight = Objects.requireNonNull(frontRight, "frontRight");
        this.backLeft = Objects.requireNonNull(backLeft, "backLeft");
        this.backRight = Objects.requireNonNull(backRight, "backRight");
    }

    /**
     * Robot-centric drive: sets the wanted motion. Positive
     * {@code forward} drives forward, positive {@code strafe} strafes
     * right, positive {@code rotate} turns counter-clockwise. Nothing
     * reaches the motors until {@link #update(double)}.
     */
    public void drive(double forward, double strafe, double rotate) {
        setWheelPowers(
                forward + strafe + rotate, // front left
                forward - strafe - rotate, // front right
                forward - strafe + rotate, // back left
                forward + strafe - rotate  // back right
        );
    }

    /**
     * Field-centric drive: {@code forward} and {@code strafe} are in the
     * field frame (forward = field forward, strafe = field right), and
     * {@code headingRadians} is the robot's heading in that frame: 0 when
     * the robot faces field forward, increasing counter-clockwise.
     * {@code rotate} is unchanged (rotation is already frame-free).
     * Nothing reaches the motors until {@link #update(double)}.
     */
    public void driveFieldCentric(double forward, double strafe, double rotate,
                                  double headingRadians) {
        double cos = Math.cos(headingRadians);
        double sin = Math.sin(headingRadians);
        double robotForward = forward * cos - strafe * sin;
        double robotStrafe = forward * sin + strafe * cos;
        drive(robotForward, robotStrafe, rotate);
    }

    /**
     * Pushes the last {@link #drive} targets through each motor's
     * limiter. Call once per loop with the loop's dt in seconds.
     */
    public void update(double dtSeconds) {
        frontLeft.update(dtSeconds);
        frontRight.update(dtSeconds);
        backLeft.update(dtSeconds);
        backRight.update(dtSeconds);
    }

    /**
     * Stops the robot: sets all wheel targets to zero. The motors
     * themselves stop on the next {@link #update(double)} (their
     * limiters ramp down from the current power).
     */
    public void stop() {
        drive(0.0, 0.0, 0.0);
    }

    /**
     * The last computed wheel powers (after normalization, before each
     * motor's limiter), in the order {frontLeft, frontRight, backLeft,
     * backRight}. Returns a copy.
     */
    public double[] getWheelPowers() {
        return wheelPowers.clone();
    }

    private void setWheelPowers(double frontLeft, double frontRight,
                                double backLeft, double backRight) {
        double max = Math.max(
                Math.max(Math.abs(frontLeft), Math.abs(frontRight)),
                Math.max(Math.abs(backLeft), Math.abs(backRight)));
        if (max > 1.0) {
            frontLeft /= max;
            frontRight /= max;
            backLeft /= max;
            backRight /= max;
        }
        wheelPowers[0] = frontLeft;
        wheelPowers[1] = frontRight;
        wheelPowers[2] = backLeft;
        wheelPowers[3] = backRight;
        this.frontLeft.setPower(frontLeft);
        this.frontRight.setPower(frontRight);
        this.backLeft.setPower(backLeft);
        this.backRight.setPower(backRight);
    }
}
