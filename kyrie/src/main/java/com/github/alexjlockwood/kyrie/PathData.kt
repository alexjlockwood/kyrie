package com.github.alexjlockwood.kyrie

import android.graphics.Path

import java.util.Arrays

private val EMPTY_PATH_DATUMS = arrayOf<PathData.PathDatum>()

/** A simple container class that represents an SVG path string. */
class PathData {

    internal val pathDatums: Array<PathDatum>

    @JvmOverloads
    internal constructor(pathDatums: Array<PathDatum> = EMPTY_PATH_DATUMS) {
        this.pathDatums = pathDatums
    }

    internal constructor(pathData: PathData) {
        pathDatums = pathData.pathDatums.map { PathDatum(it) }.toTypedArray()
    }

    /**
     * Checks if this [PathData] object is morphable with another [PathData] object.
     *
     * @param pathData The [PathData] object to compare against.
     * @return true iff this [PathData] object is morphable with the provided [PathData]
     * object.
     */
    fun canMorphWith(pathData: PathData): Boolean {
        return PathDataUtils.canMorph(this, pathData)
    }

    /**
     * Interpolates this [PathData] object between two [PathData] objects by the given
     * fraction.
     *
     * @param from The starting [PathData] object.
     * @param to The ending [PathData] object.
     * @param fraction The interpolation fraction.
     * @throws IllegalArgumentException If the from or to [PathData] arguments aren't morphable
     * with this [PathData] object.
     */
    internal fun interpolate(from: PathData, to: PathData, fraction: Float) {
        if (!canMorphWith(from) || !canMorphWith(to)) {
            throw IllegalArgumentException("Can't interpolate between two incompatible paths")
        }
        for (i in from.pathDatums.indices) {
            pathDatums[i].interpolate(from.pathDatums[i], to.pathDatums[i], fraction)
        }
    }

    /** Each PathDatum object represents one command in the "d" attribute of an SVG pathData. */
    internal class PathDatum {

        var type: Char = ' '
        var params: FloatArray

        constructor(type: Char, params: FloatArray) {
            this.type = type
            this.params = params
        }

        constructor(n: PathDatum) {
            type = n.type
            params = Arrays.copyOfRange(n.params, 0, n.params.size)
        }

        /**
         * The current PathDatum will be interpolated between the from and to values according to the
         * current fraction.
         *
         * @param from The start value as a PathDatum.
         * @param to The end value as a PathDatum
         * @param fraction The fraction to interpolate.
         */
        fun interpolate(from: PathDatum, to: PathDatum, fraction: Float) {
            for (i in from.params.indices) {
                params[i] = from.params[i] * (1 - fraction) + to.params[i] * fraction
            }
        }
    }

    companion object {

        /**
         * Constructs a [PathData] object from the provided SVG path data string.
         *
         * @param pathData The SVG path data string to convert.
         * @return A [PathData] object represented by the provided SVG path data string.
         */
        @JvmStatic
        fun parse(pathData: String): PathData {
            return PathDataUtils.parse(pathData)
        }

        /**
         * Constructs a [Path] from the provided [PathData] object.
         *
         * @param pathData The SVG path data string to convert.
         * @return A [Path] represented by the provided SVG path data string.
         */
        @JvmStatic
        fun toPath(pathData: String): Path {
            return PathDataUtils.toPath(pathData)
        }

        /**
         * Constructs a [Path] from the provided [PathData] object.
         *
         * @param pathData The [PathData] object to convert.
         * @return A [Path] represented by the provided [PathData] object.
         */
        @JvmStatic
        fun toPath(pathData: PathData): Path {
            val path = Path()
            PathDataUtils.toPath(pathData, path)
            return path
        }

        /**
         * Initializes a [Path] from the provided [PathData] object.
         *
         * @param pathData The [PathData] object to convert.
         * @param outPath The [Path] to write to.
         */
        @JvmStatic
        fun toPath(pathData: PathData, outPath: Path) {
            PathDataUtils.toPath(pathData, outPath)
        }
    }
}
