package com.github.alexjlockwood.kyrie

import android.graphics.Path
import android.graphics.RectF
import androidx.annotation.FloatRange

/** A [Node] that paints a rectangle. */
class RectangleNode private constructor(
        rotation: List<Animation<*, Float>>,
        pivotX: List<Animation<*, Float>>,
        pivotY: List<Animation<*, Float>>,
        scaleX: List<Animation<*, Float>>,
        scaleY: List<Animation<*, Float>>,
        translateX: List<Animation<*, Float>>,
        translateY: List<Animation<*, Float>>,
        fillColor: List<Animation<*, Int>>,
        fillColorComplex: ComplexColor?,
        fillAlpha: List<Animation<*, Float>>,
        strokeColor: List<Animation<*, Int>>,
        strokeColorComplex: ComplexColor?,
        strokeAlpha: List<Animation<*, Float>>,
        strokeWidth: List<Animation<*, Float>>,
        trimPathStart: List<Animation<*, Float>>,
        trimPathEnd: List<Animation<*, Float>>,
        trimPathOffset: List<Animation<*, Float>>,
        strokeLineCap: StrokeLineCap,
        strokeLineJoin: StrokeLineJoin,
        strokeMiterLimit: List<Animation<*, Float>>,
        strokeDashArray: List<Animation<*, FloatArray>>,
        strokeDashOffset: List<Animation<*, Float>>,
        fillType: FillType,
        isStrokeScaling: Boolean,
        private val x: List<Animation<*, Float>>,
        private val y: List<Animation<*, Float>>,
        private val width: List<Animation<*, Float>>,
        private val height: List<Animation<*, Float>>,
        private val cornerRadiusX: List<Animation<*, Float>>,
        private val cornerRadiusY: List<Animation<*, Float>>
) : RenderNode(
        rotation,
        pivotX,
        pivotY,
        scaleX,
        scaleY,
        translateX,
        translateY,
        fillColor,
        fillColorComplex,
        fillAlpha,
        strokeColor,
        strokeColorComplex,
        strokeAlpha,
        strokeWidth,
        trimPathStart,
        trimPathEnd,
        trimPathOffset,
        strokeLineCap,
        strokeLineJoin,
        strokeMiterLimit,
        strokeDashArray,
        strokeDashOffset,
        fillType,
        isStrokeScaling
) {

    // <editor-fold desc="Layer">

    override fun toLayer(timeline: PropertyTimeline): RectangleLayer {
        return RectangleLayer(timeline, this)
    }

    internal class RectangleLayer(timeline: PropertyTimeline, node: RectangleNode) : RenderNode.RenderLayer(timeline, node) {
        private val x = registerAnimatableProperty(node.x)
        private val y = registerAnimatableProperty(node.y)
        private val width = registerAnimatableProperty(node.width)
        private val height = registerAnimatableProperty(node.height)
        private val cornerRadiusX = registerAnimatableProperty(node.cornerRadiusX)
        private val cornerRadiusY = registerAnimatableProperty(node.cornerRadiusY)

        private val tempRect = RectF()

        override fun onInitPath(outPath: Path) {
            val l = x.animatedValue
            val t = y.animatedValue
            val r = l + width.animatedValue
            val b = t + height.animatedValue
            val rx = cornerRadiusX.animatedValue
            val ry = cornerRadiusY.animatedValue
            tempRect.set(l, t, r, b)
            outPath.addRoundRect(tempRect, rx, ry, Path.Direction.CW)
        }
    }

    // </editor-fold>

    // <editor-fold desc="Builder">

    @DslMarker
    private annotation class RectangleNodeMarker

    /** Builder class used to create [RectangleNode]s. */
    @RectangleNodeMarker
    class Builder internal constructor() : RenderNode.Builder<Builder>() {
        private val x = asAnimations(0f)
        private val y = asAnimations(0f)
        private val width = asAnimations(0f)
        private val height = asAnimations(0f)
        private val cornerRadiusX = asAnimations(0f)
        private val cornerRadiusY = asAnimations(0f)

        // X.

        fun x(initialX: Float): Builder {
            return replaceFirstAnimation(x, asAnimation(initialX))
        }

        @SafeVarargs
        fun x(vararg animations: Animation<*, Float>): Builder {
            return replaceAnimations(x, *animations)
        }

        fun x(animations: List<Animation<*, Float>>): Builder {
            return replaceAnimations(x, animations)
        }

        // Y.

        fun y(initialY: Float): Builder {
            return replaceFirstAnimation(y, asAnimation(initialY))
        }

        @SafeVarargs
        fun y(vararg animations: Animation<*, Float>): Builder {
            return replaceAnimations(y, *animations)
        }

        fun y(animations: List<Animation<*, Float>>): Builder {
            return replaceAnimations(y, animations)
        }

        // Width.

        fun width(@FloatRange(from = 0.0) initialWidth: Float): Builder {
            return replaceFirstAnimation(width, asAnimation(initialWidth))
        }

        @SafeVarargs
        fun width(vararg animations: Animation<*, Float>): Builder {
            return replaceAnimations(width, *animations)
        }

        fun width(animations: List<Animation<*, Float>>): Builder {
            return replaceAnimations(width, animations)
        }

        // Height.

        fun height(@FloatRange(from = 0.0) initialHeight: Float): Builder {
            return replaceFirstAnimation(height, asAnimation(initialHeight))
        }

        @SafeVarargs
        fun height(vararg animations: Animation<*, Float>): Builder {
            return replaceAnimations(height, *animations)
        }

        fun height(animations: List<Animation<*, Float>>): Builder {
            return replaceAnimations(height, animations)
        }

        // Corner radius X.

        fun cornerRadiusX(@FloatRange(from = 0.0) initialCornerRadiusX: Float): Builder {
            return replaceFirstAnimation(cornerRadiusX, asAnimation(initialCornerRadiusX))
        }

        @SafeVarargs
        fun cornerRadiusX(vararg animations: Animation<*, Float>): Builder {
            return replaceAnimations(cornerRadiusX, *animations)
        }

        fun cornerRadiusX(animations: List<Animation<*, Float>>): Builder {
            return replaceAnimations(cornerRadiusX, animations)
        }

        // Corner radius Y.

        fun cornerRadiusY(@FloatRange(from = 0.0) initialCornerRadiusY: Float): Builder {
            return replaceFirstAnimation(cornerRadiusY, asAnimation(initialCornerRadiusY))
        }

        @SafeVarargs
        fun cornerRadiusY(vararg animations: Animation<*, Float>): Builder {
            return replaceAnimations(cornerRadiusY, *animations)
        }

        fun cornerRadiusY(animations: List<Animation<*, Float>>): Builder {
            return replaceAnimations(cornerRadiusY, animations)
        }

        override val self = this

        override fun build(): RectangleNode {
            return RectangleNode(
                    rotation,
                    pivotX,
                    pivotY,
                    scaleX,
                    scaleY,
                    translateX,
                    translateY,
                    fillColor,
                    fillColorComplex,
                    fillAlpha,
                    strokeColor,
                    strokeColorComplex,
                    strokeAlpha,
                    strokeWidth,
                    trimPathStart,
                    trimPathEnd,
                    trimPathOffset,
                    strokeLineCap,
                    strokeLineJoin,
                    strokeMiterLimit,
                    strokeDashArray,
                    strokeDashOffset,
                    fillType,
                    isScalingStroke,
                    x,
                    y,
                    width,
                    height,
                    cornerRadiusX,
                    cornerRadiusY
            )
        }
    }

    // </editor-fold>

    companion object {

        @JvmStatic
        fun builder(): Builder {
            return Builder()
        }
    }
}
