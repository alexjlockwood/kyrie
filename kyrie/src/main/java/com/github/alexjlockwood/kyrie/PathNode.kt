package com.github.alexjlockwood.kyrie

import android.graphics.Path

/** A [Node] that paints a path. */
class PathNode private constructor(
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
        private val pathData: List<Animation<*, PathData>>
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

    override fun toLayer(timeline: PropertyTimeline): PathLayer {
        return PathLayer(timeline, this)
    }

    internal class PathLayer(timeline: PropertyTimeline, node: PathNode) : RenderNode.RenderLayer(timeline, node) {
        private val pathData = registerAnimatableProperty(node.pathData)

        override fun onInitPath(outPath: Path) {
            PathData.toPath(pathData.animatedValue, outPath)
        }
    }

    // </editor-fold>

    // <editor-fold desc="Builder">

    @DslMarker
    private annotation class PathNodeMarker

    /** Builder class used to create [PathNode]s. */
    @PathNodeMarker
    class Builder internal constructor() : RenderNode.Builder<Builder>() {
        private val pathData = asAnimations(PathData())

        // Path data.

        fun pathData(initialPathData: String): Builder {
            return pathData(PathData.parse(initialPathData))
        }

        fun pathData(initialPathData: PathData): Builder {
            return replaceFirstAnimation(pathData, asAnimation(initialPathData))
        }

        @SafeVarargs
        fun pathData(vararg animations: Animation<*, PathData>): Builder {
            return replaceAnimations(pathData, *animations)
        }

        fun pathData(animations: List<Animation<*, PathData>>): Builder {
            return replaceAnimations(pathData, animations)
        }

        override val self = this

        override fun build(): PathNode {
            return PathNode(
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
                    pathData
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
