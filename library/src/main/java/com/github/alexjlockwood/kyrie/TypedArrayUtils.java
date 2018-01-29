package com.github.alexjlockwood.kyrie;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.AnyRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleableRes;
import android.util.AttributeSet;
import android.util.TypedValue;

import org.xmlpull.v1.XmlPullParser;

/**
 * Compat methods for accessing TypedArray values.
 *
 * <p>All the getNamed*() functions added the attribute name match, to take care of potential ID
 * collision between the private attributes in older OS version (OEM) and the attributes existed in
 * the newer OS version. For example, if an private attribute named "abcdefg" in Kitkat has the same
 * id value as "android:pathData" in Lollipop, we need to match the attribute's namefirst.
 */
final class TypedArrayUtils {
  private static final String NAMESPACE = "http://schemas.android.com/apk/res/android";

  /**
   * @return Whether the current node of the {@link XmlPullParser} has an attribute with the
   *     specified {@code attrName}.
   */
  public static boolean hasAttribute(XmlPullParser parser, String attrName) {
    return parser.getAttributeValue(NAMESPACE, attrName) != null;
  }

  /**
   * Retrieves a float attribute value. In addition to the styleable resource ID, we also make sure
   * that the attribute name matches.
   *
   * @return a float value in the {@link TypedArray} with the specified {@code resId}, or {@code
   *     defaultValue} if it does not exist.
   */
  public static float getNamedFloat(
      TypedArray a,
      XmlPullParser parser,
      String attrName,
      @StyleableRes int resId,
      float defaultValue) {
    return hasAttribute(parser, attrName) ? a.getFloat(resId, defaultValue) : defaultValue;
  }

  /**
   * Retrieves a boolean attribute value. In addition to the styleable resource ID, we also make
   * sure that the attribute name matches.
   *
   * @return a boolean value in the {@link TypedArray} with the specified {@code resId}, or {@code
   *     defaultValue} if it does not exist.
   */
  public static boolean getNamedBoolean(
      TypedArray a,
      XmlPullParser parser,
      String attrName,
      @StyleableRes int resId,
      boolean defaultValue) {
    return hasAttribute(parser, attrName) ? a.getBoolean(resId, defaultValue) : defaultValue;
  }

  /**
   * Retrieves an int attribute value. In addition to the styleable resource ID, we also make sure
   * that the attribute name matches.
   *
   * @return an int value in the {@link TypedArray} with the specified {@code resId}, or {@code
   *     defaultValue} if it does not exist.
   */
  public static int getNamedInt(
      TypedArray a,
      XmlPullParser parser,
      String attrName,
      @StyleableRes int resId,
      int defaultValue) {
    return hasAttribute(parser, attrName) ? a.getInt(resId, defaultValue) : defaultValue;
  }

  /**
   * Retrieves a color attribute value. In addition to the styleable resource ID, we also make sure
   * that the attribute name matches.
   *
   * @return a color value in the {@link TypedArray} with the specified {@code resId}, or {@code
   *     defaultValue} if it does not exist.
   */
  @ColorInt
  public static int getNamedColor(
      TypedArray a,
      XmlPullParser parser,
      String attrName,
      @StyleableRes int resId,
      @ColorInt int defaultValue) {
    return hasAttribute(parser, attrName) ? a.getColor(resId, defaultValue) : defaultValue;
  }

  /**
   * Retrieves a resource ID attribute value. In addition to the styleable resource ID, we also make
   * sure that the attribute name matches.
   *
   * @return a resource ID value in the {@link TypedArray} with the specified {@code resId}, or
   *     {@code defaultValue} if it does not exist.
   */
  @AnyRes
  public static int getNamedResourceId(
      TypedArray a,
      XmlPullParser parser,
      String attrName,
      @StyleableRes int resId,
      @AnyRes int defaultValue) {
    return hasAttribute(parser, attrName) ? a.getResourceId(resId, defaultValue) : defaultValue;
  }

  /**
   * Retrieves a string attribute value. In addition to the styleable resource ID, we also make sure
   * that the attribute name matches.
   *
   * @return a string value in the {@link TypedArray} with the specified {@code resId}, or null if
   *     it does not exist.
   */
  @Nullable
  public static String getNamedString(
      TypedArray a, XmlPullParser parser, String attrName, @StyleableRes int resId) {
    return hasAttribute(parser, attrName) ? a.getString(resId) : null;
  }

  /**
   * Retrieve the raw TypedValue for the attribute at <var>index</var> and return a temporary object
   * holding its data. This object is only valid until the next call on to {@link TypedArray}.
   */
  @Nullable
  public static TypedValue peekNamedValue(
      TypedArray a, XmlPullParser parser, String attrName, @StyleableRes int resId) {
    return hasAttribute(parser, attrName) ? a.peekValue(resId) : null;
  }

  /** Obtains styled attributes from a context. */
  @NonNull
  public static TypedArray obtainAttributes(
      Context context, AttributeSet set, @StyleableRes int[] attrs) {
    return obtainAttributes(context.getResources(), context.getTheme(), set, attrs);
  }

  /**
   * Obtains styled attributes from the theme, if available, or unstyled resources if the theme is
   * null.
   */
  @NonNull
  private static TypedArray obtainAttributes(
      Resources res, @Nullable Resources.Theme theme, AttributeSet set, @StyleableRes int[] attrs) {
    return theme == null
        ? res.obtainAttributes(set, attrs)
        : theme.obtainStyledAttributes(set, attrs, 0, 0);
  }

  private TypedArrayUtils() {}
}
