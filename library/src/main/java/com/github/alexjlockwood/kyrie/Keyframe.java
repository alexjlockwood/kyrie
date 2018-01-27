package com.github.alexjlockwood.kyrie;

import android.animation.TimeInterpolator;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;

public final class Keyframe<V> {
  @FloatRange(from = 0f, to = 1f)
  private float fraction;

  @Nullable private V value;
  @Nullable private TimeInterpolator interpolator;

  public static <V> Keyframe<V> of(float fraction) {
    return of(fraction, null);
  }

  public static <V> Keyframe<V> of(float fraction, @Nullable V value) {
    return new Keyframe<>(fraction, value);
  }

  private Keyframe(float fraction, @Nullable V value) {
    this.fraction = fraction;
    this.value = value;
  }

  public float getFraction() {
    return fraction;
  }

  public Keyframe<V> setFraction(@FloatRange(from = 0f, to = 1f) float fraction) {
    this.fraction = fraction;
    return this;
  }

  @Nullable
  public V getValue() {
    return value;
  }

  public Keyframe<V> setValue(@Nullable V value) {
    this.value = value;
    return this;
  }

  @Nullable
  public TimeInterpolator getInterpolator() {
    return interpolator;
  }

  public Keyframe<V> setInterpolator(@Nullable TimeInterpolator interpolator) {
    this.interpolator = interpolator;
    return this;
  }
}
