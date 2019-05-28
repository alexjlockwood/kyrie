package com.github.alexjlockwood.kyrie

import android.graphics.Path
import android.graphics.PointF
import com.github.alexjlockwood.kyrie.Animation.ValueEvaluator
import java.util.Arrays

/**
 * Abstracts a collection of [Keyframe] objects and is used to calculate values between those
 * keyframes for a given [Animation].
 *
 * @param T The keyframe value type.
 */
internal abstract class KeyframeSet<T> {

    /** @return The list of keyframes contained by this keyframe set. */
    abstract val keyframes: List<Keyframe<T>>

    /**
     * Gets the animated value, given the elapsed fraction of the animation (interpolated by the
     * animation's interpolator) and the evaluator used to calculate in-between values. This function
     * maps the input fraction to the appropriate keyframe interval and a fraction between them and
     * returns the interpolated value. Note that the input fraction may fall outside the [0,1] bounds,
     * if the animation's interpolator made that happen (e.g., a spring interpolation that might send
     * the fraction past 1.0). We handle this situation by just using the two keyframes at the
     * appropriate end when the value is outside those bounds.
     *
     * @param fraction The elapsed fraction of the animation.
     * @return The animated value.
     */
    abstract fun getAnimatedValue(fraction: Float): T

    companion object {
        private val KEYFRAME_COMPARATOR = Comparator<Keyframe<*>> { k1, k2 -> k1.fraction.compareTo(k2.fraction) }

        /** @return An [ObjectKeyframeSet] with evenly distributed keyframe values. */
        fun <T> ofObject(evaluator: ValueEvaluator<T>, values: Array<T>): KeyframeSet<T> {
            val numKeyframes = values.size
            val keyframes = ArrayList<Keyframe<T>>(Math.max(numKeyframes, 2))
            if (numKeyframes == 1) {
                keyframes.add(Keyframe.of(0f))
                keyframes.add(Keyframe.of(1f, values[0]))
            } else {
                keyframes.add(Keyframe.of(0f, values[0]))
                for (i in 1 until numKeyframes) {
                    keyframes.add(Keyframe.of(i.toFloat() / (numKeyframes - 1), values[i]))
                }
            }
            return ObjectKeyframeSet(evaluator, keyframes)
        }

        /** @return An [ObjectKeyframeSet] with the given keyframe values. */
        fun <T> ofObject(evaluator: ValueEvaluator<T>, values: Array<Keyframe<T>>): KeyframeSet<T> {
            Arrays.sort(values, KEYFRAME_COMPARATOR)
            val list = ArrayList<Keyframe<T>>(values.size)
            val seenFractions = HashSet<Float>(values.size)
            for (i in values.indices.reversed()) {
                if (!seenFractions.contains(values[i].fraction)) {
                    list.add(values[i])
                    seenFractions.add(values[i].fraction)
                }
            }
            list.reverse()
            return ObjectKeyframeSet(evaluator, list)
        }

        /** @return A [PathKeyframeSet] that estimates motion along the given path. */
        fun ofPath(path: Path): KeyframeSet<PointF> {
            return PathKeyframeSet(path)
        }
    }
}
