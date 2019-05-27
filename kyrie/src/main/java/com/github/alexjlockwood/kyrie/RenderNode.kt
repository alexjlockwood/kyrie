package com.github.alexjlockwood.kyrie

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.graphics.RadialGradient
import android.graphics.SweepGradient
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange

/** Abstract base [Node] for all node types that paint to the canvas. */
abstract class RenderNode internal constructor(
        rotation: List<Animation<*, Float>>,
        pivotX: List<Animation<*, Float>>,
        pivotY: List<Animation<*, Float>>,
        scaleX: List<Animation<*, Float>>,
        scaleY: List<Animation<*, Float>>,
        translateX: List<Animation<*, Float>>,
        translateY: List<Animation<*, Float>>,
        private val fillColor: List<Animation<*, Int>>,
        private val fillColorComplex: ComplexColor?,
        private val fillAlpha: List<Animation<*, Float>>,
        private val strokeColor: List<Animation<*, Int>>,
        private val strokeColorComplex: ComplexColor?,
        private val strokeAlpha: List<Animation<*, Float>>,
        private val strokeWidth: List<Animation<*, Float>>,
        private val trimPathStart: List<Animation<*, Float>>,
        private val trimPathEnd: List<Animation<*, Float>>,
        private val trimPathOffset: List<Animation<*, Float>>,
        private val strokeLineCap: StrokeLineCap,
        private val strokeLineJoin: StrokeLineJoin,
        private val strokeMiterLimit: List<Animation<*, Float>>,
        private val strokeDashArray: List<Animation<*, FloatArray>>,
        private val strokeDashOffset: List<Animation<*, Float>>,
        private val fillType: FillType,
        private val isScalingStroke: Boolean
) : TransformNode(rotation, pivotX, pivotY, scaleX, scaleY, translateX, translateY) {

    // <editor-fold desc="Layer">

    abstract override fun toLayer(timeline: PropertyTimeline): RenderLayer

    internal abstract class RenderLayer(timeline: PropertyTimeline, node: RenderNode) : TransformNode.TransformLayer(timeline, node) {
        private val fillColor = registerAnimatableProperty(node.fillColor)
        private val fillColorComplex = node.fillColorComplex
        private val fillAlpha = registerAnimatableProperty(node.fillAlpha)
        private val strokeColor = registerAnimatableProperty(node.strokeColor)
        private val strokeColorComplex = node.strokeColorComplex
        private val strokeAlpha = registerAnimatableProperty(node.strokeAlpha)
        private val strokeWidth = registerAnimatableProperty(node.strokeWidth)
        private val trimPathStart = registerAnimatableProperty(node.trimPathStart)
        private val trimPathEnd = registerAnimatableProperty(node.trimPathEnd)
        private val trimPathOffset = registerAnimatableProperty(node.trimPathOffset)
        private val strokeLineCap = node.strokeLineCap
        private val strokeLineJoin = node.strokeLineJoin
        private val strokeMiterLimit = registerAnimatableProperty(node.strokeMiterLimit)
        private val strokeDashArray = registerAnimatableProperty(node.strokeDashArray)
        private val strokeDashOffset = registerAnimatableProperty(node.strokeDashOffset)
        private val fillType = node.fillType
        private val isStrokeScaling = node.isScalingStroke

        private val tempMatrix = Matrix()
        private val tempPath = Path()
        private val tempRenderPath = Path()
        private var tempStrokePaint: Paint? = null
        private var tempFillPaint: Paint? = null
        private var tempPathMeasure: PathMeasure? = null
        private var tempStrokeDashArray: FloatArray? = null

        abstract fun onInitPath(outPath: Path)

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

            tempPath.reset()
            onInitPath(tempPath)
            applyTrimPathIfNeeded(tempPath)
            tempRenderPath.reset()
            tempRenderPath.addPath(tempPath, tempMatrix)
            drawFillIfNeeded(canvas, tempRenderPath, tempMatrix)
            val strokeScaleFactor = Math.min(scaleX, scaleY) * if (isStrokeScaling) matrixScale else 1f
            drawStrokeIfNeeded(canvas, tempRenderPath, tempMatrix, strokeScaleFactor)
        }

        private fun applyTrimPathIfNeeded(outPath: Path) {
            val trimPathStart = this.trimPathStart.animatedValue
            val trimPathEnd = this.trimPathEnd.animatedValue
            val trimPathOffset = this.trimPathOffset.animatedValue
            if (trimPathStart == 0f && trimPathEnd == 1f) {
                return
            }
            var start = (trimPathStart + trimPathOffset) % 1f
            var end = (trimPathEnd + trimPathOffset) % 1f
            if (tempPathMeasure == null) {
                tempPathMeasure = PathMeasure()
            }
            tempPathMeasure!!.setPath(outPath, false)
            val len = tempPathMeasure!!.length
            start *= len
            end *= len
            outPath.reset()
            if (start > end) {
                tempPathMeasure!!.getSegment(start, len, outPath, true)
                tempPathMeasure!!.getSegment(0f, end, outPath, true)
            } else {
                tempPathMeasure!!.getSegment(start, end, outPath, true)
            }
            // Required for Android 4.4 and earlier.
            outPath.rLineTo(0f, 0f)
        }

        private fun drawFillIfNeeded(canvas: Canvas, path: Path, localMatrix: Matrix) {
            val fillColorComplex = fillColorComplex
            val fillColor = this.fillColor.animatedValue
            if ((fillColorComplex == null || !fillColorComplex.willDraw()) && fillColor == Color.TRANSPARENT) {
                return
            }
            if (tempFillPaint == null) {
                tempFillPaint = Paint()
                tempFillPaint!!.style = Paint.Style.FILL
                tempFillPaint!!.isAntiAlias = true
            }
            val paint = tempFillPaint!!
            if (fillColorComplex != null && fillColorComplex.isGradient) {
                val shader = fillColorComplex.shader!!
                shader.setLocalMatrix(localMatrix)
                paint.shader = shader
                paint.alpha = Math.round(fillAlpha.animatedValue * 255f)
            } else {
                paint.shader = null
                paint.alpha = 255
                paint.color = if (fillColorComplex != null) {
                    applyAlpha(fillColorComplex.color, fillAlpha.animatedValue)
                } else {
                    applyAlpha(fillColor, fillAlpha.animatedValue)
                }
            }
            path.fillType = getPaintFillType(fillType)
            canvas.drawPath(path, paint)
        }

        private fun drawStrokeIfNeeded(canvas: Canvas, path: Path, localMatrix: Matrix, strokeScaleFactor: Float) {
            val strokeColorComplex = strokeColorComplex
            val strokeColor = this.strokeColor.animatedValue
            val strokeWidth = this.strokeWidth.animatedValue
            if (strokeWidth == 0f) {
                return
            }
            if ((strokeColorComplex == null || !strokeColorComplex.willDraw()) && strokeColor == Color.TRANSPARENT) {
                return
            }
            if (tempStrokePaint == null) {
                tempStrokePaint = Paint()
                tempStrokePaint!!.style = Paint.Style.STROKE
                tempStrokePaint!!.isAntiAlias = true
            }
            val paint = tempStrokePaint!!
            paint.strokeCap = getPaintStrokeLineCap(strokeLineCap)
            paint.strokeJoin = getPaintStrokeLineJoin(strokeLineJoin)
            paint.strokeMiter = strokeMiterLimit.animatedValue
            paint.strokeWidth = strokeWidth * strokeScaleFactor
            // TODO: can/should we cache path effects?
            paint.pathEffect = getDashPathEffect(strokeScaleFactor)

            if (strokeColorComplex != null && strokeColorComplex.isGradient) {
                val shader = strokeColorComplex.shader!!
                shader.setLocalMatrix(localMatrix)
                paint.shader = shader
                paint.alpha = Math.round((strokeAlpha.animatedValue * 255f))
            } else {
                paint.shader = null
                paint.alpha = 255
                paint.color = if (strokeColorComplex != null) {
                    applyAlpha(strokeColorComplex.color, strokeAlpha.animatedValue)
                } else {
                    applyAlpha(strokeColor, strokeAlpha.animatedValue)
                }
            }

            canvas.drawPath(path, paint)
        }

        private fun getDashPathEffect(strokeScaleFactor: Float): DashPathEffect? {
            val strokeDashArray = this.strokeDashArray.animatedValue
            if (strokeDashArray.isEmpty()) {
                return null
            }
            // DashPathEffect throws an exception if the dash array is odd in length,
            // so double the size of the array if this is the case.
            val initialSize = strokeDashArray.size
            val expansionFactor = if (initialSize % 2 == 0) 1 else 2
            val requiredSize = initialSize * expansionFactor
            if (tempStrokeDashArray == null || tempStrokeDashArray!!.size != requiredSize) {
                tempStrokeDashArray = FloatArray(requiredSize)
            }
            val tempStrokeDashArray = tempStrokeDashArray!!
            for (i in 0 until initialSize) {
                tempStrokeDashArray[i] = strokeDashArray[i] * strokeScaleFactor
            }
            System.arraycopy(tempStrokeDashArray, 0, tempStrokeDashArray, initialSize, requiredSize - initialSize)
            val strokeDashOffset = this.strokeDashOffset.animatedValue
            return DashPathEffect(tempStrokeDashArray, strokeDashOffset)
        }

        @ColorInt
        private fun applyAlpha(@ColorInt color: Int, alpha: Float): Int {
            var c = color
            val alphaBytes = Color.alpha(c)
            c = c and 0x00FFFFFF
            c = c or ((alphaBytes * alpha).toInt() shl 24)
            return c
        }

        private fun getPaintStrokeLineCap(strokeLineCap: StrokeLineCap): Paint.Cap {
            return when (strokeLineCap) {
                StrokeLineCap.BUTT -> Paint.Cap.BUTT
                StrokeLineCap.ROUND -> Paint.Cap.ROUND
                StrokeLineCap.SQUARE -> Paint.Cap.SQUARE
            }
        }

        private fun getPaintStrokeLineJoin(strokeLineJoin: StrokeLineJoin): Paint.Join {
            return when (strokeLineJoin) {
                StrokeLineJoin.MITER -> Paint.Join.MITER
                StrokeLineJoin.ROUND -> Paint.Join.ROUND
                StrokeLineJoin.BEVEL -> Paint.Join.BEVEL
            }
        }

        private fun getPaintFillType(fillType: FillType): Path.FillType {
            return when (fillType) {
                FillType.NON_ZERO -> Path.FillType.WINDING
                FillType.EVEN_ODD -> Path.FillType.EVEN_ODD
            }
        }

        override fun isStateful(): Boolean {
            return (fillColorComplex?.isStateful ?: false)
                    || (strokeColorComplex?.isStateful ?: false)
        }

        override fun onStateChange(stateSet: IntArray): Boolean {
            var changed = fillColorComplex?.onStateChanged(stateSet) ?: false
            changed = changed || strokeColorComplex?.onStateChanged(stateSet) ?: false
            return changed
        }
    }

    // </editor-fold>

    // <editor-fold desc="Builder">

    abstract class Builder<B : Builder<B>> internal constructor() : TransformNode.Builder<B>() {
        internal val fillColor = asAnimations(Color.TRANSPARENT)
        internal var fillColorComplex: ComplexColor? = null
        internal val fillAlpha = asAnimations(1f)
        internal val strokeColor = asAnimations(Color.TRANSPARENT)
        internal var strokeColorComplex: ComplexColor? = null
        internal val strokeAlpha = asAnimations(1f)
        internal val strokeWidth = asAnimations(0f)
        internal val trimPathStart = asAnimations(0f)
        internal val trimPathEnd = asAnimations(1f)
        internal val trimPathOffset = asAnimations(0f)
        internal var strokeLineCap = StrokeLineCap.BUTT
        internal var strokeLineJoin = StrokeLineJoin.MITER
        internal val strokeMiterLimit = asAnimations(4f)
        internal val strokeDashArray = asAnimations(FloatArray(0))
        internal val strokeDashOffset = asAnimations(0f)
        internal var fillType = FillType.NON_ZERO
        internal var isScalingStroke = true

        // Fill color.

        fun fillColor(@ColorInt initialFillColor: Int): B {
            return replaceFirstAnimation(fillColor, asAnimation(initialFillColor))
        }

        @SafeVarargs
        fun fillColor(vararg animations: Animation<*, Int>): B {
            return replaceAnimations(fillColor, *animations)
        }

        fun fillColor(animations: List<Animation<*, Int>>): B {
            return replaceAnimations(fillColor, animations)
        }

        fun fillColor(colorStateList: ColorStateList?): B {
            return fillColorComplex(if (colorStateList == null) null else ComplexColor.from(colorStateList))
        }

        fun fillColor(linearGradient: LinearGradient?): B {
            return fillColorComplex(if (linearGradient == null) null else ComplexColor.from(linearGradient))
        }

        fun fillColor(radialGradient: RadialGradient?): B {
            return fillColorComplex(if (radialGradient == null) null else ComplexColor.from(radialGradient))
        }

        fun fillColor(sweepGradient: SweepGradient?): B {
            return fillColorComplex(if (sweepGradient == null) null else ComplexColor.from(sweepGradient))
        }

        private fun fillColorComplex(complexColor: ComplexColor?): B {
            this.fillColorComplex = complexColor
            return self
        }

        // Fill alpha.

        fun fillAlpha(@FloatRange(from = 0.0, to = 1.0) initialFillAlpha: Float): B {
            return replaceFirstAnimation(fillAlpha, asAnimation(initialFillAlpha))
        }

        @SafeVarargs
        fun fillAlpha(vararg animations: Animation<*, Float>): B {
            return replaceAnimations(fillAlpha, *animations)
        }

        fun fillAlpha(animations: List<Animation<*, Float>>): B {
            return replaceAnimations(fillAlpha, animations)
        }

        // Stroke color.

        fun strokeColor(@ColorInt initialStrokeColor: Int): B {
            return replaceFirstAnimation(strokeColor, asAnimation(initialStrokeColor))
        }

        @SafeVarargs
        fun strokeColor(vararg animations: Animation<*, Int>): B {
            return replaceAnimations(strokeColor, *animations)
        }

        fun strokeColor(animations: List<Animation<*, Int>>): B {
            return replaceAnimations(strokeColor, animations)
        }

        fun strokeColor(colorStateList: ColorStateList?): B {
            return strokeColorComplex(if (colorStateList == null) null else ComplexColor.from(colorStateList))
        }

        fun strokeColor(linearGradient: LinearGradient?): B {
            return strokeColorComplex(if (linearGradient == null) null else ComplexColor.from(linearGradient))
        }

        fun strokeColor(radialGradient: RadialGradient?): B {
            return strokeColorComplex(if (radialGradient == null) null else ComplexColor.from(radialGradient))
        }

        fun strokeColor(sweepGradient: SweepGradient?): B {
            return strokeColorComplex(if (sweepGradient == null) null else ComplexColor.from(sweepGradient))
        }

        private fun strokeColorComplex(complexColor: ComplexColor?): B {
            this.strokeColorComplex = complexColor
            return self
        }

        // Stroke alpha.

        fun strokeAlpha(@FloatRange(from = 0.0, to = 1.0) initialStrokeAlpha: Float): B {
            return replaceFirstAnimation(strokeAlpha, asAnimation(initialStrokeAlpha))
        }

        @SafeVarargs
        fun strokeAlpha(vararg animations: Animation<*, Float>): B {
            return replaceAnimations(strokeAlpha, *animations)
        }

        fun strokeAlpha(animations: List<Animation<*, Float>>): B {
            return replaceAnimations(strokeAlpha, animations)
        }

        // Stroke width.

        fun strokeWidth(@FloatRange(from = 0.0) initialStrokeWidth: Float): B {
            return replaceFirstAnimation(strokeWidth, asAnimation(initialStrokeWidth))
        }

        @SafeVarargs
        fun strokeWidth(vararg animations: Animation<*, Float>): B {
            return replaceAnimations(strokeWidth, *animations)
        }

        fun strokeWidth(animations: List<Animation<*, Float>>): B {
            return replaceAnimations(strokeWidth, animations)
        }

        // Trim path start.

        fun trimPathStart(@FloatRange(from = 0.0, to = 1.0) initialTrimPathStart: Float): B {
            return replaceFirstAnimation(trimPathStart, asAnimation(initialTrimPathStart))
        }

        @SafeVarargs
        fun trimPathStart(vararg animations: Animation<*, Float>): B {
            return replaceAnimations(trimPathStart, *animations)
        }

        fun trimPathStart(animations: List<Animation<*, Float>>): B {
            return replaceAnimations(trimPathStart, animations)
        }

        // Trim path end.

        fun trimPathEnd(@FloatRange(from = 0.0, to = 1.0) initialTrimPathEnd: Float): B {
            return replaceFirstAnimation(trimPathEnd, asAnimation(initialTrimPathEnd))
        }

        @SafeVarargs
        fun trimPathEnd(vararg animations: Animation<*, Float>): B {
            return replaceAnimations(trimPathEnd, *animations)
        }

        fun trimPathEnd(animations: List<Animation<*, Float>>): B {
            return replaceAnimations(trimPathEnd, animations)
        }

        // Trim path offset.

        fun trimPathOffset(@FloatRange(from = 0.0, to = 1.0) initialTrimPathOffset: Float): B {
            return replaceFirstAnimation(trimPathOffset, asAnimation(initialTrimPathOffset))
        }

        @SafeVarargs
        fun trimPathOffset(vararg animations: Animation<*, Float>): B {
            return replaceAnimations(trimPathOffset, *animations)
        }

        fun trimPathOffset(animations: List<Animation<*, Float>>): B {
            return replaceAnimations(trimPathOffset, animations)
        }

        // Stroke line cap.

        fun strokeLineCap(strokeLineCap: StrokeLineCap): B {
            this.strokeLineCap = strokeLineCap
            return self
        }

        // Stroke line join.

        fun strokeLineJoin(strokeLineJoin: StrokeLineJoin): B {
            this.strokeLineJoin = strokeLineJoin
            return self
        }

        // Stroke miter limit.

        fun strokeMiterLimit(@FloatRange(from = 0.0, to = 1.0) initialStrokeMiterLimit: Float): B {
            return replaceFirstAnimation(strokeMiterLimit, asAnimation(initialStrokeMiterLimit))
        }

        @SafeVarargs
        fun strokeMiterLimit(vararg animations: Animation<*, Float>): B {
            return replaceAnimations(strokeMiterLimit, *animations)
        }

        fun strokeMiterLimit(animations: List<Animation<*, Float>>): B {
            return replaceAnimations(strokeMiterLimit, animations)
        }

        // Stroke dash array.

        fun strokeDashArray(initialStrokeDashArray: FloatArray?): B {
            var initialStrokeDashArray = initialStrokeDashArray
            if (initialStrokeDashArray == null) {
                initialStrokeDashArray = FloatArray(0)
            }
            return replaceFirstAnimation(strokeDashArray, asAnimation(initialStrokeDashArray))
        }

        @SafeVarargs
        fun strokeDashArray(vararg animations: Animation<*, FloatArray>): B {
            return replaceAnimations(strokeDashArray, *animations)
        }

        fun strokeDashArray(animations: List<Animation<*, FloatArray>>): B {
            return replaceAnimations(strokeDashArray, animations)
        }

        // Stroke dash offset.

        fun strokeDashOffset(@FloatRange(from = 0.0, to = 1.0) initialStrokeDashOffset: Float): B {
            return replaceFirstAnimation(strokeDashOffset, asAnimation(initialStrokeDashOffset))
        }

        @SafeVarargs
        fun strokeDashOffset(vararg animations: Animation<*, Float>): B {
            return replaceAnimations(strokeDashOffset, *animations)
        }

        fun strokeDashOffset(animations: List<Animation<*, Float>>): B {
            return replaceAnimations(strokeDashOffset, animations)
        }

        // Fill type.

        fun fillType(fillType: FillType): B {
            this.fillType = fillType
            return self
        }

        // Scaling stroke.

        fun scalingStroke(isScalingStroke: Boolean): B {
            this.isScalingStroke = isScalingStroke
            return self
        }

        abstract override fun build(): RenderNode
    }

    // </editor-fold>
}
