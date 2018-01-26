package com.github.alexjlockwood.kyrie;

import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;

import java.util.List;

public final class EllipseNode extends RenderNode {
  @NonNull private final List<PropertyAnimation<?, Float>> centerX;
  @NonNull private final List<PropertyAnimation<?, Float>> centerY;
  @NonNull private final List<PropertyAnimation<?, Float>> radiusX;
  @NonNull private final List<PropertyAnimation<?, Float>> radiusY;

  private EllipseNode(
      @NonNull List<PropertyAnimation<?, Float>> rotation,
      @NonNull List<PropertyAnimation<?, Float>> pivotX,
      @NonNull List<PropertyAnimation<?, Float>> pivotY,
      @NonNull List<PropertyAnimation<?, Float>> scaleX,
      @NonNull List<PropertyAnimation<?, Float>> scaleY,
      @NonNull List<PropertyAnimation<?, Float>> translateX,
      @NonNull List<PropertyAnimation<?, Float>> translateY,
      @NonNull List<PropertyAnimation<?, Integer>> fillColor,
      @NonNull List<PropertyAnimation<?, Float>> fillAlpha,
      @NonNull List<PropertyAnimation<?, Integer>> strokeColor,
      @NonNull List<PropertyAnimation<?, Float>> strokeAlpha,
      @NonNull List<PropertyAnimation<?, Float>> strokeWidth,
      @NonNull List<PropertyAnimation<?, Float>> trimPathStart,
      @NonNull List<PropertyAnimation<?, Float>> trimPathEnd,
      @NonNull List<PropertyAnimation<?, Float>> trimPathOffset,
      @StrokeLineCap int strokeLineCap,
      @StrokeLineJoin int strokeLineJoin,
      @NonNull List<PropertyAnimation<?, Float>> strokeMiterLimit,
      @NonNull List<PropertyAnimation<?, float[]>> strokeDashArray,
      @NonNull List<PropertyAnimation<?, Float>> strokeDashOffset,
      @FillType int fillType,
      boolean isStrokeScaling,
      @NonNull List<PropertyAnimation<?, Float>> centerX,
      @NonNull List<PropertyAnimation<?, Float>> centerY,
      @NonNull List<PropertyAnimation<?, Float>> radiusX,
      @NonNull List<PropertyAnimation<?, Float>> radiusY) {
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
  public List<PropertyAnimation<?, Float>> getCenterX() {
    return centerX;
  }

  @NonNull
  public List<PropertyAnimation<?, Float>> getCenterY() {
    return centerY;
  }

  @NonNull
  public List<PropertyAnimation<?, Float>> getRadiusX() {
    return radiusX;
  }

  @NonNull
  public List<PropertyAnimation<?, Float>> getRadiusY() {
    return radiusY;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  EllipseLayer toLayer(@NonNull PropertyTimeline timeline) {
    return new EllipseLayer(timeline, this);
  }

  private static final class EllipseLayer extends RenderLayer {
    @NonNull private final AnimatableProperty<Float> centerX;
    @NonNull private final AnimatableProperty<Float> centerY;
    @NonNull private final AnimatableProperty<Float> radiusX;
    @NonNull private final AnimatableProperty<Float> radiusY;

    private final RectF tempRect = new RectF();

    public EllipseLayer(@NonNull PropertyTimeline timeline, @NonNull EllipseNode node) {
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
    @NonNull private final List<PropertyAnimation<?, Float>> centerX = asAnimations(0f);
    @NonNull private final List<PropertyAnimation<?, Float>> centerY = asAnimations(0f);
    @NonNull private final List<PropertyAnimation<?, Float>> radiusX = asAnimations(0f);
    @NonNull private final List<PropertyAnimation<?, Float>> radiusY = asAnimations(0f);

    private Builder() {}

    // Center X.

    public final Builder centerX(float centerX) {
      return replaceFirstAnimation(this.centerX, asAnimation(centerX));
    }

    @SafeVarargs
    public final Builder centerX(@NonNull PropertyAnimation<?, Float>... animations) {
      return replaceAnimations(centerX, animations);
    }

    public final Builder centerX(@NonNull List<PropertyAnimation<?, Float>> animations) {
      return replaceAnimations(centerX, animations);
    }

    // Center Y.

    public final Builder centerY(float centerY) {
      return replaceFirstAnimation(this.centerY, asAnimation(centerY));
    }

    @SafeVarargs
    public final Builder centerY(@NonNull PropertyAnimation<?, Float>... animations) {
      return replaceAnimations(centerY, animations);
    }

    public final Builder centerY(@NonNull List<PropertyAnimation<?, Float>> animations) {
      return replaceAnimations(centerY, animations);
    }

    // Radius X.

    public final Builder radiusX(@FloatRange(from = 0f) float radiusX) {
      return replaceFirstAnimation(this.radiusX, asAnimation(radiusX));
    }

    @SafeVarargs
    public final Builder radiusX(@NonNull PropertyAnimation<?, Float>... animations) {
      return replaceAnimations(radiusX, animations);
    }

    public final Builder radiusX(@NonNull List<PropertyAnimation<?, Float>> animations) {
      return replaceAnimations(radiusX, animations);
    }

    // Radius Y.

    public final Builder radiusY(@FloatRange(from = 0f) float radiusY) {
      return replaceFirstAnimation(this.radiusY, asAnimation(radiusY));
    }

    @SafeVarargs
    public final Builder radiusY(@NonNull PropertyAnimation<?, Float>... animations) {
      return replaceAnimations(radiusY, animations);
    }

    public final Builder radiusY(@NonNull List<PropertyAnimation<?, Float>> animations) {
      return replaceAnimations(radiusY, animations);
    }

    @Override
    protected final Builder self() {
      return this;
    }

    public final EllipseNode build() {
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
          isStrokeScaling,
          centerX,
          centerY,
          radiusX,
          radiusY);
    }
  }

  // </editor-fold>
}
