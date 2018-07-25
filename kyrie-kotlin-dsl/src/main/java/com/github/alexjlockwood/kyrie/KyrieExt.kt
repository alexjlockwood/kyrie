package com.github.alexjlockwood.kyrie

import android.support.v4.view.animation.PathInterpolatorCompat
import android.view.animation.Interpolator

inline fun kyrieDrawable(init: KyrieDrawableDsl.() -> Unit): KyrieDrawable =
        KyrieDrawableDsl().apply(init).build()

// KyrieDrawableDsl children functions.

inline fun KyrieDrawableDsl.circle(init: CircleNode.Builder.() -> Unit) {
    builder.child(CircleNode.builder().apply(init))
}

inline fun KyrieDrawableDsl.clipPath(init: ClipPathNode.Builder.() -> Unit) {
    builder.child(ClipPathNode.builder().apply(init))
}

inline fun KyrieDrawableDsl.ellipse(init: EllipseNode.Builder.() -> Unit) {
    builder.child(EllipseNode.builder().apply(init))
}

inline fun KyrieDrawableDsl.group(init: GroupNode.Builder.() -> Unit) {
    builder.child(GroupNode.builder().apply(init))
}

inline fun KyrieDrawableDsl.line(init: LineNode.Builder.() -> Unit) {
    builder.child(LineNode.builder().apply(init))
}

inline fun KyrieDrawableDsl.path(init: PathNode.Builder.() -> Unit) {
    builder.child(PathNode.builder().apply(init))
}

inline fun KyrieDrawableDsl.rectangle(init: RectangleNode.Builder.() -> Unit) {
    builder.child(RectangleNode.builder().apply(init))
}

// GroupNode.Builder children functions.

inline fun GroupNode.Builder.circle(init: CircleNode.Builder.() -> Unit): GroupNode.Builder =
        child(CircleNode.builder().apply(init))

inline fun GroupNode.Builder.clipPath(init: ClipPathNode.Builder.() -> Unit): GroupNode.Builder =
        child(ClipPathNode.builder().apply(init))

inline fun GroupNode.Builder.ellipse(init: EllipseNode.Builder.() -> Unit): GroupNode.Builder =
        child(EllipseNode.builder().apply(init))

inline fun GroupNode.Builder.group(init: GroupNode.Builder.() -> Unit): GroupNode.Builder =
        child(GroupNode.builder().apply(init))

inline fun GroupNode.Builder.line(init: LineNode.Builder.() -> Unit): GroupNode.Builder =
        child(LineNode.builder().apply(init))

inline fun GroupNode.Builder.path(init: PathNode.Builder.() -> Unit): GroupNode.Builder =
        child(PathNode.builder().apply(init))

inline fun GroupNode.Builder.rectangle(init: RectangleNode.Builder.() -> Unit): GroupNode.Builder =
        child(RectangleNode.builder().apply(init))

// Useful SVG path data extension functions.

fun String.asPath() = PathData.toPath(this)

fun String.asPathData() = PathData.parse(this)

fun String.asPathInterpolator(): Interpolator = PathInterpolatorCompat.create(PathData.toPath(this))