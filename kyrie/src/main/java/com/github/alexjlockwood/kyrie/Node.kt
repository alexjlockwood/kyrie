package com.github.alexjlockwood.kyrie

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import androidx.annotation.ColorInt

import java.util.ArrayList
import java.util.Collections

/** Base class for all [Node]s used to construct and animate a [KyrieDrawable].  */
abstract class Node internal constructor() {

    /**
     * Constructs a [Layer] using the information contained by this [Node].
     *
     * @param timeline The [PropertyTimeline] to use to register property animations.
     * @return A new [Layer] representing this [Node].
     */
    internal abstract fun toLayer(timeline: PropertyTimeline): Layer

    internal interface Layer {
        fun draw(canvas: Canvas, parentMatrix: Matrix, viewportScale: PointF)

        fun onDraw(canvas: Canvas, parentMatrix: Matrix, viewportScale: PointF)
    }

    @DslMarker
    private annotation class NodeMarker

    /**
     * Base class for all [Node.Builder]s used to construct new [Node] instances.
     *
     * @param <B> The concrete builder subclass type.
     */
    @NodeMarker
    abstract class Builder<B : Builder<B>> internal constructor() {
        internal val self: B

        init {
            this.self = self()
        }

        internal abstract fun self(): B

        internal abstract fun build(): Node

        internal fun <T> replaceFirstAnimation(animations: MutableList<Animation<*, T>>, animation: Animation<*, T>): B {
            Node.replaceFirstAnimation(animations, animation)
            return self
        }

        @SafeVarargs
        internal fun <T> replaceAnimations(
                animations: MutableList<Animation<*, T>>, vararg newAnimations: Animation<*, T>): B {
            Node.replaceAnimations(animations, *newAnimations)
            return self
        }

        internal fun <T> replaceAnimations(
                animations: MutableList<Animation<*, T>>, newAnimations: List<Animation<*, T>>): B {
            Node.replaceAnimations(animations, newAnimations)
            return self
        }
    }

    internal companion object {

        @JvmStatic
        internal fun asAnimation(initialValue: Float): Animation<*, Float> {
            return Animation.ofFloat(initialValue, initialValue).duration(0)
        }

        @JvmStatic
        internal fun asAnimation(@ColorInt initialValue: Int): Animation<*, Int> {
            return Animation.ofArgb(initialValue, initialValue).duration(0)
        }

        @JvmStatic
        internal fun asAnimation(initialValue: FloatArray): Animation<*, FloatArray> {
            return Animation.ofFloatArray(initialValue, initialValue).duration(0)
        }

        @JvmStatic
        internal fun asAnimation(initialValue: PathData): Animation<*, PathData> {
            return Animation.ofPathMorph(initialValue, initialValue).duration(0)
        }

        @JvmStatic
        internal fun asAnimations(initialValue: Float): MutableList<Animation<*, Float>> {
            return asList(asAnimation(initialValue))
        }

        @JvmStatic
        internal fun asAnimations(initialValue: Int): MutableList<Animation<*, Int>> {
            return asList(asAnimation(initialValue))
        }

        @JvmStatic
        internal fun asAnimations(initialValue: FloatArray): MutableList<Animation<*, FloatArray>> {
            return asList(asAnimation(initialValue))
        }

        @JvmStatic
        internal fun asAnimations(initialValue: PathData): MutableList<Animation<*, PathData>> {
            return asList(asAnimation(initialValue))
        }

        @JvmStatic
        private fun <T> asList(animation: Animation<*, T>): MutableList<Animation<*, T>> {
            val animations = ArrayList<Animation<*, T>>(1)
            animations.add(animation)
            return animations
        }

        @JvmStatic
        internal fun <T> replaceFirstAnimation(
                animations: MutableList<Animation<*, T>>, animation: Animation<*, T>) {
            animations[0] = animation
        }

        @JvmStatic
        @SafeVarargs
        internal fun <T> replaceAnimations(
                animations: MutableList<Animation<*, T>>, vararg newAnimations: Animation<*, T>) {
            for (i in animations.size - 1 downTo 1) {
                animations.removeAt(i)
            }
            Collections.addAll(animations, *newAnimations)
        }

        @JvmStatic
        internal fun <T> replaceAnimations(
                animations: MutableList<Animation<*, T>>, newAnimations: List<Animation<*, T>>) {
            for (i in animations.size - 1 downTo 1) {
                animations.removeAt(i)
            }
            animations.addAll(newAnimations)
        }
    }
}
