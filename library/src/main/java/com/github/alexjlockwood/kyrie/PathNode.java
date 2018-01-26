package com.github.alexjlockwood.kyrie;

import android.graphics.Path;
import android.support.annotation.NonNull;

import java.util.List;

public final class PathNode extends RenderNode {
  @NonNull private final List<PropertyAnimation<?, PathData>> pathData;

  private PathNode(
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
      @NonNull List<PropertyAnimation<?, PathData>> pathData) {
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
  public List<PropertyAnimation<?, PathData>> getPathData() {
    return pathData;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  PathLayer toLayer(@NonNull PropertyTimeline timeline) {
    return new PathLayer(timeline, this);
  }

  private static final class PathLayer extends RenderLayer {
    @NonNull private final AnimatableProperty<PathData> pathData;

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
    @NonNull
    private final List<PropertyAnimation<?, PathData>> pathData = asAnimations(new PathData());

    private Builder() {}

    // Path data.

    public final Builder pathData(@NonNull String pathData) {
      return pathData(PathData.parse(pathData));
    }

    public final Builder pathData(@NonNull PathData pathData) {
      return replaceFirstAnimation(this.pathData, asAnimation(pathData));
    }

    @SafeVarargs
    public final Builder pathData(@NonNull PropertyAnimation<?, PathData>... keyframes) {
      return replaceAnimations(pathData, keyframes);
    }

    public final Builder pathData(@NonNull List<PropertyAnimation<?, PathData>> keyframes) {
      return replaceAnimations(pathData, keyframes);
    }

    @Override
    protected final Builder self() {
      return this;
    }

    public final PathNode build() {
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
          isStrokeScaling,
          pathData);
    }
  }

  // </editor-fold>
}
