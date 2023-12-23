fun main() {
    fun List<String>.parse(mapping: (Char) -> Char): List<CharArray> {
        return map { it.map(mapping).toCharArray() }
    }

    fun List<CharArray>.findStartEnd(): Pair<Point2D, Point2D> {
        val start = Point2D(first().indexOf('.').toLong(), 0)
        val end = Point2D(last().indexOf('.').toLong(), lastIndex.toLong())
        return start to end
    }

    val slopes = mapOf(
        '^' to Direction4.Up,
        '>' to Direction4.Right,
        'v' to Direction4.Down,
        '<' to Direction4.Left,
    )

    fun List<CharArray>.tryMove(from: Point2D, dir: Direction4): Point2D? {
        val to = from + dir

        if (!containsPoint(to)) return null

        val currentSymbol = this[from.y.toInt()][from.x.toInt()]
        if (currentSymbol in slopes && slopes[currentSymbol] != dir) return null

        return when (val targetSymbol = this[to.y.toInt()][to.x.toInt()]) {
            in slopes -> if (slopes[targetSymbol]!!.isOppositeTo(dir)) null else to
            '.' -> to
            else -> null
        }
    }

    fun List<CharArray>.buildGraph(
        start: Point2D,
        end: Point2D,
        minEdges: Int
    ): Map<Point2D, List<Pair<Point2D, Long>>> {
        val splits = mutableMapOf(
            start to mutableListOf<Pair<Point2D, Long>>(),
            end to mutableListOf()
        )

        forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                val p = Point2D(x.toLong(), y.toLong())
                if ((c == '.' || c in slopes) && Direction4.entries.mapNotNull { tryMove(p, it) }.count() >= minEdges) {
                    splits[p] = mutableListOf()
                }
            }
        }

        splits.forEach { (splitPoint, edges) ->
            var dist = 0L
            var queue = setOf(splitPoint)
            val visited = mutableSetOf(splitPoint)
            while (queue.isNotEmpty()) {
                dist++
                queue = buildSet {
                    queue.forEach { current ->
                        Direction4.entries.mapNotNull { tryMove(current, it) }.filter { it !in visited }
                            .forEach { edge ->
                                if (edge in splits) {
                                    edges.add(edge to dist)
                                } else {
                                    this.add(edge)
                                    visited += edge
                                }
                            }
                    }
                }
            }
        }

        return splits
    }

    fun Map<Point2D, List<Pair<Point2D, Long>>>.dfs(
        current: Point2D,
        end: Point2D,
        len: Long = 0,
        visited: MutableSet<Point2D> = mutableSetOf()
    ): List<Long> {
        if (current == end) return listOf(len)
        val next = this[current]!!
        return buildList {
            next.filter { it.first !in visited }.forEach { (edge, dist) ->
                visited += edge
                addAll(dfs(edge, end, len + dist, visited))
                visited -= edge
            }
        }

    }

    fun part1(input: List<String>): Long {
        val parsed = input.parse { it }
        val (start, end) = parsed.findStartEnd()
        val paths = parsed.buildGraph(start, end, 2).dfs(start, end)
        return paths.max()
    }

    fun part2(input: List<String>): Long {
        val parsed = input.parse { if (it != '#') '.' else '#' }
        val (start, end) = parsed.findStartEnd()
        val paths = parsed.buildGraph(start, end, 3).dfs(start, end)
        return paths.max()
    }

    val testInput = readInput("Day23_test")
    var testResult = part1(testInput)
    check(testResult == 94L)
    testResult = part2(testInput)
    check(testResult == 154L)

    val input = readInput("Day23")
    part1(input).println()
    part2(input).println()
}