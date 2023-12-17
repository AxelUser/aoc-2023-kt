enum class Day17Direction(val offset: Point2D) {
    Up(Point2D(0, -1)),
    Right(Point2D(1, 0)),
    Down(Point2D(0, 1)),
    Left(Point2D(-1, 0)),
}

fun main() {
    data class Node(val point: Point2D, val direction: Day17Direction, val stepsInDir: Long)

    fun List<IntArray>.getNodesFrom(node: Node, minSteps: Long, maxSteps: Long): List<Node> {
        val maxX = this[0].lastIndex
        val maxY = lastIndex
        val nodes = mutableListOf<Node>()
        for (newDir in Day17Direction.entries) {
            if (newDir.offset == -node.direction.offset) continue
            if (newDir != node.direction && node.stepsInDir < minSteps) continue
            val steps = if (newDir == node.direction) node.stepsInDir + 1 else 1
            if (steps > maxSteps) continue
            val point = node.point + newDir.offset
            if (!point.isInBounds(maxX, maxY)) continue
            nodes += Node(point, newDir, steps)
        }
        return nodes
    }

    fun List<IntArray>.getCost(node: Node): Long {
        return this[node.point.y.toInt()][node.point.x.toInt()].toLong()
    }

    fun List<IntArray>.findMaxHeatPath(start: Point2D, maxSteps: Long, minSteps: Long = 1): Long {
        val end = Point2D(this[0].lastIndex.toLong(), lastIndex.toLong())
        val costs = dijkstraSearch(
            startingPoints = Day17Direction.entries.map { Node(start, it, 0) },
            neighborProducer = { getNodesFrom(it, minSteps, maxSteps) },
            costFunction = ::getCost
        )

        return costs.filter { (n) -> n.point == end }.minOf { it.value }
    }

    fun List<String>.parse(): List<IntArray> {
        return map { line -> line.map { c -> c.digitToInt() }.toIntArray() }
    }

    fun part1(input: List<IntArray>): Long {
        return input.findMaxHeatPath(Point2D(0, 0), 3)
    }

    fun part2(input: List<IntArray>): Long {
        return input.findMaxHeatPath(Point2D(0, 0), 10, 4)
    }

    val testInput = readInput("Day17_test").parse()
    var testResult = part1(testInput)
    check(testResult == 102L)
    testResult = part2(testInput)
    check(testResult == 94L)

    val input = readInput("Day17").parse()
    part1(input).println()
    part2(input).println()
}