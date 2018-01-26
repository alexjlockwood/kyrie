package com.github.alexjlockwood.kyrie;

import android.support.annotation.IntDef;

@IntDef({FillType.NON_ZERO, FillType.EVEN_ODD})
public @interface FillType {
  int NON_ZERO = 0;
  int EVEN_ODD = 1;
}
