package com.example.kyrie;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.alexjlockwood.kyrie.Animation;
import com.github.alexjlockwood.kyrie.GroupNode;
import com.github.alexjlockwood.kyrie.KyrieDrawable;
import com.github.alexjlockwood.kyrie.PathData;
import com.github.alexjlockwood.kyrie.PathNode;
import com.github.alexjlockwood.kyrie.StrokeLineCap;

public class ProgressFragment extends Fragment {
  @ColorInt private static final int TINT_COLOR = 0xffff4081;

  private ImageView horizontalView;
  private ImageView circularView;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_two_pane, container, false);
    horizontalView = view.findViewById(R.id.image_view_pane1);
    circularView = view.findViewById(R.id.image_view_pane2);
    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    final KyrieDrawable horizontalDrawable = createHorizontalDrawable();
    horizontalView.setImageDrawable(horizontalDrawable);
    horizontalDrawable.start();

    final KyrieDrawable circularDrawable = createCircularDrawable();
    circularView.setImageDrawable(circularDrawable);
    circularDrawable.start();
  }

  private KyrieDrawable createHorizontalDrawable() {
    return KyrieDrawable.builder()
        .viewport(360, 10)
        .tint(TINT_COLOR)
        .child(
            GroupNode.builder()
                .translateX(180)
                .translateY(5)
                .child(
                    PathNode.builder()
                        .fillAlpha(0.3f)
                        .fillColor(Color.WHITE)
                        .pathData("M -180,-1 l 360,0 l 0,2 l -360,0 Z"))
                .child(
                    GroupNode.builder()
                        .scaleX(
                            Animation.ofPathMotion(
                                    PathData.toPath("M 0 0.1 L 1 0.571 L 2 0.91 L 3 0.1"))
                                .transform(p -> p.y)
                                .duration(2000)
                                .repeatCount(Animation.INFINITE)
                                .interpolator(
                                    PathInterpolatorCompat.create(
                                        PathData.toPath(
                                            "M 0 0 C 0.068 0.02 0.192 0.159 0.333 0.349 C 0.384 0.415 0.549 0.681 0.667 0.683 C 0.753 0.682 0.737 0.879 1 1"))))
                        .translateX(
                            Animation.ofPathMotion(
                                    PathData.toPath(
                                        "M -197.6 0 C -183.318 0 -112.522 0 -62.053 0 C -7.791 0 28.371 0 106.19 0 C 250.912 0 422.6 0 422.6 0"))
                                .transform(p -> p.x)
                                .duration(2000)
                                .repeatCount(Animation.INFINITE)
                                .interpolator(
                                    PathInterpolatorCompat.create(
                                        PathData.toPath(
                                            "M 0 0 C 0.037 0 0.129 0.09 0.25 0.219 C 0.322 0.296 0.437 0.418 0.483 0.49 C 0.69 0.81 0.793 0.95 1 1"))))
                        .child(
                            PathNode.builder()
                                .fillColor(Color.WHITE)
                                .pathData("M -144,-1 l 288,0 l 0,2 l -288,0 Z")))
                .child(
                    GroupNode.builder()
                        .scaleX(
                            Animation.ofPathMotion(PathData.toPath("M 0 0.1 L 1 0.826 L 2 0.1"))
                                .transform(p -> p.y)
                                .duration(2000)
                                .repeatCount(Animation.INFINITE)
                                .interpolator(
                                    PathInterpolatorCompat.create(
                                        PathData.toPath(
                                            "M 0 0 L 0.366 0 C 0.473 0.062 0.615 0.5 0.683 0.5 C 0.755 0.5 0.757 0.815 1 1"))))
                        .translateX(
                            Animation.ofPathMotion(
                                    PathData.toPath(
                                        "M -522.6 0 C -473.7 0 -356.573 0 -221.383 0 C -23.801 0 199.6 0 199.6 0"))
                                .transform(p -> p.x)
                                .duration(2000)
                                .repeatCount(Animation.INFINITE)
                                .interpolator(
                                    PathInterpolatorCompat.create(
                                        PathData.toPath(
                                            "M 0 0 L 0.2 0 C 0.395 0 0.474 0.206 0.591 0.417 C 0.715 0.639 0.816 0.974 1 1"))))
                        .child(
                            PathNode.builder()
                                .fillColor(Color.WHITE)
                                .pathData("M -144,-1 l 288,0 l 0,2 l -288,0 Z"))))
        .build();
  }

  private KyrieDrawable createCircularDrawable() {
    return KyrieDrawable.builder()
        .viewport(48, 48)
        .tint(TINT_COLOR)
        .child(
            GroupNode.builder()
                .translateX(24f)
                .translateY(24f)
                .rotation(
                    Animation.ofFloat(0f, 720f).duration(4444).repeatCount(Animation.INFINITE))
                .child(
                    PathNode.builder()
                        .strokeColor(Color.WHITE)
                        .strokeWidth(4f)
                        .trimPathStart(
                            Animation.ofFloat(0f, 0.75f)
                                .duration(1333)
                                .repeatCount(Animation.INFINITE)
                                .interpolator(
                                    PathInterpolatorCompat.create(
                                        PathData.toPath("M 0 0 L 0.5 0 C 0.7 0 0.6 1 1 1"))))
                        .trimPathEnd(
                            Animation.ofFloat(0.03f, 0.78f)
                                .duration(1333)
                                .repeatCount(Animation.INFINITE)
                                .interpolator(
                                    PathInterpolatorCompat.create(
                                        PathData.toPath(
                                            "M 0 0 C 0.2 0 0.1 1 0.5 0.96 C 0.966 0.96 0.993 1 1 1"))))
                        .trimPathOffset(
                            Animation.ofFloat(0f, 0.25f)
                                .duration(1333)
                                .repeatCount(Animation.INFINITE))
                        .strokeLineCap(StrokeLineCap.SQUARE)
                        .pathData("M 0 0 m 0 -18 a 18 18 0 1 1 0 36 a 18 18 0 1 1 0 -36")))
        .build();
  }
}
