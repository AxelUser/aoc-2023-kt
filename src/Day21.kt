fun main() {
    fun List<CharArray>.findStart(): Point2D {
        for (y in this.indices) {
            for (x in this[0].indices) {
                if (this[y][x] == 'S') return Point2D(x.toLong(), y.toLong())
            }
        }
        error("start not found")
    }

    fun List<CharArray>.print(marked: Set<Point2D>) {
        forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                if (Point2D(x.toLong(), y.toLong()) in marked) {
                    print('█')
                } else print(this[y][x])
            }
            kotlin.io.println()
        }
    }

    fun List<CharArray>.bfs(): Map<Point2D, Long> {
        val map = this
        val start = findStart()
        val queue = ArrayDeque(listOf(0L to start))
        return buildMap {
            while (queue.isNotEmpty()) {
                val (dist, point) = queue.removeFirst()
                if (point in this) continue
                put(point, dist)
                getAdjacent4(point)
                    .filter { p -> map[p.y.toInt()][p.x.toInt()] != '#' && p !in this }
                    .forEach {
                        queue.addLast((dist + 1) to it)
                    }
            }
        }
    }

    fun List<String>.parse(): List<CharArray> {
        return map { it.toCharArray() }
    }

    fun part1(steps: Int, input: List<CharArray>): Long {
        return input.bfs().values.count { dist -> dist <= steps && dist % 2L == 0L }.toLong()
    }

    fun part2(input: List<CharArray>): Long {
        val center = Point2D(65, 65)
        val visited = input.bfs()
        val evenCorners = visited.filter { (p, dist) -> p.l1(center) > 65 && dist % 2L == 0L }
        val oddCorners = visited.filter { (p, dist) -> p.l1(center) > 65 && dist % 2L == 1L }
        val evenFull = visited.values.count { dist -> dist % 2L == 0L }.toLong()
        val oddFull = visited.values.count { dist -> dist % 2L == 1L }.toLong()
        val n = 202300L

        return ((n + 1) * (n + 1) * oddFull) +
                ((n * n) * evenFull) -
                ((n + 1) * oddCorners.count()) +
                (n * evenCorners.count()) - n
    }

    val testInput = readInput("Day21_test").parse()
    val testResult = part1(6, testInput)
    check(testResult == 16L)

    val input = readInput("Day21").parse()
    part1(64, input).println()
    part2(input).println()
}