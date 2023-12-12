import kotlin.math.absoluteValue
import kotlin.math.ceil

private data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point): Point {
        return Point(x + other.x, y + other.y)
    }

    fun shoelace(other: Point): Long {
        return (y.toLong() + other.y) * (x.toLong() - other.x)
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

    fun List<String>.findStart(): Pair<Pipe, Char> {
        val start = findStartPoint()
        val possibleWays = mutableListOf<Direction>()
        for (dir in Direction.entries) {
            move(start, dir)?.also { possibleWays += dir }
        }

        check(possibleWays.size == 2) { "Expect exactly 2 exits from Start" }
        val pipe = possibleWays.first().let { Pipe(start, it) }
        val symbol = symbols.filter { (_, dirs) -> possibleWays.containsAll(dirs) }.keys.single()
        return pipe to symbol
    }

    fun List<String>.traverseLoop(): Sequence<Point> {
        val (start, _) = findStart()
        var next = move(start.point, start.exit) ?: error("Can't traverse")
        return sequence {
            yield(start.point)
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
        val loop = input.traverseLoop().toList()
        val area = loop.plus(loop.first()).windowed(2).sumOf { (a, b) -> a.shoelace(b) }.absoluteValue
        return (area / 2L) - (loop.size / 2) + 1L
    }

    var testResult = part1(readInput("Day10_1_test"))
    check(testResult == 8L)
    testResult = part2(readInput("Day10_2_test"))
    check(testResult == 8L)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}
