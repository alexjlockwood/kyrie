package com.github.alexjlockwood.kyrie;

import android.animation.TimeInterpolator;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * This class holds a time/value pair for an animation. A keyframe is used to define the values that
 * the animation target will have over the course of the animation. As the time proceeds from one
 * keyframe to the other, the value of the target will animate between the value at the previous
 * keyframe and the value at the next keyframe. Each keyframe also holds an optional {@link
 * TimeInterpolator} object, which defines the time interpolation over the inter-value preceding the
 * keyframe.
 *
 * @param <T> The keyframe value type.
 */
public final class Keyframe<T> {

  /**
   * Constructs a Keyframe object with the given time. The value at this time will be derived from
   * the target object when the animation first starts. The time defines the time, as a proportion
   * of an overall animation's duration, at which the value will hold true for the animation. The
   * value for the animation between keyframes will be calculated as an interpolation between the
   * values at those keyframes.
   *
   * @param fraction The time, expressed as a value between 0 and 1, representing the fraction of
   *     time elapsed of the overall animation duration.
   * @param <T> The keyframe value type.
   */
  @NonNull
  public static <T> Keyframe<T> of(@FloatRange(from = 0f, to = 1f) float fraction) {
    return of(fraction, null);
  }

  /**
   * Constructs a Keyframe object with the given time and value. The time defines the time, as a
   * proportion of an overall animation's duration, at which the value will hold true for the
   * animation. The value for the animation between keyframes will be calculated as an interpolation
   * between the values at those keyframes.
   *
   * @param fraction The time, expressed as a value between 0 and 1, representing the fraction of
   *     time elapsed of the overall animation duration.
   * @param value The value that the object will animate to as the animation time approaches the
   *     time in this keyframe, and the the value animated from as the time passes the time in this
   *     keyframe.
   * @param <T> The keyframe value type.
   */
  @NonNull
  public static <T> Keyframe<T> of(
      @FloatRange(from = 0f, to = 1f) float fraction, @Nullable T value) {
    return new Keyframe<>(fraction, value);
  }

  @FloatRange(from = 0f, to = 1f)
  private float fraction;

  @Nullable private T value;
  @Nullable private TimeInterpolator interpolator;

  private Keyframe(@FloatRange(from = 0f, to = 1f) float fraction, @Nullable T value) {
    this.fraction = fraction;
    this.value = value;
  }

  /**
   * Gets the time for this keyframe, as a fraction of the overall animation duration.
   *
   * @return The time associated with this keyframe, as a fraction of the overall animation
   *     duration. This should be a value between 0 and 1.
   */
  @FloatRange(from = 0f, to = 1f)
  public float getFraction() {
    return fraction;
  }

  /**
   * Sets the time for this keyframe, as a fraction of the overall animation duration.
   *
   * @param fraction The time associated with this keyframe, as a fraction of the overall animation
   *     duration. This should be a value between 0 and 1.
   */
  @NonNull
  public Keyframe<T> fraction(@FloatRange(from = 0f, to = 1f) float fraction) {
    this.fraction = fraction;
    return this;
  }

  /**
   * Gets the value for this Keyframe.
   *
   * @return The value for this keyframe.
   */
  @Nullable
  public T getValue() {
    return value;
  }

  /**
   * Sets the value for this Keyframe.
   *
   * @param value The value for this keyframe.
   */
  @NonNull
  public Keyframe<T> value(@Nullable T value) {
    this.value = value;
    return this;
  }

  /**
   * Gets the optional interpolator for this Keyframe. A value of null indicates that there is no
   * interpolation, which is the same as linear interpolation.
   *
   * @return The optional interpolator for this Keyframe.
   */
  @Nullable
  public TimeInterpolator getInterpolator() {
    return interpolator;
  }

  /**
   * Sets the optional interpolator for this Keyframe. A value of null indicates that there is no
   * interpolation, which is the same as linear interpolation.
   *
   * @param interpolator The optional interpolator for this Keyframe.
   */
  @NonNull
  public Keyframe<T> interpolator(@Nullable TimeInterpolator interpolator) {
    this.interpolator = interpolator;
    return this;
  }
}
