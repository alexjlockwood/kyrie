package com.github.alexjlockwood.kyrie

import android.graphics.Path
import android.util.Log
import java.util.ArrayList
import java.util.Arrays

internal object PathDataUtils {

    private const val TAG = "PathDataUtils"

    private val EMPTY_PARAMS = floatArrayOf()

    fun toPath(pathData: String?): Path {
        var pathData = pathData
        if (pathData == null) {
            pathData = ""
        }
        val path = Path()
        val parsedPathData: PathData
        try {
            parsedPathData = parse(pathData)
        } catch (e: RuntimeException) {
            throw IllegalArgumentException("Error in parsing $pathData", e)
        }

        toPath(parsedPathData, path)
        return path
    }

    fun parse(pathData: String?): PathData {
        var pathData = pathData
        if (pathData == null) {
            pathData = ""
        }
        var start = 0
        var end = 1
        val list = ArrayList<PathData.PathDatum>()
        while (end < pathData.length) {
            end = nextStart(pathData, end)
            val s = pathData.substring(start, end).trim { it <= ' ' }
            if (s.isNotEmpty()) {
                addNode(list, s[0], getFloats(s))
            }
            start = end
            end++
        }
        if (end - start == 1 && start < pathData.length) {
            addNode(list, pathData[start], EMPTY_PARAMS)
        }
        return PathData(list.toTypedArray())
    }

    fun canMorph(fromPathData: PathData?, toPathData: PathData?): Boolean {
        if (fromPathData == null || toPathData == null) {
            return false
        }
        val from = fromPathData.pathDatums
        val to = toPathData.pathDatums
        if (from.size != to.size) {
            return false
        }
        for (i in from.indices) {
            if (from[i].type != to[i].type || from[i].params.size != to[i].params.size) {
                return false
            }
        }
        return true
    }

    fun toPath(pathData: PathData, path: Path) {
        val nodes = pathData.pathDatums
        val current = FloatArray(6)
        var previousCommand = 'm'

        for (i in nodes.indices) {
            addCommand(path, current, previousCommand, nodes[i].type, nodes[i].params)
            previousCommand = nodes[i].type
        }
    }

    private fun nextStart(s: String, end: Int): Int {
        var end = end
        var c: Char
        while (end < s.length) {
            c = s[end]
            // Note that 'e' or 'E' are not valid pathData commands, but could be
            // used for floating point numbers' scientific notation.
            // Therefore, when searching for next command, we should ignore 'e'
            // and 'E'.
            if (((c - 'A') * (c - 'Z') <= 0 || (c - 'a') * (c - 'z') <= 0) && c != 'e' && c != 'E') {
                return end
            }
            end++
        }
        return end
    }

    private fun addNode(list: MutableList<PathData.PathDatum>, cmd: Char, value: FloatArray) {
        list.add(PathData.PathDatum(cmd, value))
    }

    private class ExtractFloatResult internal constructor() {
        // We need to return the position of the next separator and whether the
        // next float starts with a '-' or a '.'.
        internal var endPosition: Int = 0
        internal var endWithNegOrDot: Boolean = false
    }

    /**
     * Parse the floats in the string. This is an optimized version of parseFloat(s.split(",|\\s"));
     *
     * @param s the string containing a command and list of floats
     * @return array of floats
     */
    private fun getFloats(s: String): FloatArray {
        if (s[0] == 'z' || s[0] == 'Z') {
            return EMPTY_PARAMS
        }
        try {
            val results = FloatArray(s.length)
            var count = 0
            var startPosition = 1
            var endPosition: Int

            val result = ExtractFloatResult()
            val totalLength = s.length

            // The startPosition should always be the first character of the
            // current number, and endPosition is the character after the current
            // number.
            while (startPosition < totalLength) {
                extract(s, startPosition, result)
                endPosition = result.endPosition
                if (startPosition < endPosition) {
                    results[count++] = s.substring(startPosition, endPosition).toFloat()
                }
                startPosition = if (result.endWithNegOrDot) {
                    // Keep the '-' or '.' sign with next number.
                    endPosition
                } else {
                    endPosition + 1
                }
            }
            return Arrays.copyOfRange(results, 0, count)
        } catch (e: NumberFormatException) {
            throw RuntimeException("error in parsing \"$s\"", e)
        }

    }

    /**
     * Calculate the position of the next comma or space or negative sign
     *
     * @param s the string to search
     * @param start the position to start searching
     * @param result the result of the extraction, including the position of the the starting position
     * of next number, whether it is ending with a '-'.
     */
    private fun extract(s: String, start: Int, result: ExtractFloatResult) {
        // Now looking for ' ', ',', '.' or '-' from the start.
        var currentIndex = start
        var foundSeparator = false
        result.endWithNegOrDot = false
        var secondDot = false
        var isExponential = false
        while (currentIndex < s.length) {
            val isPrevExponential = isExponential
            isExponential = false
            when (s[currentIndex]) {
                ' ', ',' -> foundSeparator = true
                '-' ->
                    // The negative sign following a 'e' or 'E' is not a separator.
                    if (currentIndex != start && !isPrevExponential) {
                        foundSeparator = true
                        result.endWithNegOrDot = true
                    }
                '.' -> if (!secondDot) {
                    secondDot = true
                } else {
                    // This is the second dot, and it is considered as a separator.
                    foundSeparator = true
                    result.endWithNegOrDot = true
                }
                'e', 'E' -> isExponential = true
            }
            if (foundSeparator) {
                break
            }
            currentIndex++
        }
        // When there is nothing found, then we put the end position to the end
        // of the string.
        result.endPosition = currentIndex
    }

    private fun addCommand(path: Path, current: FloatArray, prevCmd: Char, cmd: Char, value: FloatArray) {
        var increment = 2
        var currentX = current[0]
        var currentY = current[1]
        var ctrlPointX = current[2]
        var ctrlPointY = current[3]
        var currentSegmentStartX = current[4]
        var currentSegmentStartY = current[5]
        var reflectiveCtrlPointX: Float
        var reflectiveCtrlPointY: Float
        var prevCmd = prevCmd

        when (cmd) {
            'z', 'Z' -> {
                path.close()
                // Path is closed here, but we need to move the pen to the
                // closed position. So we cache the segment's starting position,
                // and restore it here.
                currentX = currentSegmentStartX
                currentY = currentSegmentStartY
                ctrlPointX = currentSegmentStartX
                ctrlPointY = currentSegmentStartY
                path.moveTo(currentX, currentY)
            }
            'm', 'M', 'l', 'L', 't', 'T' -> increment = 2
            'h', 'H', 'v', 'V' -> increment = 1
            'c', 'C' -> increment = 6
            's', 'S', 'q', 'Q' -> increment = 4
            'a', 'A' -> increment = 7
        }

        var k = 0
        while (k < value.size) {
            when (cmd) {
                // moveto - Start a new sub-pathData (relative)
                'm' -> {
                    currentX += value[k]
                    currentY += value[k + 1]
                    if (k > 0) {
                        // According to the spec, if a moveto is followed by multiple
                        // pairs of coordinates, the subsequent pairs are treated as
                        // implicit lineto commands.
                        path.rLineTo(value[k], value[k + 1])
                    } else {
                        path.rMoveTo(value[k], value[k + 1])
                        currentSegmentStartX = currentX
                        currentSegmentStartY = currentY
                    }
                }
                // moveto - Start a new sub-pathData
                'M' -> {
                    currentX = value[k]
                    currentY = value[k + 1]
                    if (k > 0) {
                        // According to the spec, if a moveto is followed by multiple
                        // pairs of coordinates, the subsequent pairs are treated as
                        // implicit lineto commands.
                        path.lineTo(value[k], value[k + 1])
                    } else {
                        path.moveTo(value[k], value[k + 1])
                        currentSegmentStartX = currentX
                        currentSegmentStartY = currentY
                    }
                }
                // lineto - Draw a line from the current point (relative)
                'l' -> {
                    path.rLineTo(value[k], value[k + 1])
                    currentX += value[k]
                    currentY += value[k + 1]
                }
                // lineto - Draw a line from the current point
                'L' -> {
                    path.lineTo(value[k], value[k + 1])
                    currentX = value[k]
                    currentY = value[k + 1]
                }
                // horizontal lineto - Draws a horizontal line (relative)
                'h' -> {
                    path.rLineTo(value[k], 0f)
                    currentX += value[k]
                }
                // horizontal lineto - Draws a horizontal line
                'H' -> {
                    path.lineTo(value[k], currentY)
                    currentX = value[k]
                }
                // vertical lineto - Draws a vertical line from the current point (r)
                'v' -> {
                    path.rLineTo(0f, value[k])
                    currentY += value[k]
                }
                // vertical lineto - Draws a vertical line from the current point
                'V' -> {
                    path.lineTo(currentX, value[k])
                    currentY = value[k]
                }
                // curveto - Draws a cubic Bezier curve (relative)
                'c' -> {
                    path.rCubicTo(value[k], value[k + 1], value[k + 2], value[k + 3], value[k + 4], value[k + 5])

                    ctrlPointX = currentX + value[k + 2]
                    ctrlPointY = currentY + value[k + 3]
                    currentX += value[k + 4]
                    currentY += value[k + 5]
                }
                // curveto - Draws a cubic Bezier curve
                'C' -> {
                    path.cubicTo(value[k], value[k + 1], value[k + 2], value[k + 3], value[k + 4], value[k + 5])
                    currentX = value[k + 4]
                    currentY = value[k + 5]
                    ctrlPointX = value[k + 2]
                    ctrlPointY = value[k + 3]
                }
                // smooth curveto - Draws a cubic Bezier curve (reflective cp)
                's' -> {
                    reflectiveCtrlPointX = 0f
                    reflectiveCtrlPointY = 0f
                    if (prevCmd == 'c' || prevCmd == 's' || prevCmd == 'C' || prevCmd == 'S') {
                        reflectiveCtrlPointX = currentX - ctrlPointX
                        reflectiveCtrlPointY = currentY - ctrlPointY
                    }
                    path.rCubicTo(
                            reflectiveCtrlPointX,
                            reflectiveCtrlPointY,
                            value[k],
                            value[k + 1],
                            value[k + 2],
                            value[k + 3])

                    ctrlPointX = currentX + value[k]
                    ctrlPointY = currentY + value[k + 1]
                    currentX += value[k + 2]
                    currentY += value[k + 3]
                }
                // shorthand/smooth curveto Draws a cubic Bezier curve (reflective cp)
                'S' -> {
                    reflectiveCtrlPointX = currentX
                    reflectiveCtrlPointY = currentY
                    if (prevCmd == 'c' || prevCmd == 's' || prevCmd == 'C' || prevCmd == 'S') {
                        reflectiveCtrlPointX = 2 * currentX - ctrlPointX
                        reflectiveCtrlPointY = 2 * currentY - ctrlPointY
                    }
                    path.cubicTo(
                            reflectiveCtrlPointX,
                            reflectiveCtrlPointY,
                            value[k],
                            value[k + 1],
                            value[k + 2],
                            value[k + 3])
                    ctrlPointX = value[k]
                    ctrlPointY = value[k + 1]
                    currentX = value[k + 2]
                    currentY = value[k + 3]
                }
                // Draws a quadratic Bezier (relative)
                'q' -> {
                    path.rQuadTo(value[k], value[k + 1], value[k + 2], value[k + 3])
                    ctrlPointX = currentX + value[k]
                    ctrlPointY = currentY + value[k + 1]
                    currentX += value[k + 2]
                    currentY += value[k + 3]
                }
                // Draws a quadratic Bezier
                'Q' -> {
                    path.quadTo(value[k], value[k + 1], value[k + 2], value[k + 3])
                    ctrlPointX = value[k]
                    ctrlPointY = value[k + 1]
                    currentX = value[k + 2]
                    currentY = value[k + 3]
                }
                // Draws a quadratic Bezier curve (reflective control point) (relative)
                't' -> {
                    reflectiveCtrlPointX = 0f
                    reflectiveCtrlPointY = 0f
                    if (prevCmd == 'q' || prevCmd == 't' || prevCmd == 'Q' || prevCmd == 'T') {
                        reflectiveCtrlPointX = currentX - ctrlPointX
                        reflectiveCtrlPointY = currentY - ctrlPointY
                    }
                    path.rQuadTo(reflectiveCtrlPointX, reflectiveCtrlPointY, value[k], value[k + 1])
                    ctrlPointX = currentX + reflectiveCtrlPointX
                    ctrlPointY = currentY + reflectiveCtrlPointY
                    currentX += value[k]
                    currentY += value[k + 1]
                }
                // Draws a quadratic Bezier curve (reflective control point)
                'T' -> {
                    reflectiveCtrlPointX = currentX
                    reflectiveCtrlPointY = currentY
                    if (prevCmd == 'q' || prevCmd == 't' || prevCmd == 'Q' || prevCmd == 'T') {
                        reflectiveCtrlPointX = 2 * currentX - ctrlPointX
                        reflectiveCtrlPointY = 2 * currentY - ctrlPointY
                    }
                    path.quadTo(reflectiveCtrlPointX, reflectiveCtrlPointY, value[k], value[k + 1])
                    ctrlPointX = reflectiveCtrlPointX
                    ctrlPointY = reflectiveCtrlPointY
                    currentX = value[k]
                    currentY = value[k + 1]
                }
                // Draws an elliptical arc
                'a' -> {
                    // (rx ry x-axis-rotation large-arc-flag sweep-flag x y)
                    drawArc(
                            path,
                            currentX,
                            currentY,
                            value[k + 5] + currentX,
                            value[k + 6] + currentY,
                            value[k],
                            value[k + 1],
                            value[k + 2],
                            value[k + 3] != 0f,
                            value[k + 4] != 0f)
                    currentX += value[k + 5]
                    currentY += value[k + 6]
                    ctrlPointX = currentX
                    ctrlPointY = currentY
                }
                // Draws an elliptical arc
                'A' -> {
                    drawArc(
                            path,
                            currentX,
                            currentY,
                            value[k + 5],
                            value[k + 6],
                            value[k],
                            value[k + 1],
                            value[k + 2],
                            value[k + 3] != 0f,
                            value[k + 4] != 0f)
                    currentX = value[k + 5]
                    currentY = value[k + 6]
                    ctrlPointX = currentX
                    ctrlPointY = currentY
                }
            }
            prevCmd = cmd
            k += increment
        }
        current[0] = currentX
        current[1] = currentY
        current[2] = ctrlPointX
        current[3] = ctrlPointY
        current[4] = currentSegmentStartX
        current[5] = currentSegmentStartY
    }

    private fun drawArc(
            p: Path,
            x0: Float,
            y0: Float,
            x1: Float,
            y1: Float,
            a: Float,
            b: Float,
            theta: Float,
            isMoreThanHalf: Boolean,
            isPositiveArc: Boolean
    ) {
        /* Convert rotation angle from degrees to radians */
        val thetaD = Math.toRadians(theta.toDouble())
        /* Pre-compute rotation matrix entries */
        val cosTheta = Math.cos(thetaD)
        val sinTheta = Math.sin(thetaD)
        /* Transform (x0, y0) and (x1, y1) into unit space */
        /* using (inverse) rotation, followed by (inverse) scale */
        val x0p = (x0 * cosTheta + y0 * sinTheta) / a
        val y0p = (-x0 * sinTheta + y0 * cosTheta) / b
        val x1p = (x1 * cosTheta + y1 * sinTheta) / a
        val y1p = (-x1 * sinTheta + y1 * cosTheta) / b

        /* Compute differences and averages */
        val dx = x0p - x1p
        val dy = y0p - y1p
        val xm = (x0p + x1p) / 2
        val ym = (y0p + y1p) / 2
        /* Solve for intersecting unit circles */
        val dsq = dx * dx + dy * dy
        if (dsq == 0.0) {
            Log.w(TAG, " Points are coincident")
            return  /* Points are coincident */
        }
        val disc = 1.0 / dsq - 1.0 / 4.0
        if (disc < 0.0) {
            Log.w(TAG, "Points are too far apart $dsq")
            val adjust = (Math.sqrt(dsq) / 1.99999).toFloat()
            drawArc(p, x0, y0, x1, y1, a * adjust, b * adjust, theta, isMoreThanHalf, isPositiveArc)
            return  /* Points are too far apart */
        }

        val s = Math.sqrt(disc)
        val sdx = s * dx
        val sdy = s * dy
        var cx: Double
        var cy: Double
        if (isMoreThanHalf == isPositiveArc) {
            cx = xm - sdy
            cy = ym + sdx
        } else {
            cx = xm + sdy
            cy = ym - sdx
        }

        val eta0 = Math.atan2(y0p - cy, x0p - cx)

        val eta1 = Math.atan2(y1p - cy, x1p - cx)

        var sweep = eta1 - eta0
        if (isPositiveArc != sweep >= 0) {
            if (sweep > 0) {
                sweep -= 2 * Math.PI
            } else {
                sweep += 2 * Math.PI
            }
        }

        cx *= a.toDouble()
        cy *= b.toDouble()
        cx = cx * cosTheta - cy * sinTheta
        cy = cx * sinTheta + cy * cosTheta

        arcToBezier(p, cx, cy, a.toDouble(), b.toDouble(), x0.toDouble(), y0.toDouble(), thetaD, eta0, sweep)
    }

    /**
     * Converts an arc to cubic Bezier segments and records them in p.
     *
     * @param p The target for the cubic Bezier segments
     * @param cx The x coordinate center of the ellipse
     * @param cy The y coordinate center of the ellipse
     * @param a The radius of the ellipse in the horizontal direction
     * @param b The radius of the ellipse in the vertical direction
     * @param e1x E(eta1) x coordinate of the starting point of the arc
     * @param e1y E(eta2) y coordinate of the starting point of the arc
     * @param theta The angle that the ellipse bounding rectangle makes with horizontal plane
     * @param start The start angle of the arc on the ellipse
     * @param sweep The angle (positive or negative) of the sweep of the arc on the ellipse
     */
    private fun arcToBezier(
            p: Path,
            cx: Double,
            cy: Double,
            a: Double,
            b: Double,
            e1x: Double,
            e1y: Double,
            theta: Double,
            start: Double,
            sweep: Double
    ) {
        var e1x = e1x
        var e1y = e1y
        // Taken from equations at: http://spaceroots.org/documents/ellipse/node8.html
        // and http://www.spaceroots.org/documents/ellipse/node22.html

        // Maximum of 45 degrees per cubic Bezier segment
        val numSegments = Math.ceil(Math.abs(sweep * 4 / Math.PI)).toInt()

        var eta1 = start
        val cosTheta = Math.cos(theta)
        val sinTheta = Math.sin(theta)
        val cosEta1 = Math.cos(eta1)
        val sinEta1 = Math.sin(eta1)
        var ep1x = -a * cosTheta * sinEta1 - b * sinTheta * cosEta1
        var ep1y = -a * sinTheta * sinEta1 + b * cosTheta * cosEta1

        val anglePerSegment = sweep / numSegments
        for (i in 0 until numSegments) {
            val eta2 = eta1 + anglePerSegment
            val sinEta2 = Math.sin(eta2)
            val cosEta2 = Math.cos(eta2)
            val e2x = cx + a * cosTheta * cosEta2 - b * sinTheta * sinEta2
            val e2y = cy + a * sinTheta * cosEta2 + b * cosTheta * sinEta2
            val ep2x = -a * cosTheta * sinEta2 - b * sinTheta * cosEta2
            val ep2y = -a * sinTheta * sinEta2 + b * cosTheta * cosEta2
            val tanDiff2 = Math.tan((eta2 - eta1) / 2)
            val alpha = Math.sin(eta2 - eta1) * (Math.sqrt(4 + 3.0 * tanDiff2 * tanDiff2) - 1) / 3
            val q1x = e1x + alpha * ep1x
            val q1y = e1y + alpha * ep1y
            val q2x = e2x - alpha * ep2x
            val q2y = e2y - alpha * ep2y

            // Adding this no-op call to workaround a proguard related issue.
            p.rLineTo(0f, 0f)

            p.cubicTo(q1x.toFloat(), q1y.toFloat(), q2x.toFloat(), q2y.toFloat(), e2x.toFloat(), e2y.toFloat())
            eta1 = eta2
            e1x = e2x
            e1y = e2y
            ep1x = ep2x
            ep1y = ep2y
        }
    }
}