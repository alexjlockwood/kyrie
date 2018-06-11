package com.github.alexjlockwood.kyrie;

import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;

import java.util.List;

/** A {@link Node} that paints a rectangle. */
public final class RectangleNode extends RenderNode {
  @NonNull private final List<Animation<?, Float>> x;
  @NonNull private final List<Animation<?, Float>> y;
  @NonNull private final List<Animation<?, Float>> width;
  @NonNull private final List<Animation<?, Float>> height;
  @NonNull private final List<Animation<?, Float>> cornerRadiusX;
  @NonNull private final List<Animation<?, Float>> cornerRadiusY;

  private RectangleNode(
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
      List<Animation<?, Float>> x,
      List<Animation<?, Float>> y,
      List<Animation<?, Float>> width,
      List<Animation<?, Float>> height,
      List<Animation<?, Float>> cornerRadiusX,
      List<Animation<?, Float>> cornerRadiusY) {
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
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.cornerRadiusX = cornerRadiusX;
    this.cornerRadiusY = cornerRadiusY;
  }

  @NonNull
  List<Animation<?, Float>> getX() {
    return x;
  }

  @NonNull
  List<Animation<?, Float>> getY() {
    return y;
  }

  @NonNull
  List<Animation<?, Float>> getWidth() {
    return width;
  }

  @NonNull
  List<Animation<?, Float>> getHeight() {
    return height;
  }

  @NonNull
  List<Animation<?, Float>> getCornerRadiusX() {
    return cornerRadiusX;
  }

  @NonNull
  List<Animation<?, Float>> getCornerRadiusY() {
    return cornerRadiusY;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  RectangleLayer toLayer(PropertyTimeline timeline) {
    return new RectangleLayer(timeline, this);
  }

  private static class RectangleLayer extends RenderLayer {
    @NonNull private final Property<Float> x;
    @NonNull private final Property<Float> y;
    @NonNull private final Property<Float> width;
    @NonNull private final Property<Float> height;
    @NonNull private final Property<Float> cornerRadiusX;
    @NonNull private final Property<Float> cornerRadiusY;

    private final RectF tempRect = new RectF();

    public RectangleLayer(PropertyTimeline timeline, RectangleNode node) {
      super(timeline, node);
      x = registerAnimatableProperty(node.getX());
      y = registerAnimatableProperty(node.getY());
      width = registerAnimatableProperty(node.getWidth());
      height = registerAnimatableProperty(node.getHeight());
      cornerRadiusX = registerAnimatableProperty(node.getCornerRadiusX());
      cornerRadiusY = registerAnimatableProperty(node.getCornerRadiusY());
    }

    @Override
    public void onInitPath(Path outPath) {
      final float l = x.getAnimatedValue();
      final float t = y.getAnimatedValue();
      final float r = l + width.getAnimatedValue();
      final float b = t + height.getAnimatedValue();
      final float rx = cornerRadiusX.getAnimatedValue();
      final float ry = cornerRadiusY.getAnimatedValue();
      tempRect.set(l, t, r, b);
      outPath.addRoundRect(tempRect, rx, ry, Path.Direction.CW);
    }
  }

  // </editor-fold>

  // <editor-fold desc="Builder">

  public static Builder builder() {
    return new Builder();
  }

  /** Builder class used to create {@link RectangleNode}s. */
  public static final class Builder extends RenderNode.Builder<Builder> {
    @NonNull private final List<Animation<?, Float>> x = Node.asAnimations(0f);
    @NonNull private final List<Animation<?, Float>> y = Node.asAnimations(0f);
    @NonNull private final List<Animation<?, Float>> width = Node.asAnimations(0f);
    @NonNull private final List<Animation<?, Float>> height = Node.asAnimations(0f);
    @NonNull private final List<Animation<?, Float>> cornerRadiusX = Node.asAnimations(0f);
    @NonNull private final List<Animation<?, Float>> cornerRadiusY = Node.asAnimations(0f);

    private Builder() {}

    // X.

    public Builder x(float initialX) {
      return replaceFirstAnimation(x, asAnimation(initialX));
    }

    @SafeVarargs
    public final Builder x(Animation<?, Float>... animations) {
      return replaceAnimations(x, animations);
    }

    public Builder x(List<Animation<?, Float>> animations) {
      return replaceAnimations(x, animations);
    }

    // Y.

    public Builder y(float initialY) {
      return replaceFirstAnimation(y, asAnimation(initialY));
    }

    @SafeVarargs
    public final Builder y(Animation<?, Float>... animations) {
      return replaceAnimations(y, animations);
    }

    public Builder y(List<Animation<?, Float>> animations) {
      return replaceAnimations(y, animations);
    }

    // Width.

    public Builder width(@FloatRange(from = 0f) float initialWidth) {
      return replaceFirstAnimation(width, asAnimation(initialWidth));
    }

    @SafeVarargs
    public final Builder width(Animation<?, Float>... animations) {
      return replaceAnimations(width, animations);
    }

    public Builder width(List<Animation<?, Float>> animations) {
      return replaceAnimations(width, animations);
    }

    // Height.

    public Builder height(@FloatRange(from = 0f) float initialHeight) {
      return replaceFirstAnimation(height, asAnimation(initialHeight));
    }

    @SafeVarargs
    public final Builder height(Animation<?, Float>... animations) {
      return replaceAnimations(height, animations);
    }

    public Builder height(List<Animation<?, Float>> animations) {
      return replaceAnimations(height, animations);
    }

    // Corner radius X.

    public Builder cornerRadiusX(@FloatRange(from = 0f) float initialCornerRadiusX) {
      return replaceFirstAnimation(cornerRadiusX, asAnimation(initialCornerRadiusX));
    }

    @SafeVarargs
    public final Builder cornerRadiusX(Animation<?, Float>... animations) {
      return replaceAnimations(cornerRadiusX, animations);
    }

    public Builder cornerRadiusX(List<Animation<?, Float>> animations) {
      return replaceAnimations(cornerRadiusX, animations);
    }

    // Corner radius Y.

    public Builder cornerRadiusY(@FloatRange(from = 0f) float initialCornerRadiusY) {
      return replaceFirstAnimation(cornerRadiusY, asAnimation(initialCornerRadiusY));
    }

    @SafeVarargs
    public final Builder cornerRadiusY(Animation<?, Float>... animations) {
      return replaceAnimations(cornerRadiusY, animations);
    }

    public Builder cornerRadiusY(List<Animation<?, Float>> animations) {
      return replaceAnimations(cornerRadiusY, animations);
    }

    @NonNull
    @Override
    Builder self() {
      return this;
    }

    @NonNull
    public RectangleNode build() {
      return new RectangleNode(
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
          x,
          y,
          width,
          height,
          cornerRadiusX,
          cornerRadiusY);
    }
  }

  // </editor-fold>
}
