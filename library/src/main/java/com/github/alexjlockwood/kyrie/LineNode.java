package com.github.alexjlockwood.kyrie;

import android.graphics.Path;
import android.support.annotation.NonNull;

import java.util.List;

/** A {@link Node} that paints a line. */
public final class LineNode extends RenderNode {
  @NonNull private final List<Animation<?, Float>> startX;
  @NonNull private final List<Animation<?, Float>> startY;
  @NonNull private final List<Animation<?, Float>> endX;
  @NonNull private final List<Animation<?, Float>> endY;

  private LineNode(
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
      List<Animation<?, Float>> startX,
      List<Animation<?, Float>> startY,
      List<Animation<?, Float>> endX,
      List<Animation<?, Float>> endY) {
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
    this.startX = startX;
    this.startY = startY;
    this.endX = endX;
    this.endY = endY;
  }

  @NonNull
  List<Animation<?, Float>> getStartX() {
    return startX;
  }

  @NonNull
  List<Animation<?, Float>> getStartY() {
    return startY;
  }

  @NonNull
  List<Animation<?, Float>> getEndX() {
    return endX;
  }

  @NonNull
  List<Animation<?, Float>> getEndY() {
    return endY;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  LineLayer toLayer(PropertyTimeline timeline) {
    return new LineLayer(timeline, this);
  }

  private static class LineLayer extends RenderLayer {
    @NonNull private final Property<Float> startX;
    @NonNull private final Property<Float> startY;
    @NonNull private final Property<Float> endX;
    @NonNull private final Property<Float> endY;

    public LineLayer(PropertyTimeline timeline, LineNode node) {
      super(timeline, node);
      startX = registerAnimatableProperty(node.getStartX());
      startY = registerAnimatableProperty(node.getStartY());
      endX = registerAnimatableProperty(node.getEndX());
      endY = registerAnimatableProperty(node.getEndY());
    }

    @Override
    public void onInitPath(Path outPath) {
      final float startX = this.startX.getAnimatedValue();
      final float startY = this.startY.getAnimatedValue();
      final float endX = this.endX.getAnimatedValue();
      final float endY = this.endY.getAnimatedValue();
      outPath.moveTo(startX, startY);
      outPath.lineTo(endX, endY);
    }
  }

  // </editor-fold>

  // <editor-fold desc="Builder">

  public static Builder builder() {
    return new Builder();
  }

  /** Builder class used to create {@link LineNode}s. */
  public static final class Builder extends RenderNode.Builder<Builder> {
    @NonNull private final List<Animation<?, Float>> startX = Node.asAnimations(0f);
    @NonNull private final List<Animation<?, Float>> startY = Node.asAnimations(0f);
    @NonNull private final List<Animation<?, Float>> endX = Node.asAnimations(0f);
    @NonNull private final List<Animation<?, Float>> endY = Node.asAnimations(0f);

    private Builder() {}

    // Start X.

    public Builder startX(float initialStartX) {
      return replaceFirstAnimation(startX, Node.asAnimation(initialStartX));
    }

    @SafeVarargs
    public final Builder startX(Animation<?, Float>... animations) {
      return replaceAnimations(startX, animations);
    }

    public Builder startX(List<Animation<?, Float>> animations) {
      return replaceAnimations(startX, animations);
    }

    // Start Y.

    public Builder startY(float initialStartY) {
      return replaceFirstAnimation(startY, Node.asAnimation(initialStartY));
    }

    @SafeVarargs
    public final Builder startY(Animation<?, Float>... animations) {
      return replaceAnimations(startY, animations);
    }

    public Builder startY(List<Animation<?, Float>> animations) {
      return replaceAnimations(startY, animations);
    }

    // End X.

    public Builder endX(float initialEndX) {
      return replaceFirstAnimation(endX, Node.asAnimation(initialEndX));
    }

    @SafeVarargs
    public final Builder endX(Animation<?, Float>... animations) {
      return replaceAnimations(endX, animations);
    }

    public Builder endX(List<Animation<?, Float>> animations) {
      return replaceAnimations(endX, animations);
    }

    // End Y.

    public Builder endY(float initialEndY) {
      return replaceFirstAnimation(endY, Node.asAnimation(initialEndY));
    }

    @SafeVarargs
    public final Builder endY(Animation<?, Float>... animations) {
      return replaceAnimations(endY, animations);
    }

    public Builder endY(List<Animation<?, Float>> animations) {
      return replaceAnimations(endY, animations);
    }

    @NonNull
    @Override
    Builder self() {
      return this;
    }

    @NonNull
    public LineNode build() {
      return new LineNode(
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
          startX,
          startY,
          endX,
          endY);
    }
  }

  // </editor-fold>
}
