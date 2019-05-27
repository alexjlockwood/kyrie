package com.github.alexjlockwood.kyrie

import android.view.animation.Interpolator
import androidx.core.view.animation.PathInterpolatorCompat

@JvmSynthetic
inline fun kyrieDrawable(init: KyrieDrawable.Builder.() -> Unit): KyrieDrawable =
        KyrieDrawable.builder().apply(init).build()

// KyrieDrawable.Builder children functions.

@JvmSynthetic
inline fun KyrieDrawable.Builder.circle(init: CircleNode.Builder.() -> Unit) {
    child(CircleNode.builder().apply(init))
}

@JvmSynthetic
inline fun KyrieDrawable.Builder.clipPath(init: ClipPathNode.Builder.() -> Unit) {
    child(ClipPathNode.builder().apply(init))
}

@JvmSynthetic
inline fun KyrieDrawable.Builder.ellipse(init: EllipseNode.Builder.() -> Unit) {
    child(EllipseNode.builder().apply(init))
}

@JvmSynthetic
inline fun KyrieDrawable.Builder.group(init: GroupNode.Builder.() -> Unit) {
    child(GroupNode.builder().apply(init))
}

@JvmSynthetic
inline fun KyrieDrawable.Builder.line(init: LineNode.Builder.() -> Unit) {
    child(LineNode.builder().apply(init))
}

@JvmSynthetic
inline fun KyrieDrawable.Builder.path(init: PathNode.Builder.() -> Unit) {
    child(PathNode.builder().apply(init))
}

@JvmSynthetic
inline fun KyrieDrawable.Builder.rectangle(init: RectangleNode.Builder.() -> Unit) {
    child(RectangleNode.builder().apply(init))
}

// GroupNode.Builder children functions.

@JvmSynthetic
inline fun GroupNode.Builder.circle(init: CircleNode.Builder.() -> Unit): GroupNode.Builder =
        child(CircleNode.builder().apply(init))

@JvmSynthetic
inline fun GroupNode.Builder.clipPath(init: ClipPathNode.Builder.() -> Unit): GroupNode.Builder =
        child(ClipPathNode.builder().apply(init))

@JvmSynthetic
inline fun GroupNode.Builder.ellipse(init: EllipseNode.Builder.() -> Unit): GroupNode.Builder =
        child(EllipseNode.builder().apply(init))

@JvmSynthetic
inline fun GroupNode.Builder.group(init: GroupNode.Builder.() -> Unit): GroupNode.Builder =
        child(GroupNode.builder().apply(init))

@JvmSynthetic
inline fun GroupNode.Builder.line(init: LineNode.Builder.() -> Unit): GroupNode.Builder =
        child(LineNode.builder().apply(init))

@JvmSynthetic
inline fun GroupNode.Builder.path(init: PathNode.Builder.() -> Unit): GroupNode.Builder =
        child(PathNode.builder().apply(init))

@JvmSynthetic
inline fun GroupNode.Builder.rectangle(init: RectangleNode.Builder.() -> Unit): GroupNode.Builder =
        child(RectangleNode.builder().apply(init))

// Useful SVG path data extension functions.

@JvmSynthetic
fun String.asPath() = PathData.toPath(this)

@JvmSynthetic
fun String.asPathData() = PathData.parse(this)

@JvmSynthetic
fun String.asPathInterpolator(): Interpolator = PathInterpolatorCompat.create(PathData.toPath(this))