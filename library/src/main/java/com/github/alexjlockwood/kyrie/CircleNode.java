package com.github.alexjlockwood.kyrie;

import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;

import java.util.List;

public final class CircleNode extends RenderNode {
  @NonNull private final List<Animation<?, Float>> centerX;
  @NonNull private final List<Animation<?, Float>> centerY;
  @NonNull private final List<Animation<?, Float>> radius;

  private CircleNode(
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
      @NonNull List<Animation<?, Float>> radius) {
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
  public List<Animation<?, Float>> getCenterX() {
    return centerX;
  }

  @NonNull
  public List<Animation<?, Float>> getCenterY() {
    return centerY;
  }

  @NonNull
  public List<Animation<?, Float>> getRadius() {
    return radius;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  CircleLayer toLayer(@NonNull Timeline timeline) {
    return new CircleLayer(timeline, this);
  }

  private static final class CircleLayer extends RenderLayer {
    @NonNull private final Property<Float> centerX;
    @NonNull private final Property<Float> centerY;
    @NonNull private final Property<Float> radius;

    private final RectF tempRect = new RectF();

    public CircleLayer(@NonNull Timeline timeline, @NonNull CircleNode node) {
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
    @NonNull private final List<Animation<?, Float>> centerX = Node.asAnimations(0f);

    @NonNull private final List<Animation<?, Float>> centerY = Node.asAnimations(0f);

    @NonNull private final List<Animation<?, Float>> radius = Node.asAnimations(0f);

    private Builder() {}

    // Center X.

    public final Builder centerX(float centerX) {
      return replaceFirstAnimation(this.centerX, Node.asAnimation(centerX));
    }

    @SafeVarargs
    public final Builder centerX(@NonNull Animation<?, Float>... animations) {
      return replaceAnimations(centerX, animations);
    }

    public final Builder centerX(@NonNull List<Animation<?, Float>> animations) {
      return replaceAnimations(centerX, animations);
    }

    // Center Y.

    public final Builder centerY(float centerY) {
      return replaceFirstAnimation(this.centerY, Node.asAnimation(centerY));
    }

    @SafeVarargs
    public final Builder centerY(@NonNull Animation<?, Float>... animations) {
      return replaceAnimations(centerY, animations);
    }

    public final Builder centerY(@NonNull List<Animation<?, Float>> animations) {
      return replaceAnimations(centerY, animations);
    }

    // Radius.

    public final Builder radius(@FloatRange(from = 0f) float radius) {
      return replaceFirstAnimation(this.radius, Node.asAnimation(radius));
    }

    @SafeVarargs
    public final Builder radius(@NonNull Animation<?, Float>... animations) {
      return replaceAnimations(radius, animations);
    }

    public final Builder radius(@NonNull List<Animation<?, Float>> animations) {
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
