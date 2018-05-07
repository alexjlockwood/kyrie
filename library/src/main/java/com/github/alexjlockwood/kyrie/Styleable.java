package com.github.alexjlockwood.kyrie;

import android.annotation.SuppressLint;
import android.support.annotation.StyleableRes;

import java.util.Arrays;

@SuppressLint("InlinedApi")
final class Styleable {

  /* Vector. */

  @StyleableRes
  private static final int[] VECTOR_ANDROID_NS = {
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

  @StyleableRes static final int[] VECTOR = VECTOR_ANDROID_NS;

  private interface VectorAndroidNs {
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

  interface Vector extends VectorAndroidNs {}

  /* Group. */

  @StyleableRes
  private static final int[] GROUP_ANDROID_NS = {
    android.R.attr.name,
    android.R.attr.pivotX,
    android.R.attr.pivotY,
    android.R.attr.scaleX,
    android.R.attr.scaleY,
    android.R.attr.rotation,
    android.R.attr.translateX,
    android.R.attr.translateY,
  };

  @StyleableRes static final int[] GROUP = GROUP_ANDROID_NS;

  private interface GroupAndroidNs {
    @StyleableRes int NAME = 0;
    @StyleableRes int PIVOT_X = 1;
    @StyleableRes int PIVOT_Y = 2;
    @StyleableRes int SCALE_X = 3;
    @StyleableRes int SCALE_Y = 4;
    @StyleableRes int ROTATION = 5;
    @StyleableRes int TRANSLATE_X = 6;
    @StyleableRes int TRANSLATE_Y = 7;
  }

  interface Group extends GroupAndroidNs {}

  /* Path. */

  @StyleableRes
  private static final int[] PATH_ANDROID_NS = {
    android.R.attr.name,
    android.R.attr.pivotX,
    android.R.attr.pivotY,
    android.R.attr.scaleX,
    android.R.attr.scaleY,
    android.R.attr.rotation,
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
    android.R.attr.translateX,
    android.R.attr.translateY,
    android.R.attr.strokeAlpha,
    android.R.attr.fillAlpha,
    android.R.attr.fillType
  };

  @StyleableRes
  private static final int[] PATH_APP_NS =
      createSortedArray(R.attr.strokeDashArray, R.attr.strokeDashOffset, R.attr.isScalingStroke);

  @StyleableRes static final int[] PATH = concatArrays(PATH_ANDROID_NS, PATH_APP_NS);

  private interface PathAndroidNs {
    @StyleableRes int NAME = 0;
    @StyleableRes int PIVOT_X = 1;
    @StyleableRes int PIVOT_Y = 2;
    @StyleableRes int SCALE_X = 3;
    @StyleableRes int SCALE_Y = 4;
    @StyleableRes int ROTATION = 5;
    @StyleableRes int FILL_COLOR = 6;
    @StyleableRes int PATH_DATA = 7;
    @StyleableRes int STROKE_COLOR = 8;
    @StyleableRes int STROKE_WIDTH = 9;
    @StyleableRes int TRIM_PATH_START = 10;
    @StyleableRes int TRIM_PATH_END = 11;
    @StyleableRes int TRIM_PATH_OFFSET = 12;
    @StyleableRes int STROKE_LINE_CAP = 13;
    @StyleableRes int STROKE_LINE_JOIN = 14;
    @StyleableRes int STROKE_MITER_LIMIT = 15;
    @StyleableRes int FILL_ALPHA = 16;
    @StyleableRes int STROKE_ALPHA = 17;
    @StyleableRes int FILL_TYPE = 18;
    @StyleableRes int TRANSLATE_X = 19;
    @StyleableRes int TRANSLATE_Y = 20;
  }

  private interface PathAppNs {
    @StyleableRes int STROKE_DASH_ARRAY = indexOf(PATH, R.attr.strokeDashArray);
    @StyleableRes int STROKE_DASH_OFFSET = indexOf(PATH, R.attr.strokeDashOffset);
    @StyleableRes int IS_SCALING_STROKE = indexOf(PATH, R.attr.isScalingStroke);
  }

  interface Path extends PathAndroidNs, PathAppNs {}

  /* Clip path. */

  @StyleableRes
  private static final int[] CLIP_PATH_ANDROID_NS = {
    android.R.attr.name,
    android.R.attr.pivotX,
    android.R.attr.pivotY,
    android.R.attr.scaleX,
    android.R.attr.scaleY,
    android.R.attr.rotation,
    android.R.attr.pathData,
    android.R.attr.translateX,
    android.R.attr.translateY,
    android.R.attr.fillType,
  };

  @StyleableRes private static final int[] CLIP_PATH_APP_NS = createSortedArray(R.attr.clipType);

  @StyleableRes static final int[] CLIP_PATH = concatArrays(CLIP_PATH_ANDROID_NS, CLIP_PATH_APP_NS);

  private interface ClipPathAndroidNs {
    @StyleableRes int NAME = 0;
    @StyleableRes int PIVOT_X = 1;
    @StyleableRes int PIVOT_Y = 2;
    @StyleableRes int SCALE_X = 3;
    @StyleableRes int SCALE_Y = 4;
    @StyleableRes int ROTATION = 5;
    @StyleableRes int PATH_DATA = 6;
    @StyleableRes int TRANSLATE_X = 7;
    @StyleableRes int TRANSLATE_Y = 8;
    @StyleableRes int FILL_TYPE = 9;
  }

  private interface ClipPathAppNs {
    @StyleableRes int CLIP_TYPE = indexOf(CLIP_PATH, R.attr.clipType);
  }

  interface ClipPath extends ClipPathAndroidNs, ClipPathAppNs {}

  /* AnimatedVector */

  interface AnimatedVector {
    @StyleableRes int DRAWABLE = 0;
  }

  @StyleableRes static final int[] ANIMATED_VECTOR = {android.R.attr.drawable};

  /* Target */

  interface Target {
    @StyleableRes int NAME = 0;
    @StyleableRes int ANIMATION = 1;
  }

  @StyleableRes static final int[] TARGET = {android.R.attr.name, android.R.attr.animation};

  /* AnimatorSet */

  interface AnimatorSet {
    @StyleableRes int ORDERING = 0;
  }

  @StyleableRes static final int[] ANIMATOR_SET = {android.R.attr.ordering};

  /* Animator */

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

  /* PropertyAnimator */

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

  /* PropertyValuesHolder */

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

  /* Keyframe */

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

  /* PathInterpolator */

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

  private static int[] createSortedArray(int... array) {
    Arrays.sort(array);
    return array;
  }

  private static int indexOf(int[] sortedArray, int attr) {
    return Arrays.binarySearch(sortedArray, attr);
  }

  private static int[] concatArrays(int[] first, int[] second) {
    final int[] result = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

  private static boolean isSorted(int[] array) {
    for (int i = 1; i < array.length; i++) {
      if (array[i - 1] > array[i]) {
        return false;
      }
    }
    return true;
  }

  static {
    if (!isSorted(VECTOR)) {
      throw new RuntimeException("PATH array is not sorted!");
    }
    if (!isSorted(GROUP)) {
      throw new RuntimeException("PATH array is not sorted!");
    }
    if (!isSorted(PATH)) {
      throw new RuntimeException("PATH array is not sorted!");
    }
    if (!isSorted(CLIP_PATH)) {
      throw new RuntimeException("PATH array is not sorted!");
    }
  }

  private Styleable() {}
}
