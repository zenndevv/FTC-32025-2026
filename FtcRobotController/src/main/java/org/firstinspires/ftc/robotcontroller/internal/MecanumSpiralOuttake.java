package org.firstinspires.ftc.robotcontroller.internal;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Mecanum + Spiral Outtake", group = "TeleOp")
public class MecanumSpiralOuttake extends LinearOpMode {

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor spiralOuttake;

    @Override
    public void runOpMode() {
        // These names must match the Robot Configuration in the Driver Station app.
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        spiralOuttake = hardwareMap.get(DcMotor.class, "spiralOuttake");

        // Reverse the motors on one side so positive power drives the robot forward.
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        // Change this direction if positive outtake power runs the spiral the wrong way.
        spiralOuttake.setDirection(DcMotorSimple.Direction.FORWARD);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        spiralOuttake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // This example controls drive power directly rather than using encoders.
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        spiralOuttake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addLine("Ready. Press Play to start.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Gamepad 1: left stick moves, right stick turns.
            double forward = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn = gamepad1.right_stick_x;

            // Mecanum wheel power mix.
            double frontLeftPower = forward + strafe + turn;
            double frontRightPower = forward - strafe - turn;
            double backLeftPower = forward - strafe + turn;
            double backRightPower = forward + strafe - turn;

            // Normalize to keep every motor power within [-1, 1].
            double maxPower = Math.max(
                    1.0,
                    Math.max(
                            Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)),
                            Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))
                    )
            );

            frontLeft.setPower(frontLeftPower / maxPower);
            frontRight.setPower(frontRightPower / maxPower);
            backLeft.setPower(backLeftPower / maxPower);
            backRight.setPower(backRightPower / maxPower);

            // Gamepad 1 triggers run the spiral outtake.
            // Right trigger: outtake; left trigger: reverse/jam clear.
            double outtakePower = gamepad1.right_trigger - gamepad1.left_trigger;
            spiralOuttake.setPower(outtakePower);

            telemetry.addData("Drive", "Forward %.2f  Strafe %.2f  Turn %.2f",
                    forward, strafe, turn);
            telemetry.addData("Outtake Power", "%.2f", outtakePower);
            telemetry.update();
        }

        // Stop all motors when the OpMode ends.
        stopMotors();
    }

    private void stopMotors() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
        spiralOuttake.setPower(0);
    }
}