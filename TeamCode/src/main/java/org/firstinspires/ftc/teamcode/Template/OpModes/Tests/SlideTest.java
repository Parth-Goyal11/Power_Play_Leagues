package org.firstinspires.ftc.teamcode.Template.OpModes.Tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.Template.Modules.Grabber;
import org.firstinspires.ftc.teamcode.Utils.Motor;

@Disabled
@TeleOp(name = "Temp_SlideTest", group = "Tests")
public class SlideTest extends LinearOpMode {
  @Override
  public void runOpMode() throws InterruptedException {
    Servo s = hardwareMap.get(Servo.class, "claw"),
        lv = hardwareMap.get(Servo.class, "v4bl"),
        rv = hardwareMap.get(Servo.class, "v4bl");

    Motor ls = new Motor(hardwareMap, "leftSlide"), rs = new Motor(hardwareMap, "rightSlide");
    Grabber grabber = new Grabber(ls, rs, lv, rv, s);
    int curPos = 0;
    boolean lU = false, lD = false, lL = false;

    waitForStart();
    while (opModeIsActive()) {
      if (gamepad1.dpad_up && !lU) {
        curPos += 10;
      }

      lU = gamepad1.dpad_up;

      if (gamepad1.dpad_down && !lD) {
        curPos -= 10;
      }
      lD = gamepad1.dpad_down;

      if (gamepad1.dpad_left && !lL) {
        grabber.raiseToPosition(curPos, 0.1);
      }
      lL = gamepad1.dpad_left;
    }
    telemetry.addData("pos", curPos);
    telemetry.update();
  }
}
