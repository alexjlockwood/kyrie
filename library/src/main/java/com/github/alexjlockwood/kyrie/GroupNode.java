package com.github.alexjlockwood.kyrie;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class GroupNode extends BaseNode {
  @NonNull private final List<Node> children;

  private GroupNode(
      @NonNull List<Animation<?, Float>> rotation,
      @NonNull List<Animation<?, Float>> pivotX,
      @NonNull List<Animation<?, Float>> pivotY,
      @NonNull List<Animation<?, Float>> scaleX,
      @NonNull List<Animation<?, Float>> scaleY,
      @NonNull List<Animation<?, Float>> translateX,
      @NonNull List<Animation<?, Float>> translateY,
      @NonNull List<Node> children) {
    super(rotation, pivotX, pivotY, scaleX, scaleY, translateX, translateY);
    this.children = children;
  }

  @NonNull
  public List<Node> getChildren() {
    return children;
  }

  // <editor-fold desc="Layer">

  @NonNull
  @Override
  GroupLayer toLayer(@NonNull Timeline timeline) {
    return new GroupLayer(timeline, this);
  }

  private static final class GroupLayer extends BaseLayer {
    @NonNull private final List<Layer> children;

    public GroupLayer(@NonNull Timeline timeline, @NonNull GroupNode node) {
      super(timeline, node);
      final List<Node> childrenNodes = node.getChildren();
      children = new ArrayList<>(childrenNodes.size());
      for (int i = 0, size = childrenNodes.size(); i < size; i++) {
        children.add(childrenNodes.get(i).toLayer(timeline));
      }
    }

    @Override
    public void onDraw(
        @NonNull Canvas canvas, @NonNull Matrix parentMatrix, @NonNull PointF viewportScale) {
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

  public static final class Builder extends BaseNode.Builder<GroupNode, Builder> {
    private final List<Node> children = new ArrayList<>();

    private Builder() {}

    // Children.

    public final Builder child(@NonNull Node node) {
      children.add(node);
      return this;
    }

    public final Builder child(@NonNull Node.Builder builder) {
      return child(builder.build());
    }

    @Override
    protected final Builder self() {
      return this;
    }

    public final GroupNode build() {
      return new GroupNode(
          rotation, pivotX, pivotY, scaleX, scaleY, translateX, translateY, children);
    }
  }

  // </editor-fold>
}
