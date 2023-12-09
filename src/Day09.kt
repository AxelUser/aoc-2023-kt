import java.util.Stack

fun main() {
    fun List<String>.parse(): List<List<Long>> {
        return map { line -> line.split(" ").map { s -> s.toLong() } }
    }

    fun List<Long>.extrapolateForward(): Long {
        val stack = Stack<List<Long>>()
        stack.push(this)
        while (true) {
            val new = stack.peek().zipWithNext().map { (a, b) -> b - a }
            if (new.all { it == 0L }) break
            stack.push(new)
        }

        var prevDiff = 0L
        while (stack.isNotEmpty()) {
            val diff = stack.pop().last()
            prevDiff += diff
        }

        return prevDiff
    }

    fun part1(input: List<List<Long>>): Long {
        return input.sumOf { it.extrapolateForward() }
    }

    fun part2(input: List<List<Long>>): Long {
        return input.map { it.reversed() }.sumOf { it.extrapolateForward() }
    }

    val testInput = readInput("Day09_test").parse()
    var testResult = part1(testInput)
    check(testResult == 114L)
    testResult = part2(testInput)
    check(testResult == 2L)

    val input = readInput("Day09").parse()
    part1(input).println()
    part2(input).println()
}