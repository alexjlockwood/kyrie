package com.github.alexjlockwood.kyrie

import android.support.v4.view.animation.PathInterpolatorCompat

class KyrieDrawableDsl {

    val builder = KyrieDrawable.builder()

    fun build() = builder.build()

    var viewport: Size
        get() = Size(0f, 0f)
        set(value) {
            builder.viewport(value.width, value.height)
        }

    var tint: Int
        get() = 0
        set(value) {
            builder.tint(value)
        }

    infix fun Float.x(height: Float) = Size(this, height)

    data class Size (val width: Float, val height: Float)
}