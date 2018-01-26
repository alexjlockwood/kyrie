package com.github.alexjlockwood.kyrie;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

import java.util.List;

abstract class BaseNode extends Node {
  @NonNull private final List<PropertyAnimation<?, Float>> rotation;
  @NonNull private final List<PropertyAnimation<?, Float>> pivotX;
  @NonNull private final List<PropertyAnimation<?, Float>> pivotY;
  @NonNull private final List<PropertyAnimation<?, Float>> scaleX;
  @NonNull private final List<PropertyAnimation<?, Float>> scaleY;
  @NonNull private final List<PropertyAnimation<?, Float>> translateX;
  @NonNull private final List<PropertyAnimation<?, Float>> translateY;

  public BaseNode(
      @NonNull List<PropertyAnimation<?, Float>> rotation,
      @NonNull List<PropertyAnimation<?, Float>> pivotX,
      @NonNull List<PropertyAnimation<?, Float>> pivotY,
      @NonNull List<PropertyAnimation<?, Float>> scaleX,
      @NonNull List<PropertyAnimation<?, Float>> scaleY,
      @NonNull List<PropertyAnimation<?, Float>> translateX,
      @NonNull List<PropertyAnimation<?, Float>> translateY) {
    this.rotation = rotation;
    this.pivotX = pivotX;
    this.pivotY = pivotY;
    this.scaleX = scaleX;
    this.scaleY = scaleY;
    this.translateX = translateX;
    this.translateY = translateY;
  }

  @NonNull
  public final List<PropertyAnimation<?, Float>> getRotation() {
    return rotation;
  }

  @NonNull
  public final List<PropertyAnimation<?, Float>> getPivotX() {
    return pivotX;
  }

  @NonNull
  public final List<PropertyAnimation<?, Float>> getPivotY() {
    return pivotY;
  }

  @NonNull
  public final List<PropertyAnimation<?, Float>> getScaleX() {
    return scaleX;
  }

  @NonNull
  public final List<PropertyAnimation<?, Float>> getScaleY() {
    return scaleY;
  }

  @NonNull
  public final List<PropertyAnimation<?, Float>> getTranslateX() {
    return translateX;
  }

  @NonNull
  public final List<PropertyAnimation<?, Float>> getTranslateY() {
    return translateY;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  abstract BaseLayer toLayer(@NonNull PropertyTimeline timeline);

  abstract static class BaseLayer implements Layer {
    @NonNull private final PropertyTimeline timeline;
    @NonNull private final AnimatableProperty<Float> rotation;
    @NonNull private final AnimatableProperty<Float> pivotX;
    @NonNull private final AnimatableProperty<Float> pivotY;
    @NonNull private final AnimatableProperty<Float> scaleX;
    @NonNull private final AnimatableProperty<Float> scaleY;
    @NonNull private final AnimatableProperty<Float> translateX;
    @NonNull private final AnimatableProperty<Float> translateY;

    private final Matrix tempMatrix = new Matrix();

    @Size(value = 4)
    private final float[] tempUnitVectors = new float[4];

    public BaseLayer(@NonNull PropertyTimeline timeline, @NonNull BaseNode node) {
      this.timeline = timeline;
      rotation = registerAnimatableProperty(node.getRotation());
      pivotX = registerAnimatableProperty(node.getPivotX());
      pivotY = registerAnimatableProperty(node.getPivotY());
      scaleX = registerAnimatableProperty(node.getScaleX());
      scaleY = registerAnimatableProperty(node.getScaleY());
      translateX = registerAnimatableProperty(node.getTranslateX());
      translateY = registerAnimatableProperty(node.getTranslateY());
    }

    @NonNull
    public final <V> AnimatableProperty<V> registerAnimatableProperty(
        @NonNull List<PropertyAnimation<?, V>> animations) {
      return timeline.registerAnimatableProperty(animations);
    }

    @Override
    public final void draw(
        @NonNull Canvas canvas, @NonNull Matrix parentMatrix, @NonNull PointF viewportScale) {
      final float pivotX = this.pivotX.getAnimatedValue();
      final float pivotY = this.pivotY.getAnimatedValue();
      final float rotation = this.rotation.getAnimatedValue();
      final float scaleX = this.scaleX.getAnimatedValue();
      final float scaleY = this.scaleY.getAnimatedValue();
      final float translateX = this.translateX.getAnimatedValue();
      final float translateY = this.translateY.getAnimatedValue();
      tempMatrix.set(parentMatrix);
      if (translateX + pivotX != 0f || translateY + pivotY != 0f) {
        tempMatrix.preTranslate(translateX + pivotX, translateY + pivotY);
      }
      if (rotation != 0f) {
        tempMatrix.preRotate(rotation, 0, 0);
      }
      if (scaleX != 1f || scaleY != 1f) {
        tempMatrix.preScale(scaleX, scaleY);
      }
      if (pivotX != 0f || pivotY != 0f) {
        tempMatrix.preTranslate(-pivotX, -pivotY);
      }
      onDraw(canvas, tempMatrix, viewportScale);
    }

    final float getMatrixScale(@NonNull Matrix matrix) {
      // Given unit vectors A = (0, 1) and B = (1, 0).
      // After matrix mapping, we got A' and B'. Let theta = the angle b/t A' and B'.
      // Therefore, the final scale we want is min(|A'| * sin(theta), |B'| * sin(theta)),
      // which is (|A'| * |B'| * sin(theta)) / max (|A'|, |B'|);
      // If max (|A'|, |B'|) = 0, that means either x or y has a scale of 0.
      // For non-skew case, which is most of the cases, matrix scale is computing exactly the
      // scale on x and y axis, and take the minimal of these two.
      // For skew case, an unit square will mapped to a parallelogram. And this function will
      // return the minimal height of the 2 bases.
      final float[] unitVectors = tempUnitVectors;
      unitVectors[0] = 0;
      unitVectors[1] = 1;
      unitVectors[2] = 1;
      unitVectors[3] = 0;
      matrix.mapVectors(unitVectors);
      final float scaleX = (float) Math.hypot(unitVectors[0], unitVectors[1]);
      final float scaleY = (float) Math.hypot(unitVectors[2], unitVectors[3]);
      final float crossProduct =
          cross(unitVectors[0], unitVectors[1], unitVectors[2], unitVectors[3]);
      final float maxScale = Math.max(scaleX, scaleY);
      return maxScale > 0 ? Math.abs(crossProduct) / maxScale : 0;
    }

    private static float cross(float v1x, float v1y, float v2x, float v2y) {
      return v1x * v2y - v1y * v2x;
    }
  }

  // </editor-fold>

  // <editor-fold desc="Builder">

  abstract static class Builder<N extends BaseNode, B extends Builder<N, B>>
      extends Node.Builder<N, B> {
    @NonNull final List<PropertyAnimation<?, Float>> rotation = Node.asAnimations(0f);
    @NonNull final List<PropertyAnimation<?, Float>> pivotX = Node.asAnimations(0f);
    @NonNull final List<PropertyAnimation<?, Float>> pivotY = Node.asAnimations(0f);
    @NonNull final List<PropertyAnimation<?, Float>> scaleX = Node.asAnimations(1f);
    @NonNull final List<PropertyAnimation<?, Float>> scaleY = Node.asAnimations(1f);
    @NonNull final List<PropertyAnimation<?, Float>> translateX = Node.asAnimations(0f);
    @NonNull final List<PropertyAnimation<?, Float>> translateY = Node.asAnimations(0f);

    Builder() {}

    // Rotation.

    public final B rotation(float rotation) {
      return replaceFirstAnimation(this.rotation, Node.asAnimation(rotation));
    }

    @SafeVarargs
    public final B rotation(@NonNull PropertyAnimation<?, Float>... animations) {
      return replaceAnimations(rotation, animations);
    }

    public final B rotation(@NonNull List<PropertyAnimation<?, Float>> animations) {
      return replaceAnimations(rotation, animations);
    }

    // Pivot X.

    public final B pivotX(float pivotX) {
      return replaceFirstAnimation(this.pivotX, Node.asAnimation(pivotX));
    }

    @SafeVarargs
    public final B pivotX(@NonNull PropertyAnimation<?, Float>... animations) {
      return replaceAnimations(pivotX, animations);
    }

    public final B pivotX(@NonNull List<PropertyAnimation<?, Float>> animations) {
      return replaceAnimations(pivotX, animations);
    }

    // Pivot Y.

    public final B pivotY(float pivotY) {
      return replaceFirstAnimation(this.pivotY, Node.asAnimation(pivotY));
    }

    @SafeVarargs
    public final B pivotY(@NonNull PropertyAnimation<?, Float>... animations) {
      return replaceAnimations(pivotY, animations);
    }

    public final B pivotY(@NonNull List<PropertyAnimation<?, Float>> keyframes) {
      return replaceAnimations(pivotY, keyframes);
    }

    // Scale X.

    public final B scaleX(float scaleX) {
      return replaceFirstAnimation(this.scaleX, Node.asAnimation(scaleX));
    }

    @SafeVarargs
    public final B scaleX(@NonNull PropertyAnimation<?, Float>... keyframes) {
      return replaceAnimations(scaleX, keyframes);
    }

    public final B scaleX(@NonNull List<PropertyAnimation<?, Float>> keyframes) {
      return replaceAnimations(scaleX, keyframes);
    }

    // Scale Y.

    public final B scaleY(float scaleY) {
      return replaceFirstAnimation(this.scaleY, Node.asAnimation(scaleY));
    }

    @SafeVarargs
    public final B scaleY(@NonNull PropertyAnimation<?, Float>... keyframes) {
      return replaceAnimations(scaleY, keyframes);
    }

    public final B scaleY(@NonNull List<PropertyAnimation<?, Float>> keyframes) {
      return replaceAnimations(scaleY, keyframes);
    }

    // Translate X.

    public final B translateX(float translateX) {
      return replaceFirstAnimation(this.translateX, Node.asAnimation(translateX));
    }

    @SafeVarargs
    public final B translateX(@NonNull PropertyAnimation<?, Float>... animations) {
      return replaceAnimations(translateX, animations);
    }

    public final B translateX(@NonNull List<PropertyAnimation<?, Float>> animations) {
      return replaceAnimations(translateX, animations);
    }

    // Translate Y.

    public final B translateY(float translateY) {
      return replaceFirstAnimation(this.translateY, Node.asAnimation(translateY));
    }

    @SafeVarargs
    public final B translateY(@NonNull PropertyAnimation<?, Float>... animations) {
      return replaceAnimations(translateY, animations);
    }

    public final B translateY(@NonNull List<PropertyAnimation<?, Float>> animations) {
      return replaceAnimations(translateY, animations);
    }

    public abstract N build();
  }

  // </editor-fold>
}
