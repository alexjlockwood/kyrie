package com.github.alexjlockwood.kyrie;

import android.animation.TimeInterpolator;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.animation.LinearInterpolator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * An {@link Animation} encapsulates the information required to animate a single property of a
 * {@link Node}.
 *
 * @param <T> The animation's original value type.
 * @param <V> The animation's transformed value type.
 */
public final class Animation<T, V> {

  /**
   * Constructs and returns an {@link Animation} that animates between float values. A single value
   * implies that the value is the one being animated to, in which case the start value will be
   * derived from the property being animated and the target object when the animation is started.
   * Two values imply starting and ending values. More than two values imply a starting value,
   * values to animate through along the way, and an ending value (these values will be distributed
   * evenly across the duration of the animation).
   *
   * @param values A set of values that the animation will animate through over time.
   * @return A new {@link Animation}.
   */
  @NonNull
  public static Animation<Float, Float> ofFloat(Float... values) {
    return ofObject(new FloatValueEvaluator(), values);
  }

  /**
   * Same as {@link #ofFloat(Float...)} except with {@link Keyframe}s instead of float values.
   *
   * @param values A set of {@link Keyframe}s that the animation will animate through over time.
   * @return A new {@link Animation}.
   */
  @NonNull
  @SafeVarargs
  public static Animation<Float, Float> ofFloat(Keyframe<Float>... values) {
    return ofObject(new FloatValueEvaluator(), values);
  }

  /**
   * Constructs and returns an {@link Animation} that animates between color values. A single value
   * implies that the value is the one being animated to, in which case the start value will be
   * derived from the property being animated and the target object when the animation is started.
   * Two values imply starting and ending values. More than two values imply a starting value,
   * values to animate through along the way, and an ending value (these values will be distributed
   * evenly across the duration of the animation).
   *
   * @param values A set of values that the animation will animate through over time.
   * @return A new {@link Animation}.
   */
  @NonNull
  public static Animation<Integer, Integer> ofArgb(Integer... values) {
    return ofObject(new ArgbValueEvaluator(), values);
  }

  /**
   * Same as {@link #ofArgb(Integer...)} except with {@link Keyframe}s instead of color values.
   *
   * @param values A set of {@link Keyframe}s that the animation will animate through over time.
   * @return A new {@link Animation}.
   */
  @NonNull
  @SafeVarargs
  public static Animation<Integer, Integer> ofArgb(Keyframe<Integer>... values) {
    return ofObject(new ArgbValueEvaluator(), values);
  }

  /**
   * Constructs and returns an {@link Animation} that animates between float[] values. A single
   * value implies that the value is the one being animated to, in which case the start value will
   * be derived from the property being animated and the target object when the animation is
   * started. Two values imply starting and ending values. More than two values imply a starting
   * value, values to animate through along the way, and an ending value (these values will be
   * distributed evenly across the duration of the animation).
   *
   * @param values A set of values that the animation will animate through over time. The float[]
   *     values should all have the same length.
   * @return A new {@link Animation}.
   */
  @NonNull
  public static Animation<float[], float[]> ofFloatArray(float[]... values) {
    return ofObject(new FloatArrayValueEvaluator(), values);
  }

  /**
   * Same as {@link #ofFloatArray(float[]...)} except with {@link Keyframe}s instead of float[]
   * values.
   *
   * @param values A set of {@link Keyframe}s that the animation will animate through over time.
   * @return A new {@link Animation}.
   */
  @NonNull
  @SafeVarargs
  public static Animation<float[], float[]> ofFloatArray(Keyframe<float[]>... values) {
    return ofObject(new FloatArrayValueEvaluator(), values);
  }

  /**
   * Constructs and returns an {@link Animation} that animates between {@link PathData} values. A
   * single value implies that the value is the one being animated to, in which case the start value
   * will be derived from the property being animated and the target object when the animation is
   * started. Two values imply starting and ending values. More than two values imply a starting
   * value, values to animate through along the way, and an ending value (these values will be
   * distributed evenly across the duration of the animation).
   *
   * @param values A set of values that the animation will animate through over time. The {@link
   *     PathData} values should all be morphable with each other.
   * @return A new {@link Animation}.
   */
  @NonNull
  public static Animation<PathData, PathData> ofPathMorph(PathData... values) {
    return ofObject(new PathDataValueEvaluator(), values);
  }

  /**
   * Same as {@link #ofPathMorph(PathData...)} except with {@link Keyframe}s instead of float[]
   * values.
   *
   * @param values A set of {@link Keyframe}s that the animation will animate through over time.
   * @return A new {@link Animation}.
   */
  @NonNull
  @SafeVarargs
  public static Animation<PathData, PathData> ofPathMorph(Keyframe<PathData>... values) {
    return ofObject(new PathDataValueEvaluator(), values);
  }

  @NonNull
  private static <V> Animation<V, V> ofObject(ValueEvaluator<V> evaluator, V[] values) {
    if (values.length < 1) {
      throw new IllegalArgumentException("Must specify at least one value");
    }
    return new Animation<>(
        KeyframeSet.ofObject(evaluator, values), new IdentityValueTransformer<V>());
  }

  @NonNull
  private static <V> Animation<V, V> ofObject(ValueEvaluator<V> evaluator, Keyframe<V>[] values) {
    if (values.length < 1) {
      throw new IllegalArgumentException("Must specify at least one keyframe");
    }
    return new Animation<>(
        KeyframeSet.ofObject(evaluator, values), new IdentityValueTransformer<V>());
  }

  /**
   * Constructs and returns an {@link Animation} that animates through {@link PointF} values in
   * order to simulate motion along the given path. Clients can use {@link
   * #transform(ValueTransformer)} to transform the returned animation into one that outputs floats
   * corresponding to the path's x/y coordinates.
   *
   * @param path The path to animate values along.
   * @return A new {@link Animation}.
   */
  @NonNull
  public static Animation<PointF, PointF> ofPathMotion(Path path) {
    if (path.isEmpty()) {
      throw new IllegalArgumentException("The path must not be empty");
    }
    return new Animation<>(KeyframeSet.ofPath(path), new IdentityValueTransformer<PointF>());
  }

  /**
   * This value used used with the {@link #repeatCount(int)} property to repeat the animation
   * indefinitely. Also used to indicate infinite duration.
   */
  public static final int INFINITE = -1;

  /** Repeat mode determines how a repeating animation should behave once it completes. */
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({RepeatMode.RESTART, RepeatMode.REVERSE})
  public @interface RepeatMode {
    /**
     * When the animation reaches the end and <code>repeatCount</code> is {@link #INFINITE} or a
     * positive value, the animation restarts from the beginning.
     */
    int RESTART = 1;
    /**
     * When the animation reaches the end and <code>repeatCount</code> is {@link #INFINITE} or a
     * positive value, the animation reverses direction on every iteration.
     */
    int REVERSE = 2;
  }

  @NonNull private final KeyframeSet<T> keyframeSet;

  @IntRange(from = 0L)
  private long startDelay;

  @IntRange(from = 0L)
  private long duration = 300;

  @NonNull private final ValueTransformer<T, V> transformer;
  @Nullable private TimeInterpolator interpolator;

  private int repeatCount;
  @RepeatMode private int repeatMode = RepeatMode.RESTART;

  private boolean isInitialized;

  private Animation(KeyframeSet<T> keyframeSet, ValueTransformer<T, V> transformer) {
    this.keyframeSet = keyframeSet;
    this.transformer = transformer;
  }

  private void throwIfInitialized() {
    if (isInitialized) {
      throw new IllegalStateException(
          "Animation must not be mutated after the KyrieDrawable has been created");
    }
  }

  /**
   * Gets the start delay of the animation.
   *
   * @return The start delay of the animation in milliseconds.
   */
  public long getStartDelay() {
    return startDelay;
  }

  /**
   * Sets the start delay of the animation.
   *
   * @param startDelay The start delay of the animation in milliseconds.
   * @return This {@link Animation} object (to allow for chaining of calls to setter methods).
   */
  @NonNull
  public Animation<T, V> startDelay(@IntRange(from = 0L) long startDelay) {
    throwIfInitialized();
    this.startDelay = startDelay;
    return this;
  }

  /**
   * Gets the duration of the animation.
   *
   * @return The length of the animation in milliseconds.
   */
  public long getDuration() {
    return duration;
  }

  /**
   * Sets the duration of the animation.
   *
   * @param duration The length of the animation in milliseconds.
   * @return This {@link Animation} object (to allow for chaining of calls to setter methods).
   */
  @NonNull
  public Animation<T, V> duration(@IntRange(from = 0L) long duration) {
    throwIfInitialized();
    this.duration = duration;
    return this;
  }

  /**
   * Defines how many times the animation should repeat. The default value is 0.
   *
   * @return The number of times the animation should repeat, or {@link #INFINITE}.
   */
  public int getRepeatCount() {
    return repeatCount;
  }

  /**
   * Sets how many times the animation should be repeated. If the repeat count is 0, the animation
   * is never repeated. If the repeat count is greater than 0 or {@link #INFINITE}, the repeat mode
   * will be taken into account. The repeat count is 0 by default.
   *
   * @param repeatCount The number of times the animation should be repeated.
   * @return This {@link Animation} object (to allow for chaining of calls to setter methods).
   */
  @NonNull
  public Animation<T, V> repeatCount(int repeatCount) {
    throwIfInitialized();
    this.repeatCount = repeatCount;
    return this;
  }

  /**
   * Defines what this animation should do when it reaches the end.
   *
   * @return Either one of {@link RepeatMode#RESTART} or {@link RepeatMode#REVERSE}.
   */
  @RepeatMode
  public int getRepeatMode() {
    return repeatMode;
  }

  /**
   * Defines what this animation should do when it reaches the end. This setting is applied only
   * when the repeat count is either greater than 0 or {@link #INFINITE}. Defaults to {@link
   * RepeatMode#RESTART}.
   *
   * @param repeatMode {@link RepeatMode#RESTART} or {@link RepeatMode#REVERSE}.
   * @return This {@link Animation} object (to allow for chaining of calls to setter methods).
   */
  @NonNull
  public Animation<T, V> repeatMode(@RepeatMode int repeatMode) {
    throwIfInitialized();
    this.repeatMode = repeatMode;
    return this;
  }

  /**
   * Returns the timing interpolator that this animation uses. If null, a {@link LinearInterpolator}
   * will be used by default.
   *
   * @return The timing interpolator for this animation.
   */
  @Nullable
  public TimeInterpolator getInterpolator() {
    return interpolator;
  }

  /**
   * Sets the timing interpolator that this animation uses. If null, a {@link LinearInterpolator}
   * will be used by default.
   *
   * @param interpolator The timing interpolator that this animation uses.
   * @return This {@link Animation} object (to allow for chaining of calls to setter methods).
   */
  @NonNull
  public Animation<T, V> interpolator(@Nullable TimeInterpolator interpolator) {
    throwIfInitialized();
    this.interpolator = interpolator;
    return this;
  }

  /**
   * Gets the total duration of the animation in milliseconds, accounting for start delay and repeat
   * count. Returns {@link #INFINITE} if the repeat count is infinite.
   *
   * @return Total time an animation takes to finish, starting from the time it is started. {@link
   *     #INFINITE} will be returned if the animation repeats infinite times.
   */
  public long getTotalDuration() {
    return repeatCount == INFINITE ? INFINITE : startDelay + (duration * (repeatCount + 1));
  }

  /**
   * Called when the animations are first initialized, so that the animation's keyframes can fill in
   * any missing start values.
   */
  void setupStartValue(V startValue) {
    isInitialized = true;
    final List<Keyframe<T>> keyframes = keyframeSet.getKeyframes();
    for (int i = 0, size = keyframes.size(); i < size; i++) {
      final Keyframe<T> kf = keyframes.get(i);
      if (kf.getValue() == null) {
        kf.value(transformBack(startValue));
      }
    }
  }

  @NonNull
  private T transformBack(V value) {
    if (!(transformer instanceof BidirectionalValueTransformer)) {
      throw new IllegalArgumentException(
          "Transformer "
              + transformer.getClass().getName()
              + " must be a BidirectionalValueTransformer");
    }
    return ((BidirectionalValueTransformer<T, V>) transformer).transformBack(value);
  }

  /**
   * Returns the animated value of this animation at the given fraction.
   *
   * @param fraction The current animation fraction. Typically between 0 and 1 (but may slightly
   *     extend these bounds depending on the interpolator used).
   * @return The animated value of this animation at the given fraction.
   */
  @NonNull
  public V getAnimatedValue(float fraction) {
    return transformer.transform(keyframeSet.getAnimatedValue(fraction));
  }

  /**
   * Creates a new animation with original value type <code>T</code> and a new transformed value
   * type <code>W</code>.
   *
   * @param <W> The animation's new transformed value type.
   * @param transformer The value transformer to use to transform the animation's original type
   *     <code>T</code> to a new transformed value type <code>W</code>.
   * @return A new animation with the same original value type <code>T</code> and transformed value
   *     type <code>W</code>.
   */
  @NonNull
  public <W> Animation<T, W> transform(ValueTransformer<T, W> transformer) {
    return new Animation<>(keyframeSet, transformer)
        .startDelay(startDelay)
        .duration(duration)
        .repeatCount(repeatCount)
        .repeatMode(repeatMode)
        .interpolator(interpolator);
  }

  /**
   * Interface that can transform type <code>T</code> to another type <code>V</code>. This is
   * necessary when the original value type of an animation is different than the desired value
   * type.
   *
   * @param <T> The animation's original value type.
   * @param <V> The animation's transformed value type.
   */
  public interface ValueTransformer<T, V> {
    /**
     * Transforms a value from one type to another.
     *
     * @param value The value to transform.
     * @return The transformed value.
     */
    @NonNull
    V transform(T value);
  }

  /**
   * Interface that can transform type <code>T</code> to another type <code>V</code> and back again.
   * This is necessary when the value types of in animation are different from the property type.
   * This interface is only needed when working with an {@link Animation} with no explicitly set
   * start value and that has been transformed using {@link #transform(Object)}.
   *
   * @param <T> The animation's original value type.
   * @param <V> The animation's transformed value type.
   */
  public interface BidirectionalValueTransformer<T, V> extends ValueTransformer<T, V> {
    /**
     * Transforms the output type back to the input type. *
     *
     * @param value The value to transform back.
     * @return The value that has been transformed back.
     */
    @NonNull
    T transformBack(V value);
  }

  private static class IdentityValueTransformer<V> implements BidirectionalValueTransformer<V, V> {
    @NonNull
    @Override
    public V transform(V value) {
      return value;
    }

    @NonNull
    @Override
    public V transformBack(V value) {
      return value;
    }
  }

  interface ValueEvaluator<T> {
    @NonNull
    T evaluate(float fraction, T startValue, T endValue);
  }

  private static final class FloatValueEvaluator implements ValueEvaluator<Float> {
    @NonNull
    @Override
    public Float evaluate(float fraction, Float startValue, Float endValue) {
      return startValue + (endValue - startValue) * fraction;
    }
  }

  private static final class ArgbValueEvaluator implements ValueEvaluator<Integer> {
    @NonNull
    @Override
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
      final float startA = ((startValue >> 24) & 0xff) / 255f;
      float startR = ((startValue >> 16) & 0xff) / 255f;
      float startG = ((startValue >> 8) & 0xff) / 255f;
      float startB = (startValue & 0xff) / 255f;
      float endA = ((endValue >> 24) & 0xff) / 255f;
      float endR = ((endValue >> 16) & 0xff) / 255f;
      float endG = ((endValue >> 8) & 0xff) / 255f;
      float endB = (endValue & 0xff) / 255f;
      // Transform from sRGB to linear.
      startR = (float) Math.pow(startR, 2.2);
      startG = (float) Math.pow(startG, 2.2);
      startB = (float) Math.pow(startB, 2.2);
      endR = (float) Math.pow(endR, 2.2);
      endG = (float) Math.pow(endG, 2.2);
      endB = (float) Math.pow(endB, 2.2);
      // Compute the interpolated color in linear space.
      float a = startA + fraction * (endA - startA);
      float r = startR + fraction * (endR - startR);
      float g = startG + fraction * (endG - startG);
      float b = startB + fraction * (endB - startB);
      // Transform back to sRGB in the [0..255] range.
      a = a * 255f;
      r = (float) Math.pow(r, 1.0 / 2.2) * 255f;
      g = (float) Math.pow(g, 1.0 / 2.2) * 255f;
      b = (float) Math.pow(b, 1.0 / 2.2) * 255f;
      return Math.round(a) << 24 | Math.round(r) << 16 | Math.round(g) << 8 | Math.round(b);
    }
  }

  private static final class FloatArrayValueEvaluator implements ValueEvaluator<float[]> {
    @Nullable private float[] array;

    @NonNull
    @Override
    public float[] evaluate(float fraction, float[] startValue, float[] endValue) {
      if (array == null || array.length != startValue.length) {
        array = new float[startValue.length];
      }
      for (int i = 0; i < array.length; i++) {
        float start = startValue[i];
        float end = endValue[i];
        array[i] = start + (fraction * (end - start));
      }
      return array;
    }
  }

  private static final class PathDataValueEvaluator implements ValueEvaluator<PathData> {
    @Nullable private PathData pathData;

    @NonNull
    @Override
    public PathData evaluate(float fraction, PathData startValue, PathData endValue) {
      if (pathData == null || !pathData.canMorphWith(startValue)) {
        pathData = new PathData(startValue);
      }
      pathData.interpolate(startValue, endValue, fraction);
      return pathData;
    }
  }
}
