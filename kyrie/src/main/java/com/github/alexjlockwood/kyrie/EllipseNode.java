package com.github.alexjlockwood.kyrie;

import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;

import java.util.List;

/** A {@link Node} that paints an ellipse. */
public final class EllipseNode extends RenderNode {
  @NonNull private final List<Animation<?, Float>> centerX;
  @NonNull private final List<Animation<?, Float>> centerY;
  @NonNull private final List<Animation<?, Float>> radiusX;
  @NonNull private final List<Animation<?, Float>> radiusY;

  private EllipseNode(
      List<Animation<?, Float>> rotation,
      List<Animation<?, Float>> pivotX,
      List<Animation<?, Float>> pivotY,
      List<Animation<?, Float>> scaleX,
      List<Animation<?, Float>> scaleY,
      List<Animation<?, Float>> translateX,
      List<Animation<?, Float>> translateY,
      List<Animation<?, Integer>> fillColor,
      List<Animation<?, Float>> fillAlpha,
      List<Animation<?, Integer>> strokeColor,
      List<Animation<?, Float>> strokeAlpha,
      List<Animation<?, Float>> strokeWidth,
      List<Animation<?, Float>> trimPathStart,
      List<Animation<?, Float>> trimPathEnd,
      List<Animation<?, Float>> trimPathOffset,
      @StrokeLineCap int strokeLineCap,
      @StrokeLineJoin int strokeLineJoin,
      List<Animation<?, Float>> strokeMiterLimit,
      List<Animation<?, float[]>> strokeDashArray,
      List<Animation<?, Float>> strokeDashOffset,
      @FillType int fillType,
      boolean isStrokeScaling,
      List<Animation<?, Float>> centerX,
      List<Animation<?, Float>> centerY,
      List<Animation<?, Float>> radiusX,
      List<Animation<?, Float>> radiusY) {
    super(
        rotation,
        pivotX,
        pivotY,
        scaleX,
        scaleY,
        translateX,
        translateY,
        fillColor,
        fillAlpha,
        strokeColor,
        strokeAlpha,
        strokeWidth,
        trimPathStart,
        trimPathEnd,
        trimPathOffset,
        strokeLineCap,
        strokeLineJoin,
        strokeMiterLimit,
        strokeDashArray,
        strokeDashOffset,
        fillType,
        isStrokeScaling);
    this.centerX = centerX;
    this.centerY = centerY;
    this.radiusX = radiusX;
    this.radiusY = radiusY;
  }

  @NonNull
  List<Animation<?, Float>> getCenterX() {
    return centerX;
  }

  @NonNull
  List<Animation<?, Float>> getCenterY() {
    return centerY;
  }

  @NonNull
  List<Animation<?, Float>> getRadiusX() {
    return radiusX;
  }

  @NonNull
  List<Animation<?, Float>> getRadiusY() {
    return radiusY;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  EllipseLayer toLayer(PropertyTimeline timeline) {
    return new EllipseLayer(timeline, this);
  }

  private static class EllipseLayer extends RenderLayer {
    @NonNull private final Property<Float> centerX;
    @NonNull private final Property<Float> centerY;
    @NonNull private final Property<Float> radiusX;
    @NonNull private final Property<Float> radiusY;

    private final RectF tempRect = new RectF();

    public EllipseLayer(PropertyTimeline timeline, EllipseNode node) {
      super(timeline, node);
      centerX = registerAnimatableProperty(node.getCenterX());
      centerY = registerAnimatableProperty(node.getCenterY());
      radiusX = registerAnimatableProperty(node.getRadiusX());
      radiusY = registerAnimatableProperty(node.getRadiusY());
    }

    @Override
    public void onInitPath(Path outPath) {
      final float cx = centerX.getAnimatedValue();
      final float cy = centerY.getAnimatedValue();
      final float rx = radiusX.getAnimatedValue();
      final float ry = radiusY.getAnimatedValue();
      tempRect.set(cx - rx, cy - ry, cx + rx, cy + ry);
      outPath.addOval(tempRect, Path.Direction.CW);
    }
  }

  // </editor-fold>

  // <editor-fold desc="Builder">

  public static Builder builder() {
    return new Builder();
  }

  /** Builder class used to create {@link EllipseNode}s. */
  public static final class Builder extends RenderNode.Builder<Builder> {
    @NonNull private final List<Animation<?, Float>> centerX = asAnimations(0f);
    @NonNull private final List<Animation<?, Float>> centerY = asAnimations(0f);
    @NonNull private final List<Animation<?, Float>> radiusX = asAnimations(0f);
    @NonNull private final List<Animation<?, Float>> radiusY = asAnimations(0f);

    private Builder() {}

    // Center X.

    public Builder centerX(float initialCenterX) {
      return replaceFirstAnimation(centerX, asAnimation(initialCenterX));
    }

    @SafeVarargs
    public final Builder centerX(Animation<?, Float>... animations) {
      return replaceAnimations(centerX, animations);
    }

    public Builder centerX(List<Animation<?, Float>> animations) {
      return replaceAnimations(centerX, animations);
    }

    // Center Y.

    public Builder centerY(float initialCenterY) {
      return replaceFirstAnimation(centerY, asAnimation(initialCenterY));
    }

    @SafeVarargs
    public final Builder centerY(Animation<?, Float>... animations) {
      return replaceAnimations(centerY, animations);
    }

    public Builder centerY(List<Animation<?, Float>> animations) {
      return replaceAnimations(centerY, animations);
    }

    // Radius X.

    public Builder radiusX(@FloatRange(from = 0f) float initialRadiusX) {
      return replaceFirstAnimation(radiusX, asAnimation(initialRadiusX));
    }

    @SafeVarargs
    public final Builder radiusX(Animation<?, Float>... animations) {
      return replaceAnimations(radiusX, animations);
    }

    public Builder radiusX(List<Animation<?, Float>> animations) {
      return replaceAnimations(radiusX, animations);
    }

    // Radius Y.

    public Builder radiusY(@FloatRange(from = 0f) float initialRadiusY) {
      return replaceFirstAnimation(radiusY, asAnimation(initialRadiusY));
    }

    @SafeVarargs
    public final Builder radiusY(Animation<?, Float>... animations) {
      return replaceAnimations(radiusY, animations);
    }

    public Builder radiusY(List<Animation<?, Float>> animations) {
      return replaceAnimations(radiusY, animations);
    }

    @NonNull
    @Override
    Builder self() {
      return this;
    }

    @NonNull
    public EllipseNode build() {
      return new EllipseNode(
          rotation,
          pivotX,
          pivotY,
          scaleX,
          scaleY,
          translateX,
          translateY,
          fillColor,
          fillAlpha,
          strokeColor,
          strokeAlpha,
          strokeWidth,
          trimPathStart,
          trimPathEnd,
          trimPathOffset,
          strokeLineCap,
          strokeLineJoin,
          strokeMiterLimit,
          strokeDashArray,
          strokeDashOffset,
          fillType,
          isScalingStroke,
          centerX,
          centerY,
          radiusX,
          radiusY);
    }
  }

  // </editor-fold>
}
