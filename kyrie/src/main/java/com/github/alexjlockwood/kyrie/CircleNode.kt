package com.github.alexjlockwood.kyrie

import android.graphics.Path
import android.graphics.RectF
import androidx.annotation.FloatRange

/** A [Node] that paints a circle. */
class CircleNode private constructor(
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
        private val centerX: List<Animation<*, Float>>,
        private val centerY: List<Animation<*, Float>>,
        private val radius: List<Animation<*, Float>>
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

    override fun toLayer(timeline: PropertyTimeline): CircleLayer {
        return CircleLayer(timeline, this)
    }

    internal class CircleLayer(timeline: PropertyTimeline, node: CircleNode) : RenderNode.RenderLayer(timeline, node) {
        private val centerX = registerAnimatableProperty(node.centerX)
        private val centerY = registerAnimatableProperty(node.centerY)
        private val radius = registerAnimatableProperty(node.radius)

        private val tempRect = RectF()

        override fun onInitPath(outPath: Path) {
            val cx = centerX.animatedValue
            val cy = centerY.animatedValue
            val r = radius.animatedValue
            tempRect.set(cx - r, cy - r, cx + r, cy + r)
            outPath.addOval(tempRect, Path.Direction.CW)
        }
    }

    // </editor-fold>

    // <editor-fold desc="Builder">

    @DslMarker
    private annotation class CircleNodeMarker

    /** Builder class used to create [CircleNode]s. */
    @CircleNodeMarker
    class Builder internal constructor() : RenderNode.Builder<Builder>() {
        private val centerX = asAnimations(0f)
        private val centerY = asAnimations(0f)
        private val radius = asAnimations(0f)

        // Center X.

        fun centerX(initialCenterX: Float): Builder {
            return replaceFirstAnimation(centerX, asAnimation(initialCenterX))
        }

        @SafeVarargs
        fun centerX(vararg animations: Animation<*, Float>): Builder {
            return replaceAnimations(centerX, *animations)
        }

        fun centerX(animations: List<Animation<*, Float>>): Builder {
            return replaceAnimations(centerX, animations)
        }

        // Center Y.

        fun centerY(initialCenterY: Float): Builder {
            return replaceFirstAnimation(centerY, asAnimation(initialCenterY))
        }

        @SafeVarargs
        fun centerY(vararg animations: Animation<*, Float>): Builder {
            return replaceAnimations(centerY, *animations)
        }

        fun centerY(animations: List<Animation<*, Float>>): Builder {
            return replaceAnimations(centerY, animations)
        }

        // Radius.

        fun radius(@FloatRange(from = 0.0) initialRadius: Float): Builder {
            return replaceFirstAnimation(radius, asAnimation(initialRadius))
        }

        @SafeVarargs
        fun radius(vararg animations: Animation<*, Float>): Builder {
            return replaceAnimations(radius, *animations)
        }

        fun radius(animations: List<Animation<*, Float>>): Builder {
            return replaceAnimations(radius, animations)
        }

        override val self = this

        override fun build(): CircleNode {
            return CircleNode(
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
                    centerX,
                    centerY,
                    radius
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
