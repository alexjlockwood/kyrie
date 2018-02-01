package com.github.alexjlockwood.kyrie;

import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;

import java.util.List;

/** A {@link Node} that paints a circle. */
public final class CircleNode extends RenderNode {
  @NonNull private final List<Animation<?, Float>> centerX;
  @NonNull private final List<Animation<?, Float>> centerY;
  @NonNull private final List<Animation<?, Float>> radius;

  private CircleNode(
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
      List<Animation<?, Float>> radius) {
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
  List<Animation<?, Float>> getCenterX() {
    return centerX;
  }

  @NonNull
  List<Animation<?, Float>> getCenterY() {
    return centerY;
  }

  @NonNull
  List<Animation<?, Float>> getRadius() {
    return radius;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  CircleLayer toLayer(PropertyTimeline timeline) {
    return new CircleLayer(timeline, this);
  }

  private static class CircleLayer extends RenderLayer {
    @NonNull private final Property<Float> centerX;
    @NonNull private final Property<Float> centerY;
    @NonNull private final Property<Float> radius;

    private final RectF tempRect = new RectF();

    public CircleLayer(PropertyTimeline timeline, CircleNode node) {
      super(timeline, node);
      centerX = registerAnimatableProperty(node.getCenterX());
      centerY = registerAnimatableProperty(node.getCenterY());
      radius = registerAnimatableProperty(node.getRadius());
    }

    @Override
    public void onInitPath(Path outPath) {
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

  /** Builder class used to create {@link CircleNode}s. */
  public static final class Builder extends RenderNode.Builder<Builder> {
    @NonNull private final List<Animation<?, Float>> centerX = Node.asAnimations(0f);
    @NonNull private final List<Animation<?, Float>> centerY = Node.asAnimations(0f);
    @NonNull private final List<Animation<?, Float>> radius = Node.asAnimations(0f);

    private Builder() {}

    // Center X.

    public Builder centerX(float initialCenterX) {
      return replaceFirstAnimation(centerX, Node.asAnimation(initialCenterX));
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
      return replaceFirstAnimation(centerY, Node.asAnimation(initialCenterY));
    }

    @SafeVarargs
    public final Builder centerY(Animation<?, Float>... animations) {
      return replaceAnimations(centerY, animations);
    }

    public Builder centerY(List<Animation<?, Float>> animations) {
      return replaceAnimations(centerY, animations);
    }

    // Radius.

    public Builder radius(@FloatRange(from = 0f) float initialRadius) {
      return replaceFirstAnimation(radius, Node.asAnimation(initialRadius));
    }

    @SafeVarargs
    public final Builder radius(Animation<?, Float>... animations) {
      return replaceAnimations(radius, animations);
    }

    public Builder radius(List<Animation<?, Float>> animations) {
      return replaceAnimations(radius, animations);
    }

    @NonNull
    @Override
    Builder self() {
      return this;
    }

    @NonNull
    public CircleNode build() {
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
          isScalingStroke,
          centerX,
          centerY,
          radius);
    }
  }

  // </editor-fold>
}
