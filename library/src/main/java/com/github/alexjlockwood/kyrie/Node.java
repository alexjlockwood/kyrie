package com.github.alexjlockwood.kyrie;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class Node {

  @NonNull
  abstract Layer toLayer(@NonNull PropertyTimeline timeline);

  interface Layer {
    void draw(@NonNull Canvas canvas, @NonNull Matrix parentMatrix, @NonNull PointF viewportScale);

    void onDraw(
        @NonNull Canvas canvas, @NonNull Matrix parentMatrix, @NonNull PointF viewportScale);
  }

  abstract static class Builder<N extends Node, B extends Builder<N, B>> {
    final B self;

    Builder() {
      this.self = self();
    }

    abstract B self();

    final <T> B replaceFirstAnimation(
        @NonNull List<PropertyAnimation<?, T>> animations,
        @NonNull PropertyAnimation<?, T> animation) {
      Node.replaceFirstAnimation(animations, animation);
      return self;
    }

    @SafeVarargs
    final <T> B replaceAnimations(
        @NonNull List<PropertyAnimation<?, T>> animations,
        @NonNull PropertyAnimation<?, T>... newAnimations) {
      Node.replaceAnimations(animations, newAnimations);
      return self;
    }

    final <T> B replaceAnimations(
        @NonNull List<PropertyAnimation<?, T>> animations,
        @NonNull List<PropertyAnimation<?, T>> newAnimations) {
      Node.replaceAnimations(animations, newAnimations);
      return self;
    }
  }

  @NonNull
  static PropertyAnimation<?, Float> asAnimation(float initialValue) {
    return PropertyAnimation.ofFloat(initialValue, initialValue).duration(0);
  }

  @NonNull
  static PropertyAnimation<?, Integer> asAnimation(@ColorInt int initialValue) {
    return PropertyAnimation.ofArgb(initialValue, initialValue).duration(0);
  }

  @NonNull
  static PropertyAnimation<?, float[]> asAnimation(@NonNull float[] initialValue) {
    return PropertyAnimation.ofFloatArray(initialValue, initialValue).duration(0);
  }

  @NonNull
  static PropertyAnimation<?, PathData> asAnimation(@NonNull PathData initialValue) {
    return PropertyAnimation.ofPathMorph(initialValue, initialValue).duration(0);
  }

  @NonNull
  static List<PropertyAnimation<?, Float>> asAnimations(float initialValue) {
    return asList(asAnimation(initialValue));
  }

  @NonNull
  static List<PropertyAnimation<?, Integer>> asAnimations(int initialValue) {
    return asList(asAnimation(initialValue));
  }

  @NonNull
  static List<PropertyAnimation<?, float[]>> asAnimations(@NonNull float[] initialValue) {
    return asList(asAnimation(initialValue));
  }

  @NonNull
  static List<PropertyAnimation<?, PathData>> asAnimations(@NonNull PathData initialValue) {
    return asList(asAnimation(initialValue));
  }

  @NonNull
  private static <T> List<PropertyAnimation<?, T>> asList(
      @NonNull PropertyAnimation<?, T> animation) {
    final List<PropertyAnimation<?, T>> animations = new ArrayList<>(1);
    animations.add(animation);
    return animations;
  }

  static <T> void replaceFirstAnimation(
      @NonNull List<PropertyAnimation<?, T>> animations,
      @NonNull PropertyAnimation<?, T> animation) {
    animations.set(0, animation);
  }

  @SafeVarargs
  static <T> void replaceAnimations(
      @NonNull List<PropertyAnimation<?, T>> animations,
      @NonNull PropertyAnimation<?, T>... newAnimations) {
    for (int i = animations.size() - 1; i > 0; i--) {
      animations.remove(i);
    }
    Collections.addAll(animations, newAnimations);
  }

  static <T> void replaceAnimations(
      @NonNull List<PropertyAnimation<?, T>> animations,
      @NonNull List<PropertyAnimation<?, T>> newAnimations) {
    for (int i = animations.size() - 1; i > 0; i--) {
      animations.remove(i);
    }
    animations.addAll(newAnimations);
  }
}
