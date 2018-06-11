package com.github.alexjlockwood.kyrie;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/** A {@link Node} that holds a group of children {@link Node}s. */
public final class GroupNode extends BaseNode {
  @NonNull private final List<Node> children;

  private GroupNode(
      List<Animation<?, Float>> rotation,
      List<Animation<?, Float>> pivotX,
      List<Animation<?, Float>> pivotY,
      List<Animation<?, Float>> scaleX,
      List<Animation<?, Float>> scaleY,
      List<Animation<?, Float>> translateX,
      List<Animation<?, Float>> translateY,
      List<Node> children) {
    super(rotation, pivotX, pivotY, scaleX, scaleY, translateX, translateY);
    this.children = children;
  }

  @NonNull
  List<Node> getChildren() {
    return children;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  GroupLayer toLayer(PropertyTimeline timeline) {
    return new GroupLayer(timeline, this);
  }

  private static class GroupLayer extends BaseLayer {
    @NonNull private final List<Layer> children;

    public GroupLayer(PropertyTimeline timeline, GroupNode node) {
      super(timeline, node);
      final List<Node> childrenNodes = node.getChildren();
      children = new ArrayList<>(childrenNodes.size());
      for (int i = 0, size = childrenNodes.size(); i < size; i++) {
        children.add(childrenNodes.get(i).toLayer(timeline));
      }
    }

    @Override
    public void onDraw(Canvas canvas, Matrix parentMatrix, PointF viewportScale) {
      canvas.save();
      for (int i = 0, size = children.size(); i < size; i++) {
        children.get(i).draw(canvas, parentMatrix, viewportScale);
      }
      canvas.restore();
    }
  }

  // </editor-fold>

  // <editor-fold desc="Builder">

  public static Builder builder() {
    return new Builder();
  }

  /** Builder class used to create {@link GroupNode}s. */
  public static final class Builder extends BaseNode.Builder<Builder> {
    private final List<Node> children = new ArrayList<>();

    private Builder() {}

    // Children.

    public Builder child(Node node) {
      children.add(node);
      return this;
    }

    public Builder child(Node.Builder builder) {
      return child(builder.build());
    }

    @NonNull
    @Override
    Builder self() {
      return this;
    }

    @NonNull
    public GroupNode build() {
      return new GroupNode(
          rotation, pivotX, pivotY, scaleX, scaleY, translateX, translateY, children);
    }
  }

  // </editor-fold>
}
