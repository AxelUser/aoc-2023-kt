fun main() {
    data class TravelMap(val directions: String, val path: Map<String, Pair<String, String>>) {
        fun traverse(from: String, toPredicate: (String) -> Boolean): Sequence<String> {
            val instrSeq = sequence {
                while (true) {
                    for (dir in directions) {
                        yield(dir)
                    }
                }
            }

            return sequence {
                var current = from
                for (instruction in instrSeq) {
                    val ways = path[current]!!
                    current = when (instruction) {
                        'L' -> ways.first
                        'R' -> ways.second
                        else -> error("unsupported instruction $instruction")
                    }
                    yield(current)
                    if (toPredicate(current)) break
                }
            }
        }
    }

    fun String.parse(): TravelMap {
        val nodeRegex = Regex("[1-9A-Z]{3}")
        return split(System.lineSeparator() + System.lineSeparator())
                .let { (dirs, mappings) ->
                    TravelMap(
                            directions = dirs,
                            path = mappings.lines().associate { line ->
                                nodeRegex.findAll(line).map { m -> m.value }.toList()
                                        .let { (src, left, right) -> src to (left to right) }
                            }
                    )
                }
    }

    fun part1(input: TravelMap): Long {
        return input.traverse("AAA") { it == "ZZZ" }.count().toLong()
    }

    fun part2(input: TravelMap): Long {
        return input.path.keys.filter { it[2] == 'A' }
                .map { start -> input.traverse(start) { it[2] == 'Z' }.count().toLong() }
                .reduce(::lcm)
    }

    var testResult = part1(readText("Day08_1_test").parse())
    check(testResult == 6L)
    testResult = part2(readText("Day08_2_test").parse())
    check(testResult == 6L)

    val input = readText("Day08").parse()
    part1(input).println()
    part2(input).println()
}