fun main() {
    fun String.parse(): List<List<String>> {
        return split(System.lineSeparator() + System.lineSeparator()).map { it.lines() }
    }

    fun List<String>.colsDiff(a: Int, b: Int): Int {
        var diff = 0
        for (y in 0..lastIndex) {
            if (this[y][a] != this[y][b]) diff++
        }
        return diff
    }

    fun List<String>.verticalReflectionDiff(left: Int, right: Int, diff: Int = 0): Int {
        if (left < 0 || right > this[0].lastIndex) return diff
        return verticalReflectionDiff(left - 1, right + 1, diff + colsDiff(left, right))
    }

    fun List<String>.findVertical(targetDiff: Int): Int? {
        val n = this[0].lastIndex

        for (x in 0 until n) {
            if (verticalReflectionDiff(x, x + 1) == targetDiff)
                return x
        }
        return null
    }

    fun List<String>.rowsDiff(a: Int, b: Int): Int {
        var diff = 0
        for (x in 0..this[0].lastIndex) {
            if (this[a][x] != this[b][x]) diff++
        }

        return diff
    }

    fun List<String>.horizontalReflectionDiff(top: Int, bottom: Int, diff: Int = 0): Int {
        if (top < 0 || bottom > lastIndex) return diff
        return horizontalReflectionDiff(top - 1, bottom + 1, diff + rowsDiff(top, bottom))
    }

    fun List<String>.findHorizontal(targetDiff: Int): Int? {
        val n = lastIndex
        for (y in 0 until n) {
            if (horizontalReflectionDiff(y, y + 1) == targetDiff)
                return y
        }
        return null
    }

    fun part1(input: List<List<String>>): Long {
        val horizontals = input.mapNotNull { it.findHorizontal(0) }.sumOf { it + 1 }
        val verticals = input.mapNotNull { it.findVertical(0) }.sumOf { it + 1 }
        return horizontals * 100L + verticals
    }

    fun part2(input: List<List<String>>): Long {
        val horizontals = input.mapNotNull { it.findHorizontal(1) }.sumOf { it + 1 }
        val verticals = input.mapNotNull { it.findVertical(1) }.sumOf { it + 1 }
        return horizontals * 100L + verticals
    }

    val testInput = readText("Day13_test").parse()
    var testResult = part1(testInput)
    check(testResult == 405L)
    testResult = part2(testInput)
    check(testResult == 400L)

    val input = readText("Day13").parse()
    part1(input).println()
    part2(input).println()
}