package com.example.kyrie;

import android.support.annotation.NonNull;
import android.view.View;

import com.github.alexjlockwood.kyrie.KyrieDrawable;

final class SampleOnClickListener implements View.OnClickListener {
  @NonNull private final KyrieDrawable drawable;

  public SampleOnClickListener(@NonNull KyrieDrawable drawable) {
    this.drawable = drawable;
  }

  @Override
  public void onClick(View v) {
    if (drawable.isPaused()) {
      drawable.resume();
    } else {
      if (drawable.isStarted()) {
        drawable.pause();
      } else {
        drawable.start();
      }
    }
  }
}
