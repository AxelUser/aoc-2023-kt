fun main() {
    fun part1(input: List<String>): Long {
        return 0L
    }

    fun part2(input: List<String>): Long {
        return 0L
    }

    val testInput = readInput("DayXX_test")
    var testResult = part1(testInput)
    check(testResult == 0L)
    testResult = part2(testInput)
    check(testResult == 0L)

    val input = readInput("DayXX")
    part1(input).println()
    part2(input).println()
}