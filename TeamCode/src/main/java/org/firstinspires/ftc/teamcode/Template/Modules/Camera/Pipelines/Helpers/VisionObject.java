package org.firstinspires.ftc.teamcode.Template.Modules.Camera.Pipelines.Helpers;

public class VisionObject implements Comparable<VisionObject> {
  public int left, right, top, bottom, centerX, centerY, area;
  public double sx = 0.06053, sy, yintx = -1.074, yinty, inchX, inchY;

  public VisionObject(int l, int r, int b, int t) {
    left = l;
    right = r;
    bottom = b;
    top = t;
    centerX = (left + right) / 2;
    centerY = (top + bottom) / 2;
    inchX =
        (sx * centerX)
            + yintx; // drag horizontally and record (centerX , inches from the camera horizontally)
    inchY =
        (sy * centerY)
            + yinty; // drag vertically and record (centerY, inches from the camera vertically)
    area = (Math.abs(right - left)) * (Math.abs(top - bottom));
    // input into linear regression calc to get slope and y int for each curve.
  }

  @Override
  public int compareTo(VisionObject o) {
    return Integer.compare(this.area, o.area);
  }
}
