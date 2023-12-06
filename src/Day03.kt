fun main() {
    data class Point(val x: Int, val y: Int) {
        fun getAdjacent(width: Int, height: Int): Sequence<Point> {
            return sequence {
                yield(Point(x - 1, y - 1))
                yield(Point(x, y - 1))
                yield(Point(x + 1, y - 1))

                yield(Point(x - 1, y))
                yield(Point(x + 1, y))

                yield(Point(x - 1, y + 1))
                yield(Point(x, y + 1))
                yield(Point(x + 1, y + 1))
            }.filter { p ->
                p.x in 0..<width && p.y in 0..<height
            }
        }
    }

    fun List<String>.getDetails(filter: (Char) -> Boolean): Sequence<Point> {
        return sequence {
            forEachIndexed { hI, line ->
                line.forEachIndexed { wI, sym ->
                    if (!sym.isDigit() && sym != '.' && filter(sym)) {
                        yield(Point(wI, hI))
                    }
                }
            }
        }
    }

    fun getNumber(from: Point, line: String, visited: MutableSet<Point>): Long {
        visited += from
        var start = from.x
        while (start > 0 && line[start - 1].isDigit()) {
            visited += Point(--start, from.y)
        }
        var end = from.x
        while (end < line.lastIndex && line[end + 1].isDigit()) {
            visited += Point(++end, from.y)
        }

        return line.substring(start, end + 1).toLong()
    }

    fun Sequence<Point>.getAdjacentNumbers(input: List<String>): Sequence<List<Long>> {
        val visited = mutableSetOf<Point>()
        val width = input[0].length
        val height = input.size

        return sequence {
            forEach { dP ->
                visited += dP
                val found = dP.getAdjacent(width, height)
                    .filter { aP -> !visited.contains(aP) && input[aP.y][aP.x].isDigit() }
                    .map { nP ->
                        getNumber(nP, input[nP.y], visited)
                    }.toList()

                yield(found)
            }
        }
    }


    fun part1(input: List<String>): Long {
        return input.getDetails { true }.getAdjacentNumbers(input).flatten().sum()
    }

    fun part2(input: List<String>): Long {
        return input.getDetails { it == '*' }.getAdjacentNumbers(input)
            .filter { it.size == 2 }
            .map { (a, b) -> a * b }
            .sum()
    }

    val testInput = readInput("Day03_test")
    var testRes = part1(testInput)
    check(testRes == 4361L)

    testRes = part2(testInput)
    check(testRes == 467835L)

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
