package com.example.kyrie;

import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.github.alexjlockwood.kyrie.KyrieDrawable;

final class SampleListenerAdapter extends KyrieDrawable.ListenerAdapter {
  @NonNull private final SeekBar seekBar;

  public SampleListenerAdapter(@NonNull SeekBar seekBar) {
    this.seekBar = seekBar;
  }

  @Override
  public void onAnimationUpdate(@NonNull KyrieDrawable drawable) {
    final float playTime = drawable.getCurrentPlayTime();
    final float totalDuration = drawable.getTotalDuration();
    final float fraction = playTime / totalDuration;
    seekBar.setProgress(Math.round(fraction * seekBar.getMax()));
  }
}
