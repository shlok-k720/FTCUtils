package com.rmsftc.utils.ftc.hardware;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.rmsftc.utils.control.Limiter;
import com.rmsftc.utils.control.LimiterConfig;
import com.rmsftc.utils.ftc.DaemonManager;


public class SmartMotor {
    DcMotorEx motor;

    Limiter limiter;
    private double targetPower = 0;

    public SmartMotor(DcMotorEx motor, LimiterConfig config) {
        this.motor = motor;
        this.limiter = config.limiter();
    }

    public void setPower(double power) {
        this.targetPower = power;
    }

    /**
     * Pushes the last {@link #setPower(double)} target through the
     * limiter to the motor. Call once per loop with the loop's dt in
     * seconds; the limiter ramps the power toward the target by at most
     * {@code maxRate * dt} per call.
     */
    public void update(double dtSeconds) {
        motor.setPower(limiter.limit(targetPower, dtSeconds));
    }

}
