package com.github.alexjlockwood.kyrie

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.annotation.Px
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import com.github.alexjlockwood.kyrie.Node.Companion.asAnimation
import com.github.alexjlockwood.kyrie.Node.Companion.asAnimations
import com.github.alexjlockwood.kyrie.Node.Companion.replaceAnimations
import com.github.alexjlockwood.kyrie.Node.Companion.replaceFirstAnimation
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

/** An animatable drawable based on scalable vector graphics. */
class KyrieDrawable private constructor(
        @param:Px @field:Px private val width: Int,
        @param:Px @field:Px private val height: Int,
        @param:FloatRange(from = 0.0) @field:FloatRange(from = 0.0)
        private val viewportWidth: Float,
        @param:FloatRange(from = 0.0) @field:FloatRange(from = 0.0)
        private val viewportHeight: Float,
        alphaAnimations: List<Animation<*, Float>>,
        childrenNodes: List<Node>,
        private var tintList: ColorStateList?,
        private var tintMode: PorterDuff.Mode,
        private var isAutoMirrored: Boolean
) : Drawable(), Animatable {

    private val alphaProperty: Property<Float>

    private val timeline: PropertyTimeline
    private val animator: KyrieValueAnimator
    private val childrenLayers = mutableListOf<Node.Layer>()

    @IntRange(from = 0, to = 0xff)
    private var alpha = 0xff
    private var tintFilter: PorterDuffColorFilter? = null
    private var colorFilter: ColorFilter? = null

    private val tempMatrix = Matrix()
    private val tempMatrixFloats = FloatArray(9)
    private val tempBounds = Rect()
    private val viewportScale = PointF()
    private var offscreenBitmap: Bitmap? = null
    private var offscreenPaint: Paint? = null

    /**
     * Gets the total duration of the animation, accounting for start delay and repeating. Return
     * [Animation.INFINITE] if the duration is infinite.
     */
    val totalDuration: Long
        get() = timeline.totalDuration

    /**
     * Gets the current position of the animation in time, which is equal to the current time minus
     * the time that the animation started.
     */
    /**
     * Sets the position of the animation to the specified point in time. This time should be between
     * 0 and the total duration of the animation, including any repetition.
     */
    var currentPlayTime: Long
        @IntRange(from = 0L)
        get() = animator.currentPlayTime
        set(@IntRange(from = 0L) currentPlayTime) {
            var playTime = currentPlayTime
            playTime = Math.max(0, playTime)
            val totalDuration = totalDuration
            if (totalDuration != Animation.INFINITE) {
                playTime = Math.min(totalDuration, playTime)
            }
            animator.currentPlayTime = playTime
        }

    /** Returns true if the animation has been started. */
    val isStarted: Boolean
        get() = animator.isStarted

    /** Returns true if the animation has been paused. */
    val isPaused: Boolean
        get() = animator.isPaused

    init {
        this.tintFilter = createTintFilter()
        timeline = PropertyTimeline(this)
        alphaProperty = timeline.registerAnimatableProperty(alphaAnimations)
        var i = 0
        val size = childrenNodes.size
        while (i < size) {
            childrenLayers.add(childrenNodes[i].toLayer(timeline))
            i++
        }
        animator = KyrieValueAnimator(this)
    }

    @Px
    override fun getIntrinsicWidth(): Int {
        return width
    }

    @Px
    override fun getIntrinsicHeight(): Int {
        return height
    }

    override fun isStateful(): Boolean {
        if (tintList?.isStateful == true) {
            return true
        }
        if (areLayersStateful()) {
            return true
        }
        return super.isStateful()
    }

    override fun onStateChange(stateSet: IntArray): Boolean {
        var changed = false

        if (tintList != null) {
            tintFilter = createTintFilter()
            changed = true
        }

        if (areLayersStateful() && onLayerStateChange(stateSet)) {
            changed = true
        }

        if (changed) {
            invalidateSelf()
        }

        return true
    }

    private fun areLayersStateful(): Boolean {
        return childrenLayers.any { it.isStateful() }
    }

    private fun onLayerStateChange(stateSet: IntArray): Boolean {
        var changed = false
        for (i in 0 until childrenLayers.size) {
            changed = changed or childrenLayers[i].onStateChange(stateSet)
        }
        return changed
    }

    override fun setTint(@ColorInt tint: Int) {
        setTintList(ColorStateList.valueOf(tint))
    }

    override fun setTintList(tintList: ColorStateList?) {
        if (this.tintList !== tintList) {
            this.tintList = tintList
            tintFilter = createTintFilter()
            invalidateSelf()
        }
    }

    override fun setTintMode(tintMode: PorterDuff.Mode) {
        if (this.tintMode != tintMode) {
            this.tintMode = tintMode
            tintFilter = createTintFilter()
            invalidateSelf()
        }
    }

    private fun createTintFilter(): PorterDuffColorFilter? {
        if (tintList == null) {
            return null
        }
        val tintColor = tintList!!.getColorForState(state, Color.TRANSPARENT)
        return PorterDuffColorFilter(tintColor, tintMode)
    }

    override fun isAutoMirrored(): Boolean {
        return isAutoMirrored
    }

    override fun setAutoMirrored(mirrored: Boolean) {
        if (isAutoMirrored != mirrored) {
            isAutoMirrored = mirrored
            invalidateSelf()
        }
    }

    override fun setAlpha(@IntRange(from = 0, to = 0xff) alpha: Int) {
        if (this.alpha != alpha) {
            this.alpha = alpha
            invalidateSelf()
        }
    }

    @IntRange(from = 0, to = 0xff)
    override fun getAlpha(): Int {
        return alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        if (this.colorFilter != colorFilter) {
            this.colorFilter = colorFilter
            invalidateSelf()
        }
    }

    override fun getColorFilter(): ColorFilter? {
        return colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun draw(canvas: Canvas) {
        val bounds = tempBounds
        copyBounds(bounds)
        if (bounds.width() <= 0 || bounds.height() <= 0) {
            return
        }

        // Color filters always override tint filters.
        val cf = if (colorFilter == null) tintFilter else colorFilter

        // The ImageView can scale the canvas in different ways, so in order to
        // avoid blurry scaling we have to draw into a bitmap with exact pixel
        // size first. This bitmap size is determined by the bounds and the
        // canvas scale.
        canvas.getMatrix(tempMatrix)
        tempMatrix.getValues(tempMatrixFloats)
        var canvasScaleX = Math.abs(tempMatrixFloats[Matrix.MSCALE_X])
        var canvasScaleY = Math.abs(tempMatrixFloats[Matrix.MSCALE_Y])
        val canvasSkewX = Math.abs(tempMatrixFloats[Matrix.MSKEW_X])
        val canvasSkewY = Math.abs(tempMatrixFloats[Matrix.MSKEW_Y])

        // The scale is invalid if there is any rotation or skew.
        if (canvasSkewX != 0f || canvasSkewY != 0f) {
            canvasScaleX = 1f
            canvasScaleY = 1f
        }

        val scaledWidth = Math.min((bounds.width() * canvasScaleX).toInt(), MAX_CACHED_BITMAP_SIZE)
        val scaledHeight = Math.min((bounds.height() * canvasScaleY).toInt(), MAX_CACHED_BITMAP_SIZE)
        if (scaledWidth <= 0 || scaledHeight <= 0) {
            return
        }

        val saveCount = canvas.save()
        canvas.translate(bounds.left.toFloat(), bounds.top.toFloat())

        // Handle RTL mirroring.
        val shouldAutoMirror =
                isAutoMirrored && DrawableCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL
        if (shouldAutoMirror) {
            canvas.translate(bounds.width().toFloat(), 0f)
            canvas.scale(-1f, 1f)
        }

        // At this point, the canvas has been translated to the right position.
        // We use these bounds as the destination rect when drawing the bitmap, so
        // offset to (0, 0);
        bounds.offsetTo(0, 0)

        // Recreate the offscreen bitmap if the dimensions have changed.
        if (offscreenBitmap == null
                || scaledWidth != offscreenBitmap!!.width
                || scaledHeight != offscreenBitmap!!.height) {
            offscreenBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
        }

        // Clear the offscreen bitmap.
        offscreenBitmap!!.eraseColor(Color.TRANSPARENT)
        viewportScale.set(scaledWidth / viewportWidth, scaledHeight / viewportHeight)
        val offscreenCanvas = Canvas(offscreenBitmap!!)
        childrenLayers.forEach { it.draw(offscreenCanvas, IDENTITY_MATRIX, viewportScale) }

        // Draw the offscreen bitmap.
        var paint: Paint? = null
        val alphaFloat = this.alpha / 255f * alphaProperty.animatedValue
        val alphaInt = Math.round(alphaFloat * 255f)
        if (alphaInt < 0xff || cf != null) {
            if (offscreenPaint == null) {
                offscreenPaint = Paint()
                offscreenPaint!!.isFilterBitmap = true
            }
            offscreenPaint!!.alpha = alphaInt
            offscreenPaint!!.colorFilter = cf
            paint = offscreenPaint
        }
        canvas.drawBitmap(offscreenBitmap!!, null, bounds, paint)
        canvas.restoreToCount(saveCount)
    }

    /** Starts the animation. */
    override fun start() {
        animator.start()
    }

    /** Stops the animation. If the animation is running, it will be canceled. */
    override fun stop() {
        animator.cancel()
    }

    /** Pauses the animation. */
    fun pause() {
        animator.pause()
    }

    /** Resumes the animation. */
    fun resume() {
        animator.resume()
    }

    /** Returns true if the animation is running. */
    override fun isRunning(): Boolean {
        return animator.isRunning
    }

    /** Adds a [Listener] to this [KyrieDrawable]'s set of listeners. */
    fun addListener(listener: Listener) {
        animator.addListener(listener)
    }

    /** Removes a [Listener] from this [KyrieDrawable]'s set of listeners. */
    fun removeListener(listener: Listener) {
        animator.removeListener(listener)
    }

    /** Removes all [Listener]s from this [KyrieDrawable]'s set of listeners. */
    fun clearListeners() {
        animator.clearListeners()
    }

    /**
     * A listener that receives notifications from an animation. Notifications indicate animation
     * related events, such as the start or end of the animation.
     */
    interface Listener {
        /**
         * Notifies the start of the animation.
         *
         * @param drawable The KyrieDrawable instance being started.
         */
        fun onAnimationStart(drawable: KyrieDrawable)

        /**
         * Notifies the occurrence of another frame of the animation.
         *
         * @param drawable The KyrieDrawable instance being updated.
         */
        fun onAnimationUpdate(drawable: KyrieDrawable)

        /**
         * Notifies that the animation was paused.
         *
         * @param drawable The KyrieDrawable instance being paused.
         * @see .pause
         */
        fun onAnimationPause(drawable: KyrieDrawable)

        /**
         * Notifies that the animation was resumed, after being previously paused.
         *
         * @param drawable The KyrieDrawable instance being resumed.
         * @see .resume
         */
        fun onAnimationResume(drawable: KyrieDrawable)

        /**
         * Notifies the cancellation of the animation.
         *
         * @param drawable The KyrieDrawable instance being canceled.
         */
        fun onAnimationCancel(drawable: KyrieDrawable)

        /**
         * Notifies the end of the animation. This callback is not invoked for animations with repeat
         * count set to INFINITE.
         *
         * @param drawable The KyrieDrawable instance being ended.
         */
        fun onAnimationEnd(drawable: KyrieDrawable)
    }

    /**
     * This adapter class provides empty implementations of the methods from [Listener]. Any
     * custom listener that cares only about a subset of the methods of this listener can simply
     * subclass this adapter class instead of implementing the interface directly.
     */
    abstract class ListenerAdapter : Listener {
        override fun onAnimationStart(drawable: KyrieDrawable) {}

        override fun onAnimationUpdate(drawable: KyrieDrawable) {}

        override fun onAnimationPause(drawable: KyrieDrawable) {}

        override fun onAnimationResume(drawable: KyrieDrawable) {}

        override fun onAnimationCancel(drawable: KyrieDrawable) {}

        override fun onAnimationEnd(drawable: KyrieDrawable) {}
    }

    private class KyrieValueAnimator(private val drawable: KyrieDrawable) : ValueAnimator() {
        private val listeners = mutableListOf<Listener>()
        private var isPaused = false

        @IntRange(from = 0L)
        private var playTime: Long = 0L

        private val listenerAdapter = object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                listeners.forEach { it.onAnimationStart(drawable) }
            }

            override fun onAnimationCancel(animation: Animator) {
                listeners.forEach { it.onAnimationCancel(drawable) }
            }

            override fun onAnimationEnd(animation: Animator) {
                listeners.forEach { it.onAnimationEnd(drawable) }
            }
        }

        init {
            setFloatValues(0f, 1f)
            interpolator = LinearInterpolator()
            val timeline = drawable.timeline
            addListener(listenerAdapter)
            addUpdateListener { animation ->
                playTime = animation.currentPlayTime
                timeline.setCurrentPlayTime(playTime)
                listeners.forEach { it.onAnimationUpdate(drawable) }
            }
            val totalDuration = timeline.totalDuration
            duration = if (totalDuration == Animation.INFINITE) Long.MAX_VALUE else totalDuration
        }

        override fun pause() {
            if (isStarted && !isPaused) {
                isPaused = true
                val currentPlayTime = this.playTime
                cancelWithoutNotify()
                setCurrentPlayTime(currentPlayTime)
                listeners.forEach { it.onAnimationPause(drawable) }
            }
        }

        private fun cancelWithoutNotify() {
            removeListener(listenerAdapter)
            cancel()
            addListener(listenerAdapter)
        }

        override fun resume() {
            if (isPaused) {
                isPaused = false
                val currentPlayTime = this.playTime
                startWithoutNotify()
                setCurrentPlayTime(currentPlayTime)
                listeners.forEach { it.onAnimationResume(drawable) }
            }
        }

        private fun startWithoutNotify() {
            removeListener(listenerAdapter)
            start()
            addListener(listenerAdapter)
        }

        override fun isPaused(): Boolean {
            return isPaused
        }

        fun addListener(listener: Listener) {
            listeners.add(listener)
        }

        fun removeListener(listener: Listener) {
            listeners.remove(listener)
        }

        fun clearListeners() {
            listeners.clear()
        }
    }

    // <editor-fold desc="Builder">

    @DslMarker
    private annotation class KyrieDrawableMarker

    /** Builder class used to create a [KyrieDrawable]. */
    @KyrieDrawableMarker
    class Builder internal constructor() {

        private var width = -1
        private var height = -1
        private var viewportWidth = -1f
        private var viewportHeight = -1f
        private val alpha = asAnimations(1f)
        private val children = mutableListOf<Node>()
        private var isAutoMirrored: Boolean = false
        private var tintList: ColorStateList? = null
        private var tintMode = DEFAULT_TINT_MODE

        // Dimensions.

        fun width(@Px width: Int): Builder {
            this.width = width
            return this
        }

        fun height(@Px height: Int): Builder {
            this.height = height
            return this
        }

        fun dimensions(@Px width: Int, @Px height: Int): Builder {
            width(width)
            height(height)
            return this
        }

        // Viewport

        fun viewportWidth(@FloatRange(from = 0.0) viewportWidth: Float): Builder {
            this.viewportWidth = viewportWidth
            return this
        }

        fun viewportHeight(@FloatRange(from = 0.0) viewportHeight: Float): Builder {
            this.viewportHeight = viewportHeight
            return this
        }

        fun viewport(@FloatRange(from = 0.0) viewportWidth: Float, @FloatRange(from = 0.0) viewportHeight: Float): Builder {
            viewportWidth(viewportWidth)
            viewportHeight(viewportHeight)
            return this
        }

        // Alpha.

        fun alpha(@FloatRange(from = 0.0, to = 1.0) initialAlpha: Float): Builder {
            replaceFirstAnimation(alpha, asAnimation(initialAlpha))
            return this
        }

        @SafeVarargs
        fun alpha(vararg animations: Animation<*, Float>): Builder {
            replaceAnimations(alpha, *animations)
            return this
        }

        fun alpha(animations: List<Animation<*, Float>>): Builder {
            replaceAnimations(alpha, animations)
            return this
        }

        // Auto-mirror.

        fun autoMirrored(isAutoMirrored: Boolean): Builder {
            this.isAutoMirrored = isAutoMirrored
            return this
        }

        // Tint.

        fun tint(@ColorInt tint: Int): Builder {
            return tintList(ColorStateList.valueOf(tint))
        }

        fun tintList(tintList: ColorStateList?): Builder {
            this.tintList = tintList
            return this
        }

        fun tintMode(tintMode: PorterDuff.Mode): Builder {
            this.tintMode = tintMode
            return this
        }

        // Children.

        fun child(node: Node): Builder {
            children.add(node)
            return this
        }

        fun child(builder: Node.Builder<*>): Builder {
            return child(builder.build())
        }

        fun build(): KyrieDrawable {
            if (viewportWidth <= 0 || viewportHeight <= 0) {
                throw IllegalStateException(
                        "Viewport width/height must be greater than 0: "
                                + "viewportWidth="
                                + viewportWidth
                                + ", viewportHeight="
                                + viewportHeight)
            }
            if (width < 0 && height < 0) {
                width = Math.round(viewportWidth)
                height = Math.round(viewportHeight)
            } else if (width < 0) {
                width = Math.round(height * viewportWidth / viewportHeight)
            } else if (height < 0) {
                height = Math.round(width * viewportHeight / viewportWidth)
            }
            // TODO: handle viewport/dimensions aspect ratio mismatch using preserveAspectRatio
            return KyrieDrawable(
                    width,
                    height,
                    viewportWidth,
                    viewportHeight,
                    alpha,
                    children,
                    tintList,
                    tintMode,
                    isAutoMirrored
            )
        }

        private companion object {
            private val DEFAULT_TINT_MODE = PorterDuff.Mode.SRC_IN
        }
    }

    // </editor-fold>

    companion object {
        private const val TAG = "KyrieDrawable"

        private val IDENTITY_MATRIX = Matrix()

        // Cap the bitmap size, such that it won't hurt the performance too much
        // and it won't crash due to a very large scale.
        // The drawable will look blurry above this size.
        private const val MAX_CACHED_BITMAP_SIZE = 2048

        /**
         * Creates a [KyrieDrawable] from an existing [VectorDrawable]
         * or [AnimatedVectorDrawable] XML file.
         */
        @JvmStatic
        fun create(context: Context, @DrawableRes resId: Int): KyrieDrawable? {
            return try {
                val builder = builder()
                InflationUtils.inflate(builder, context, resId)
                builder.build()
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
                Log.e(TAG, "Error parsing drawable", e)
                null
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, "Error parsing drawable", e)
                null
            }

        }

        /** Constructs a new [KyrieDrawable.Builder]. */
        @JvmStatic
        fun builder(): Builder {
            return Builder()
        }
    }

}
