private data class Day12Input(val pattern: String, val groups: IntArray)

fun main() {
    fun List<String>.parse(): List<Day12Input> {
        return map { line ->
            line.split(' ').let { (p, g) ->
                Day12Input(p, g.split(',').map { d -> d.toInt() }.toIntArray())
            }
        }
    }

    fun findPermutations(pattern: String, groups: IntArray, curPos: Int = 0, curGroupIdx: Int = 0, curGroupLen: Int = 0, memo: MutableMap<Triple<Int, Int, Int>, Long> = mutableMapOf()): Long {
        val key = Triple(curPos, curGroupIdx, curGroupLen)
        memo[key]?.apply { return this }

        if (curPos == pattern.length) {
            if (curGroupIdx == groups.lastIndex && curGroupLen == groups[curGroupIdx]) return 1
            if (curGroupIdx == groups.size && curGroupLen == 0) return 1
            return 0
        }

        fun putDot(): Long {
            return when {
                curGroupLen == 0 -> findPermutations(pattern, groups, curPos + 1, curGroupIdx, 0, memo)
                curGroupIdx < groups.size && curGroupLen == groups[curGroupIdx] ->
                    findPermutations(pattern, groups, curPos + 1, curGroupIdx + 1, 0, memo)
                else -> 0
            }
        }

        fun putHash(): Long {
            return findPermutations(pattern, groups, curPos + 1, curGroupIdx, curGroupLen + 1, memo)
        }

        val count = when (pattern[curPos]) {
            '?' -> putDot() + putHash()
            '.' -> putDot()
            '#' -> putHash()
            else -> 0
        }

        memo[key] = count

        return count
    }

    fun part1(input: List<Day12Input>): Long {
        return input.sumOf { (pattern, groups) -> findPermutations(pattern, groups) }
    }

    fun part2(input: List<Day12Input>): Long {
        val unfolded = input.map { (fp, fg) ->
            val pattern = (1..5).joinToString("?") { fp }
            val groups = (1..5).flatMap { fg.toList() }
            Day12Input(pattern, groups.toIntArray())
        }

        return unfolded.sumOf { (pattern, groups) -> findPermutations(pattern, groups) }
    }

    val testInput = readInput("Day12_test").parse()
    var testResult = part1(testInput)
    check(testResult == 21L)
    testResult = part2(testInput)
    check(testResult == 525152L)

    val input = readInput("Day12").parse()
    part1(input).println()
    part2(input).println()
}