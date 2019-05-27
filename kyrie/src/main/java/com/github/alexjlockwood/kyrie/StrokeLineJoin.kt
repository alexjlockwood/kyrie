package com.github.alexjlockwood.kyrie

/** Stroke line join determines the shape that should be used at the ends of a stroked sub-path. */
enum class StrokeLineJoin {
    /** A miter stroke line join. */
    MITER,
    /** A round stroke line join. */
    ROUND,
    /** A bevel stroke line join. */
    BEVEL
}
