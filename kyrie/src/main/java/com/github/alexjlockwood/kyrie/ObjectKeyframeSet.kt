package com.github.alexjlockwood.kyrie

import com.github.alexjlockwood.kyrie.Animation.ValueEvaluator

/**
 * Abstracts a collection of [Keyframe] objects and is used to calculate values between those
 * keyframes for a given [Animation].
 *
 * @param T The keyframe value type.
 */
internal class ObjectKeyframeSet<T>(
        private val evaluator: ValueEvaluator<T>,
        // Only used when there are more than 2 keyframes.
        override val keyframes: List<Keyframe<T>>
) : KeyframeSet<T>() {

    private val firstKf = keyframes.first()
    private val lastKf = keyframes.last()
    // Only used in the 2-keyframe case.
    private val interpolator = lastKf.interpolator

    override fun getAnimatedValue(fraction: Float): T {
        val numKeyframes = keyframes.size
        var fraction = fraction
        // Special-case optimization for the common case of only two keyframes.
        if (numKeyframes == 2) {
            if (interpolator != null) {
                fraction = interpolator.getInterpolation(fraction)
            }
            return evaluator.evaluate(fraction, firstKf.value!!, lastKf.value!!)
        }
        if (fraction <= 0) {
            val nextKf = keyframes[1]
            val interpolator = nextKf.interpolator
            if (interpolator != null) {
                fraction = interpolator.getInterpolation(fraction)
            }
            val prevFraction = firstKf.fraction
            val intervalFraction = (fraction - prevFraction) / (nextKf.fraction - prevFraction)
            return evaluator.evaluate(intervalFraction, firstKf.value!!, nextKf.value!!)
        }
        if (fraction >= 1) {
            val prefKf = keyframes[numKeyframes - 2]
            val interpolator = lastKf.interpolator
            if (interpolator != null) {
                fraction = interpolator.getInterpolation(fraction)
            }
            val prevFraction = prefKf.fraction
            val intervalFraction = (fraction - prevFraction) / (lastKf.fraction - prevFraction)
            return evaluator.evaluate(intervalFraction, prefKf.value!!, lastKf.value!!)
        }
        var prevKf = firstKf
        for (i in 1 until numKeyframes) {
            val nextKf = keyframes[i]
            if (fraction < nextKf.fraction) {
                val interpolator = nextKf.interpolator
                val prevFraction = prevKf.fraction
                var intervalFraction = (fraction - prevFraction) / (nextKf.fraction - prevFraction)
                // Apply getInterpolator on the proportional duration.
                if (interpolator != null) {
                    intervalFraction = interpolator.getInterpolation(intervalFraction)
                }
                return evaluator.evaluate(intervalFraction, prevKf.value!!, nextKf.value!!)
            }
            prevKf = nextKf
        }
        // Shouldn't get here.
        return lastKf.value!!
    }
}
