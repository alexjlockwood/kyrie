package com.github.alexjlockwood.kyrie;

import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;

import java.util.List;

public final class RectangleNode extends RenderNode {
  @NonNull private final List<Animation<?, Float>> x;
  @NonNull private final List<Animation<?, Float>> y;
  @NonNull private final List<Animation<?, Float>> width;
  @NonNull private final List<Animation<?, Float>> height;
  @NonNull private final List<Animation<?, Float>> cornerRadiusX;
  @NonNull private final List<Animation<?, Float>> cornerRadiusY;

  private RectangleNode(
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
      @NonNull List<Animation<?, Float>> x,
      @NonNull List<Animation<?, Float>> y,
      @NonNull List<Animation<?, Float>> width,
      @NonNull List<Animation<?, Float>> height,
      @NonNull List<Animation<?, Float>> cornerRadiusX,
      @NonNull List<Animation<?, Float>> cornerRadiusY) {
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
  public List<Animation<?, Float>> getX() {
    return x;
  }

  @NonNull
  public List<Animation<?, Float>> getY() {
    return y;
  }

  @NonNull
  public List<Animation<?, Float>> getWidth() {
    return width;
  }

  @NonNull
  public List<Animation<?, Float>> getHeight() {
    return height;
  }

  @NonNull
  public List<Animation<?, Float>> getCornerRadiusX() {
    return cornerRadiusX;
  }

  @NonNull
  public List<Animation<?, Float>> getCornerRadiusY() {
    return cornerRadiusY;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  RectangleLayer toLayer(@NonNull Timeline timeline) {
    return new RectangleLayer(timeline, this);
  }

  private static final class RectangleLayer extends RenderLayer {
    @NonNull private final Property<Float> x;
    @NonNull private final Property<Float> y;
    @NonNull private final Property<Float> width;
    @NonNull private final Property<Float> height;
    @NonNull private final Property<Float> cornerRadiusX;
    @NonNull private final Property<Float> cornerRadiusY;

    private final RectF tempRect = new RectF();

    public RectangleLayer(@NonNull Timeline timeline, @NonNull RectangleNode node) {
      super(timeline, node);
      x = registerAnimatableProperty(node.getX());
      y = registerAnimatableProperty(node.getY());
      width = registerAnimatableProperty(node.getWidth());
      height = registerAnimatableProperty(node.getHeight());
      cornerRadiusX = registerAnimatableProperty(node.getCornerRadiusX());
      cornerRadiusY = registerAnimatableProperty(node.getCornerRadiusY());
    }

    @Override
    public void onInitPath(@NonNull Path outPath) {
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

  public static final class Builder extends RenderNode.Builder<RectangleNode, Builder> {
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
    public final Builder x(@NonNull Animation<?, Float>... animations) {
      return replaceAnimations(x, animations);
    }

    public Builder x(@NonNull List<Animation<?, Float>> animations) {
      return replaceAnimations(x, animations);
    }

    // Y.

    public Builder y(float initialY) {
      return replaceFirstAnimation(y, asAnimation(initialY));
    }

    @SafeVarargs
    public final Builder y(@NonNull Animation<?, Float>... animations) {
      return replaceAnimations(y, animations);
    }

    public Builder y(@NonNull List<Animation<?, Float>> animations) {
      return replaceAnimations(y, animations);
    }

    // Width.

    public Builder width(@FloatRange(from = 0f) float initialWidth) {
      return replaceFirstAnimation(width, asAnimation(initialWidth));
    }

    @SafeVarargs
    public final Builder width(@NonNull Animation<?, Float>... animations) {
      return replaceAnimations(width, animations);
    }

    public Builder width(@NonNull List<Animation<?, Float>> animations) {
      return replaceAnimations(width, animations);
    }

    // Height.

    public Builder height(@FloatRange(from = 0f) float initialHeight) {
      return replaceFirstAnimation(height, asAnimation(initialHeight));
    }

    @SafeVarargs
    public final Builder height(@NonNull Animation<?, Float>... animations) {
      return replaceAnimations(height, animations);
    }

    public Builder height(@NonNull List<Animation<?, Float>> animations) {
      return replaceAnimations(height, animations);
    }

    // Corner radius X.

    public Builder cornerRadiusX(@FloatRange(from = 0f) float initialCornerRadiusX) {
      return replaceFirstAnimation(cornerRadiusX, asAnimation(initialCornerRadiusX));
    }

    @SafeVarargs
    public final Builder cornerRadiusX(@NonNull Animation<?, Float>... animations) {
      return replaceAnimations(cornerRadiusX, animations);
    }

    public Builder cornerRadiusX(@NonNull List<Animation<?, Float>> animations) {
      return replaceAnimations(cornerRadiusX, animations);
    }

    // Corner radius Y.

    public Builder cornerRadiusY(@FloatRange(from = 0f) float initialCornerRadiusY) {
      return replaceFirstAnimation(cornerRadiusY, asAnimation(initialCornerRadiusY));
    }

    @SafeVarargs
    public final Builder cornerRadiusY(@NonNull Animation<?, Float>... animations) {
      return replaceAnimations(cornerRadiusY, animations);
    }

    public Builder cornerRadiusY(@NonNull List<Animation<?, Float>> animations) {
      return replaceAnimations(cornerRadiusY, animations);
    }

    @Override
    protected Builder self() {
      return this;
    }

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
