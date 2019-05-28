package com.github.alexjlockwood.kyrie

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.AnyRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleableRes
import androidx.appcompat.content.res.AppCompatResources
import org.xmlpull.v1.XmlPullParser

/**
 * Compat methods for accessing TypedArray values.
 *
 * All the `getNamed*()` functions added the attribute name match, to take care of potential ID
 * collision between the private attributes in older OS version (OEM) and the attributes existed in
 * the newer OS version. For example, if an private attribute named `"abcdefg"` in Kitkat has the same
 * id value as `android:pathData` in Lollipop, we need to match the attribute's name first.
 */
internal object TypedArrayUtils {

    private const val NAMESPACE = "http://schemas.android.com/apk/res/android"

    /**
     * @return Whether the current node of the [XmlPullParser] has an attribute with the
     * specified `attrName`.
     */
    @JvmStatic
    fun hasAttribute(parser: XmlPullParser, attrName: String): Boolean {
        return parser.getAttributeValue(NAMESPACE, attrName) != null
    }

    /**
     * Retrieves a float attribute value. In addition to the styleable resource ID, we also make sure
     * that the attribute name matches.
     *
     * @return a float value in the [TypedArray] with the specified `resId`, or `defaultValue` if it does not exist.
     */
    @JvmStatic
    fun getNamedFloat(
            a: TypedArray,
            parser: XmlPullParser,
            attrName: String,
            @StyleableRes resId: Int,
            defaultValue: Float
    ): Float {
        val hasAttr = hasAttribute(parser, attrName)
        return if (!hasAttr) {
            defaultValue
        } else {
            a.getFloat(resId, defaultValue)
        }
    }

    /**
     * Retrieves a boolean attribute value. In addition to the styleable resource ID, we also make
     * sure that the attribute name matches.
     *
     * @return a boolean value in the [TypedArray] with the specified `resId`, or `defaultValue` if it does not exist.
     */
    @JvmStatic
    fun getNamedBoolean(
            a: TypedArray,
            parser: XmlPullParser,
            attrName: String,
            @StyleableRes resId: Int,
            defaultValue: Boolean
    ): Boolean {
        val hasAttr = hasAttribute(parser, attrName)
        return if (!hasAttr) {
            defaultValue
        } else {
            a.getBoolean(resId, defaultValue)
        }
    }

    /**
     * Retrieves an int attribute value. In addition to the styleable resource ID, we also make sure
     * that the attribute name matches.
     *
     * @return an int value in the [TypedArray] with the specified `resId`, or `defaultValue` if it does not exist.
     */
    @JvmStatic
    fun getNamedInt(
            a: TypedArray,
            parser: XmlPullParser,
            attrName: String,
            @StyleableRes resId: Int,
            defaultValue: Int
    ): Int {
        val hasAttr = hasAttribute(parser, attrName)
        return if (!hasAttr) {
            defaultValue
        } else {
            a.getInt(resId, defaultValue)
        }
    }

    /**
     * Retrieves a color attribute value. In addition to the styleable resource ID, we also make sure
     * that the attribute name matches.
     *
     * @return a color value in the [TypedArray] with the specified `resId`, or `defaultValue` if it does not exist.
     */
    @JvmStatic
    @ColorInt
    fun getNamedColor(
            a: TypedArray,
            parser: XmlPullParser,
            attrName: String,
            @StyleableRes resId: Int,
            @ColorInt defaultValue: Int
    ): Int {
        val hasAttr = hasAttribute(parser, attrName)
        return if (!hasAttr) {
            defaultValue
        } else {
            a.getColor(resId, defaultValue)
        }
    }

    /**
     * Retrieves a complex color attribute value. In addition to the styleable resource ID, we also
     * make sure that the attribute name matches.
     *
     * @return a complex color value form the [TypedArray] with the specified `resId`, or
     * containing `defaultValue` if it does not exist.
     */
    @JvmStatic
    fun getNamedComplexColor(
            a: TypedArray,
            parser: XmlPullParser,
            context: Context,
            attrName: String,
            @StyleableRes resId: Int,
            @ColorInt defaultValue: Int
    ): ComplexColor {
        if (hasAttribute(parser, attrName)) {
            // first check if is a simple color
            val value = TypedValue()
            a.getValue(resId, value)
            if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                return ComplexColor.from(value.data)
            }

            // not a simple color, attempt to inflate complex types
            val complexColor = ComplexColor.inflate(context, a.getResourceId(resId, 0))
            if (complexColor != null) return complexColor
        }
        return ComplexColor.from(defaultValue)
    }

    /**
     * Retrieves a color state list object. In addition to the styleable resource ID, we also make
     * sure that the attribute name matches.
     *
     * @return a color state list object form the [TypedArray] with the specified `resId`,
     * or null if it does not exist.
     */
    @JvmStatic
    fun getNamedColorStateList(
            a: TypedArray,
            parser: XmlPullParser,
            context: Context,
            attrName: String,
            @StyleableRes resId: Int
    ): ColorStateList? {
        if (hasAttribute(parser, attrName)) {
            val value = TypedValue()
            a.getValue(resId, value)
            if (value.type == TypedValue.TYPE_ATTRIBUTE) {
                throw UnsupportedOperationException(
                        "Failed to resolve attribute at index $resId: $value")
            } else if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                // Handle inline color definitions.
                return getNamedColorStateListFromInt(value)
            }
            return AppCompatResources.getColorStateList(context, a.getResourceId(resId, 0))
        }
        return null
    }

    private fun getNamedColorStateListFromInt(value: TypedValue): ColorStateList {
        // This is copied from ResourcesImpl#getNamedColorStateListFromInt in the platform, but the
        // ComplexColor caching mechanism has been removed. The practical implication of this is
        // minimal, since platform caching is only used by Zygote-preloaded resources.
        return ColorStateList.valueOf(value.data)
    }

    /**
     * Retrieves a resource ID attribute value. In addition to the styleable resource ID, we also make
     * sure that the attribute name matches.
     *
     * @return a resource ID value in the [TypedArray] with the specified `resId`, or
     * `defaultValue` if it does not exist.
     */
    @JvmStatic
    @AnyRes
    fun getNamedResourceId(
            a: TypedArray,
            parser: XmlPullParser,
            attrName: String,
            @StyleableRes resId: Int,
            @AnyRes defaultValue: Int
    ): Int {
        val hasAttr = hasAttribute(parser, attrName)
        return if (!hasAttr) {
            defaultValue
        } else {
            a.getResourceId(resId, defaultValue)
        }
    }

    /**
     * Retrieves a string attribute value. In addition to the styleable resource ID, we also make sure
     * that the attribute name matches.
     *
     * @return a string value in the [TypedArray] with the specified `resId`, or null if
     * it does not exist.
     */
    @JvmStatic
    fun getNamedString(
            a: TypedArray,
            parser: XmlPullParser,
            attrName: String,
            @StyleableRes resId: Int
    ): String? {
        val hasAttr = hasAttribute(parser, attrName)
        return if (!hasAttr) {
            null
        } else {
            a.getString(resId)
        }
    }

    /**
     * Retrieve the raw TypedValue for the attribute at <var>index</var> and return a temporary object
     * holding its data. This object is only valid until the next call on to [TypedArray].
     */
    @JvmStatic
    fun peekNamedValue(a: TypedArray, parser: XmlPullParser, attrName: String, resId: Int): TypedValue? {
        val hasAttr = hasAttribute(parser, attrName)
        return if (!hasAttr) {
            null
        } else {
            a.peekValue(resId)
        }
    }

    /**
     * Obtains styled attributes from the theme, if available, or unstyled resources if the theme is
     * null.
     */
    @JvmStatic
    fun obtainAttributes(
            res: Resources,
            theme: Resources.Theme?,
            set: AttributeSet,
            attrs: IntArray
    ): TypedArray {
        return if (theme == null) {
            res.obtainAttributes(set, attrs)
        } else theme.obtainStyledAttributes(set, attrs, 0, 0)
    }
}
