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

    public abstract N build();

    final <T> B replaceFirstAnimation(
        @NonNull List<Animation<?, T>> animations, @NonNull Animation<?, T> animation) {
      Node.replaceFirstAnimation(animations, animation);
      return self;
    }

    @SafeVarargs
    final <T> B replaceAnimations(
        @NonNull List<Animation<?, T>> animations, @NonNull Animation<?, T>... newAnimations) {
      Node.replaceAnimations(animations, newAnimations);
      return self;
    }

    final <T> B replaceAnimations(
        @NonNull List<Animation<?, T>> animations, @NonNull List<Animation<?, T>> newAnimations) {
      Node.replaceAnimations(animations, newAnimations);
      return self;
    }
  }

  @NonNull
  static Animation<?, Float> asAnimation(float initialValue) {
    return Animation.ofFloat(initialValue, initialValue).duration(0);
  }

  @NonNull
  static Animation<?, Integer> asAnimation(@ColorInt int initialValue) {
    return Animation.ofArgb(initialValue, initialValue).duration(0);
  }

  @NonNull
  static Animation<?, float[]> asAnimation(@NonNull float[] initialValue) {
    return Animation.ofFloatArray(initialValue, initialValue).duration(0);
  }

  @NonNull
  static Animation<?, PathData> asAnimation(@NonNull PathData initialValue) {
    return Animation.ofPathMorph(initialValue, initialValue).duration(0);
  }

  @NonNull
  static List<Animation<?, Float>> asAnimations(float initialValue) {
    return asList(asAnimation(initialValue));
  }

  @NonNull
  static List<Animation<?, Integer>> asAnimations(int initialValue) {
    return asList(asAnimation(initialValue));
  }

  @NonNull
  static List<Animation<?, float[]>> asAnimations(@NonNull float[] initialValue) {
    return asList(asAnimation(initialValue));
  }

  @NonNull
  static List<Animation<?, PathData>> asAnimations(@NonNull PathData initialValue) {
    return asList(asAnimation(initialValue));
  }

  @NonNull
  private static <T> List<Animation<?, T>> asList(@NonNull Animation<?, T> animation) {
    final List<Animation<?, T>> animations = new ArrayList<>(1);
    animations.add(animation);
    return animations;
  }

  static <T> void replaceFirstAnimation(
      @NonNull List<Animation<?, T>> animations, @NonNull Animation<?, T> animation) {
    animations.set(0, animation);
  }

  @SafeVarargs
  static <T> void replaceAnimations(
      @NonNull List<Animation<?, T>> animations, @NonNull Animation<?, T>... newAnimations) {
    for (int i = animations.size() - 1; i > 0; i--) {
      animations.remove(i);
    }
    Collections.addAll(animations, newAnimations);
  }

  static <T> void replaceAnimations(
      @NonNull List<Animation<?, T>> animations, @NonNull List<Animation<?, T>> newAnimations) {
    for (int i = animations.size() - 1; i > 0; i--) {
      animations.remove(i);
    }
    animations.addAll(newAnimations);
  }
}
