package com.github.alexjlockwood.kyrie

import android.graphics.Path

/** A [Node] that paints a line. */
class LineNode private constructor(
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
        private val startX: List<Animation<*, Float>>,
        private val startY: List<Animation<*, Float>>,
        private val endX: List<Animation<*, Float>>,
        private val endY: List<Animation<*, Float>>
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

    override fun toLayer(timeline: PropertyTimeline): LineLayer {
        return LineLayer(timeline, this)
    }

    internal class LineLayer(timeline: PropertyTimeline, node: LineNode) : RenderNode.RenderLayer(timeline, node) {
        private val startX = registerAnimatableProperty(node.startX)
        private val startY = registerAnimatableProperty(node.startY)
        private val endX = registerAnimatableProperty(node.endX)
        private val endY = registerAnimatableProperty(node.endY)

        override fun onInitPath(outPath: Path) {
            val startX = this.startX.animatedValue
            val startY = this.startY.animatedValue
            val endX = this.endX.animatedValue
            val endY = this.endY.animatedValue
            outPath.moveTo(startX, startY)
            outPath.lineTo(endX, endY)
        }
    }

    // </editor-fold>

    // <editor-fold desc="Builder">

    @DslMarker
    private annotation class LineNodeMarker

    /** Builder class used to create [LineNode]s. */
    @LineNodeMarker
    class Builder internal constructor() : RenderNode.Builder<Builder>() {
        private val startX = asAnimations(0f)
        private val startY = asAnimations(0f)
        private val endX = asAnimations(0f)
        private val endY = asAnimations(0f)

        // Start X.

        fun startX(initialStartX: Float): Builder {
            return replaceFirstAnimation(startX, asAnimation(initialStartX))
        }

        @SafeVarargs
        fun startX(vararg animations: Animation<*, Float>): Builder {
            return replaceAnimations(startX, *animations)
        }

        fun startX(animations: List<Animation<*, Float>>): Builder {
            return replaceAnimations(startX, animations)
        }

        // Start Y.

        fun startY(initialStartY: Float): Builder {
            return replaceFirstAnimation(startY, asAnimation(initialStartY))
        }

        @SafeVarargs
        fun startY(vararg animations: Animation<*, Float>): Builder {
            return replaceAnimations(startY, *animations)
        }

        fun startY(animations: List<Animation<*, Float>>): Builder {
            return replaceAnimations(startY, animations)
        }

        // End X.

        fun endX(initialEndX: Float): Builder {
            return replaceFirstAnimation(endX, asAnimation(initialEndX))
        }

        @SafeVarargs
        fun endX(vararg animations: Animation<*, Float>): Builder {
            return replaceAnimations(endX, *animations)
        }

        fun endX(animations: List<Animation<*, Float>>): Builder {
            return replaceAnimations(endX, animations)
        }

        // End Y.

        fun endY(initialEndY: Float): Builder {
            return replaceFirstAnimation(endY, asAnimation(initialEndY))
        }

        @SafeVarargs
        fun endY(vararg animations: Animation<*, Float>): Builder {
            return replaceAnimations(endY, *animations)
        }

        fun endY(animations: List<Animation<*, Float>>): Builder {
            return replaceAnimations(endY, animations)
        }

        override val self = this

        override fun build(): LineNode {
            return LineNode(
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
                    startX,
                    startY,
                    endX,
                    endY
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
