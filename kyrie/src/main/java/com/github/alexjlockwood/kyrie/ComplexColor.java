package com.github.alexjlockwood.kyrie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import static android.graphics.Color.TRANSPARENT;

/**
 * Represents a color which is one of either:
 *
 * <ol>
 *   <li>A Gradient; as represented by a {@link Shader}.
 *   <li>A {@link ColorStateList}
 *   <li>A simple color represented by an {@code int}
 * </ol>
 */
final class ComplexColor {
  private static final String LOG_TAG = "ComplexColor";

  @Nullable private final Shader mShader;
  @Nullable private final ColorStateList mColorStateList;
  private int mColor; // mutable for animation/state changes

  private ComplexColor(
      @Nullable Shader shader, @Nullable ColorStateList colorStateList, @ColorInt int color) {
    mShader = shader;
    mColorStateList = colorStateList;
    mColor = color;
  }

  static ComplexColor from(@NonNull Shader shader) {
    return new ComplexColor(shader, null, TRANSPARENT);
  }

  static ComplexColor from(@NonNull ColorStateList colorStateList) {
    return new ComplexColor(null, colorStateList, colorStateList.getDefaultColor());
  }

  static ComplexColor from(@ColorInt int color) {
    return new ComplexColor(null, null, color);
  }

  @Nullable
  public Shader getShader() {
    return mShader;
  }

  @Nullable
  public ColorStateList getColorStateList() {
    return mColorStateList;
  }

  @ColorInt
  public int getColor() {
    return mColor;
  }

  public void setColor(@ColorInt int color) {
    mColor = color;
  }

  public boolean isGradient() {
    return mShader != null;
  }

  public boolean isStateful() {
    return mShader == null && mColorStateList != null && mColorStateList.isStateful();
  }

  /**
   * @return {@code true} if the given state causes this color to change, otherwise {@code false}.
   *     If the color has changed, it can be retrieved via {@link #getColor}.
   * @see #isStateful()
   * @see #getColor()
   */
  public boolean onStateChanged(int[] stateSet) {
    boolean changed = false;
    if (isStateful()) {
      final int colorForState =
          mColorStateList.getColorForState(stateSet, mColorStateList.getDefaultColor());
      if (colorForState != mColor) {
        changed = true;
        mColor = colorForState;
      }
    }
    return changed;
  }

  /** @return {@code true} if the this color will draw. */
  public boolean willDraw() {
    return isGradient() || mColor != TRANSPARENT;
  }

  /**
   * Creates a ComplexColor from an XML document using given a set of {@link Resources} and a {@link
   * Resources.Theme}.
   *
   * @param context Context against which the ComplexColor should be inflated.
   * @param resId the resource identifier of the ColorStateList of GradientColor to retrieve.
   * @return A new color.
   */
  @Nullable
  public static ComplexColor inflate(@NonNull Context context, @ColorRes int resId) {
    try {
      return createFromXml(context, resId);
    } catch (Exception e) {
      Log.e(LOG_TAG, "Failed to inflate ComplexColor.", e);
    }
    return null;
  }

  @NonNull
  private static ComplexColor createFromXml(@NonNull Context context, @ColorRes int resId)
      throws IOException, XmlPullParserException {
    @SuppressLint("ResourceType")
    XmlPullParser parser = context.getResources().getXml(resId);
    final AttributeSet attrs = Xml.asAttributeSet(parser);
    int type;
    while ((type = parser.next()) != XmlPullParser.START_TAG && type != XmlPullParser.END_DOCUMENT) {
      // Empty loop
    }
    if (type != XmlPullParser.START_TAG) {
      throw new XmlPullParserException("No start tag found");
    }
    final String name = parser.getName();
    switch (name) {
      case "selector":
        return ComplexColor.from(AppCompatResources.getColorStateList(context, resId));
      case "gradient":
        return ComplexColor.from(
            GradientColorInflater.createFromXmlInner(
                context.getResources(), parser, attrs, context.getTheme()));
      default:
        throw new XmlPullParserException(
            parser.getPositionDescription() + ": unsupported complex color tag " + name);
    }
  }
}
