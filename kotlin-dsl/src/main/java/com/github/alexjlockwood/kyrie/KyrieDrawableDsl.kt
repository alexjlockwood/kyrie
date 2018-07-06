package com.github.alexjlockwood.kyrie

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

    fun size(width:Float, height: Float) = Size(width, height)

    data class Size (val width: Float, val height: Float)
}