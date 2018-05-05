package com.github.alexjlockwood.kyrie;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * A {@link Node} that defines a region to be clipped. Note that a {@link ClipPathNode} only clips
 * its sibling {@link Node}s.
 */
public final class ClipPathNode extends BaseNode {
  @NonNull private final List<Animation<?, PathData>> pathData;
  @FillType private final int fillType;
  @ClipType private final int clipType;

  private ClipPathNode(
      @NonNull List<Animation<?, Float>> rotation,
      @NonNull List<Animation<?, Float>> pivotX,
      @NonNull List<Animation<?, Float>> pivotY,
      @NonNull List<Animation<?, Float>> scaleX,
      @NonNull List<Animation<?, Float>> scaleY,
      @NonNull List<Animation<?, Float>> translateX,
      @NonNull List<Animation<?, Float>> translateY,
      @NonNull List<Animation<?, PathData>> pathData,
      @FillType int fillType,
      @ClipType int clipType) {
    super(rotation, pivotX, pivotY, scaleX, scaleY, translateX, translateY);
    this.pathData = pathData;
    this.fillType = fillType;
    this.clipType = clipType;
  }

  @NonNull
  List<Animation<?, PathData>> getPathData() {
    return pathData;
  }

  @FillType
  int getFillType() {
    return fillType;
  }

  @ClipType
  int getClipType() {
    return clipType;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  ClipPathLayer toLayer(PropertyTimeline timeline) {
    return new ClipPathLayer(timeline, this);
  }

  private static class ClipPathLayer extends BaseLayer {
    @NonNull private final Property<PathData> pathData;
    @FillType private final int fillType;
    @ClipType private final int clipType;

    private final Matrix tempMatrix = new Matrix();
    private final Path tempPath = new Path();
    private final Path tempRenderPath = new Path();

    public ClipPathLayer(PropertyTimeline timeline, ClipPathNode node) {
      super(timeline, node);
      pathData = registerAnimatableProperty(node.getPathData());
      fillType = node.getFillType();
      clipType = node.getClipType();
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
      tempRenderPath.setFillType(getPaintFillType(fillType));
      if (clipType == ClipType.INTERSECT) {
        canvas.clipPath(tempRenderPath);
      } else {
        canvas.clipPath(tempRenderPath, Region.Op.DIFFERENCE);
      }
    }

    private static Path.FillType getPaintFillType(@FillType int fillType) {
      switch (fillType) {
        case FillType.NON_ZERO:
          return Path.FillType.WINDING;
        case FillType.EVEN_ODD:
          return Path.FillType.EVEN_ODD;
        default:
          throw new IllegalArgumentException("Invalid fill type: " + fillType);
      }
    }
  }

  // </editor-fold>

  // <editor-fold desc="Builder">

  public static Builder builder() {
    return new Builder();
  }

  /** Builder class used to create {@link ClipPathNode}s. */
  public static final class Builder extends BaseNode.Builder<Builder> {
    @NonNull private List<Animation<?, PathData>> pathData = asAnimations(new PathData());
    @FillType private int fillType = FillType.NON_ZERO;
    @ClipType private int clipType = ClipType.INTERSECT;

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

    // Fill type.

    public final Builder fillType(@FillType int fillType) {
      this.fillType = fillType;
      return self;
    }

    // Clip type.

    public final Builder clipType(@ClipType int clipType) {
      this.clipType = clipType;
      return self;
    }

    @NonNull
    @Override
    Builder self() {
      return this;
    }

    @NonNull
    @Override
    public ClipPathNode build() {
      return new ClipPathNode(
          rotation,
          pivotX,
          pivotY,
          scaleX,
          scaleY,
          translateX,
          translateY,
          pathData,
          fillType,
          clipType);
    }
  }

  // </editor-fold>
}
