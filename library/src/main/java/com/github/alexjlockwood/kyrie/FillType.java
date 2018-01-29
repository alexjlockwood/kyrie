package com.github.alexjlockwood.kyrie;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Fill type determines how a shape should be filled when painted. */
@Retention(RetentionPolicy.SOURCE)
@IntDef({FillType.NON_ZERO, FillType.EVEN_ODD})
public @interface FillType {
  int NON_ZERO = 0;
  int EVEN_ODD = 1;
}
