package com.github.alexjlockwood.kyrie

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import androidx.annotation.Size

/** Abstract base [Node] for all node types that can be transformed. */
abstract class TransformNode internal constructor(
        val rotation: List<Animation<*, Float>>,
        val pivotX: List<Animation<*, Float>>,
        val pivotY: List<Animation<*, Float>>,
        val scaleX: List<Animation<*, Float>>,
        val scaleY: List<Animation<*, Float>>,
        val translateX: List<Animation<*, Float>>,
        val translateY: List<Animation<*, Float>>
) : Node() {

    // <editor-fold desc="Layer">

    abstract override fun toLayer(timeline: PropertyTimeline): TransformLayer

    internal abstract class TransformLayer(private val timeline: PropertyTimeline, node: TransformNode) : Layer {
        private val rotation = registerAnimatableProperty(node.rotation)
        private val pivotX = registerAnimatableProperty(node.pivotX)
        private val pivotY = registerAnimatableProperty(node.pivotY)
        private val scaleX = registerAnimatableProperty(node.scaleX)
        private val scaleY = registerAnimatableProperty(node.scaleY)
        private val translateX = registerAnimatableProperty(node.translateX)
        private val translateY = registerAnimatableProperty(node.translateY)

        private val tempMatrix = Matrix()

        @Size(value = 4)
        private val tempUnitVectors = FloatArray(4)

        fun <V> registerAnimatableProperty(animations: List<Animation<*, V>>): Property<V> {
            return timeline.registerAnimatableProperty(animations)
        }

        override fun draw(canvas: Canvas, parentMatrix: Matrix, viewportScale: PointF) {
            val rotation = this.rotation.animatedValue
            val pivotX = this.pivotX.animatedValue
            val pivotY = this.pivotY.animatedValue
            val scaleX = this.scaleX.animatedValue
            val scaleY = this.scaleY.animatedValue
            val translateX = this.translateX.animatedValue
            val translateY = this.translateY.animatedValue
            tempMatrix.set(parentMatrix)
            if (translateX + pivotX != 0f || translateY + pivotY != 0f) {
                tempMatrix.preTranslate(translateX + pivotX, translateY + pivotY)
            }
            if (rotation != 0f) {
                tempMatrix.preRotate(rotation, 0f, 0f)
            }
            if (scaleX != 1f || scaleY != 1f) {
                tempMatrix.preScale(scaleX, scaleY)
            }
            if (pivotX != 0f || pivotY != 0f) {
                tempMatrix.preTranslate(-pivotX, -pivotY)
            }
            onDraw(canvas, tempMatrix, viewportScale)
        }

        fun getMatrixScale(matrix: Matrix): Float {
            // Given unit vectors A = (0, 1) and B = (1, 0).
            // After matrix mapping, we got A' and B'. Let theta = the angle b/t A' and B'.
            // Therefore, the final scale we want is min(|A'| * sin(theta), |B'| * sin(theta)),
            // which is (|A'| * |B'| * sin(theta)) / max (|A'|, |B'|);
            // If max (|A'|, |B'|) = 0, that means either x or y has a scale of 0.
            // For non-skew case, which is most of the cases, matrix scale is computing exactly the
            // scale on x and y axis, and take the minimal of these two.
            // For skew case, an unit square will mapped to a parallelogram. And this function will
            // return the minimal height of the 2 bases.
            val unitVectors = tempUnitVectors
            unitVectors[0] = 0f
            unitVectors[1] = 1f
            unitVectors[2] = 1f
            unitVectors[3] = 0f
            matrix.mapVectors(unitVectors)
            val scaleX = Math.hypot(unitVectors[0].toDouble(), unitVectors[1].toDouble()).toFloat()
            val scaleY = Math.hypot(unitVectors[2].toDouble(), unitVectors[3].toDouble()).toFloat()
            val crossProduct = cross(unitVectors[0], unitVectors[1], unitVectors[2], unitVectors[3])
            val maxScale = Math.max(scaleX, scaleY)
            return if (maxScale > 0) Math.abs(crossProduct) / maxScale else 0f
        }

        private fun cross(v1x: Float, v1y: Float, v2x: Float, v2y: Float): Float {
            return v1x * v2y - v1y * v2x
        }
    }

    // </editor-fold>

    // <editor-fold desc="Builder">

    @DslMarker
    private annotation class TransformNodeMarker

    @TransformNodeMarker
    abstract class Builder<B : Builder<B>> internal constructor() : Node.Builder<B>() {
        val rotation = asAnimations(0f)
        val pivotX = asAnimations(0f)
        val pivotY = asAnimations(0f)
        val scaleX = asAnimations(1f)
        val scaleY = asAnimations(1f)
        val translateX = asAnimations(0f)
        val translateY = asAnimations(0f)

        // Rotation.

        fun rotation(initialRotation: Float): B {
            return replaceFirstAnimation(rotation, asAnimation(initialRotation))
        }

        @SafeVarargs
        fun rotation(vararg animations: Animation<*, Float>): B {
            return replaceAnimations(rotation, *animations)
        }

        fun rotation(animations: List<Animation<*, Float>>): B {
            return replaceAnimations(rotation, animations)
        }

        // Pivot X.

        fun pivotX(initialPivotX: Float): B {
            return replaceFirstAnimation(pivotX, asAnimation(initialPivotX))
        }

        @SafeVarargs
        fun pivotX(vararg animations: Animation<*, Float>): B {
            return replaceAnimations(pivotX, *animations)
        }

        fun pivotX(animations: List<Animation<*, Float>>): B {
            return replaceAnimations(pivotX, animations)
        }

        // Pivot Y.

        fun pivotY(initialPivotY: Float): B {
            return replaceFirstAnimation(pivotY, asAnimation(initialPivotY))
        }

        @SafeVarargs
        fun pivotY(vararg animations: Animation<*, Float>): B {
            return replaceAnimations(pivotY, *animations)
        }

        fun pivotY(animations: List<Animation<*, Float>>): B {
            return replaceAnimations(pivotY, animations)
        }

        // Scale X.

        fun scaleX(initialScaleX: Float): B {
            return replaceFirstAnimation(scaleX, asAnimation(initialScaleX))
        }

        @SafeVarargs
        fun scaleX(vararg animations: Animation<*, Float>): B {
            return replaceAnimations(scaleX, *animations)
        }

        fun scaleX(animations: List<Animation<*, Float>>): B {
            return replaceAnimations(scaleX, animations)
        }

        // Scale Y.

        fun scaleY(initialScaleY: Float): B {
            return replaceFirstAnimation(scaleY, asAnimation(initialScaleY))
        }

        @SafeVarargs
        fun scaleY(vararg animations: Animation<*, Float>): B {
            return replaceAnimations(scaleY, *animations)
        }

        fun scaleY(animations: List<Animation<*, Float>>): B {
            return replaceAnimations(scaleY, animations)
        }

        // Translate X.

        fun translateX(initialTranslateX: Float): B {
            return replaceFirstAnimation(translateX, asAnimation(initialTranslateX))
        }

        @SafeVarargs
        fun translateX(vararg animations: Animation<*, Float>): B {
            return replaceAnimations(translateX, *animations)
        }

        fun translateX(animations: List<Animation<*, Float>>): B {
            return replaceAnimations(translateX, animations)
        }

        // Translate Y.

        fun translateY(initialTranslateY: Float): B {
            return replaceFirstAnimation(translateY, asAnimation(initialTranslateY))
        }

        @SafeVarargs
        fun translateY(vararg animations: Animation<*, Float>): B {
            return replaceAnimations(translateY, *animations)
        }

        fun translateY(animations: List<Animation<*, Float>>): B {
            return replaceAnimations(translateY, animations)
        }

        abstract override fun build(): TransformNode
    }

    // </editor-fold>
}
