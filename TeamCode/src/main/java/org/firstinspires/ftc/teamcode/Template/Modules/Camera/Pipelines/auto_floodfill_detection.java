package org.firstinspires.ftc.teamcode.Template.Modules.Camera.Pipelines;

import java.util.ArrayList;
import java.util.Stack;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Template.Modules.Camera.Pipelines.Helpers.Pos;
import org.firstinspires.ftc.teamcode.Template.Modules.Camera.Pipelines.Helpers.VisionObject;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.PipelineRecordingParameters;

// Use Linear regression for distance based on cluster size + linear regression for x and y
public class auto_floodfill_detection extends OpenCvPipeline {
  // CV parameters
  Telemetry telemetry;
  public Scalar lower = new Scalar(0, 136, 85);
  public Scalar upper = new Scalar(255, 255, 255);
  public Mat img;
  private Mat hsvMat = new Mat();
  private Mat binaryMat = new Mat();
  private boolean saveImg = false;
  private OpenCvCamera camera;

  // Floodfill parameters
  public ArrayList<VisionObject> objs = new ArrayList<>();
  private static Mat grid; // the grid itself
  private static int rowNum;
  private static int colNum; // grid dimensions, rows and columns
  private static boolean[][] visited; // keeps track of which nodes have been visited
  private static int currSize = 0; // reset to 0 each time we start a new component
  public static final int R_CHANGE[] = {0, 1, 0, -1};
  public static final int C_CHANGE[] = {1, 0, -1, 0};
  private static int l = -1, right = Integer.MAX_VALUE, t = Integer.MAX_VALUE, b = -1;

  public auto_floodfill_detection(Telemetry telemetry) {
    this.telemetry = telemetry;
    saveImg = false;
  }

  public auto_floodfill_detection() {
    saveImg = false;
  }

  public auto_floodfill_detection(boolean save, OpenCvCamera camera) {
    saveImg = save;
    this.camera = camera;
  }

  @Override
  public Mat processFrame(Mat input) {
    // objs = new ArrayList<>();
    Imgproc.cvtColor(input, hsvMat, Imgproc.COLOR_RGB2HSV);
    Core.inRange(hsvMat, lower, upper, binaryMat);
    grid = binaryMat;

    // objs = new ArrayList<>();
    rowNum = (int) input.size().height;
    colNum = (int) input.size().width;
    visited = new boolean[rowNum][colNum];
    double w1 = 0, w2 = 0;
    boolean f = false;
    // process the pixel value for each rectangle  (255 = W, 0 = B)
    for (int i = 133; i < input.size().height; i++) {
      for (int j = 0; j < input.size().width; j++) {
        if (!visited[i][j] && binaryMat.get(i, j)[0] == 255) {
          l = Integer.MAX_VALUE;
          right = -1;
          t = Integer.MAX_VALUE;
          b = -1;
          currSize = 0;
          floodfill(i, j, 255);
          Imgproc.rectangle(input, new Point(l, t), new Point(right, b), new Scalar(255, 0, 0));
          VisionObject obj = new VisionObject(l, right, b, t);
          objs.add(obj);
        }
      }
    }

    if (saveImg) {
      saveImg = false;
      // saveMatToDisk(input, "auto_rect_img");
    }

    return input;
  }

  private static void floodfill(int r, int c, int color) {
    if ((r < 0 || r >= rowNum || c < 0 || c >= colNum) // if out of bounds
        || grid.get(r, c)[0] != color // wrong color
        || visited[r][c] // already visited this square
    ) return;

    Stack<Pos> frontier = new Stack<>();
    frontier.push(new Pos(r, c));
    while (!frontier.isEmpty()) {
      Pos curr = frontier.pop();
      r = curr.row;
      c = curr.col;

      if (r < 0
          || r >= rowNum
          || c < 0
          || c >= colNum
          || grid.get(r, c)[0] != color
          || visited[r][c]) continue;

      t = Math.min(t, r);
      b = Math.max(b, r);
      right = Math.max(right, c);
      l = Math.min(l, c);

      visited[r][c] = true;
      for (int i = 0; i < 4; i++) {
        frontier.add(new Pos(r + R_CHANGE[i], c + C_CHANGE[i]));
      }
    }
  }

  @Override
  public void onViewportTapped() {
    boolean toggleRecording = false;
    toggleRecording = !toggleRecording;

    if (toggleRecording) {
      /*
       * This is all you need to do to start recording.
       */
      camera.startRecordingPipeline(
          new PipelineRecordingParameters.Builder()
              .setBitrate(4, PipelineRecordingParameters.BitrateUnits.Mbps)
              .setEncoder(PipelineRecordingParameters.Encoder.H264)
              .setOutputFormat(PipelineRecordingParameters.OutputFormat.MPEG_4)
              .setFrameRate(30)
              .setPath("/sdcard/EasyOpenCV/pipeline_rec.mp4")
              .build());
    } else {

    }

    /*
     * Note: if you don't stop recording by yourself, it will be automatically
     * stopped for you at the end of your OpMode
     *.stopRecordingPipeline(); */
  }
}
