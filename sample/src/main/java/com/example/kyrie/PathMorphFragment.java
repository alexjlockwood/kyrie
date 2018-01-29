package com.example.kyrie;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.github.alexjlockwood.kyrie.Animation;
import com.github.alexjlockwood.kyrie.GroupNode;
import com.github.alexjlockwood.kyrie.KyrieDrawable;
import com.github.alexjlockwood.kyrie.PathData;
import com.github.alexjlockwood.kyrie.PathNode;

public class PathMorphFragment extends Fragment {
  private static final int TINT_COLOR = 0xff757575;
  private static final long DURATION = 4000;

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
            .viewport(18, 18)
            .tint(TINT_COLOR)
            .child(
                GroupNode.builder()
                    .pivotX(9)
                    .pivotY(9)
                    .rotation(
                        Animation.ofFloat(90f, 180f)
                            .duration(DURATION)
                            .interpolator(new FastOutSlowInInterpolator()))
                    .translateX(
                        Animation.ofFloat(0.75f, 0f)
                            .duration(DURATION)
                            .interpolator(new FastOutSlowInInterpolator()))
                    .child(
                        PathNode.builder()
                            .fillColor(Color.WHITE)
                            .pathData(
                                Animation.ofPathMorph(
                                        PathData.parse(
                                            "M9,5 L9,5 L9,13 L4,13 L9,5 M9,5 L9,5 L14,13 L9,13 L9,5"),
                                        PathData.parse(
                                            "M6,5 L8,5 L8,13 L6,13 L6,5 M10,5 L12,5 L12,13 L10,13 L10,5"))
                                    .duration(DURATION))))
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
