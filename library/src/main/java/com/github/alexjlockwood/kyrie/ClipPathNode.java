package com.github.alexjlockwood.kyrie;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.NonNull;

import java.util.List;

public final class ClipPathNode extends BaseNode {
  @NonNull private final List<Animation<?, PathData>> pathData;

  private ClipPathNode(
      @NonNull List<Animation<?, Float>> rotation,
      @NonNull List<Animation<?, Float>> pivotX,
      @NonNull List<Animation<?, Float>> pivotY,
      @NonNull List<Animation<?, Float>> scaleX,
      @NonNull List<Animation<?, Float>> scaleY,
      @NonNull List<Animation<?, Float>> translateX,
      @NonNull List<Animation<?, Float>> translateY,
      @NonNull List<Animation<?, PathData>> pathData) {
    super(rotation, pivotX, pivotY, scaleX, scaleY, translateX, translateY);
    this.pathData = pathData;
  }

  @NonNull
  public List<Animation<?, PathData>> getPathData() {
    return pathData;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  ClipPathLayer toLayer(PropertyTimeline timeline) {
    return new ClipPathLayer(timeline, this);
  }

  private static class ClipPathLayer extends BaseLayer {
    @NonNull private final Property<PathData> pathData;

    private final Matrix tempMatrix = new Matrix();
    private final Path tempPath = new Path();
    private final Path tempRenderPath = new Path();

    public ClipPathLayer(PropertyTimeline timeline, ClipPathNode node) {
      super(timeline, node);
      pathData = registerAnimatableProperty(node.getPathData());
    }

    @Override
    public void onDraw(Canvas canvas, Matrix parentMatrix, PointF viewportScale) {
      final float matrixScale = getMatrixScale(parentMatrix);
      if (matrixScale == 0) {
        return;
      }

      final float scaleX = viewportScale.x;
      final float scaleY = viewportScale.y;
      tempMatrix.set(parentMatrix);
      if (scaleX != 1f || scaleY != 1f) {
        tempMatrix.postScale(scaleX, scaleY);
      }

      tempRenderPath.reset();
      tempPath.reset();
      PathData.toPath(pathData.getAnimatedValue(), tempPath);
      tempRenderPath.addPath(tempPath, tempMatrix);
      canvas.clipPath(tempRenderPath);
    }
  }

  // </editor-fold>

  // <editor-fold desc="Builder">

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder extends BaseNode.Builder<ClipPathNode, Builder> {
    @NonNull private List<Animation<?, PathData>> pathData = asAnimations(new PathData());

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
    protected Builder self() {
      return this;
    }

    @NonNull
    @Override
    public ClipPathNode build() {
      return new ClipPathNode(
          rotation, pivotX, pivotY, scaleX, scaleY, translateX, translateY, pathData);
    }
  }

  // </editor-fold>
}
