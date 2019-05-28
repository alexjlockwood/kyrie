package com.github.alexjlockwood.kyrie

import android.graphics.Path
import android.graphics.RectF
import androidx.annotation.FloatRange

/** A [Node] that paints an ellipse. */
class EllipseNode private constructor(
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
        private val radiusX: List<Animation<*, Float>>,
        private val radiusY: List<Animation<*, Float>>
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

    override fun toLayer(timeline: PropertyTimeline): EllipseLayer {
        return EllipseLayer(timeline, this)
    }

    internal class EllipseLayer(timeline: PropertyTimeline, node: EllipseNode) : RenderNode.RenderLayer(timeline, node) {
        private val centerX = registerAnimatableProperty(node.centerX)
        private val centerY = registerAnimatableProperty(node.centerY)
        private val radiusX = registerAnimatableProperty(node.radiusX)
        private val radiusY = registerAnimatableProperty(node.radiusY)

        private val tempRect = RectF()

        override fun onInitPath(outPath: Path) {
            val cx = centerX.animatedValue
            val cy = centerY.animatedValue
            val rx = radiusX.animatedValue
            val ry = radiusY.animatedValue
            tempRect.set(cx - rx, cy - ry, cx + rx, cy + ry)
            outPath.addOval(tempRect, Path.Direction.CW)
        }
    }

    // </editor-fold>

    // <editor-fold desc="Builder">

    @DslMarker
    private annotation class EllipseNodeMarker

    /** Builder class used to create [EllipseNode]s. */
    @EllipseNodeMarker
    class Builder internal constructor() : RenderNode.Builder<Builder>() {
        private val centerX = asAnimations(0f)
        private val centerY = asAnimations(0f)
        private val radiusX = asAnimations(0f)
        private val radiusY = asAnimations(0f)

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

        // Radius X.

        fun radiusX(@FloatRange(from = 0.0) initialRadiusX: Float): Builder {
            return replaceFirstAnimation(radiusX, asAnimation(initialRadiusX))
        }

        @SafeVarargs
        fun radiusX(vararg animations: Animation<*, Float>): Builder {
            return replaceAnimations(radiusX, *animations)
        }

        fun radiusX(animations: List<Animation<*, Float>>): Builder {
            return replaceAnimations(radiusX, animations)
        }

        // Radius Y.

        fun radiusY(@FloatRange(from = 0.0) initialRadiusY: Float): Builder {
            return replaceFirstAnimation(radiusY, asAnimation(initialRadiusY))
        }

        @SafeVarargs
        fun radiusY(vararg animations: Animation<*, Float>): Builder {
            return replaceAnimations(radiusY, *animations)
        }

        fun radiusY(animations: List<Animation<*, Float>>): Builder {
            return replaceAnimations(radiusY, animations)
        }

        override val self = this

        override fun build(): EllipseNode {
            return EllipseNode(
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
                    radiusX,
                    radiusY
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
