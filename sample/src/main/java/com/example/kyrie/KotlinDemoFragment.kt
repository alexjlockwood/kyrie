package com.example.kyrie

import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.alexjlockwood.kyrie.Animation
import com.github.alexjlockwood.kyrie.KyrieDrawable
import com.github.alexjlockwood.kyrie.PathData
import com.github.alexjlockwood.kyrie.StrokeLineCap
import com.github.alexjlockwood.kyrie.asPath
import com.github.alexjlockwood.kyrie.asPathData
import com.github.alexjlockwood.kyrie.asPathInterpolator
import com.github.alexjlockwood.kyrie.circle
import com.github.alexjlockwood.kyrie.group
import com.github.alexjlockwood.kyrie.kyrieDrawable
import com.github.alexjlockwood.kyrie.path
import com.github.alexjlockwood.kyrie.pathDataAnimation
import com.github.alexjlockwood.kyrie.withListener
import kotlinx.android.synthetic.main.fragment_choice.*
import java.util.*

class KotlinDemoFragment : Fragment() {

    private val polygons = arrayOf(
        Polygon(15, -0x17b39b, 362f, 2),
        Polygon(14, -0x17b39b, 338f, 3),
        Polygon(13, -0x2aab27, 314f, 4),
        Polygon(12, -0x509112, 292f, 5),
        Polygon(11, -0xb5b51a, 268f, 6),
        Polygon(10, -0xbd6b19, 244f, 7),
        Polygon(9, -0x941112, 220f, 8),
        Polygon(8, -0xbd186c, 196f, 9),
        Polygon(7, -0xa518a6, 172f, 10),
        Polygon(6, -0x521895, 148f, 11),
        Polygon(5, -0x101045, 128f, 12),
        Polygon(4, -0x186bbe, 106f, 13),
        Polygon(3, -0x17b39b, 90f, 14)
    )

    private lateinit var listener: SampleListenerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_choice, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listener = SampleListenerAdapter(seekbar)
        buttonHeart.setOnClickListener { startDrawable(createHeartDrawable()) }
        buttonProgCirc.setOnClickListener { startDrawable(createCircularDrawable()) }
        buttonProgLine.setOnClickListener { startDrawable(createHorizontalDrawable()) }
        buttonPolygon.setOnClickListener { startDrawable(createPolygonDrawable()) }
        buttonMorph.setOnClickListener { startDrawable(createMorphDrawable()) }
    }

    private fun startDrawable(drawable: KyrieDrawable) {
        imageView.setImageDrawable(drawable withListener listener)
        drawable.start()
    }


    private fun createHeartDrawable(): KyrieDrawable {
        return context?.let {
            KyrieDrawable.create(it, R.drawable.avd_heartbreak)
        } ?: error("No context available")
    }

    private fun createHorizontalDrawable(): KyrieDrawable {
        return kyrieDrawable {
            viewport = 360f x 10f
            tint = TINT_COLOR
            group {
                translateX(180f)
                translateY(5f)
                path {
                    fillAlpha(0.3f)
                    fillColor(Color.WHITE)
                    pathData("M -180,-1 l 360,0 l 0,2 l -360,0 Z")
                }
                group {
                    scaleX(
                        Animation.ofPathMotion("M 0 0.1 L 1 0.571 L 2 0.91 L 3 0.1".asPath())
                            .transform { it.y }
                            .duration(2000)
                            .repeatCount(Animation.INFINITE)
                            .interpolator("M 0 0 C 0.068 0.02 0.192 0.159 0.333 0.349 C 0.384 0.415 0.549 0.681 0.667 0.683 C 0.753 0.682 0.737 0.879 1 1".asPathInterpolator())
                    )
                    translateX(
                        Animation.ofPathMotion("M -197.6 0 C -183.318 0 -112.522 0 -62.053 0 C -7.791 0 28.371 0 106.19 0 C 250.912 0 422.6 0 422.6 0".asPath())
                            .transform { it.x }
                            .duration(2000)
                            .repeatCount(Animation.INFINITE)
                            .interpolator("M 0 0 C 0.037 0 0.129 0.09 0.25 0.219 C 0.322 0.296 0.437 0.418 0.483 0.49 C 0.69 0.81 0.793 0.95 1 1".asPathInterpolator())
                    )
                    path {
                        fillColor(Color.WHITE)
                        pathData("M -144,-1 l 288,0 l 0,2 l -288,0 Z")
                    }
                }
                group {
                    scaleX(
                        Animation.ofPathMotion("M 0 0.1 L 1 0.826 L 2 0.1".asPath())
                            .transform { it.y }
                            .duration(2000)
                            .repeatCount(Animation.INFINITE)
                            .interpolator("M 0 0 L 0.366 0 C 0.473 0.062 0.615 0.5 0.683 0.5 C 0.755 0.5 0.757 0.815 1 1".asPathInterpolator())
                    )
                    translateX(
                        Animation.ofPathMotion("M -522.6 0 C -473.7 0 -356.573 0 -221.383 0 C -23.801 0 199.6 0 199.6 0".asPath())
                            .transform { it.x }
                            .duration(2000)
                            .repeatCount(Animation.INFINITE)
                            .interpolator("M 0 0 L 0.2 0 C 0.395 0 0.474 0.206 0.591 0.417 C 0.715 0.639 0.816 0.974 1 1".asPathInterpolator())
                    )
                    path {
                        fillColor(Color.WHITE)
                        pathData("M -144,-1 l 288,0 l 0,2 l -288,0 Z")
                    }
                }
            }
        }
    }

    private fun createCircularDrawable(): KyrieDrawable {
        return kyrieDrawable {

            viewport = 48f x 48f
            tint = TINT_COLOR
            group {
                translateX(24f)
                translateY(24f)
                rotation(
                    Animation.ofFloat(0f, 720f).duration(4444).repeatCount(Animation.INFINITE)
                )
                path {
                    strokeColor(Color.WHITE)
                    strokeWidth(4f)
                    trimPathStart(
                        Animation.ofFloat(0f, 0.75f)
                            .duration(1333)
                            .repeatCount(Animation.INFINITE)
                            .interpolator(
                                "M 0 0 L 0.5 0 C 0.7 0 0.6 1 1 1".asPathInterpolator()
                            )
                    )
                    trimPathEnd(
                        Animation.ofFloat(0.03f, 0.78f)
                            .duration(1333)
                            .repeatCount(Animation.INFINITE)
                            .interpolator("M 0 0 C 0.2 0 0.1 1 0.5 0.96 C 0.966 0.96 0.993 1 1 1".asPathInterpolator())
                    )
                    trimPathOffset(
                        Animation.ofFloat(0f, 0.25f)
                            .duration(1333)
                            .repeatCount(Animation.INFINITE)
                    )
                    strokeLineCap(StrokeLineCap.SQUARE)
                    pathData("M 0 0 m 0 -18 a 18 18 0 1 1 0 36 a 18 18 0 1 1 0 -36")
                }
            }
        }
    }

    private fun createPolygonDrawable(): KyrieDrawable {
        return kyrieDrawable {

            viewport = VIEWPORT_WIDTH x VIEWPORT_HEIGHT

            polygons.forEach { polygon ->
                path {
                    pathData(PathData.parse(polygon.pathData))
                    strokeWidth(4f)
                    strokeColor(polygon.color)
                }
            }

            polygons.forEach { polygon ->
                val pathData = (1..polygon.laps).joinToString(" ") { polygon.pathData }
                    .asPathData()
                val pathMotion = Animation.ofPathMotion(PathData.toPath(pathData))
                    .repeatCount(Animation.INFINITE)
                    .duration(DURATION)
                circle {
                    fillColor(Color.BLACK)
                    radius(8f)
                    centerX(pathMotion.transform { p -> p.x })
                    centerY(pathMotion.transform { p -> p.y })
                }
            }
        }
    }

    private fun createMorphDrawable(): KyrieDrawable {
        val hippoPathData = PathData.parse(getString(R.string.hippo))
        val elephantPathData = PathData.parse(getString(R.string.elephant))
        val buffaloPathData = PathData.parse(getString(R.string.buffalo))
        var hippoFillColor = Color.BLUE
        var elephantFillColor = Color.GRAY
        var buffaloFillColor = Color.GREEN
        context?.let { ctx ->
            hippoFillColor = ContextCompat.getColor(ctx, R.color.hippo)
            elephantFillColor = ContextCompat.getColor(ctx, R.color.elephant)
            buffaloFillColor = ContextCompat.getColor(ctx, R.color.buffalo)
        }
        return kyrieDrawable {
            viewport = 409f x 280f
            path {
                strokeColor(Color.BLACK)
                strokeWidth(1f)
                fillColor(
                    Animation.ofArgb(hippoFillColor, elephantFillColor).duration(300),
                    Animation.ofArgb(buffaloFillColor).startDelay(600).duration(300),
                    Animation.ofArgb(hippoFillColor).startDelay(1200).duration(300)
                )

                pathData(
                    pathDataAnimation(
                        0f to hippoPathData,
                        0.2f to elephantPathData,
                        0.4f to elephantPathData,
                        0.6f to buffaloPathData,
                        0.8f to buffaloPathData,
                        1f to hippoPathData
                    ).duration(1500)
                )
            }
        }
    }

    private class Polygon internal constructor(
        sides: Int, @param:ColorInt @field:ColorInt internal val color: Int,
        radius: Float,
        internal val laps: Int
    ) {
        internal val pathData: String
        internal val length: Float

        init {
            val points = getPoints(sides, radius)
            this.pathData = pointsToPathData(points)
            this.length = pointsToLength(points)
        }

        private fun getPoints(sides: Int, radius: Float): List<PointF> {
            val points = ArrayList<PointF>(sides)
            val angle = (2 * Math.PI / sides).toFloat()
            val startAngle = (3 * Math.PI / 2).toFloat()
            for (i in 0..sides) {
                val theta = startAngle + angle * i
                points.add(getPolygonPoint(radius, theta))
            }
            return points
        }

        private fun getPolygonPoint(radius: Float, theta: Float): PointF {
            return PointF(
                VIEWPORT_WIDTH / 2 + (radius * Math.cos(theta.toDouble())).toFloat(),
                VIEWPORT_HEIGHT / 2 + (radius * Math.sin(theta.toDouble())).toFloat()
            )
        }

        private fun pointsToPathData(points: List<PointF>): String =
            "M " + points.joinToString(" ") { "${it.x} ${it.y}" }

        private fun pointsToLength(points: List<PointF>): Float {
            var length = 0f
            var i = 1
            val size = points.size
            while (i < size) {
                val prev = points[i - 1]
                val curr = points[i]
                length += Math.hypot((curr.x - prev.x).toDouble(), (curr.y - prev.y).toDouble())
                    .toFloat()
                i++
            }
            return length
        }
    }

    companion object {
        @ColorInt
        private const val TINT_COLOR = -0xbf7f
        private const val VIEWPORT_WIDTH = 1080f
        private const val VIEWPORT_HEIGHT = 1080f
        private const val DURATION = 7500L

    }
}
