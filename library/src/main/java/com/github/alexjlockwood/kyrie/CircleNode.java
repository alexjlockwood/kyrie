package com.github.alexjlockwood.kyrie;

import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;

import java.util.List;

public final class CircleNode extends RenderNode {
  @NonNull private final List<PropertyAnimation<?, Float>> centerX;
  @NonNull private final List<PropertyAnimation<?, Float>> centerY;
  @NonNull private final List<PropertyAnimation<?, Float>> radius;

  private CircleNode(
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
      @NonNull List<PropertyAnimation<?, Float>> radius) {
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
    this.radius = radius;
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
  public List<PropertyAnimation<?, Float>> getRadius() {
    return radius;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  CircleLayer toLayer(@NonNull PropertyTimeline timeline) {
    return new CircleLayer(timeline, this);
  }

  private static final class CircleLayer extends RenderLayer {
    @NonNull private final AnimatableProperty<Float> centerX;
    @NonNull private final AnimatableProperty<Float> centerY;
    @NonNull private final AnimatableProperty<Float> radius;

    private final RectF tempRect = new RectF();

    public CircleLayer(@NonNull PropertyTimeline timeline, @NonNull CircleNode node) {
      super(timeline, node);
      centerX = registerAnimatableProperty(node.getCenterX());
      centerY = registerAnimatableProperty(node.getCenterY());
      radius = registerAnimatableProperty(node.getRadius());
    }

    @Override
    public void onInitPath(@NonNull Path outPath) {
      final float cx = centerX.getAnimatedValue();
      final float cy = centerY.getAnimatedValue();
      final float r = radius.getAnimatedValue();
      tempRect.set(cx - r, cy - r, cx + r, cy + r);
      outPath.addOval(tempRect, Path.Direction.CW);
    }
  }

  // </editor-fold>

  // <editor-fold desc="Builder">

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder extends RenderNode.Builder<CircleNode, Builder> {
    @NonNull private final List<PropertyAnimation<?, Float>> centerX = Node.asAnimations(0f);

    @NonNull private final List<PropertyAnimation<?, Float>> centerY = Node.asAnimations(0f);

    @NonNull private final List<PropertyAnimation<?, Float>> radius = Node.asAnimations(0f);

    private Builder() {}

    // Center X.

    public final Builder centerX(float centerX) {
      return replaceFirstAnimation(this.centerX, Node.asAnimation(centerX));
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
      return replaceFirstAnimation(this.centerY, Node.asAnimation(centerY));
    }

    @SafeVarargs
    public final Builder centerY(@NonNull PropertyAnimation<?, Float>... animations) {
      return replaceAnimations(centerY, animations);
    }

    public final Builder centerY(@NonNull List<PropertyAnimation<?, Float>> animations) {
      return replaceAnimations(centerY, animations);
    }

    // Radius.

    public final Builder radius(@FloatRange(from = 0f) float radius) {
      return replaceFirstAnimation(this.radius, Node.asAnimation(radius));
    }

    @SafeVarargs
    public final Builder radius(@NonNull PropertyAnimation<?, Float>... animations) {
      return replaceAnimations(radius, animations);
    }

    public final Builder radius(@NonNull List<PropertyAnimation<?, Float>> animations) {
      return replaceAnimations(radius, animations);
    }

    @Override
    protected final Builder self() {
      return this;
    }

    public final CircleNode build() {
      return new CircleNode(
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
          radius);
    }
  }

  // </editor-fold>
}
