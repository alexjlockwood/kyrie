package com.github.alexjlockwood.kyrie

import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.os.Build
import androidx.annotation.FloatRange
import androidx.annotation.Size

private const val MAX_NUM_POINTS = 100
private const val FRACTION_OFFSET = 0
private const val X_OFFSET = 1
private const val Y_OFFSET = 2
private const val NUM_COMPONENTS = 3

/**
 * PathKeyframeSet relies on approximating the Path as a series of line segments. The line segments
 * are recursively divided until there is less than 1/2 pixel error between the lines and the curve.
 * Each point of the line segment is converted to a [Keyframe] and a linear interpolation
 * between keyframes creates a good approximation of the curve.
 */
internal class PathKeyframeSet(path: Path) : KeyframeSet<PointF>() {

    private val tempPointF = PointF()
    private val keyframeData: FloatArray

    override val keyframes: List<Keyframe<PointF>> = emptyList()

    init {
        if (path.isEmpty) {
            throw IllegalArgumentException("The path must not be empty")
        }
        keyframeData = approximate(path, 0.5f)
    }

    override fun getAnimatedValue(fraction: Float): PointF {
        val numPoints = keyframeData.size / NUM_COMPONENTS
        if (fraction < 0) {
            return interpolateInRange(fraction, 0, 1)
        }
        if (fraction > 1) {
            return interpolateInRange(fraction, numPoints - 2, numPoints - 1)
        }
        if (fraction == 0f) {
            return pointForIndex(0)
        }
        if (fraction == 1f) {
            return pointForIndex(numPoints - 1)
        }
        // Binary search for the correct section.
        var low = 0
        var high = numPoints - 1
        while (low <= high) {
            val mid = (low + high) / 2
            val midFraction = keyframeData[mid * NUM_COMPONENTS + FRACTION_OFFSET]
            if (fraction < midFraction) {
                high = mid - 1
            } else if (fraction > midFraction) {
                low = mid + 1
            } else {
                return pointForIndex(mid)
            }
        }
        // Now high is below the fraction and low is above the fraction.
        return interpolateInRange(fraction, high, low)
    }

    private fun interpolateInRange(fraction: Float, startIndex: Int, endIndex: Int): PointF {
        val startBase = startIndex * NUM_COMPONENTS
        val endBase = endIndex * NUM_COMPONENTS
        val startFraction = keyframeData[startBase + FRACTION_OFFSET]
        val endFraction = keyframeData[endBase + FRACTION_OFFSET]
        val intervalFraction = (fraction - startFraction) / (endFraction - startFraction)
        val startX = keyframeData[startBase + X_OFFSET]
        val endX = keyframeData[endBase + X_OFFSET]
        val startY = keyframeData[startBase + Y_OFFSET]
        val endY = keyframeData[endBase + Y_OFFSET]
        val x = lerp(startX, endX, intervalFraction)
        val y = lerp(startY, endY, intervalFraction)
        tempPointF.set(x, y)
        return tempPointF
    }

    private fun pointForIndex(index: Int): PointF {
        val base = index * NUM_COMPONENTS
        val xOffset = base + X_OFFSET
        val yOffset = base + Y_OFFSET
        tempPointF.set(keyframeData[xOffset], keyframeData[yOffset])
        return tempPointF
    }
}

/** Implementation of [Path.approximate] for pre-O devices.  */
@Size(multiple = 3)
private fun approximate(path: Path, @FloatRange(from = 0.0) acceptableError: Float): FloatArray {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        return path.approximate(acceptableError)
    }
    if (acceptableError < 0) {
        throw IllegalArgumentException("acceptableError must be greater than or equal to 0")
    }
    // Measure the total length the whole pathData.
    val measureForTotalLength = PathMeasure(path, false)
    var totalLength = 0f
    // The sum of the previous contour plus the current one. Using the sum here
    // because we want to directly subtract from it later.
    val summedContourLengths = mutableListOf<Float>()
    summedContourLengths.add(0f)
    do {
        val pathLength = measureForTotalLength.length
        totalLength += pathLength
        summedContourLengths.add(totalLength)
    } while (measureForTotalLength.nextContour())

    // Now determine how many sample points we need, and the step for next sample.
    val pathMeasure = PathMeasure(path, false)

    val numPoints = Math.min(MAX_NUM_POINTS, (totalLength / acceptableError).toInt() + 1)

    val coords = FloatArray(NUM_COMPONENTS * numPoints)
    val position = FloatArray(2)

    var contourIndex = 0
    val step = totalLength / (numPoints - 1)
    var cumulativeDistance = 0f

    // For each sample point, determine whether we need to move on to next contour.
    // After we find the right contour, then sample it using the current distance value minus
    // the previously sampled contours' total length.
    for (i in 0 until numPoints) {
        // The cumulative distance traveled minus the total length of the previous contours
        // (not including the current contour).
        val contourDistance = cumulativeDistance - summedContourLengths[contourIndex]
        pathMeasure.getPosTan(contourDistance, position, null)

        coords[i * NUM_COMPONENTS + FRACTION_OFFSET] = cumulativeDistance / totalLength
        coords[i * NUM_COMPONENTS + X_OFFSET] = position[0]
        coords[i * NUM_COMPONENTS + Y_OFFSET] = position[1]

        cumulativeDistance = Math.min(cumulativeDistance + step, totalLength)

        // Using a while statement is necessary in the rare case where step is greater than
        // the length a path contour.
        while (summedContourLengths[contourIndex + 1] < cumulativeDistance) {
            contourIndex++
            pathMeasure.nextContour()
        }
    }

    coords[(numPoints - 1) * NUM_COMPONENTS + FRACTION_OFFSET] = 1f
    return coords
}

private fun lerp(a: Float, b: Float, @FloatRange(from = 0.0, to = 1.0) t: Float): Float {
    return a + (b - a) * t
}
