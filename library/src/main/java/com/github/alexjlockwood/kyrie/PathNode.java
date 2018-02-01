package com.github.alexjlockwood.kyrie;

import android.graphics.Path;
import android.support.annotation.NonNull;

import java.util.List;

/** A {@link Node} that paints a path. */
public final class PathNode extends RenderNode {
  @NonNull private final List<Animation<?, PathData>> pathData;

  private PathNode(
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
      List<Animation<?, PathData>> pathData) {
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
    this.pathData = pathData;
  }

  @NonNull
  List<Animation<?, PathData>> getPathData() {
    return pathData;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  PathLayer toLayer(PropertyTimeline timeline) {
    return new PathLayer(timeline, this);
  }

  private static class PathLayer extends RenderLayer {
    @NonNull private final Property<PathData> pathData;

    public PathLayer(PropertyTimeline timeline, PathNode node) {
      super(timeline, node);
      pathData = registerAnimatableProperty(node.getPathData());
    }

    @Override
    public void onInitPath(Path outPath) {
      PathData.toPath(pathData.getAnimatedValue(), outPath);
    }
  }

  // </editor-fold>

  // <editor-fold desc="Builder">

  public static Builder builder() {
    return new Builder();
  }

  /** Builder class used to create {@link PathNode}s. */
  public static final class Builder extends RenderNode.Builder<Builder> {
    @NonNull private final List<Animation<?, PathData>> pathData = asAnimations(new PathData());

    private Builder() {}

    // Path data.

    public Builder pathData(String initialPathData) {
      return pathData(PathData.parse(initialPathData));
    }

    public Builder pathData(PathData initialPathData) {
      return replaceFirstAnimation(pathData, asAnimation(initialPathData));
    }

    @SafeVarargs
    public final Builder pathData(Animation<?, PathData>... animations) {
      return replaceAnimations(pathData, animations);
    }

    public Builder pathData(List<Animation<?, PathData>> animations) {
      return replaceAnimations(pathData, animations);
    }

    @NonNull
    @Override
    Builder self() {
      return this;
    }

    @NonNull
    public PathNode build() {
      return new PathNode(
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
          pathData);
    }
  }

  // </editor-fold>
}
