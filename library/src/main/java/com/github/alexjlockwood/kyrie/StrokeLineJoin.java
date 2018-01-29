package com.github.alexjlockwood.kyrie;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Stroke line join determines the shape that should be used at the ends of a stroked sub-path. */
@Retention(RetentionPolicy.SOURCE)
@IntDef({StrokeLineJoin.MITER, StrokeLineJoin.ROUND, StrokeLineJoin.BEVEL})
public @interface StrokeLineJoin {
  int MITER = 0;
  int ROUND = 1;
  int BEVEL = 2;
}
