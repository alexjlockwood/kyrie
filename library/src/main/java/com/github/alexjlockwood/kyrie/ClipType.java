package com.github.alexjlockwood.kyrie;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Determines the clipping strategy of a {@link ClipPathNode}. */
@Retention(RetentionPolicy.SOURCE)
@IntDef({ClipType.INTERSECT, ClipType.SUBTRACT})
public @interface ClipType {
  /** Only the pixels drawn inside the bounds of the clip path will be displayed. */
  int INTERSECT = 0;
  /** Only the pixels drawn outside the bounds of the clip path will be displayed. */
  int SUBTRACT = 1;
}
