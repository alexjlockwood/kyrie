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
      @NonNull List<PropertyAnimation<?, Float>> rotation,
      @NonNull List<PropertyAnimation<?, Float>> pivotX,
      @NonNull List<PropertyAnimation<?, Float>> pivotY,
      @NonNull List<PropertyAnimation<?, Float>> scaleX,
      @NonNull List<PropertyAnimation<?, Float>> scaleY,
      @NonNull List<PropertyAnimation<?, Float>> translateX,
      @NonNull List<PropertyAnimation<?, Float>> translateY,
      @NonNull List<Node> children) {
    super(rotation, pivotX, pivotY, scaleX, scaleY, translateX, translateY);
    this.children = children;
  }

  @NonNull
  public List<Node> getChildren() {
    return children;
  }

  @NonNull
  @Override
  GroupLayer toLayer(@NonNull PropertyTimeline timeline) {
    return new GroupLayer(timeline, this);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder extends BaseNode.Builder<GroupNode, Builder> {
    private final List<Node> children = new ArrayList<>();

    private Builder() {}

    // Children.

    public final Builder child(@NonNull GroupNode node) {
      return addChild(node);
    }

    public final Builder child(@NonNull GroupNode.Builder builder) {
      return child(builder.build());
    }

    public final Builder child(@NonNull ClipPathNode node) {
      return addChild(node);
    }

    public final Builder child(@NonNull ClipPathNode.Builder builder) {
      return child(builder.build());
    }

    public final Builder child(@NonNull PathNode node) {
      return addChild(node);
    }

    public final Builder child(@NonNull PathNode.Builder builder) {
      return child(builder.build());
    }

    public final Builder child(@NonNull RectangleNode node) {
      return addChild(node);
    }

    public final Builder child(@NonNull RectangleNode.Builder builder) {
      return child(builder.build());
    }

    public final Builder child(@NonNull EllipseNode node) {
      return addChild(node);
    }

    public final Builder child(@NonNull EllipseNode.Builder builder) {
      return child(builder.build());
    }

    public final Builder child(@NonNull CircleNode node) {
      return addChild(node);
    }

    public final Builder child(@NonNull CircleNode.Builder builder) {
      return child(builder.build());
    }

    private Builder addChild(@NonNull Node node) {
      children.add(node);
      return self;
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

  public static final class GroupLayer extends BaseLayer {
    @NonNull private final List<Layer> children;

    public GroupLayer(@NonNull PropertyTimeline timeline, @NonNull GroupNode node) {
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
}
