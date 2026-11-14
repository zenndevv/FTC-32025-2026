package org.firstinspires.ftc.teamcode.manual;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "SimpleManualOpMode", group = "TeleOp")
public class SimpleManualOpMode extends LinearOpMode {

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor spiralOuttake;

    // Largest change in drive power per loop. Lower = smoother but slower to respond.
    private static final double MAX_STEP = 0.1;
    // Stick values smaller than this are ignored (stick drift).
    private static final double DEADBAND = 0.05;

    @Override
    public void runOpMode() throws InterruptedException {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        spiralOuttake = hardwareMap.get(DcMotor.class, "spiralOuttake");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        spiralOuttake.setDirection(DcMotorSimple.Direction.FORWARD);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        spiralOuttake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        spiralOuttake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addLine("ready!");
        telemetry.update();

        waitForStart();

        double lastForward = 0;
        double lastStrafe = 0;
        double lastTurn = 0;

        while (opModeIsActive()) {
            // Deadband and squaring, then limit how fast each value can change
            double forward = ramp(shape(-gamepad1.left_stick_y), lastForward, MAX_STEP);
            double strafe = ramp(shape(gamepad1.left_stick_x), lastStrafe, MAX_STEP);
            double turn = ramp(shape(gamepad1.right_stick_x), lastTurn, MAX_STEP);

            lastForward = forward;
            lastStrafe = strafe;
            lastTurn = turn;

            double frontLeftPower = forward + strafe + turn;
            double frontRightPower = forward - strafe - turn;
            double backLeftPower = forward - strafe + turn;
            double backRightPower = forward + strafe - turn;

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

        stopAllMotors();
    }

    // Ignores a small dead zone, then squares the input (keeping the sign) for finer control
    private double shape(double x) {
        if (Math.abs(x) < DEADBAND) {
            return 0;
        }
        return Math.copySign(x * x, x);
    }

    // Moves toward the target by at most maxStep per loop
    private double ramp(double target, double current, double maxStep) {
        return current + Math.max(-maxStep, Math.min(maxStep, target - current));
    }

    private void stopAllMotors() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
        spiralOuttake.setPower(0);
    }
}
