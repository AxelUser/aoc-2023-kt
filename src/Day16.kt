enum class Day16Direction(val offset: Point2D) {
    Up(Point2D(0, -1)),
    Right(Point2D(1, 0)),
    Down(Point2D(0, 1)),
    Left(Point2D(-1, 0)),
}

fun main() {

    val directionMap = mapOf(
            (Day16Direction.Up to '/') to listOf(Day16Direction.Right),
            (Day16Direction.Right to '/') to listOf(Day16Direction.Up),
            (Day16Direction.Down to '/') to listOf(Day16Direction.Left),
            (Day16Direction.Left to '/') to listOf(Day16Direction.Down),

            (Day16Direction.Up to '\\') to listOf(Day16Direction.Left),
            (Day16Direction.Right to '\\') to listOf(Day16Direction.Down),
            (Day16Direction.Down to '\\') to listOf(Day16Direction.Right),
            (Day16Direction.Left to '\\') to listOf(Day16Direction.Up),

            (Day16Direction.Up to '-') to listOf(Day16Direction.Left, Day16Direction.Right),
            (Day16Direction.Down to '-') to listOf(Day16Direction.Left, Day16Direction.Right),

            (Day16Direction.Right to '|') to listOf(Day16Direction.Up, Day16Direction.Down),
            (Day16Direction.Left to '|') to listOf(Day16Direction.Up, Day16Direction.Down),
    )

    fun List<CharArray>.checkDirection(current: Point2D, prevDir: Day16Direction): List<Day16Direction> {
        return directionMap[prevDir to this[current.y.toInt()][current.x.toInt()]] ?: listOf(prevDir)
    }

    fun List<CharArray>.bfs(start: Point2D, startDir: Day16Direction): Int {
        val maxX = this[0].lastIndex
        val maxY = lastIndex
        val visited = mutableSetOf<Pair<Point2D, Day16Direction>>()
        val queue = ArrayDeque<Pair<Point2D, Day16Direction>>().apply { addLast(start to startDir) }
        while (queue.isNotEmpty()) {
            val (current, movedInDir) = queue.removeFirst()
            if (!current.isInBounds(maxX, maxY)) continue
            if (!visited.add(current to movedInDir)) continue
            checkDirection(current, movedInDir)
                    .map { newDir -> current + newDir.offset to newDir }
                    .forEach { queue.addLast(it) }
        }
        return visited.map { it.first }.toSet().size
    }

    fun List<String>.parse(): List<CharArray> {
        return map { it.toCharArray() }
    }

    fun part1(input: List<CharArray>): Int {
        return input.bfs(Point2D(0, 0), Day16Direction.Right)
    }

    fun List<CharArray>.generateStarts(): Sequence<Pair<Point2D, Day16Direction>> {
        val maxX = this[0].lastIndex.toLong()
        val maxY = this.lastIndex.toLong()
        return sequence {
            for (x in 0..maxX) {
                yield(Point2D(x, 0) to Day16Direction.Down)
                yield(Point2D(x, maxY) to Day16Direction.Up)
            }
            for (y in 0..maxY) {
                yield(Point2D(0, y) to Day16Direction.Right)
                yield(Point2D(maxX, y) to Day16Direction.Left)
            }
        }
    }

    fun part2(input: List<CharArray>): Int {
        return input.generateStarts().maxOf { (p, d) -> input.bfs(p, d) }
    }

    val testInput = readInput("Day16_test").parse()
    var testResult = part1(testInput)
    check(testResult == 46)
    testResult = part2(testInput)
    check(testResult == 51)

    val input = readInput("Day16").parse()
    part1(input).println()
    part2(input).println()
}