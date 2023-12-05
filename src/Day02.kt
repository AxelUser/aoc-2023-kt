private data class CubeSet(val red: Long, val green: Long, val blue: Long)

fun main() {
    fun parseInput(input: List<String>): List<List<CubeSet>> = input.map { line ->
        line.split(";", ":")
            .drop(1)
            .map { setStr ->
                setStr
                    .split(",")
                    .associate { cubesStr ->
                        cubesStr
                            .trim().split(" ")
                            .let { (n, color) -> color to n.toLong() }
                    }.let { colorMap ->
                        CubeSet(
                            red = colorMap["red"] ?: 0L,
                            green = colorMap["green"] ?: 0L,
                            blue = colorMap["blue"] ?: 0L
                        )
                    }
            }
    }

    fun part1(input: List<List<CubeSet>>): Long {
        return input.mapIndexedNotNull { index, game ->
            if (game.all { set -> set.red <= 12 && set.green <= 13 && set.blue <= 14 }) index + 1L else null
        }.sum()
    }

    fun part2(input: List<List<CubeSet>>): Long {
        return input.map { game ->
            game.reduce { acc, set ->
                CubeSet(
                    red = maxOf(acc.red, set.red),
                    green = maxOf(acc.green, set.green),
                    blue = maxOf(acc.blue, set.blue),
                )
            }
        }.sumOf { set -> set.red * set.green * set.blue }
    }

    val testInput = parseInput(readInput("Day02_test"))
    var testRes = part1(testInput)
    check(testRes == 8L)

    testRes = part2(testInput)
    check(testRes == 2286L)

    val input = parseInput(readInput("Day02"))
    part1(input).println()
    part2(input).println()
}
