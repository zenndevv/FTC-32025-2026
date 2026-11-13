package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "Main Drive", group = "Competition")
public class Teleop32025 extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        // Control 0
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeft");

        // Control 3
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeft");

        // Control 1
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRight");

        // Control 2
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRight");

        // Control 3
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Control 2
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Expansion 2
        DcMotorEx shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        shooter.setDirection(DcMotorSimple.Direction.REVERSE);
//        shooter.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // Expansion 0
        DcMotor intake = hardwareMap.dcMotor.get("intake");

        // Expansion 1
        DcMotor transfer = hardwareMap.dcMotor.get("transfer");
        transfer.setDirection(DcMotorSimple.Direction.REVERSE);

        //int power = 1600;

        IMU imu = hardwareMap.get(IMU.class, "imu");
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD));
        imu.initialize(parameters);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double x = -gamepad1.left_stick_y;
            double y = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;

//            if (gamepad1.dpadUpWasPressed()) {
//                power += 50;
//            }
//            if (gamepad1.dpadDownWasPressed()) {
//                power -= 50;
//            }

            shooter.setPower(0.60);

//            telemetry.addData("Target Power", power);
//            telemetry.addData("Actual Flywheel", shooter.getVelocity());
            telemetry.addData("Heading (Deg)", imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));

            if (gamepad1.options) {
                imu.resetYaw();
            }

            if (gamepad1.right_trigger > 0.4) {
                intake.setPower(1);
                transfer.setPower(1);
            } else if (gamepad1.left_trigger > 0.4) {
                intake.setPower(1);
                transfer.setPower(gamepad1.left_bumper ? 1 : 0);
            } else {
                intake.setPower(0);
                transfer.setPower(gamepad1.left_bumper ? 1 : 0);
            }

            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

            double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

            rotX = rotX * 1.1;

            double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
            double frontLeftPower = (rotY + rotX - rx) / denominator;
            double backLeftPower = (rotY - rotX + rx) / denominator;
            double frontRightPower = (rotY - rotX - rx) / denominator;
            double backRightPower = (rotY + rotX + rx) / denominator;

            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);

            telemetry.update();
        }
    }
}