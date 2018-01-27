package com.example.kyrie;

import android.graphics.Color;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.alexjlockwood.kyrie.Animation;
import com.github.alexjlockwood.kyrie.CircleNode;
import com.github.alexjlockwood.kyrie.KyrieDrawable;
import com.github.alexjlockwood.kyrie.PathData;
import com.github.alexjlockwood.kyrie.PathNode;

import java.util.Collections;

public class PolygonsFragment extends Fragment {
  private static final float VIEWPORT_WIDTH = 1080;
  private static final float VIEWPORT_HEIGHT = 1080;
  private static final int DURATION = 7500;

  private final Polygon[] polygons = {
    new Polygon(15, 0xffe84c65, 362f, 2),
    new Polygon(14, 0xffe84c65, 338f, 3),
    new Polygon(13, 0xffd554d9, 314f, 4),
    new Polygon(12, 0xffaf6eee, 292f, 5),
    new Polygon(11, 0xff4a4ae6, 268f, 6),
    new Polygon(10, 0xff4294e7, 244f, 7),
    new Polygon(9, 0xff6beeee, 220f, 8),
    new Polygon(8, 0xff42e794, 196f, 9),
    new Polygon(7, 0xff5ae75a, 172f, 10),
    new Polygon(6, 0xffade76b, 148f, 11),
    new Polygon(5, 0xffefefbb, 128f, 12),
    new Polygon(4, 0xffe79442, 106f, 13),
    new Polygon(3, 0xffe84c65, 90f, 14)
  };

  private View rootView;
  private ImageView imageViewLaps;
  private ImageView imageViewVortex;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.fragment_two_pane, container, false);
    imageViewLaps = rootView.findViewById(R.id.image_view_pane1);
    imageViewVortex = rootView.findViewById(R.id.image_view_pane2);
    return rootView;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    final KyrieDrawable lapsDrawable = createLapsDrawable();
    imageViewLaps.setImageDrawable(lapsDrawable);

    final KyrieDrawable vortexDrawable = createVortexDrawable();
    imageViewVortex.setImageDrawable(vortexDrawable);

    rootView.setOnClickListener(
        v -> {
          lapsDrawable.start();
          vortexDrawable.start();
        });
  }

  private KyrieDrawable createLapsDrawable() {
    final KyrieDrawable.Builder builder =
        KyrieDrawable.builder().viewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

    for (Polygon polygon : polygons) {
      builder.child(
          PathNode.builder()
              .pathData(PathData.parse(polygon.pathData))
              .strokeWidth(4f)
              .strokeColor(polygon.color));
    }

    for (Polygon polygon : polygons) {
      final PathData pathData =
          PathData.parse(TextUtils.join(" ", Collections.nCopies(polygon.laps, polygon.pathData)));
      final Animation<PointF, PointF> pathMotion =
          Animation.ofPathMotion(PathData.toPath(pathData))
              .repeatCount(Animation.INFINITE)
              .duration(DURATION);
      builder.child(
          CircleNode.builder()
              .centerX(0)
              .centerY(0)
              .radius(8)
              .fillColor(Color.BLACK)
              .translateX(pathMotion.transform(p -> p.x))
              .translateY(pathMotion.transform(p -> p.y)));
    }

    return builder.build();
  }

  private KyrieDrawable createVortexDrawable() {
    final KyrieDrawable.Builder builder =
        KyrieDrawable.builder().viewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

    for (Polygon polygon : polygons) {
      final float length = polygon.length;
      final float totalLength = length * polygon.laps;
      builder.child(
          PathNode.builder()
              .pathData(PathData.parse(polygon.pathData))
              .strokeWidth(4f)
              .strokeColor(polygon.color)
              .strokeDashArray(
                  Animation.ofFloatArray(new float[] {0, length}, new float[] {length, 0})
                      .repeatCount(Animation.INFINITE)
                      .duration(DURATION))
              .strokeDashOffset(
                  Animation.ofFloat(0, 2 * totalLength)
                      .repeatCount(Animation.INFINITE)
                      .duration(DURATION)));
    }

    return builder.build();
  }

  private static class Polygon {
    final int sides;
    @ColorInt final int color;
    final float radius;
    final int laps;
    final String pathData;
    final float length;

    Polygon(int sides, @ColorInt int color, float radius, int laps) {
      this.sides = sides;
      this.color = color;
      this.radius = radius;
      this.laps = laps;
      pathData = getPathData(sides, radius);
      final PathMeasure pathMeasure = new PathMeasure();
      pathMeasure.setPath(PathData.toPath(pathData), false);
      this.length = pathMeasure.getLength();
    }

    private static String getPathData(int sides, float radius) {
      final double angle = 2 * Math.PI / sides;
      final double startAngle = 3 * Math.PI / 2.0;
      final StringBuilder sb = new StringBuilder();
      final float mx = (VIEWPORT_WIDTH / 2) + (float) (radius * Math.cos(startAngle));
      final float my = (VIEWPORT_HEIGHT / 2) + (float) (radius * Math.sin(startAngle));
      sb.append("M ").append(mx).append(" ").append(my);
      for (int i = 1; i < sides; i++) {
        final float lx = (VIEWPORT_WIDTH / 2) + (float) (radius * Math.cos(startAngle + angle * i));
        final float ly =
            (VIEWPORT_HEIGHT / 2) + (float) (radius * Math.sin(startAngle + angle * i));
        sb.append(" ").append(lx).append(" ").append(ly);
      }
      sb.append(" Z");
      return sb.toString();
    }
  }
}
