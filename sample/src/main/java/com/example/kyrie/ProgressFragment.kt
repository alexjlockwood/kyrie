package com.example.kyrie

import android.graphics.Color
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.alexjlockwood.kyrie.*
import kotlinx.android.synthetic.main.fragment_two_pane.*

class ProgressFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_two_pane, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val horizontalDrawable = createHorizontalDrawable()
        imageViewPane1.setImageDrawable(horizontalDrawable)
        horizontalDrawable.start()

        val circularDrawable = createCircularDrawable()
        imageViewPane2.setImageDrawable(circularDrawable)
        circularDrawable.start()
    }

    private fun createHorizontalDrawable(): KyrieDrawable {
        return kyrieDrawable {
            viewport(360f, 10f)
            tint(TINT_COLOR)
            group {
                translateX(180f)
                translateY(5f)
                path {
                    fillAlpha(0.3f)
                    fillColor(Color.WHITE)
                    pathData("M -180,-1 l 360,0 l 0,2 l -360,0 Z")
                }
                path {
                    scaleX(
                            Animation.ofPathMotion("M 0 0.1 L 1 0.571 L 2 0.91 L 3 0.1".asPath())
                                    .transform { p -> p.y }
                                    .duration(2000)
                                    .repeatCount(Animation.INFINITE)
                                    .interpolator("M 0 0 C 0.068 0.02 0.192 0.159 0.333 0.349 C 0.384 0.415 0.549 0.681 0.667 0.683 C 0.753 0.682 0.737 0.879 1 1".asPathInterpolator()))
                    translateX(
                            Animation.ofPathMotion("M -197.6 0 C -183.318 0 -112.522 0 -62.053 0 C -7.791 0 28.371 0 106.19 0 C 250.912 0 422.6 0 422.6 0".asPath())
                                    .transform { p -> p.x }
                                    .duration(2000)
                                    .repeatCount(Animation.INFINITE)
                                    .interpolator("M 0 0 C 0.037 0 0.129 0.09 0.25 0.219 C 0.322 0.296 0.437 0.418 0.483 0.49 C 0.69 0.81 0.793 0.95 1 1".asPathInterpolator()))

                    fillColor(Color.WHITE)
                    pathData("M -144,-1 l 288,0 l 0,2 l -288,0 Z")
                }
                path {
                    scaleX(
                            Animation.ofPathMotion("M 0 0.1 L 1 0.826 L 2 0.1".asPath())
                                    .transform { p -> p.y }
                                    .duration(2000)
                                    .repeatCount(Animation.INFINITE)
                                    .interpolator("M 0 0 L 0.366 0 C 0.473 0.062 0.615 0.5 0.683 0.5 C 0.755 0.5 0.757 0.815 1 1".asPathInterpolator()))
                    translateX(
                            Animation.ofPathMotion("M -522.6 0 C -473.7 0 -356.573 0 -221.383 0 C -23.801 0 199.6 0 199.6 0".asPath())
                                    .transform { p -> p.x }
                                    .duration(2000)
                                    .repeatCount(Animation.INFINITE)
                                    .interpolator("M 0 0 L 0.2 0 C 0.395 0 0.474 0.206 0.591 0.417 C 0.715 0.639 0.816 0.974 1 1".asPathInterpolator()))
                    fillColor(Color.WHITE)
                    pathData("M -144,-1 l 288,0 l 0,2 l -288,0 Z")
                }
            }
        }
    }

    private fun createCircularDrawable(): KyrieDrawable {
        return kyrieDrawable {
            viewport(48f, 48f)
            tint(TINT_COLOR)
            group {
                translateX(24f)
                translateY(24f)
                rotation(
                        Animation.ofFloat(0f, 720f)
                                .duration(4444)
                                .repeatCount(Animation.INFINITE)
                )
                path {
                    strokeColor(Color.WHITE)
                    strokeWidth(4f)
                    trimPathStart(
                            Animation.ofFloat(0f, 0.75f)
                                    .duration(1333)
                                    .repeatCount(Animation.INFINITE)
                                    .interpolator("M 0 0 L 0.5 0 C 0.7 0 0.6 1 1 1".asPathInterpolator())
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

    companion object {
        @ColorInt
        private val TINT_COLOR = -0xbf7f
    }
}
