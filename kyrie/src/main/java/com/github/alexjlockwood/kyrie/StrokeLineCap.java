package com.github.alexjlockwood.kyrie;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Stroke line cap determines the shape that should be used at the corners of stroked paths. */
@Retention(RetentionPolicy.SOURCE)
@IntDef({StrokeLineCap.BUTT, StrokeLineCap.ROUND, StrokeLineCap.SQUARE})
public @interface StrokeLineCap {
  int BUTT = 0;
  int ROUND = 1;
  int SQUARE = 2;
}
