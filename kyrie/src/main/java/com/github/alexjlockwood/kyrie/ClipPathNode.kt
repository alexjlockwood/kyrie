package com.github.alexjlockwood.kyrie

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Region

/**
 * A [Node] that defines a region to be clipped. Note that a [ClipPathNode] only clips
 * its sibling [Node]s.
 */
class ClipPathNode private constructor(
        rotation: List<Animation<*, Float>>,
        pivotX: List<Animation<*, Float>>,
        pivotY: List<Animation<*, Float>>,
        scaleX: List<Animation<*, Float>>,
        scaleY: List<Animation<*, Float>>,
        translateX: List<Animation<*, Float>>,
        translateY: List<Animation<*, Float>>,
        private val pathData: List<Animation<*, PathData>>,
        private val fillType: FillType,
        private val clipType: ClipType
) : TransformNode(rotation, pivotX, pivotY, scaleX, scaleY, translateX, translateY) {

    // <editor-fold desc="Layer">

    override fun toLayer(timeline: PropertyTimeline): ClipPathLayer {
        return ClipPathLayer(timeline, this)
    }

    internal class ClipPathLayer(timeline: PropertyTimeline, node: ClipPathNode) : TransformNode.TransformLayer(timeline, node) {
        private val pathData = registerAnimatableProperty(node.pathData)
        private val fillType = node.fillType
        private val clipType = node.clipType

        private val tempMatrix = Matrix()
        private val tempPath = Path()
        private val tempRenderPath = Path()

        override fun onDraw(canvas: Canvas, parentMatrix: Matrix, viewportScale: PointF) {
            val matrixScale = getMatrixScale(parentMatrix)
            if (matrixScale == 0f) {
                return
            }

            val scaleX = viewportScale.x
            val scaleY = viewportScale.y
            tempMatrix.set(parentMatrix)
            if (scaleX != 1f || scaleY != 1f) {
                tempMatrix.postScale(scaleX, scaleY)
            }

            tempRenderPath.reset()
            tempPath.reset()
            PathData.toPath(pathData.animatedValue, tempPath)
            tempRenderPath.addPath(tempPath, tempMatrix)
            tempRenderPath.fillType = getPaintFillType(fillType)
            if (clipType == ClipType.INTERSECT) {
                canvas.clipPath(tempRenderPath)
            } else {
                canvas.clipPath(tempRenderPath, Region.Op.DIFFERENCE)
            }
        }

        private fun getPaintFillType(fillType: FillType): Path.FillType {
            return when (fillType) {
                FillType.NON_ZERO -> Path.FillType.WINDING
                FillType.EVEN_ODD -> Path.FillType.EVEN_ODD
            }
        }

        override fun isStateful(): Boolean {
            return false
        }

        override fun onStateChange(stateSet: IntArray): Boolean {
            return false
        }
    }

    // </editor-fold>

    // <editor-fold desc="Builder">

    @DslMarker
    private annotation class ClipPathNodeMarker

    /** Builder class used to create [ClipPathNode]s. */
    @ClipPathNodeMarker
    class Builder internal constructor() : TransformNode.Builder<Builder>() {
        private val pathData = asAnimations(PathData())
        private var fillType = FillType.NON_ZERO
        private var clipType = ClipType.INTERSECT

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

        // Fill type.

        fun fillType(fillType: FillType): Builder {
            this.fillType = fillType
            return self
        }

        // Clip type.

        fun clipType(clipType: ClipType): Builder {
            this.clipType = clipType
            return self
        }

        override val self = this

        override fun build(): ClipPathNode {
            return ClipPathNode(
                    rotation,
                    pivotX,
                    pivotY,
                    scaleX,
                    scaleY,
                    translateX,
                    translateY,
                    pathData,
                    fillType,
                    clipType
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
