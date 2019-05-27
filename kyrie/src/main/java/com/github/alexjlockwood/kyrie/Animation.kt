package com.github.alexjlockwood.kyrie

import android.animation.TimeInterpolator
import android.graphics.Path
import android.graphics.PointF
import android.view.animation.LinearInterpolator
import androidx.annotation.IntRange

/**
 * An [Animation] encapsulates the information required to animate a single property of a [Node].
 *
 * @param T The animation's original value type.
 * @param V The animation's transformed value type.
 */
class Animation<T, V> private constructor(
        private val keyframeSet: KeyframeSet<T>,
        private val transformer: ValueTransformer<T, V>
) {

    /**
     * Gets the start delay of the animation.
     *
     * @return The start delay of the animation in milliseconds.
     */
    @IntRange(from = 0L)
    var startDelay: Long = 0
        private set

    /**
     * Gets the duration of the animation.
     *
     * @return The length of the animation in milliseconds.
     */
    @IntRange(from = 0L)
    var duration: Long = 300
        private set

    /**
     * Returns the timing interpolator that this animation uses. If null, a [LinearInterpolator]
     * will be used by default.
     *
     * @return The timing interpolator for this animation.
     */
    var interpolator: TimeInterpolator? = null
        private set

    /**
     * Defines how many times the animation should repeat. The default value is 0.
     *
     * @return The number of times the animation should repeat, or [INFINITE].
     */
    var repeatCount: Long = 0
        private set

    /**
     * Defines what this animation should do when it reaches the end.
     *
     * @return Either one of [RepeatMode.RESTART] or [RepeatMode.REVERSE].
     */
    var repeatMode = RepeatMode.RESTART
        private set

    private var isInitialized: Boolean = false

    /**
     * Gets the total duration of the animation in milliseconds, accounting for start delay and repeat
     * count. Returns [INFINITE] if the repeat count is infinite.
     *
     * @return Total time an animation takes to finish, starting from the time it is started.
     * [INFINITE] will be returned if the animation repeats infinite times.
     */
    val totalDuration: Long
        get() = if (repeatCount == INFINITE) INFINITE else startDelay + duration * (repeatCount + 1)

    /** Repeat mode determines how a repeating animation should behave once it completes. */
    enum class RepeatMode {
        /**
         * When the animation reaches the end and `repeatCount` is [INFINITE] or a
         * positive value, the animation restarts from the beginning.
         */
        RESTART,
        /**
         * When the animation reaches the end and `repeatCount` is [INFINITE] or a
         * positive value, the animation reverses direction on every iteration.
         */
        REVERSE
    }

    private fun throwIfInitialized() {
        if (isInitialized) {
            throw IllegalStateException(
                    "Animation must not be mutated after the KyrieDrawable has been created")
        }
    }

    /**
     * Sets the start delay of the animation.
     *
     * @param startDelay The start delay of the animation in milliseconds.
     * @return This [Animation] object (to allow for chaining of calls to setter methods).
     */
    fun startDelay(@IntRange(from = 0L) startDelay: Long): Animation<T, V> {
        throwIfInitialized()
        this.startDelay = startDelay
        return this
    }

    /**
     * Sets the duration of the animation.
     *
     * @param duration The length of the animation in milliseconds.
     * @return This [Animation] object (to allow for chaining of calls to setter methods).
     */
    fun duration(@IntRange(from = 0L) duration: Long): Animation<T, V> {
        throwIfInitialized()
        this.duration = duration
        return this
    }

    /**
     * Sets how many times the animation should be repeated. If the repeat count is 0, the animation
     * is never repeated. If the repeat count is greater than 0 or [INFINITE], the repeat mode
     * will be taken into account. The repeat count is 0 by default.
     *
     * @param repeatCount The number of times the animation should be repeated.
     * @return This [Animation] object (to allow for chaining of calls to setter methods).
     */
    fun repeatCount(repeatCount: Long): Animation<T, V> {
        throwIfInitialized()
        this.repeatCount = repeatCount
        return this
    }

    /**
     * Defines what this animation should do when it reaches the end. This setting is applied only
     * when the repeat count is either greater than 0 or [INFINITE]. Defaults to [RepeatMode.RESTART].
     *
     * @param repeatMode [RepeatMode.RESTART] or [RepeatMode.REVERSE].
     * @return This [Animation] object (to allow for chaining of calls to setter methods).
     */
    fun repeatMode(repeatMode: RepeatMode): Animation<T, V> {
        throwIfInitialized()
        this.repeatMode = repeatMode
        return this
    }

    /**
     * Sets the timing interpolator that this animation uses. If null, a [LinearInterpolator]
     * will be used by default.
     *
     * @param interpolator The timing interpolator that this animation uses.
     * @return This [Animation] object (to allow for chaining of calls to setter methods).
     */
    fun interpolator(interpolator: TimeInterpolator?): Animation<T, V> {
        throwIfInitialized()
        this.interpolator = interpolator
        return this
    }

    /**
     * Called when the animations are first initialized, so that the animation's keyframes can fill in
     * any missing start values.
     */
    internal fun setupStartValue(startValue: V) {
        isInitialized = true
        keyframeSet.keyframes.forEach {
            if (it.value == null) {
                it.value(transformBack(startValue))
            }
        }
    }

    private fun transformBack(value: V): T {
        if (transformer !is BidirectionalValueTransformer<*, *>) {
            throw IllegalArgumentException(
                    "Transformer ${transformer.javaClass.name} must be a BidirectionalValueTransformer")
        }
        @Suppress("UNCHECKED_CAST")
        return (transformer as BidirectionalValueTransformer<T, V>).transformBack(value)
    }

    /**
     * Returns the animated value of this animation at the given fraction.
     *
     * @param fraction The current animation fraction. Typically between 0 and 1 (but may slightly
     * extend these bounds depending on the interpolator used).
     * @return The animated value of this animation at the given fraction.
     */
    fun getAnimatedValue(fraction: Float): V {
        return transformer.transform(keyframeSet.getAnimatedValue(fraction))
    }

    /**
     * Creates a new animation with original value type `T` and a new transformed value
     * type `W`.
     *
     * @param W The animation's new transformed value type.
     * @param transformer The value transformer to use to transform the animation's original type
     * `T` to a new transformed value type `W`.
     * @return A new animation with the same original value type `T` and transformed value
     * type `W`.
     */
    fun <W> transform(transformer: ValueTransformer<T, W>): Animation<T, W> {
        return Animation(keyframeSet, transformer)
                .startDelay(startDelay)
                .duration(duration)
                .repeatCount(repeatCount)
                .repeatMode(repeatMode)
                .interpolator(interpolator)
    }

    /**
     * Creates a new animation with original value type `T` and a new transformed value
     * type `W`.
     *
     * @param W The animation's new transformed value type.
     * @param transformer The value transformer to use to transform the animation's original type
     * `T` to a new transformed value type `W`.
     * @return A new animation with the same original value type `T` and transformed value
     * type `W`.
     */
    @JvmSynthetic
    fun <W> transform(transformer: (value: T) -> W): Animation<T, W> {
        return transform(object : ValueTransformer<T, W> {
            override fun transform(value: T): W {
                return transformer.invoke(value)
            }
        })
    }

    /**
     * Interface that can transform type `T` to another type `V`. This is
     * necessary when the original value type of an animation is different than the desired value
     * type.
     *
     * @param T The animation's original value type.
     * @param V The animation's transformed value type.
     */
    interface ValueTransformer<T, V> {
        /**
         * Transforms a value from one type to another.
         *
         * @param value The value to transform.
         * @return The transformed value.
         */
        fun transform(value: T): V
    }

    /**
     * Interface that can transform type `T` to another type `V` and back again.
     * This is necessary when the value types of an animation are different from the property type.
     * This interface is only needed when working with an [Animation] with no explicitly set
     * start value and that has been transformed using [transform].
     *
     * @param T The animation's original value type.
     * @param V The animation's transformed value type.
     */
    interface BidirectionalValueTransformer<T, V> : ValueTransformer<T, V> {
        /**
         * Transforms the output type back to the input type. *
         *
         * @param value The value to transform back.
         * @return The value that has been transformed back.
         */
        fun transformBack(value: V): T
    }

    private class IdentityValueTransformer<V> : BidirectionalValueTransformer<V, V> {
        override fun transform(value: V): V {
            return value
        }

        override fun transformBack(value: V): V {
            return value
        }
    }

    internal interface ValueEvaluator<T> {
        fun evaluate(fraction: Float, startValue: T, endValue: T): T
    }

    private class FloatValueEvaluator : ValueEvaluator<Float> {
        override fun evaluate(fraction: Float, startValue: Float, endValue: Float): Float {
            return startValue + (endValue - startValue) * fraction
        }
    }

    private class ArgbValueEvaluator : ValueEvaluator<Int> {
        override fun evaluate(fraction: Float, startValue: Int, endValue: Int): Int {
            val startA = (startValue shr 24 and 0xff) / 255f
            var startR = (startValue shr 16 and 0xff) / 255f
            var startG = (startValue shr 8 and 0xff) / 255f
            var startB = (startValue and 0xff) / 255f
            val endA = (endValue shr 24 and 0xff) / 255f
            var endR = (endValue shr 16 and 0xff) / 255f
            var endG = (endValue shr 8 and 0xff) / 255f
            var endB = (endValue and 0xff) / 255f
            // Transform from sRGB to linear.
            startR = Math.pow(startR.toDouble(), 2.2).toFloat()
            startG = Math.pow(startG.toDouble(), 2.2).toFloat()
            startB = Math.pow(startB.toDouble(), 2.2).toFloat()
            endR = Math.pow(endR.toDouble(), 2.2).toFloat()
            endG = Math.pow(endG.toDouble(), 2.2).toFloat()
            endB = Math.pow(endB.toDouble(), 2.2).toFloat()
            // Compute the interpolated color in linear space.
            var a = startA + fraction * (endA - startA)
            var r = startR + fraction * (endR - startR)
            var g = startG + fraction * (endG - startG)
            var b = startB + fraction * (endB - startB)
            // Transform back to sRGB in the [0..255] range.
            a *= 255f
            r = Math.pow(r.toDouble(), 1.0 / 2.2).toFloat() * 255f
            g = Math.pow(g.toDouble(), 1.0 / 2.2).toFloat() * 255f
            b = Math.pow(b.toDouble(), 1.0 / 2.2).toFloat() * 255f
            return Math.round(a) shl 24 or (Math.round(r) shl 16) or (Math.round(g) shl 8) or Math.round(b)
        }
    }

    private class FloatArrayValueEvaluator : ValueEvaluator<FloatArray> {
        private var array: FloatArray? = null

        override fun evaluate(fraction: Float, startValue: FloatArray, endValue: FloatArray): FloatArray {
            if (array == null || array!!.size != startValue.size) {
                array = FloatArray(startValue.size)
            }
            for (i in array!!.indices) {
                val start = startValue[i]
                val end = endValue[i]
                array!![i] = start + fraction * (end - start)
            }
            return array!!
        }
    }

    private class PathDataValueEvaluator : ValueEvaluator<PathData> {
        private var pathData: PathData? = null

        override fun evaluate(fraction: Float, startValue: PathData, endValue: PathData): PathData {
            if (pathData == null || !pathData!!.canMorphWith(startValue)) {
                pathData = PathData(startValue)
            }
            pathData!!.interpolate(startValue, endValue, fraction)
            return pathData!!
        }
    }

    companion object {

        /**
         * Constructs and returns an [Animation] that animates between float values. A single value
         * implies that the value is the one being animated to, in which case the start value will be
         * derived from the property being animated and the target object when the animation is started.
         * Two values imply starting and ending values. More than two values imply a starting value,
         * values to animate through along the way, and an ending value (these values will be distributed
         * evenly across the duration of the animation).
         *
         * @param values A set of values that the animation will animate through over time.
         * @return A new [Animation].
         */
        @JvmStatic
        fun ofFloat(vararg values: Float): Animation<Float, Float> {
            return ofObject(FloatValueEvaluator(), floatArrayOf(*values).toTypedArray())
        }

        /**
         * Same as [ofFloat] except with [Keyframe]s instead of float values.
         *
         * @param values A set of [Keyframe]s that the animation will animate through over time.
         * @return A new [Animation].
         */
        @JvmStatic
        @SafeVarargs
        fun ofFloat(vararg values: Keyframe<Float>): Animation<Float, Float> {
            return ofObject(FloatValueEvaluator(), arrayOf(*values))
        }

        /**
         * Constructs and returns an [Animation] that animates between color values. A single value
         * implies that the value is the one being animated to, in which case the start value will be
         * derived from the property being animated and the target object when the animation is started.
         * Two values imply starting and ending values. More than two values imply a starting value,
         * values to animate through along the way, and an ending value (these values will be distributed
         * evenly across the duration of the animation).
         *
         * @param values A set of values that the animation will animate through over time.
         * @return A new [Animation].
         */
        @JvmStatic
        fun ofArgb(vararg values: Int): Animation<Int, Int> {
            return ofObject(ArgbValueEvaluator(), intArrayOf(*values).toTypedArray())
        }

        /**
         * Same as [ofArgb] except with [Keyframe]s instead of color values.
         *
         * @param values A set of [Keyframe]s that the animation will animate through over time.
         * @return A new [Animation].
         */
        @SafeVarargs
        @JvmStatic
        fun ofArgb(vararg values: Keyframe<Int>): Animation<Int, Int> {
            return ofObject(ArgbValueEvaluator(), arrayOf(*values))
        }

        /**
         * Constructs and returns an [Animation] that animates between `float[]` values. A single
         * value implies that the value is the one being animated to, in which case the start value will
         * be derived from the property being animated and the target object when the animation is
         * started. Two values imply starting and ending values. More than two values imply a starting
         * value, values to animate through along the way, and an ending value (these values will be
         * distributed evenly across the duration of the animation).
         *
         * @param values A set of values that the animation will animate through over time. The `float[]`
         * values should all have the same length.
         * @return A new [Animation].
         */
        @JvmStatic
        fun ofFloatArray(vararg values: FloatArray): Animation<FloatArray, FloatArray> {
            return ofObject(FloatArrayValueEvaluator(), arrayOf(*values))
        }

        /**
         * Same as [ofFloatArray] except with [Keyframe]s instead of `float[]`
         * values.
         *
         * @param values A set of [Keyframe]s that the animation will animate through over time.
         * @return A new [Animation].
         */
        @JvmStatic
        @SafeVarargs
        fun ofFloatArray(vararg values: Keyframe<FloatArray>): Animation<FloatArray, FloatArray> {
            return ofObject(FloatArrayValueEvaluator(), arrayOf(*values))
        }

        /**
         * Constructs and returns an [Animation] that animates between [PathData] values. A
         * single value implies that the value is the one being animated to, in which case the start value
         * will be derived from the property being animated and the target object when the animation is
         * started. Two values imply starting and ending values. More than two values imply a starting
         * value, values to animate through along the way, and an ending value (these values will be
         * distributed evenly across the duration of the animation).
         *
         * @param values A set of values that the animation will animate through over time.
         * The [PathData] values should all be morphable with each other.
         * @return A new [Animation].
         */
        @JvmStatic
        fun ofPathMorph(vararg values: PathData): Animation<PathData, PathData> {
            return ofObject(PathDataValueEvaluator(), arrayOf(*values))
        }

        /**
         * Same as [ofPathMorph] except with [Keyframe]s instead of `float[]` values.
         *
         * @param values A set of [Keyframe]s that the animation will animate through over time.
         * @return A new [Animation].
         */
        @JvmStatic
        @SafeVarargs
        fun ofPathMorph(vararg values: Keyframe<PathData>): Animation<PathData, PathData> {
            return ofObject(PathDataValueEvaluator(), arrayOf(*values))
        }

        private fun <V> ofObject(evaluator: ValueEvaluator<V>, values: Array<V>): Animation<V, V> {
            if (values.isEmpty()) {
                throw IllegalArgumentException("Must specify at least one value")
            }
            return Animation(KeyframeSet.ofObject(evaluator, values), IdentityValueTransformer())
        }

        private fun <V> ofObject(evaluator: ValueEvaluator<V>, values: Array<Keyframe<V>>): Animation<V, V> {
            if (values.isEmpty()) {
                throw IllegalArgumentException("Must specify at least one keyframe")
            }
            return Animation(KeyframeSet.ofObject(evaluator, values), IdentityValueTransformer())
        }

        /**
         * Constructs and returns an [Animation] that animates through [PointF] values in
         * order to simulate motion along the given path. Clients can use [transform] to transform
         * the returned animation into one that outputs floats corresponding to the path's x/y coordinates.
         *
         * @param path The path to animate values along.
         * @return A new [Animation].
         */
        @JvmStatic
        fun ofPathMotion(path: Path): Animation<PointF, PointF> {
            if (path.isEmpty) {
                throw IllegalArgumentException("The path must not be empty")
            }
            return Animation(KeyframeSet.ofPath(path), IdentityValueTransformer())
        }

        /**
         * This value used used with the [repeatCount] property to repeat the animation
         * indefinitely. Also used to indicate infinite duration.
         */
        const val INFINITE = -1L
    }
}
