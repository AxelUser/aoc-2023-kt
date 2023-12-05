fun main() {
    val digMap = mapOf(
        "one" to 1L,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9
    )

    fun String.findLast(regex: Regex): MatchResult {
        indices.reversed().forEach {idx ->
            val match = regex.find(this, idx)
            if (match != null) return match
        }

        error("no last value found")
    }

    fun String.concatDigits(regex: Regex): Long {
        val first = regex.find(this)!!.value.let { d -> d.toLongOrNull() ?: digMap.getValue(d) }
        val last = findLast(regex).value.let { d -> d.toLongOrNull() ?: digMap.getValue(d) }
        return first * 10 + last
    }

    fun part1(input: List<String>): Long {
        return input.sumOf { it.concatDigits(Regex("\\d")) }
    }

    fun part2(input: List<String>): Long {
        return input.sumOf { it.concatDigits(Regex("\\d|" + digMap.keys.joinToString(separator = "|"))) }
    }

    check(part1(readInput("Day01_1_test")) == 142L)
    check(part2(readInput("Day01_2_test")) == 281L)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
