fun main() {
    data class Command(val direction: Direction4, val steps: Long)

    fun List<String>.parse(): List<Command> {
        return map { line ->
            line.split(' ').let { (d, s) ->
                Command(
                    direction = when (d) {
                        "L" -> Direction4.Left
                        "D" -> Direction4.Down
                        "R" -> Direction4.Right
                        "U" -> Direction4.Up
                        else -> error("No such direction $d")
                    },
                    steps = s.toLong()
                )
            }
        }
    }

    fun List<String>.parseHex(): List<Command> {
        return map { line ->
            line.split(' ').let { (_, _, hex) ->
                val trimmed = hex.trim('(', ')', '#')
                val steps = trimmed.take(5).toLong(16)
                Command(
                    direction = when (trimmed.last()) {
                        '0' -> Direction4.Right
                        '1' -> Direction4.Down
                        '2' -> Direction4.Left
                        '3' -> Direction4.Up
                        else -> error("No such direction $trimmed")
                    },
                    steps = steps
                )
            }
        }
    }

    fun List<Command>.getArea(): Long {
        val commands = this@getArea
        var current = Point2D(0, 0)
        var minY = 0L
        var minX = 0L
        var points = 0L
        val path = mutableListOf<Point2D>()
        for ((dir, steps) in commands) {
            current += dir.offset * steps
            points += steps
            minX = minOf(minX, current.x)
            minY = minOf(minY, current.y)
            path.add(current)
        }
        val halfArea = path.shoelaceArea() / 2
        return (halfArea) - (points / 2) + 1L + points
    }

    fun part1(input: List<String>): Long {
        return input.parse().getArea()
    }

    fun part2(input: List<String>): Long {
        return input.parseHex().getArea()
    }

    val testInput = readInput("Day18_test")
    var testResult = part1(testInput)
    check(testResult == 62L)
    testResult = part2(testInput)
    check(testResult == 952408144115L)

    val input = readInput("Day18")
    part1(input).println()
    part2(input).println()
}