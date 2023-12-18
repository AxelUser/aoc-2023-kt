fun main() {
    fun List<String>.parse(expandBy: Long): List<Point2D> {
        val set = mutableSetOf<Point2D>()
        val cols = BooleanArray(this[0].length)
        val rows = BooleanArray(size)
        forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                if (c == '#') {
                    if (!cols[x]) cols[x] = true
                    if (!rows[y]) rows[y] = true
                    set += Point2D(x.toLong(), y.toLong())
                }
            }
        }

        val out = mutableListOf<Point2D>()
        for (p in set) {
            val xInc = cols.asSequence().take(p.x.toInt()).count { !it } * (expandBy - 1)
            val yInc = rows.asSequence().take(p.y.toInt()).count { !it } * (expandBy - 1)

            out += if (xInc > 0 || yInc > 0) {
                Point2D(p.x + xInc, p.y + yInc)
            } else p
        }

        return out
    }

    fun List<String>.solve(expandBy: Long): Long {
        val expanded = parse(expandBy)
        var sum = 0L
        for (i in 0..expanded.lastIndex) {
            for (j in i + 1..expanded.lastIndex) {
                sum += expanded[i].l1(expanded[j])
            }
        }

        return sum
    }

    val testInput = readInput("Day11_test")
    var testResult = testInput.solve(2)
    check(testResult == 374L)
    testResult = testInput.solve(10)
    check(testResult == 1030L)

    val input = readInput("Day11")
    input.solve(2).println()
    input.solve(1000000).println()
}