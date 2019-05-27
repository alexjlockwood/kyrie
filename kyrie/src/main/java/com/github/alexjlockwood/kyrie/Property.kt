package com.github.alexjlockwood.kyrie

import android.view.animation.LinearInterpolator
import androidx.annotation.IntRange
import com.github.alexjlockwood.kyrie.Animation.RepeatMode
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator

internal class Property<V>(animations: List<Animation<*, V>>) {

    private val animations: List<Animation<*, V>>
    private val listeners = ArrayList<Listener>()
    private var currentPlayTime: Long = 0

    val totalDuration: Long

    // Iterate backwards through the list and stop at the first
    // animation that has a start time less than or equal to the
    // current play time.
    private val currentAnimation: Animation<*, V>
        get() {
            // TODO: can this search be faster?
            val size = animations.size
            val lastAnimation = animations[size - 1]
            if (lastAnimation.startDelay <= currentPlayTime) {
                return lastAnimation
            }
            var animation = lastAnimation
            for (i in size - 1 downTo 0) {
                animation = animations[i]
                val startTime = animation.startDelay
                if (startTime <= currentPlayTime) {
                    break
                }
            }
            return animation
        }

    val animatedValue: V
        get() {
            val animation = currentAnimation
            return animation.getAnimatedValue(getInterpolatedCurrentAnimationFraction(animation))
        }

    init {
        // Sort the animations.
        this.animations = ArrayList(animations)
        Collections.sort(this.animations, ANIMATION_COMPARATOR)

        // Compute the total duration.
        var totalDuration: Long = 0
        run {
            var i = 0
            val size = this.animations.size
            while (i < size) {
                val currTotalDuration = this.animations[i].totalDuration
                if (currTotalDuration == Animation.INFINITE) {
                    totalDuration = Animation.INFINITE
                    break
                }
                totalDuration = Math.max(currTotalDuration, totalDuration)
                i++
            }
        }
        this.totalDuration = totalDuration

        // Fill in any missing start values.
        var prevAnimation: Animation<*, V>? = null
        var i = 0
        val size = this.animations.size
        while (i < size) {
            val currAnimation = this.animations[i]
            if (prevAnimation != null) {
                currAnimation.setupStartValue(prevAnimation.getAnimatedValue(1f))
            }
            prevAnimation = currAnimation
            i++
        }
    }

    fun setCurrentPlayTime(@IntRange(from = 0L) currentPlayTime: Long) {
        var currentPlayTime = currentPlayTime
        if (currentPlayTime < 0) {
            currentPlayTime = 0
        } else if (totalDuration != Animation.INFINITE && totalDuration < currentPlayTime) {
            currentPlayTime = totalDuration
        }
        if (this.currentPlayTime != currentPlayTime) {
            this.currentPlayTime = currentPlayTime
            // TODO: optimize this by notifying only when we know the computed value has changed
            // TODO: add a computeValue() method or something on Animation?
            notifyListeners()
        }
    }

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    private fun notifyListeners() {
        listeners.forEach { it.onCurrentPlayTimeChanged(this) }
    }

    /**
     * Returns the progress into the current animation between 0 and 1. This does not take into
     * account any interpolation that the animation may have.
     */
    private fun getLinearCurrentAnimationFraction(animation: Animation<*, V>): Float {
        val startTime = animation.startDelay.toFloat()
        val duration = animation.duration.toFloat()
        if (duration == 0f) {
            return 1f
        }
        val totalDuration = animation.totalDuration
        var currentPlayTime = this.currentPlayTime
        if (totalDuration != Animation.INFINITE) {
            // Don't let the current play time exceed the animation's total duration if it isn't infinite.
            currentPlayTime = Math.min(currentPlayTime, totalDuration)
        }
        val fraction = (currentPlayTime - startTime) / duration
        val currentIteration = getCurrentIteration(fraction)
        val repeatCount = animation.repeatCount
        val repeatMode = animation.repeatMode
        var currentFraction = fraction - currentIteration
        if (0 < currentIteration
                && repeatMode == RepeatMode.REVERSE
                && (currentIteration < repeatCount + 1 || repeatCount == Animation.INFINITE)) {
            // TODO: when reversing, check if currentIteration % 2 == 0 instead
            if (currentIteration % 2 != 0) {
                currentFraction = 1 - currentFraction
            }
        }
        return currentFraction
    }

    /**
     * Takes the value of [.getLinearCurrentAnimationFraction] and interpolates it
     * with the current animation's interpolator.
     */
    private fun getInterpolatedCurrentAnimationFraction(animation: Animation<*, V>): Float {
        var interpolator = animation.interpolator
        if (interpolator == null) {
            interpolator = DEFAULT_INTERPOLATOR
        }
        return interpolator.getInterpolation(getLinearCurrentAnimationFraction(animation))
    }

    interface Listener {
        fun onCurrentPlayTimeChanged(property: Property<*>)
    }

    companion object {
        private val DEFAULT_INTERPOLATOR = LinearInterpolator()
        private val ANIMATION_COMPARATOR = Comparator<Animation<*, *>> { a1, a2 ->
            // Animations with smaller start times are sorted first.
            val s1 = a1.startDelay
            val s2 = a2.startDelay
            if (s1 != s2) {
                return@Comparator if (s1 < s2) -1 else 1
            }
            val d1 = a1.totalDuration
            val d2 = a2.totalDuration
            if (d1 == Animation.INFINITE || d2 == Animation.INFINITE) {
                // Infinite animations are sorted last.
                return@Comparator if (d1 == d2) 0 else if (d1 == Animation.INFINITE) 1 else -1
            }
            // Animations with smaller end times are sorted first.
            val e1 = s1 + d1
            val e2 = s2 + d2

            if (e1 < e2) -1 else if (e1 > e2) 1 else 0
        }

        private fun getCurrentIteration(fraction: Float): Int {
            // If the overall fraction is a positive integer, we consider the current iteration to be
            // complete. In other words, the fraction for the current iteration would be 1, and the
            // current iteration would be overall fraction - 1.
            var iteration = Math.floor(fraction.toDouble()).toFloat()
            if (fraction == iteration && fraction > 0) {
                iteration--
            }
            return iteration.toInt()
        }
    }
}
