fun main() {
    fun List<String>.parse(): List<Pair<Point3D, Point3D>> {
        val regex = Regex("-?\\d+")
        return asSequence().flatMap { regex.findAll(it).map { m -> m.value.toLong() } }
            .chunked(3).map { (x, y, z) -> Point3D(x, y, z) }.chunked(2).map { (p, v) -> p to v }.toList()
    }

    fun calcSlopeAndIntercept(pos: Point3D, vel: Point3D): Pair<Double, Double> {
        val (x, y, _) = pos
        val (dx, dy, _) = vel
        val slope = dy.toDouble() / dx
        val intercept = y - slope * x
        return slope to intercept
    }

    fun List<Pair<Point3D, Point3D>>.countInterceptions(xyMin: Long, xyMax: Long): Long {
        var count = 0L
        forEachIndexed { i, (pos1, vel1) ->
            val (m1, b1) = calcSlopeAndIntercept(pos1, vel1)
            for (j in i + 1..lastIndex) {
                val (pos2, vel2) = this[j]
                val (m2, b2) = calcSlopeAndIntercept(pos2, vel2)

                if (m1 == m2) continue

                val xCross = (b2 - b1) / (m1 - m2)
                val yCross = m1 * xCross + b1
                if (xCross in xyMin.toDouble()..xyMax.toDouble() && yCross in xyMin.toDouble()..xyMax.toDouble()) {
                    val (x1) = pos1
                    val (dx1) = vel1
                    val (x2) = pos2
                    val (dx2) = vel2
                    val tCross1 = (xCross - x1) / dx1
                    val tCross2 = (xCross - x2) / dx2
                    if (tCross1 >= 0 && tCross2 >= 0) count++
                }
            }
        }


        return count
    }

    fun part1(input: List<String>, xyMin: Long, xyMax: Long): Long {
        val parsed = input.parse()
        return parsed.countInterceptions(xyMin, xyMax)
    }

    fun part2(input: List<String>): Long {
        // solved via external program
        return 0L
    }

    val testInput = readInput("Day24_test")
    var testResult = part1(testInput, 7, 27)
    check(testResult == 2L)
    testResult = part2(testInput)
    check(testResult == 0L)

    val input = readInput("Day24")
    part1(input, 200000000000000, 400000000000000).println()
    part2(input).println()
}