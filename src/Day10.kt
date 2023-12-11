import kotlin.math.ceil

private data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }
}

private enum class Direction(val offset: Point) {
    North(Point(0, -1)),
    East(Point(1, 0)),
    South(Point(0, 1)),
    West(Point(-1, 0))
}

private data class Pipe(val point: Point, val exit: Direction)

fun main() {
    val mirroredDirections = mapOf(
            Direction.North to Direction.South,
            Direction.South to Direction.North,
            Direction.West to Direction.East,
            Direction.East to Direction.West
    )

    val symbols = mapOf(
            '|' to listOf(Direction.North, Direction.South),
            '-' to listOf(Direction.West, Direction.East),
            'L' to listOf(Direction.North, Direction.East),
            'J' to listOf(Direction.North, Direction.West),
            '7' to listOf(Direction.South, Direction.West),
            'F' to listOf(Direction.South, Direction.East),
            'S' to Direction.entries
    )

    fun List<String>.hasPipe(target: Point): Boolean {
        val (x, y) = target
        val lastY = lastIndex
        val lastX = this[0].lastIndex
        if (y < 0 || y > lastY || x < 0 || x > lastX) return false
        return this[y][x] != '.'
    }

    fun List<String>.move(from: Point, to: Direction): Pipe? {
        val target = from + to.offset
        if (!hasPipe(target)) return null

        val directions = symbols[this[target.y][target.x]] ?: error("Unknown symbol at $target")
        val mirroredDir = mirroredDirections[to] ?: error("No mirrored direction for $to")
        return if (directions.contains(mirroredDir))
            Pipe(target, directions.first { it != mirroredDir })
        else null
    }

    fun List<String>.findStartPoint(): Point {
        forEachIndexed { y, line ->
            line.indexOf('S').let { x ->
                if (x >= 0) {
                    return Point(x, y)
                }
            }
        }

        error("Failed to find S")
    }

    fun List<String>.findStart(): Pipe {
        val start = findStartPoint()
        for (dir in Direction.entries) {
            move(start, dir)?.also { return Pipe(start, dir) }
        }

        error("Failed to find starting point")
    }

    fun List<String>.traverseLoop(): Sequence<Point> {
        val start = findStart()
        var next = move(start.point, start.exit) ?: error("Can't traverse")
        return sequence {
            while (next.point != start.point) {
                yield(next.point)
                next = move(next.point, next.exit) ?: error("Can't traverse from $next")
            }
        }
    }

    fun part1(input: List<String>): Long {
        val steps = input.traverseLoop().count()
        return ceil(steps.toDouble() / 2).toLong()
    }

    fun part2(input: List<String>): Long {
        return 0L
    }

    val testInput = readInput("Day10_test")
    var testResult = part1(testInput)
    check(testResult == 8L)
    testResult = part2(testInput)
    check(testResult == 0L)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}
