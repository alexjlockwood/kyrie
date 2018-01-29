package com.github.alexjlockwood.kyrie;

import android.graphics.Path;
import android.support.annotation.NonNull;

import java.util.List;

public final class PathNode extends RenderNode {
  @NonNull private final List<Animation<?, PathData>> pathData;

  private PathNode(
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
      @NonNull List<Animation<?, PathData>> pathData) {
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
  public List<Animation<?, PathData>> getPathData() {
    return pathData;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  PathLayer toLayer(@NonNull PropertyTimeline timeline) {
    return new PathLayer(timeline, this);
  }

  private static class PathLayer extends RenderLayer {
    @NonNull private final Property<PathData> pathData;

    public PathLayer(@NonNull PropertyTimeline timeline, @NonNull PathNode node) {
      super(timeline, node);
      pathData = registerAnimatableProperty(node.getPathData());
    }

    @Override
    public void onInitPath(@NonNull Path outPath) {
      PathData.toPath(pathData.getAnimatedValue(), outPath);
    }
  }

  // </editor-fold>

  // <editor-fold desc="Builder">

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder extends RenderNode.Builder<PathNode, Builder> {
    @NonNull private final List<Animation<?, PathData>> pathData = asAnimations(new PathData());

    private Builder() {}

    // Path data.

    public Builder pathData(@NonNull String initialPathData) {
      return pathData(PathData.parse(initialPathData));
    }

    public Builder pathData(@NonNull PathData initialPathData) {
      return replaceFirstAnimation(pathData, asAnimation(initialPathData));
    }

    @SafeVarargs
    public final Builder pathData(@NonNull Animation<?, PathData>... animations) {
      return replaceAnimations(pathData, animations);
    }

    public Builder pathData(@NonNull List<Animation<?, PathData>> animations) {
      return replaceAnimations(pathData, animations);
    }

    @Override
    protected Builder self() {
      return this;
    }

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
