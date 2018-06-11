package com.example.kyrie;

import android.support.annotation.NonNull;
import android.widget.SeekBar;

import com.github.alexjlockwood.kyrie.KyrieDrawable;

final class SampleOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
  @NonNull private final KyrieDrawable drawable;

  public SampleOnSeekBarChangeListener(@NonNull KyrieDrawable drawable) {
    this.drawable = drawable;
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    final long totalDuration = drawable.getTotalDuration();
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
}
