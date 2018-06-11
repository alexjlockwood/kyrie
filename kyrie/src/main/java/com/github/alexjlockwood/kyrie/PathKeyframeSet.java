package com.github.alexjlockwood.kyrie;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PathKeyframeSet relies on approximating the Path as a series of line segments. The line segments
 * are recursively divided until there is less than 1/2 pixel error between the lines and the curve.
 * Each point of the line segment is converted to a {@link Keyframe} and a linear interpolation
 * between keyframes creates a good approximation of the curve.
 */
final class PathKeyframeSet extends KeyframeSet<PointF> {
  private static final int MAX_NUM_POINTS = 100;
  private static final int FRACTION_OFFSET = 0;
  private static final int X_OFFSET = 1;
  private static final int Y_OFFSET = 2;
  private static final int NUM_COMPONENTS = 3;

  private final PointF tempPointF = new PointF();
  private final float[] keyframeData;

  public PathKeyframeSet(Path path) {
    if (path.isEmpty()) {
      throw new IllegalArgumentException("The path must not be empty");
    }
    keyframeData = approximate(path, 0.5f);
  }

  @NonNull
  @Override
  public PointF getAnimatedValue(float fraction) {
    final int numPoints = keyframeData.length / NUM_COMPONENTS;
    if (fraction < 0) {
      return interpolateInRange(fraction, 0, 1);
    }
    if (fraction > 1) {
      return interpolateInRange(fraction, numPoints - 2, numPoints - 1);
    }
    if (fraction == 0) {
      return pointForIndex(0);
    }
    if (fraction == 1) {
      return pointForIndex(numPoints - 1);
    }
    // Binary search for the correct section.
    int low = 0;
    int high = numPoints - 1;
    while (low <= high) {
      final int mid = (low + high) / 2;
      final float midFraction = keyframeData[(mid * NUM_COMPONENTS) + FRACTION_OFFSET];
      if (fraction < midFraction) {
        high = mid - 1;
      } else if (fraction > midFraction) {
        low = mid + 1;
      } else {
        return pointForIndex(mid);
      }
    }
    // Now high is below the fraction and low is above the fraction.
    return interpolateInRange(fraction, high, low);
  }

  @NonNull
  @Override
  public List<Keyframe<PointF>> getKeyframes() {
    return Collections.emptyList();
  }

  @NonNull
  private PointF interpolateInRange(float fraction, int startIndex, int endIndex) {
    final int startBase = startIndex * NUM_COMPONENTS;
    final int endBase = endIndex * NUM_COMPONENTS;
    final float startFraction = keyframeData[startBase + FRACTION_OFFSET];
    final float endFraction = keyframeData[endBase + FRACTION_OFFSET];
    final float intervalFraction = (fraction - startFraction) / (endFraction - startFraction);
    final float startX = keyframeData[startBase + X_OFFSET];
    final float endX = keyframeData[endBase + X_OFFSET];
    final float startY = keyframeData[startBase + Y_OFFSET];
    final float endY = keyframeData[endBase + Y_OFFSET];
    final float x = lerp(startX, endX, intervalFraction);
    final float y = lerp(startY, endY, intervalFraction);
    tempPointF.set(x, y);
    return tempPointF;
  }

  private static float lerp(float a, float b, @FloatRange(from = 0f, to = 1f) float t) {
    return a + (b - a) * t;
  }

  @NonNull
  private PointF pointForIndex(int index) {
    final int base = index * NUM_COMPONENTS;
    final int xOffset = base + X_OFFSET;
    final int yOffset = base + Y_OFFSET;
    tempPointF.set(keyframeData[xOffset], keyframeData[yOffset]);
    return tempPointF;
  }

  /** Implementation of {@link Path#approximate(float)} for pre-O devices. */
  @NonNull
  @Size(multiple = 3)
  private static float[] approximate(
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
    final List<Float> summedContourLengths = new ArrayList<>();
    summedContourLengths.add(0f);
    do {
      final float pathLength = measureForTotalLength.getLength();
      totalLength += pathLength;
      summedContourLengths.add(totalLength);
    } while (measureForTotalLength.nextContour());

    // Now determine how many sample points we need, and the step for next sample.
    final PathMeasure pathMeasure = new PathMeasure(path, false);

    final int numPoints = Math.min(MAX_NUM_POINTS, (int) (totalLength / acceptableError) + 1);

    final float[] coords = new float[NUM_COMPONENTS * numPoints];
    final float[] position = new float[2];

    int contourIndex = 0;
    final float step = totalLength / (numPoints - 1);
    float cumulativeDistance = 0;

    // For each sample point, determine whether we need to move on to next contour.
    // After we find the right contour, then sample it using the current distance value minus
    // the previously sampled contours' total length.
    for (int i = 0; i < numPoints; i++) {
      // The cumulative distance traveled minus the total length of the previous contours
      // (not including the current contour).
      final float contourDistance = cumulativeDistance - summedContourLengths.get(contourIndex);
      pathMeasure.getPosTan(contourDistance, position, null);

      coords[i * NUM_COMPONENTS + FRACTION_OFFSET] = cumulativeDistance / totalLength;
      coords[i * NUM_COMPONENTS + X_OFFSET] = position[0];
      coords[i * NUM_COMPONENTS + Y_OFFSET] = position[1];

      cumulativeDistance = Math.min(cumulativeDistance + step, totalLength);

      // Using a while statement is necessary in the rare case where step is greater than
      // the length a path contour.
      while (summedContourLengths.get(contourIndex + 1) < cumulativeDistance) {
        contourIndex++;
        pathMeasure.nextContour();
      }
    }

    coords[(numPoints - 1) * NUM_COMPONENTS + FRACTION_OFFSET] = 1f;
    return coords;
  }
}
