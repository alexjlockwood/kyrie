package com.github.alexjlockwood.kyrie;

import android.animation.TimeInterpolator;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

abstract class KeyframeSet<V> {

  @NonNull
  @SafeVarargs
  public static <V> KeyframeSet<V> ofObject(
      @NonNull PropertyAnimation.ValueEvaluator<V> evaluator, V... values) {
    final int numKeyframes = values.length;
    final List<Keyframe<V>> keyframes = new ArrayList<>(Math.max(numKeyframes, 2));
    if (numKeyframes == 1) {
      keyframes.add(Keyframe.<V>of(0f));
      keyframes.add(Keyframe.of(1f, values[0]));
    } else {
      keyframes.add(Keyframe.of(0f, values[0]));
      for (int i = 1; i < numKeyframes; i++) {
        keyframes.add(Keyframe.of((float) i / (numKeyframes - 1), values[i]));
      }
    }
    return new ObjectKeyframeSet<>(evaluator, keyframes);
  }

  @NonNull
  @SafeVarargs
  public static <V> KeyframeSet<V> ofKeyframes(
      @NonNull PropertyAnimation.ValueEvaluator<V> evaluator, Keyframe<V>... keyframes) {
    final List<Keyframe<V>> list = new ArrayList<>(keyframes.length);
    //noinspection ForLoopReplaceableByForEach
    for (int i = 0, size = keyframes.length; i < size; i++) {
      list.add(keyframes[i]);
    }
    return new ObjectKeyframeSet<>(evaluator, list);
  }

  @NonNull
  public static KeyframeSet<PointF> ofPath(@NonNull Path path) {
    return new PathKeyframeSet(path);
  }

  @NonNull
  public abstract V getAnimatedValue(float fraction);

  private static final class ObjectKeyframeSet<V> extends KeyframeSet<V> {

    private final int numKeyframes;
    private final Keyframe<V> firstKeyframe;
    private final Keyframe<V> lastKeyframe;
    // Only used in the 2-keyframe case.
    private final TimeInterpolator interpolator;
    // Only used when there are not 2 keyframes.
    private final List<Keyframe<V>> keyframes;
    private final PropertyAnimation.ValueEvaluator<V> evaluator;

    public ObjectKeyframeSet(
        @NonNull PropertyAnimation.ValueEvaluator<V> evaluator,
        @NonNull List<Keyframe<V>> keyframes) {
      this.evaluator = evaluator;
      this.numKeyframes = keyframes.size();
      this.keyframes = keyframes;
      this.firstKeyframe = keyframes.get(0);
      this.lastKeyframe = keyframes.get(numKeyframes - 1);
      this.interpolator = lastKeyframe.getInterpolator();
    }

    @NonNull
    @Override
    public V getAnimatedValue(float fraction) {
      // Special-case optimization for the common case of only two keyframes.
      if (numKeyframes == 2) {
        if (interpolator != null) {
          fraction = interpolator.getInterpolation(fraction);
        }
        return evaluator.evaluate(fraction, firstKeyframe.getValue(), lastKeyframe.getValue());
      }
      if (fraction <= 0f) {
        final Keyframe<V> nextKeyframe = keyframes.get(1);
        final TimeInterpolator interpolator = nextKeyframe.getInterpolator();
        if (interpolator != null) {
          fraction = interpolator.getInterpolation(fraction);
        }
        final float prevFraction = firstKeyframe.getFraction();
        float intervalFraction =
            (fraction - prevFraction) / (nextKeyframe.getFraction() - prevFraction);
        return evaluator.evaluate(
            intervalFraction, firstKeyframe.getValue(), nextKeyframe.getValue());
      } else if (fraction >= 1f) {
        final Keyframe<V> prevKeyframe = keyframes.get(numKeyframes - 2);
        final TimeInterpolator interpolator = lastKeyframe.getInterpolator();
        if (interpolator != null) {
          fraction = interpolator.getInterpolation(fraction);
        }
        final float prevFraction = prevKeyframe.getFraction();
        final float intervalFraction =
            (fraction - prevFraction) / (lastKeyframe.getFraction() - prevFraction);
        return evaluator.evaluate(
            intervalFraction, prevKeyframe.getValue(), lastKeyframe.getValue());
      }
      Keyframe<V> prevKeyframe = firstKeyframe;
      for (int i = 1; i < numKeyframes; i++) {
        Keyframe<V> nextKeyframe = keyframes.get(i);
        if (fraction < nextKeyframe.getFraction()) {
          final TimeInterpolator interpolator = nextKeyframe.getInterpolator();
          final float prevFraction = prevKeyframe.getFraction();
          float intervalFraction =
              (fraction - prevFraction) / (nextKeyframe.getFraction() - prevFraction);
          // Apply getInterpolator on the proportional duration.
          if (interpolator != null) {
            intervalFraction = interpolator.getInterpolation(intervalFraction);
          }
          return evaluator.evaluate(
              intervalFraction, prevKeyframe.getValue(), nextKeyframe.getValue());
        }
        prevKeyframe = nextKeyframe;
      }
      // Shouldn't reach here.
      return lastKeyframe.getValue();
    }
  }

  private static final class PathKeyframeSet extends KeyframeSet<PointF> {
    private static final int FRACTION_OFFSET = 0;
    private static final int X_OFFSET = 1;
    private static final int Y_OFFSET = 2;
    private static final int NUM_COMPONENTS = 3;

    private final PointF tempPointF = new PointF();
    private final float[] keyframeData;

    public PathKeyframeSet(@NonNull Path path) {
      if (path.isEmpty()) {
        throw new IllegalArgumentException("The path must not be empty");
      }
      keyframeData = PathCompat.approximate(path, 0.5f);
    }

    @NonNull
    @Override
    public PointF getAnimatedValue(float fraction) {
      final int numPoints = keyframeData.length / NUM_COMPONENTS;
      if (fraction < 0) {
        return interpolateInRange(fraction, 0, 1);
      } else if (fraction > 1) {
        return interpolateInRange(fraction, numPoints - 2, numPoints - 1);
      } else if (fraction == 0) {
        return pointForIndex(0);
      } else if (fraction == 1) {
        return pointForIndex(numPoints - 1);
      } else {
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
    }

    @NonNull
    private PointF interpolateInRange(float fraction, int startIndex, int endIndex) {
      final int startBase = (startIndex * NUM_COMPONENTS);
      final int endBase = (endIndex * NUM_COMPONENTS);
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

    @NonNull
    private PointF pointForIndex(int index) {
      final int base = (index * NUM_COMPONENTS);
      final int xOffset = base + X_OFFSET;
      int yOffset = base + Y_OFFSET;
      tempPointF.set(keyframeData[xOffset], keyframeData[yOffset]);
      return tempPointF;
    }

    private static float lerp(float a, float b, @FloatRange(from = 0f, to = 1f) float t) {
      return a + (b - a) * t;
    }
  }
}
