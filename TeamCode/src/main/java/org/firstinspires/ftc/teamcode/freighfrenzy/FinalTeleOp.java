package org.firstinspires.ftc.teamcode.freighfrenzy;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.TouchSensor;
import org.firstinspires.ftc.teamcode.clawstuff.template;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

@TeleOp
public class FinalTeleOp extends template {

    private DcMotor frontLeftMotor, rearLeftMotor, frontRightMotor, rearRightMotor, duckMotor;
    public TouchSensor limit;
    static final double MAX_POSITION = 1;
    static final double MIN_POSITION = 0;

    public void runOpMode() {
        frontLeftMotor = hardwareMap.dcMotor.get("leftFront");
        rearLeftMotor = hardwareMap.dcMotor.get("leftRear");
        frontRightMotor = hardwareMap.dcMotor.get("rightFront");
        rearRightMotor = hardwareMap.dcMotor.get("rightRear");
        duckMotor = hardwareMap.dcMotor.get("duckMotor");

        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        initialize();
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            double horizontal = -gamepad1.left_stick_x;
            double vertical = -gamepad1.left_stick_y;
            double angle = -gamepad1.right_stick_x;

            drive.setWeightedDrivePower(new Pose2d(vertical, horizontal, angle)); // in roadrunner x is vertical and y is horizontal
            drive.update();


            if (this.gamepad1.right_trigger > 0.4) {
                double power = 0.5;
                while(this.gamepad1.right_trigger > 0.4){
                    power += 0.0025;
                    duckMotor.setPower(power);
                }
            } else if (this.gamepad1.left_trigger > 0.4) {
                double power = -0.5;
                while (this.gamepad1.left_trigger > 0.4) {
                    power -= 0.0025;
                    duckMotor.setPower(power);
                }
            } else {
                duckMotor.setPower(0);
            }

            telemetry.addData("FrontLeftPower", frontLeftMotor.getPower());
            telemetry.addData("BackLeftPower", rearLeftMotor.getPower());
            telemetry.addData("FrontRightPower", frontRightMotor.getPower());
            telemetry.addData("BackRightPower", rearRightMotor.getPower());

            telemetry.addData("rotationPosition", armEncoder.getCurrentPosition());
            telemetry.addData("intakeMotorPower", intakeMotor.getPower());
            telemetry.addData("servoPos", directionServo.getPosition());
            telemetry.addData("armMoving", armMoving);

            if (gamepad2.right_trigger > 0.2) {
                intakeMotor.setPower(-1);
            } else if (gamepad2.left_trigger > 0.2) {
                intakeMotor.setPower(1);
            } else {
                intakeMotor.setPower(0);
            }

            if(!armMoving){
                double x = gamepad2.left_stick_x;
                telemetry.addData("arm almoa", x);
                if (armRotationMotor.getCurrentPosition() < 800 && x > 0) {
                    armRotationMotor.setPower(x * 0.5);
                } else if (armRotationMotor.getCurrentPosition() > 800 && x > 0) {
                    armRotationMotor.setPower(x * 0.75);
                } else if (armRotationMotor.getCurrentPosition() > 800 && x < 0) {
                    armRotationMotor.setPower(x * 0.5);
                } else {
                    armRotationMotor.setPower(x * 0.75);
                }
            }

            if (gamepad2.left_bumper) {
                armEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                armEncoder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            }
            double servoInput = gamepad2.right_stick_x;
            if (servoInput > 0.2 && directionServo.getPosition() < MAX_POSITION) {
                directionServo.setPosition(directionServo.getPosition() + 0.02);
            }
            if (servoInput < -0.2 && directionServo.getPosition() > MIN_POSITION) {
                directionServo.setPosition(directionServo.getPosition() - 0.02);
            }
            if(gamepad2.x){
                targetPosition = 2400;
                directionServo.setPosition(0.38);
                armMoving = true;
            }
            if(gamepad2.y){
                targetPosition = 3430;
                directionServo.setPosition(0.22);
                armMoving = true;
            }
            if(gamepad2.a){
                targetPosition = 0;
                directionServo.setPosition(0.06);
                armMoving = true;
            }
            if(armMoving){
                if(armEncoder.getCurrentPosition() < targetPosition+20 && armEncoder.getCurrentPosition() > targetPosition-20){
                    if(recheck){
                        armMoving = false;
                        armRotationMotor.setPower(0);
                        recheck = false;
                    }else{
                        armRotationMotor.setPower(0);
                        sleep(500);
                        recheck = true;
                    }
                }
                else{
                    if(armEncoder.getCurrentPosition() < targetPosition && targetPosition-armEncoder.getCurrentPosition() > 200){
                        armRotationMotor.setPower(-1);
                    }else if(armEncoder.getCurrentPosition() < targetPosition && targetPosition-armEncoder.getCurrentPosition() < 200){
                        armRotationMotor.setPower(-0.15);
                    }else if(armEncoder.getCurrentPosition() > targetPosition && armEncoder.getCurrentPosition()-targetPosition >200){
                        armRotationMotor.setPower(0.7);
                    }else{
                        armRotationMotor.setPower(0.15);
                    }
                }
            }
            telemetry.update();
        }
    }
}
