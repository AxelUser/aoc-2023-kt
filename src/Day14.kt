private enum class Day14Direction(val shift: Point2D) {
    North(Point2D(0, -1)),
    West(Point2D(-1, 0)),
    South(Point2D(0, 1)),
    East(Point2D(1, 0)),
}

fun main() {
    data class Ends(val x: Int, val y: Int)

    fun List<CharArray>.getEnds(): Ends {
        val maxX = this[0].lastIndex
        val maxY = lastIndex
        return Ends(maxX, maxY)
    }

    fun List<CharArray>.tiltOne(start: Point2D, shift: Point2D) {
        val (maxX, maxY) = getEnds()
        var newPos = start
        while (true) {
            val shifted = newPos + shift
            if (!shifted.isInBounds(maxX, maxY)
                    || this[shifted.y.toInt()][shifted.x.toInt()].let { it == '#' || it == 'O' }) {
                break
            }
            newPos = shifted
        }

        this[start.y.toInt()][start.x.toInt()] = '.'
        this[newPos.y.toInt()][newPos.x.toInt()] = 'O'
    }

    fun List<CharArray>.tiltRow(rowStart: Point2D, selectShift: Point2D, itemShift: Point2D) {
        val (maxX, maxY) = getEnds()

        var selected = rowStart
        while (true) {
            if (!selected.isInBounds(maxX, maxY)) break
            if (this[selected.y.toInt()][selected.x.toInt()] == 'O') {
                tiltOne(selected, itemShift)
            }
            selected += selectShift
        }
    }

    data class Shifts(val start: Point2D, val rowShift: Day14Direction, val itemRowSelectionShift: Day14Direction, val itemShift: Day14Direction)

    fun List<CharArray>.tiltInDirection(direction: Day14Direction): List<CharArray> {
        val (maxX, maxY) = getEnds()
        val config = when (direction) {
            Day14Direction.North -> Shifts(
                    start = Point2D(0, 0),
                    rowShift = Day14Direction.South,
                    itemRowSelectionShift = Day14Direction.East,
                    itemShift = direction
            )

            Day14Direction.East -> Shifts(
                    start = Point2D(0, 0),
                    rowShift = Day14Direction.South,
                    itemRowSelectionShift = Day14Direction.East,
                    itemShift = direction
            )

            Day14Direction.South -> Shifts(
                    start = Point2D(0, 0),
                    rowShift = Day14Direction.South,
                    itemRowSelectionShift = Day14Direction.East,
                    itemShift = direction
            )

            Day14Direction.West -> Shifts(
                    start = Point2D(0, 0),
                    rowShift = Day14Direction.South,
                    itemRowSelectionShift = Day14Direction.East,
                    itemShift = direction
            )

        }

        var curRowStart = config.start
        while (curRowStart.isInBounds(maxX, maxY)) {
            tiltRow(curRowStart, config.itemRowSelectionShift.shift, config.itemShift.shift)
            curRowStart += config.rowShift.shift
        }

        return this
    }

    fun List<CharArray>.calcLoad(): Long {
        return mapIndexed { idx, line -> line.count { c -> c == 'O' }.toLong() * (size - idx) }.sum()
    }

    fun List<String>.parse(): List<CharArray> {
        return map { it.toCharArray() }
    }

    fun part1(input: List<String>): Long {
        return input.parse().tiltInDirection(Day14Direction.North).calcLoad()
    }

    fun List<CharArray>.cycle() {
        for (dir in Day14Direction.entries) {
            tiltInDirection(dir)
        }
    }

    fun part2(input: List<String>): Long {
        return input.parse().apply {
            repeat(10000000){
                cycle()
            }
        }.calcLoad()
    }

    val testInput = readInput("Day14_test")
    var testResult = part1(testInput)
    check(testResult == 136L)
    testResult = part2(testInput)
    check(testResult == 64L)

    val input = readInput("Day14")
    part1(input).println()
    //part2(input).println()
}