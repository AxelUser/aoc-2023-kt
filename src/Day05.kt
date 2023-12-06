fun main() {
    data class Mapping(val destStart: Long, val srcStart: Long, val length: Long) {
        fun map(srcVal: Long): Long? {
            if (srcVal !in srcStart until (srcStart + length)) return null
            return destStart + srcVal - srcStart
        }
    }

    fun List<Mapping>.mapFrom(srcVal: Long): Long {
        forEach { m ->
            m.map(srcVal)?.let {
                return it
            }
        }
        return srcVal
    }

    data class Almanac(val initialSeeds: List<Long>, val sections: List<List<Mapping>>)

    fun String.parse(): Almanac {
        fun String.parseMapping(): Mapping {
            return split(" ").map { v ->
                v.toLong()
            }.let { (dest, src, len) ->
                Mapping(
                    destStart = dest,
                    srcStart = src,
                    length = len
                )
            }
        }

        fun String.parseSection(): List<Mapping> {
            return lines().drop(1).map { l -> l.parseMapping() }
        }

        val groups = split(System.lineSeparator() + System.lineSeparator())

        return Almanac(
            initialSeeds = groups[0].split(" ").drop(1).map { it.toLong() },
            sections = groups.drop(1).map { it.parseSection() }
        )
    }

    fun List<List<Mapping>>.findLocation(seed: Long): Long {
        var res = seed
        forEach { section ->
            res = section.mapFrom(res)
        }

        return res
    }

    fun part1(input: Almanac): Long {
        return input.initialSeeds.minOf { seed ->
            input.sections.findLocation(seed)
        }
    }

    fun part2(input: Almanac): Long {
        val ranges = input.initialSeeds.chunked(2).map { (s, l) -> s until s + l }.sortedBy { it.first }
        val seq = sequence {
            for (range in ranges) {
                for (seed in range) {
                    yield(seed)
                }
            }
        }

        return seq.minOf { seed ->
            input.sections.findLocation(seed)
        }
    }

    val testInput = readText("Day05_test").parse()
    var testResult = part1(testInput)
    check(testResult == 35L)
    testResult = part2(testInput)
    check(testResult == 46L)

    val input = readText("Day05").parse()
    part1(input).println()
    part2(input).println()
}