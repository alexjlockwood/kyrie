package com.github.alexjlockwood.kyrie;

import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.NonNull;

import com.github.alexjlockwood.kyrie.Animation.ValueEvaluator;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstracts a collection of {@link Keyframe} objects and is used to calculate values between those
 * keyframes for a given {@link Animation}.
 *
 * @param <T> The keyframe value type.
 */
abstract class KeyframeSet<T> {

  /** @return An {@link ObjectKeyframeSet} with evenly distributed keyframe values. */
  @NonNull
  public static <T> KeyframeSet<T> ofObject(ValueEvaluator<T> evaluator, T[] values) {
    final int numKeyframes = values.length;
    final List<Keyframe<T>> keyframes = new ArrayList<>(Math.max(numKeyframes, 2));
    if (numKeyframes == 1) {
      keyframes.add(Keyframe.<T>of(0f));
      keyframes.add(Keyframe.of(1f, values[0]));
    } else {
      keyframes.add(Keyframe.of(0f, values[0]));
      for (int i = 1; i < numKeyframes; i++) {
        keyframes.add(Keyframe.of((float) i / (numKeyframes - 1), values[i]));
      }
    }
    return new ObjectKeyframeSet<>(evaluator, keyframes);
  }

  /** @return An {@link ObjectKeyframeSet} with the given keyframe values. */
  @NonNull
  public static <T> KeyframeSet<T> ofObject(ValueEvaluator<T> evaluator, Keyframe<T>[] values) {
    final List<Keyframe<T>> list = new ArrayList<>(values.length);
    //noinspection ForLoopReplaceableByForEach
    for (int i = 0, size = values.length; i < size; i++) {
      list.add(values[i]);
    }
    return new ObjectKeyframeSet<>(evaluator, list);
  }

  /** @return A {@link PathKeyframeSet} that estimates motion along the given path. */
  @NonNull
  public static KeyframeSet<PointF> ofPath(Path path) {
    return new PathKeyframeSet(path);
  }

  /**
   * Gets the animated value, given the elapsed fraction of the animation (interpolated by the
   * animation's interpolator) and the evaluator used to calculate in-between values. This function
   * maps the input fraction to the appropriate keyframe interval and a fraction between them and
   * returns the interpolated value. Note that the input fraction may fall outside the [0,1] bounds,
   * if the animation's interpolator made that happen (e.g., a spring interpolation that might send
   * the fraction past 1.0). We handle this situation by just using the two keyframes at the
   * appropriate end when the value is outside those bounds.
   *
   * @param fraction The elapsed fraction of the animation.
   * @return The animated value.
   */
  @NonNull
  public abstract T getAnimatedValue(float fraction);

  /** @return The list of keyframes contained by this keyframe set. */
  @NonNull
  public abstract List<Keyframe<T>> getKeyframes();
}
