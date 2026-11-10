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

    public void update() {
        motor.setPower(limiter.limit(targetPower, 0.0));
    }

}
