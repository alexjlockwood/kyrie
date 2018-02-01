package com.github.alexjlockwood.kyrie;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Size;

import java.util.List;

abstract class BaseNode extends Node {
  @NonNull private final List<Animation<?, Float>> rotation;
  @NonNull private final List<Animation<?, Float>> pivotX;
  @NonNull private final List<Animation<?, Float>> pivotY;
  @NonNull private final List<Animation<?, Float>> scaleX;
  @NonNull private final List<Animation<?, Float>> scaleY;
  @NonNull private final List<Animation<?, Float>> translateX;
  @NonNull private final List<Animation<?, Float>> translateY;

  BaseNode(
      List<Animation<?, Float>> rotation,
      List<Animation<?, Float>> pivotX,
      List<Animation<?, Float>> pivotY,
      List<Animation<?, Float>> scaleX,
      List<Animation<?, Float>> scaleY,
      List<Animation<?, Float>> translateX,
      List<Animation<?, Float>> translateY) {
    super();
    this.rotation = rotation;
    this.pivotX = pivotX;
    this.pivotY = pivotY;
    this.scaleX = scaleX;
    this.scaleY = scaleY;
    this.translateX = translateX;
    this.translateY = translateY;
  }

  @NonNull
  final List<Animation<?, Float>> getRotation() {
    return rotation;
  }

  @NonNull
  final List<Animation<?, Float>> getPivotX() {
    return pivotX;
  }

  @NonNull
  final List<Animation<?, Float>> getPivotY() {
    return pivotY;
  }

  @NonNull
  final List<Animation<?, Float>> getScaleX() {
    return scaleX;
  }

  @NonNull
  final List<Animation<?, Float>> getScaleY() {
    return scaleY;
  }

  @NonNull
  final List<Animation<?, Float>> getTranslateX() {
    return translateX;
  }

  @NonNull
  final List<Animation<?, Float>> getTranslateY() {
    return translateY;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  abstract BaseLayer toLayer(PropertyTimeline timeline);

  abstract static class BaseLayer implements Layer {
    @NonNull private final PropertyTimeline timeline;
    @NonNull private final Property<Float> rotation;
    @NonNull private final Property<Float> pivotX;
    @NonNull private final Property<Float> pivotY;
    @NonNull private final Property<Float> scaleX;
    @NonNull private final Property<Float> scaleY;
    @NonNull private final Property<Float> translateX;
    @NonNull private final Property<Float> translateY;

    private final Matrix tempMatrix = new Matrix();

    @Size(value = 4)
    private final float[] tempUnitVectors = new float[4];

    public BaseLayer(PropertyTimeline timeline, BaseNode node) {
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
    public final <V> Property<V> registerAnimatableProperty(List<Animation<?, V>> animations) {
      return timeline.registerAnimatableProperty(animations);
    }

    @Override
    public final void draw(Canvas canvas, Matrix parentMatrix, PointF viewportScale) {
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

    final float getMatrixScale(Matrix matrix) {
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

  abstract static class Builder<B extends Builder<B>> extends Node.Builder<B> {
    @NonNull final List<Animation<?, Float>> rotation = Node.asAnimations(0f);
    @NonNull final List<Animation<?, Float>> pivotX = Node.asAnimations(0f);
    @NonNull final List<Animation<?, Float>> pivotY = Node.asAnimations(0f);
    @NonNull final List<Animation<?, Float>> scaleX = Node.asAnimations(1f);
    @NonNull final List<Animation<?, Float>> scaleY = Node.asAnimations(1f);
    @NonNull final List<Animation<?, Float>> translateX = Node.asAnimations(0f);
    @NonNull final List<Animation<?, Float>> translateY = Node.asAnimations(0f);

    Builder() {}

    // Rotation.

    public final B rotation(float initialRotation) {
      return replaceFirstAnimation(rotation, Node.asAnimation(initialRotation));
    }

    @SafeVarargs
    public final B rotation(Animation<?, Float>... animations) {
      return replaceAnimations(rotation, animations);
    }

    public final B rotation(List<Animation<?, Float>> animations) {
      return replaceAnimations(rotation, animations);
    }

    // Pivot X.

    public final B pivotX(float initialPivotX) {
      return replaceFirstAnimation(pivotX, Node.asAnimation(initialPivotX));
    }

    @SafeVarargs
    public final B pivotX(Animation<?, Float>... animations) {
      return replaceAnimations(pivotX, animations);
    }

    public final B pivotX(List<Animation<?, Float>> animations) {
      return replaceAnimations(pivotX, animations);
    }

    // Pivot Y.

    public final B pivotY(float initialPivotY) {
      return replaceFirstAnimation(pivotY, Node.asAnimation(initialPivotY));
    }

    @SafeVarargs
    public final B pivotY(Animation<?, Float>... animations) {
      return replaceAnimations(pivotY, animations);
    }

    public final B pivotY(List<Animation<?, Float>> animations) {
      return replaceAnimations(pivotY, animations);
    }

    // Scale X.

    public final B scaleX(float initialScaleX) {
      return replaceFirstAnimation(scaleX, Node.asAnimation(initialScaleX));
    }

    @SafeVarargs
    public final B scaleX(Animation<?, Float>... animations) {
      return replaceAnimations(scaleX, animations);
    }

    public final B scaleX(List<Animation<?, Float>> animations) {
      return replaceAnimations(scaleX, animations);
    }

    // Scale Y.

    public final B scaleY(float initialScaleY) {
      return replaceFirstAnimation(scaleY, Node.asAnimation(initialScaleY));
    }

    @SafeVarargs
    public final B scaleY(Animation<?, Float>... animations) {
      return replaceAnimations(scaleY, animations);
    }

    public final B scaleY(List<Animation<?, Float>> animations) {
      return replaceAnimations(scaleY, animations);
    }

    // Translate X.

    public final B translateX(float initialTranslateX) {
      return replaceFirstAnimation(translateX, Node.asAnimation(initialTranslateX));
    }

    @SafeVarargs
    public final B translateX(Animation<?, Float>... animations) {
      return replaceAnimations(translateX, animations);
    }

    public final B translateX(List<Animation<?, Float>> animations) {
      return replaceAnimations(translateX, animations);
    }

    // Translate Y.

    public final B translateY(float initialTranslateY) {
      return replaceFirstAnimation(translateY, Node.asAnimation(initialTranslateY));
    }

    @SafeVarargs
    public final B translateY(Animation<?, Float>... animations) {
      return replaceAnimations(translateY, animations);
    }

    public final B translateY(List<Animation<?, Float>> animations) {
      return replaceAnimations(translateY, animations);
    }

    @NonNull
    @Override
    abstract BaseNode build();
  }

  // </editor-fold>
}
