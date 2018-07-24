package com.github.alexjlockwood.kyrie;

import android.support.annotation.StyleableRes;

final class Styleable {

  interface Vector {
    @StyleableRes int NAME = 0;
    @StyleableRes int TINT = 1;
    @StyleableRes int HEIGHT = 2;
    @StyleableRes int WIDTH = 3;
    @StyleableRes int ALPHA = 4;
    @StyleableRes int AUTO_MIRRORED = 5;
    @StyleableRes int TINT_MODE = 6;
    @StyleableRes int VIEWPORT_WIDTH = 7;
    @StyleableRes int VIEWPORT_HEIGHT = 8;
  }

  @StyleableRes
  static final int[] VECTOR = {
    android.R.attr.name,
    android.R.attr.tint,
    android.R.attr.height,
    android.R.attr.width,
    android.R.attr.alpha,
    android.R.attr.autoMirrored,
    android.R.attr.tintMode,
    android.R.attr.viewportWidth,
    android.R.attr.viewportHeight,
  };

  interface Group {
    @StyleableRes int NAME = 0;
    @StyleableRes int PIVOT_X = 1;
    @StyleableRes int PIVOT_Y = 2;
    @StyleableRes int SCALE_X = 3;
    @StyleableRes int SCALE_Y = 4;
    @StyleableRes int ROTATION = 5;
    @StyleableRes int TRANSLATE_X = 6;
    @StyleableRes int TRANSLATE_Y = 7;
  }

  @StyleableRes
  static final int[] GROUP = {
    android.R.attr.name,
    android.R.attr.pivotX,
    android.R.attr.pivotY,
    android.R.attr.scaleX,
    android.R.attr.scaleY,
    android.R.attr.rotation,
    android.R.attr.translateX,
    android.R.attr.translateY,
  };

  interface Path {
    @StyleableRes int NAME = 0;
    @StyleableRes int FILL_COLOR = 1;
    @StyleableRes int PATH_DATA = 2;
    @StyleableRes int STROKE_COLOR = 3;
    @StyleableRes int STROKE_WIDTH = 4;
    @StyleableRes int TRIM_PATH_START = 5;
    @StyleableRes int TRIM_PATH_END = 6;
    @StyleableRes int TRIM_PATH_OFFSET = 7;
    @StyleableRes int STROKE_LINE_CAP = 8;
    @StyleableRes int STROKE_LINE_JOIN = 9;
    @StyleableRes int STROKE_MITER_LIMIT = 10;
    @StyleableRes int STROKE_ALPHA = 11;
    @StyleableRes int FILL_ALPHA = 12;
    @StyleableRes int FILL_TYPE = 13;
  }

  @StyleableRes
  static final int[] PATH = {
    android.R.attr.name,
    android.R.attr.fillColor,
    android.R.attr.pathData,
    android.R.attr.strokeColor,
    android.R.attr.strokeWidth,
    android.R.attr.trimPathStart,
    android.R.attr.trimPathEnd,
    android.R.attr.trimPathOffset,
    android.R.attr.strokeLineCap,
    android.R.attr.strokeLineJoin,
    android.R.attr.strokeMiterLimit,
    android.R.attr.strokeAlpha,
    android.R.attr.fillAlpha,
    android.R.attr.fillType,
  };

  interface ClipPath {
    @StyleableRes int NAME = 0;
    @StyleableRes int PATH_DATA = 1;
  }

  @StyleableRes static final int[] CLIP_PATH = {android.R.attr.name, android.R.attr.pathData};

  interface AnimatedVector {
    @StyleableRes int DRAWABLE = 0;
  }

  @StyleableRes static final int[] ANIMATED_VECTOR = {android.R.attr.drawable};

  interface Target {
    @StyleableRes int NAME = 0;
    @StyleableRes int ANIMATION = 1;
  }

  @StyleableRes static final int[] TARGET = {android.R.attr.name, android.R.attr.animation};

  interface AnimatorSet {
    @StyleableRes int ORDERING = 0;
  }

  @StyleableRes static final int[] ANIMATOR_SET = {android.R.attr.ordering};

  interface Animator {
    @StyleableRes int INTERPOLATOR = 0;
    @StyleableRes int DURATION = 1;
    @StyleableRes int START_OFFSET = 2;
    @StyleableRes int REPEAT_COUNT = 3;
    @StyleableRes int REPEAT_MODE = 4;
    @StyleableRes int VALUE_FROM = 5;
    @StyleableRes int VALUE_TO = 6;
    @StyleableRes int VALUE_TYPE = 7;
  }

  @StyleableRes
  static final int[] ANIMATOR = {
    android.R.attr.interpolator,
    android.R.attr.duration,
    android.R.attr.startOffset,
    android.R.attr.repeatCount,
    android.R.attr.repeatMode,
    android.R.attr.valueFrom,
    android.R.attr.valueTo,
    android.R.attr.valueType,
  };

  interface PropertyAnimator {
    @StyleableRes int PROPERTY_NAME = 0;
    @StyleableRes int PATH_DATA = 1;
    @StyleableRes int PROPERTY_X_NAME = 2;
    @StyleableRes int PROPERTY_Y_NAME = 3;
  }

  @StyleableRes
  static final int[] PROPERTY_ANIMATOR = {
    android.R.attr.propertyName,
    android.R.attr.pathData,
    android.R.attr.propertyXName,
    android.R.attr.propertyYName,
  };

  interface PropertyValuesHolder {
    @StyleableRes int VALUE_FROM = 0;
    @StyleableRes int VALUE_TO = 1;
    @StyleableRes int VALUE_TYPE = 2;
    @StyleableRes int PROPERTY_NAME = 3;
  }

  @StyleableRes
  static final int[] PROPERTY_VALUES_HOLDER = {
    android.R.attr.valueFrom,
    android.R.attr.valueTo,
    android.R.attr.valueType,
    android.R.attr.propertyName,
  };

  interface Keyframe {
    @StyleableRes int VALUE = 0;
    @StyleableRes int INTERPOLATOR = 1;
    @StyleableRes int VALUE_TYPE = 2;
    @StyleableRes int FRACTION = 3;
  }

  @StyleableRes
  static final int[] KEYFRAME = {
    android.R.attr.value,
    android.R.attr.interpolator,
    android.R.attr.valueType,
    android.R.attr.fraction,
  };

  interface PathInterpolator {
    @StyleableRes int CONTROL_X1 = 0;
    @StyleableRes int CONTROL_Y1 = 1;
    @StyleableRes int CONTROL_X2 = 2;
    @StyleableRes int CONTROL_Y2 = 3;
    @StyleableRes int PATH_DATA = 4;
  }

  @StyleableRes
  static final int[] PATH_INTERPOLATOR = {
    android.R.attr.controlX1,
    android.R.attr.controlY1,
    android.R.attr.controlX2,
    android.R.attr.controlY2,
    android.R.attr.pathData,
  };

  private Styleable() {}
}
