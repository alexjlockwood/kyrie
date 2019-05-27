package com.github.alexjlockwood.kyrie

import android.animation.TimeInterpolator
import androidx.annotation.FloatRange

/**
 * This class holds a time/value pair for an animation. A [Keyframe] is used to define the
 * values that the animation target will have over the course of the animation. As the time proceeds
 * from one keyframe to the other, the value of the target will animate between the value at the
 * previous keyframe and the value at the next keyframe. Each keyframe also holds an optional
 * [TimeInterpolator] object, which defines the time interpolation over the inter-value preceding
 * the keyframe.
 *
 * @param T The keyframe value type.
 */
class Keyframe<T> private constructor(@FloatRange(from = 0.0, to = 1.0) fraction: Float, value: T?) {

    /**
     * Gets the time for this [Keyframe], as a fraction of the overall animation duration.
     *
     * @return The time associated with this [Keyframe], as a fraction of the overall animation
     * duration. This should be a value between 0 and 1.
     */
    @FloatRange(from = 0.0, to = 1.0)
    @get:FloatRange(from = 0.0, to = 1.0)
    var fraction: Float = 0f
        private set

    /**
     * Gets the value for this [Keyframe].
     *
     * @return The value for this [Keyframe].
     */
    var value: T? = null
        private set

    /**
     * Gets the optional interpolator for this [Keyframe]. A value of null indicates that there
     * is no interpolation, which is the same as linear interpolation.
     *
     * @return The optional interpolator for this [Keyframe]. May be null.
     */
    var interpolator: TimeInterpolator? = null
        private set

    init {
        this.fraction = fraction
        this.value = value
    }

    /**
     * Sets the time for this [Keyframe], as a fraction of the overall animation duration.
     *
     * @param fraction The time associated with this [Keyframe], as a fraction of the overall
     * animation duration. This should be a value between 0 and 1.
     * @return This [Keyframe] object (to allow for chaining of calls to setter methods).
     */
    fun fraction(@FloatRange(from = 0.0, to = 1.0) fraction: Float): Keyframe<T> {
        this.fraction = fraction
        return this
    }

    /**
     * Sets the value for this [Keyframe].
     *
     * @param value The value for this [Keyframe]. May be null.
     * @return This [Keyframe] object (to allow for chaining of calls to setter methods).
     */
    fun value(value: T?): Keyframe<T> {
        this.value = value
        return this
    }

    /**
     * Sets the optional interpolator for this [Keyframe]. A value of null indicates that there
     * is no interpolation, which is the same as linear interpolation.
     *
     * @param interpolator The optional interpolator for this [Keyframe]. May be null.
     * @return This [Keyframe] object (to allow for chaining of calls to setter methods).
     */
    fun interpolator(interpolator: TimeInterpolator?): Keyframe<T> {
        this.interpolator = interpolator
        return this
    }

    companion object {

        /**
         * Constructs a [Keyframe] object with the given time. The value at this time will be
         * derived from the target object when the animation first starts. The time defines the time, as a
         * proportion of an overall animation's duration, at which the value will hold true for the
         * animation. The value for the animation between keyframes will be calculated as an interpolation
         * between the values at those keyframes.
         *
         * @param T The keyframe value type.
         * @param fraction The time, expressed as a value between 0 and 1, representing the fraction of
         * time elapsed of the overall animation duration.
         * @return The constructed [Keyframe] object.
         */
        @JvmStatic
        fun <T> of(@FloatRange(from = 0.0, to = 1.0) fraction: Float): Keyframe<T> {
            return of(fraction, null)
        }

        /**
         * Constructs a [Keyframe] object with the given time and value. The time defines the time,
         * as a proportion of an overall animation's duration, at which the value will hold true for the
         * animation. The value for the animation between keyframes will be calculated as an interpolation
         * between the values at those keyframes.
         *
         * @param T The keyframe value type.
         * @param fraction The time, expressed as a value between 0 and 1, representing the fraction of
         * time elapsed of the overall animation duration.
         * @param value The value that the object will animate to as the animation time approaches the
         * time in this [Keyframe], and the the value animated from as the time passes the time
         * in this [Keyframe]. May be null.
         * @return The constructed [Keyframe] object.
         */
        @JvmStatic
        fun <T> of(@FloatRange(from = 0.0, to = 1.0) fraction: Float, value: T?): Keyframe<T> {
            return Keyframe(fraction, value)
        }
    }
}
