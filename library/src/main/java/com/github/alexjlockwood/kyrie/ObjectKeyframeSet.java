package com.github.alexjlockwood.kyrie;

import android.animation.TimeInterpolator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.alexjlockwood.kyrie.Animation.ValueEvaluator;

import java.util.List;

/**
 * Abstracts a collection of {@link Keyframe} objects and is used to calculate values between those
 * keyframes for a given {@link Animation}.
 *
 * @param <T> The keyframe value type.
 */
final class ObjectKeyframeSet<T> extends KeyframeSet<T> {

  private final int numKeyframes;
  @NonNull private final Keyframe<T> firstKf;
  @NonNull private final Keyframe<T> lastKf;
  // Only used in the 2-keyframe case.
  @Nullable private final TimeInterpolator interpolator;
  // Only used when there are more than 2 keyframes.
  @NonNull private final List<Keyframe<T>> keyframes;
  @NonNull private final ValueEvaluator<T> evaluator;

  public ObjectKeyframeSet(ValueEvaluator<T> evaluator, List<Keyframe<T>> keyframes) {
    this.evaluator = evaluator;
    this.numKeyframes = keyframes.size();
    this.keyframes = keyframes;
    this.firstKf = keyframes.get(0);
    this.lastKf = keyframes.get(numKeyframes - 1);
    this.interpolator = lastKf.getInterpolator();
  }

  @NonNull
  @Override
  public T getAnimatedValue(float fraction) {
    // Special-case optimization for the common case of only two keyframes.
    if (numKeyframes == 2) {
      if (interpolator != null) {
        fraction = interpolator.getInterpolation(fraction);
      }
      return evaluator.evaluate(fraction, firstKf.getValue(), lastKf.getValue());
    }
    if (fraction <= 0) {
      final Keyframe<T> nextKf = keyframes.get(1);
      final TimeInterpolator interpolator = nextKf.getInterpolator();
      if (interpolator != null) {
        fraction = interpolator.getInterpolation(fraction);
      }
      final float prevFraction = firstKf.getFraction();
      final float intervalFraction =
          (fraction - prevFraction) / (nextKf.getFraction() - prevFraction);
      return evaluator.evaluate(intervalFraction, firstKf.getValue(), nextKf.getValue());
    }
    if (fraction >= 1) {
      final Keyframe<T> prefKf = keyframes.get(numKeyframes - 2);
      final TimeInterpolator interpolator = lastKf.getInterpolator();
      if (interpolator != null) {
        fraction = interpolator.getInterpolation(fraction);
      }
      final float prevFraction = prefKf.getFraction();
      final float intervalFraction =
          (fraction - prevFraction) / (lastKf.getFraction() - prevFraction);
      return evaluator.evaluate(intervalFraction, prefKf.getValue(), lastKf.getValue());
    }
    Keyframe<T> prevKf = firstKf;
    for (int i = 1; i < numKeyframes; i++) {
      final Keyframe<T> nextKf = keyframes.get(i);
      if (fraction < nextKf.getFraction()) {
        final TimeInterpolator interpolator = nextKf.getInterpolator();
        final float prevFraction = prevKf.getFraction();
        float intervalFraction = (fraction - prevFraction) / (nextKf.getFraction() - prevFraction);
        // Apply getInterpolator on the proportional duration.
        if (interpolator != null) {
          intervalFraction = interpolator.getInterpolation(intervalFraction);
        }
        return evaluator.evaluate(intervalFraction, prevKf.getValue(), nextKf.getValue());
      }
      prevKf = nextKf;
    }
    // Shouldn't get here.
    return lastKf.getValue();
  }

  @NonNull
  @Override
  public List<Keyframe<T>> getKeyframes() {
    return keyframes;
  }
}
