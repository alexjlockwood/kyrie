package com.example.kyrie;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.github.alexjlockwood.kyrie.Animation;
import com.github.alexjlockwood.kyrie.ClipPathNode;
import com.github.alexjlockwood.kyrie.GroupNode;
import com.github.alexjlockwood.kyrie.KyrieDrawable;
import com.github.alexjlockwood.kyrie.PathData;
import com.github.alexjlockwood.kyrie.PathNode;

public class HeartbreakFragment extends Fragment {
  private static final int TINT_COLOR = 0xffff4081;

  private ImageView imageView;
  private SeekBar seekBar;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_seekbar, container, false);
    imageView = view.findViewById(R.id.image_view);
    seekBar = view.findViewById(R.id.seekbar);
    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    final KyrieDrawable drawable = createDrawable();
    imageView.setImageDrawable(drawable);
    imageView.setOnClickListener(
        v -> {
          if (drawable.isPaused()) {
            drawable.resume();
          } else {
            if (drawable.isStarted()) {
              drawable.pause();
            } else {
              drawable.start();
            }
          }
        });

    final long totalDuration = drawable.getTotalDuration();
    seekBar.setOnSeekBarChangeListener(
        new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            drawable.setCurrentPlayTime((long) (progress / 100f * totalDuration));
          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {
            if (drawable.isRunning()) {
              drawable.pause();
            }
          }

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {}
        });
  }

  private KyrieDrawable createDrawable() {
    final KyrieDrawable kyrieDrawable =
        KyrieDrawable.builder()
            .viewport(56, 56)
            .tint(TINT_COLOR)
            .alpha(
                Animation.ofFloat(0.4f, 1f)
                    .startDelay(500)
                    .duration(400)
                    .interpolator(new FastOutSlowInInterpolator()))
            .child(
                GroupNode.builder()
                    .pivotX(28f)
                    .pivotY(37.3f)
                    .rotation(
                        Animation.ofFloat(0f, -20f)
                            .duration(400)
                            .interpolator(new LinearOutSlowInInterpolator()))
                    .child(
                        PathNode.builder()
                            .fillColor(Color.WHITE)
                            .pathData(
                                "M 28.031 21.054 C 28.02 21.066 28.01 21.078 28 21.09 C 26.91 19.81 25.24 19 23.5 19 C 20.42 19 18 21.42 18 24.5 C 18 28.28 21.4 31.36 26.55 36.03 L 28 37.35 L 28.002 37.348 L 27.781 36.988 L 28.489 36.073 L 27.506 34.764 L 28.782 33.027 L 26.944 31.008 L 29.149 28.725 L 27.117 27.143 L 29.149 25.018 L 26.488 22.977 L 28.031 21.054 L 28.031 21.054 Z")
                            .fillAlpha(
                                Animation.ofFloat(1f, 0f)
                                    .startDelay(100)
                                    .duration(300)
                                    .interpolator(new LinearOutSlowInInterpolator()))))
            .child(
                GroupNode.builder()
                    .pivotX(28f)
                    .pivotY(37.3f)
                    .rotation(
                        Animation.ofFloat(0f, 20f)
                            .duration(400)
                            .interpolator(new LinearOutSlowInInterpolator()))
                    .child(
                        PathNode.builder()
                            .fillColor(Color.WHITE)
                            .pathData(
                                "M 28.031 21.054 C 28.169 20.895 28.316 20.743 28.471 20.599 L 28.915 20.226 C 29.926 19.457 31.193 19 32.5 19 C 35.58 19 38 21.42 38 24.5 C 38 28.28 34.6 31.36 29.45 36.04 L 28.002 37.348 L 27.781 36.988 L 28.489 36.073 L 27.506 34.764 L 28.782 33.027 L 26.944 31.008 L 29.149 28.725 L 27.117 27.143 L 29.149 25.018 L 26.488 22.977 L 28.031 21.054 L 28.031 21.054 Z")
                            .fillAlpha(
                                Animation.ofFloat(1f, 0f)
                                    .startDelay(100)
                                    .duration(300)
                                    .interpolator(new LinearOutSlowInInterpolator()))))
            .child(
                PathNode.builder()
                    .strokeColor(Color.WHITE)
                    .strokeWidth(2f)
                    .trimPathEnd(0f)
                    .trimPathEnd(
                        Animation.ofFloat(0f, 1f)
                            .startDelay(500)
                            .duration(400)
                            .interpolator(new FastOutSlowInInterpolator()))
                    .pathData(
                        "M 28.719 38.296 L 25.669 35.552 C 21.621 31.793 18.016 28.891 18.016 24.845 C 18.016 21.588 20.631 19.965 23.634 19.965 C 24.999 19.965 26.799 21.181 28.644 23.13"))
            .child(
                PathNode.builder()
                    .strokeColor(Color.WHITE)
                    .strokeWidth(2f)
                    .trimPathEnd(0f)
                    .trimPathEnd(
                        Animation.ofFloat(0f, 1f)
                            .startDelay(500)
                            .duration(400)
                            .interpolator(new FastOutSlowInInterpolator()))
                    .pathData(
                        "M 27.231 38.294 L 30.765 35.2 C 34.834 31.235 37.752 29.118 38.004 25.084 C 38.168 22.459 35.773 20.035 33.379 20.035 C 30.432 20.035 29.672 21.047 27.231 23.133"))
            .child(
                GroupNode.builder()
                    .child(
                        ClipPathNode.builder()
                            .pathData("M 18 37 L 38 37 L 38 37 L 18 37 Z")
                            .pathData(
                                Animation.ofPathMorph(
                                        PathData.parse(
                                            "M 18 38 C 18 38 24 38 24 38 C 24 38 32 38 32 38 C 32 38 38 38 38 38 L 38 38 L 18 38 L 18 38 Z"),
                                        PathData.parse(
                                            "M 18 26 C 18 26 21 28 24 28 C 27 28 29 25 32 25 C 35 25 38 26 38 26 L 38 38 L 18 38 L 18 26 Z"))
                                    .startDelay(1000)
                                    .duration(160)
                                    .interpolator(new FastOutLinearInInterpolator()),
                                Animation.ofPathMorph(
                                        PathData.parse(
                                            "M 18 26 C 18 26 21 28 24 28 C 27 28 29 25 32 25 C 35 25 38 26 38 26 L 38 38 L 18 38 L 18 26 Z"),
                                        PathData.parse(
                                            "M 18 18 C 18 18 24 18 24 18 C 24 18 32 18 32 18 C 32 18 38 18 38 18 L 38 38 L 18 38 L 18 18 Z"))
                                    .startDelay(1160)
                                    .duration(120)
                                    .interpolator(new FastOutLinearInInterpolator())))
                    .child(
                        PathNode.builder()
                            .pathData(
                                "M 28 39 L 26.405 37.567 C 20.74 32.471 17 29.109 17 24.995 C 17 21.632 19.657 19 23.05 19 C 24.964 19 26.801 19.883 28 21.272 C 29.199 19.883 31.036 19 32.95 19 C 36.343 19 39 21.632 39 24.995 C 39 29.109 35.26 32.471 29.595 37.567 L 28 39 L 28 39 Z")
                            .fillColor(Color.WHITE)))
            .build();
    kyrieDrawable.addListener(
        new KyrieDrawable.ListenerAdapter() {
          @Override
          public void onAnimationUpdate(@NonNull KyrieDrawable drawable) {
            final float playTime = drawable.getCurrentPlayTime();
            final float totalDuration = drawable.getTotalDuration();
            final float fraction = playTime / totalDuration;
            seekBar.setProgress(Math.round(fraction * seekBar.getMax()));
          }
        });
    return kyrieDrawable;
  }
}
