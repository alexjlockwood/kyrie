package com.github.alexjlockwood.kyrie;

import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;

import java.util.List;

public final class EllipseNode extends RenderNode {
  @NonNull private final List<Animation<?, Float>> centerX;
  @NonNull private final List<Animation<?, Float>> centerY;
  @NonNull private final List<Animation<?, Float>> radiusX;
  @NonNull private final List<Animation<?, Float>> radiusY;

  private EllipseNode(
      @NonNull List<Animation<?, Float>> rotation,
      @NonNull List<Animation<?, Float>> pivotX,
      @NonNull List<Animation<?, Float>> pivotY,
      @NonNull List<Animation<?, Float>> scaleX,
      @NonNull List<Animation<?, Float>> scaleY,
      @NonNull List<Animation<?, Float>> translateX,
      @NonNull List<Animation<?, Float>> translateY,
      @NonNull List<Animation<?, Integer>> fillColor,
      @NonNull List<Animation<?, Float>> fillAlpha,
      @NonNull List<Animation<?, Integer>> strokeColor,
      @NonNull List<Animation<?, Float>> strokeAlpha,
      @NonNull List<Animation<?, Float>> strokeWidth,
      @NonNull List<Animation<?, Float>> trimPathStart,
      @NonNull List<Animation<?, Float>> trimPathEnd,
      @NonNull List<Animation<?, Float>> trimPathOffset,
      @StrokeLineCap int strokeLineCap,
      @StrokeLineJoin int strokeLineJoin,
      @NonNull List<Animation<?, Float>> strokeMiterLimit,
      @NonNull List<Animation<?, float[]>> strokeDashArray,
      @NonNull List<Animation<?, Float>> strokeDashOffset,
      @FillType int fillType,
      boolean isStrokeScaling,
      @NonNull List<Animation<?, Float>> centerX,
      @NonNull List<Animation<?, Float>> centerY,
      @NonNull List<Animation<?, Float>> radiusX,
      @NonNull List<Animation<?, Float>> radiusY) {
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
  public List<Animation<?, Float>> getCenterX() {
    return centerX;
  }

  @NonNull
  public List<Animation<?, Float>> getCenterY() {
    return centerY;
  }

  @NonNull
  public List<Animation<?, Float>> getRadiusX() {
    return radiusX;
  }

  @NonNull
  public List<Animation<?, Float>> getRadiusY() {
    return radiusY;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  EllipseLayer toLayer(@NonNull Timeline timeline) {
    return new EllipseLayer(timeline, this);
  }

  private static final class EllipseLayer extends RenderLayer {
    @NonNull private final Property<Float> centerX;
    @NonNull private final Property<Float> centerY;
    @NonNull private final Property<Float> radiusX;
    @NonNull private final Property<Float> radiusY;

    private final RectF tempRect = new RectF();

    public EllipseLayer(@NonNull Timeline timeline, @NonNull EllipseNode node) {
      super(timeline, node);
      centerX = registerAnimatableProperty(node.getCenterX());
      centerY = registerAnimatableProperty(node.getCenterY());
      radiusX = registerAnimatableProperty(node.getRadiusX());
      radiusY = registerAnimatableProperty(node.getRadiusY());
    }

    @Override
    public void onInitPath(@NonNull Path outPath) {
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

  public static final class Builder extends RenderNode.Builder<EllipseNode, Builder> {
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
    public final Builder centerX(@NonNull Animation<?, Float>... animations) {
      return replaceAnimations(centerX, animations);
    }

    public Builder centerX(@NonNull List<Animation<?, Float>> animations) {
      return replaceAnimations(centerX, animations);
    }

    // Center Y.

    public Builder centerY(float initialCenterY) {
      return replaceFirstAnimation(centerY, asAnimation(initialCenterY));
    }

    @SafeVarargs
    public final Builder centerY(@NonNull Animation<?, Float>... animations) {
      return replaceAnimations(centerY, animations);
    }

    public Builder centerY(@NonNull List<Animation<?, Float>> animations) {
      return replaceAnimations(centerY, animations);
    }

    // Radius X.

    public Builder radiusX(@FloatRange(from = 0f) float initialRadiusX) {
      return replaceFirstAnimation(radiusX, asAnimation(initialRadiusX));
    }

    @SafeVarargs
    public final Builder radiusX(@NonNull Animation<?, Float>... animations) {
      return replaceAnimations(radiusX, animations);
    }

    public Builder radiusX(@NonNull List<Animation<?, Float>> animations) {
      return replaceAnimations(radiusX, animations);
    }

    // Radius Y.

    public Builder radiusY(@FloatRange(from = 0f) float initialRadiusY) {
      return replaceFirstAnimation(radiusY, asAnimation(initialRadiusY));
    }

    @SafeVarargs
    public final Builder radiusY(@NonNull Animation<?, Float>... animations) {
      return replaceAnimations(radiusY, animations);
    }

    public Builder radiusY(@NonNull List<Animation<?, Float>> animations) {
      return replaceAnimations(radiusY, animations);
    }

    @Override
    protected Builder self() {
      return this;
    }

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
