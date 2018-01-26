package com.github.alexjlockwood.kyrie;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

import java.util.ArrayList;
import java.util.List;

final class PathCompat {
  private static final int MAX_NUM_POINTS = 100;
  private static final int FRACTION_OFFSET = 0;
  private static final int X_OFFSET = 1;
  private static final int Y_OFFSET = 2;
  private static final int NUM_COMPONENTS = 3;

  private PathCompat() {}

  @Size(multiple = 3)
  @NonNull
  public static float[] approximate(
      @NonNull Path path, @FloatRange(from = 0f) float acceptableError) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      return path.approximate(acceptableError);
    }
    if (acceptableError < 0) {
      throw new IllegalArgumentException("acceptableError must be greater than or equal to 0");
    }
    // Measure the total length the whole pathData.
    final PathMeasure measureForTotalLength = new PathMeasure(path, false);
    float totalLength = 0;
    // The sum of the previous contour plus the current one. Using the sum here
    // because we want to directly subtract from it later.
    final List<Float> contourLengths = new ArrayList<>();
    contourLengths.add(0f);
    do {
      final float pathLength = measureForTotalLength.getLength();
      totalLength += pathLength;
      contourLengths.add(totalLength);
    } while (measureForTotalLength.nextContour());

    // Now determine how many sample points we need, and the step for next sample.
    final PathMeasure pathMeasure = new PathMeasure(path, false);

    final int numPoints = Math.min(MAX_NUM_POINTS, (int) (totalLength / acceptableError) + 1);

    final float[] coords = new float[NUM_COMPONENTS * numPoints];
    final float[] position = new float[2];

    int contourIndex = 0;
    final float step = totalLength / (numPoints - 1);
    float currentDistance = 0;

    // For each sample point, determine whether we need to move on to next contour.
    // After we find the right contour, then sample it using the current distance value minus
    // the previously sampled contours' total length.
    for (int i = 0; i < numPoints; i++) {
      pathMeasure.getPosTan(currentDistance, position, null);

      coords[i * NUM_COMPONENTS + FRACTION_OFFSET] = currentDistance / totalLength;
      coords[i * NUM_COMPONENTS + X_OFFSET] = position[0];
      coords[i * NUM_COMPONENTS + Y_OFFSET] = position[1];

      currentDistance += step;
      if ((contourIndex + 1) < contourLengths.size()
          && currentDistance > contourLengths.get(contourIndex + 1)) {
        currentDistance -= contourLengths.get(contourIndex + 1);
        contourIndex++;
        pathMeasure.nextContour();
      }
    }

    return coords;
  }
}
