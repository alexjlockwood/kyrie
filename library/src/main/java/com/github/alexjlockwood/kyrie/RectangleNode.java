package com.github.alexjlockwood.kyrie;

import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;

import java.util.List;

public final class RectangleNode extends RenderNode {
  @NonNull private final List<PropertyAnimation<?, Float>> x;
  @NonNull private final List<PropertyAnimation<?, Float>> y;
  @NonNull private final List<PropertyAnimation<?, Float>> width;
  @NonNull private final List<PropertyAnimation<?, Float>> height;
  @NonNull private final List<PropertyAnimation<?, Float>> cornerRadiusX;
  @NonNull private final List<PropertyAnimation<?, Float>> cornerRadiusY;

  private RectangleNode(
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
      @NonNull List<PropertyAnimation<?, Float>> x,
      @NonNull List<PropertyAnimation<?, Float>> y,
      @NonNull List<PropertyAnimation<?, Float>> width,
      @NonNull List<PropertyAnimation<?, Float>> height,
      @NonNull List<PropertyAnimation<?, Float>> cornerRadiusX,
      @NonNull List<PropertyAnimation<?, Float>> cornerRadiusY) {
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
  public List<PropertyAnimation<?, Float>> getX() {
    return x;
  }

  @NonNull
  public List<PropertyAnimation<?, Float>> getY() {
    return y;
  }

  @NonNull
  public List<PropertyAnimation<?, Float>> getWidth() {
    return width;
  }

  @NonNull
  public List<PropertyAnimation<?, Float>> getHeight() {
    return height;
  }

  @NonNull
  public List<PropertyAnimation<?, Float>> getCornerRadiusX() {
    return cornerRadiusX;
  }

  @NonNull
  public List<PropertyAnimation<?, Float>> getCornerRadiusY() {
    return cornerRadiusY;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  RectangleLayer toLayer(@NonNull PropertyTimeline timeline) {
    return new RectangleLayer(timeline, this);
  }

  private static final class RectangleLayer extends RenderLayer {
    @NonNull private final AnimatableProperty<Float> x;
    @NonNull private final AnimatableProperty<Float> y;
    @NonNull private final AnimatableProperty<Float> width;
    @NonNull private final AnimatableProperty<Float> height;
    @NonNull private final AnimatableProperty<Float> cornerRadiusX;
    @NonNull private final AnimatableProperty<Float> cornerRadiusY;

    private final RectF tempRect = new RectF();

    public RectangleLayer(@NonNull PropertyTimeline timeline, @NonNull RectangleNode node) {
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
    @NonNull private final List<PropertyAnimation<?, Float>> x = Node.asAnimations(0f);
    @NonNull private final List<PropertyAnimation<?, Float>> y = Node.asAnimations(0f);

    @NonNull private final List<PropertyAnimation<?, Float>> width = Node.asAnimations(0f);

    @NonNull private final List<PropertyAnimation<?, Float>> height = Node.asAnimations(0f);

    @NonNull private final List<PropertyAnimation<?, Float>> cornerRadiusX = Node.asAnimations(0f);

    @NonNull private final List<PropertyAnimation<?, Float>> cornerRadiusY = Node.asAnimations(0f);

    private Builder() {}

    // X.

    public final Builder x(float x) {
      return replaceFirstAnimation(this.x, asAnimation(x));
    }

    @SafeVarargs
    public final Builder x(@NonNull PropertyAnimation<?, Float>... keyframes) {
      return replaceAnimations(x, keyframes);
    }

    public final Builder x(@NonNull List<PropertyAnimation<?, Float>> keyframes) {
      return replaceAnimations(x, keyframes);
    }

    // Y.

    public final Builder y(float y) {
      return replaceFirstAnimation(this.y, asAnimation(y));
    }

    @SafeVarargs
    public final Builder y(@NonNull PropertyAnimation<?, Float>... keyframes) {
      return replaceAnimations(y, keyframes);
    }

    public final Builder y(@NonNull List<PropertyAnimation<?, Float>> keyframes) {
      return replaceAnimations(y, keyframes);
    }

    // Width.

    public final Builder width(@FloatRange(from = 0f) float width) {
      return replaceFirstAnimation(this.width, asAnimation(width));
    }

    @SafeVarargs
    public final Builder width(@NonNull PropertyAnimation<?, Float>... animations) {
      return replaceAnimations(width, animations);
    }

    public final Builder width(@NonNull List<PropertyAnimation<?, Float>> animations) {
      return replaceAnimations(width, animations);
    }

    // Height.

    public final Builder height(@FloatRange(from = 0f) float height) {
      return replaceFirstAnimation(this.height, asAnimation(height));
    }

    @SafeVarargs
    public final Builder height(@NonNull PropertyAnimation<?, Float>... animations) {
      return replaceAnimations(height, animations);
    }

    public final Builder height(@NonNull List<PropertyAnimation<?, Float>> keyframes) {
      return replaceAnimations(height, keyframes);
    }

    // Corner radius X.

    public final Builder cornerRadiusX(@FloatRange(from = 0f) float cornerRadiusX) {
      return replaceFirstAnimation(this.cornerRadiusX, asAnimation(cornerRadiusX));
    }

    @SafeVarargs
    public final Builder cornerRadiusX(@NonNull PropertyAnimation<?, Float>... animations) {
      return replaceAnimations(cornerRadiusX, animations);
    }

    public final Builder cornerRadiusX(@NonNull List<PropertyAnimation<?, Float>> animations) {
      return replaceAnimations(cornerRadiusX, animations);
    }

    // Corner radius Y.

    public final Builder cornerRadiusY(@FloatRange(from = 0f) float cornerRadiusY) {
      return replaceFirstAnimation(this.cornerRadiusY, asAnimation(cornerRadiusY));
    }

    @SafeVarargs
    public final Builder cornerRadiusY(@NonNull PropertyAnimation<?, Float>... animations) {
      return replaceAnimations(cornerRadiusY, animations);
    }

    public final Builder cornerRadiusY(@NonNull List<PropertyAnimation<?, Float>> animations) {
      return replaceAnimations(cornerRadiusY, animations);
    }

    @Override
    protected final Builder self() {
      return this;
    }

    public final RectangleNode build() {
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
          isStrokeScaling,
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
