package com.github.alexjlockwood.kyrie;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({RepeatMode.RESTART, RepeatMode.REVERSE})
public @interface RepeatMode {
  /**
   * When the animation reaches the end and <code>repeatCount</code> is INFINITE or a positive
   * value, the animation restarts from the beginning.
   */
  int RESTART = 1;
  /**
   * When the animation reaches the end and <code>repeatCount</code> is INFINITE or a positive
   * value, the animation reverses direction on every iteration.
   */
  int REVERSE = 2;
}
