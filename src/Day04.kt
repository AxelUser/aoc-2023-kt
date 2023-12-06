fun main() {
    data class Card(val winning: Set<Long>, val got: Set<Long>)

    fun List<String>.parse(): Sequence<Card> {
        return sequence {
            forEach { line ->
                val card = line.split(":", "|").let { (_, winPt, gotPt) ->
                    Card(
                        winning = winPt.split(" ").map { it.trim() }.filter { it.isNotEmpty() }.map { it.toLong() }
                            .toSet(),
                        got = gotPt.split(" ").map { it.trim() }.filter { it.isNotEmpty() }.map { it.toLong() }.toSet(),
                    )
                }
                yield(card)
            }
        }
    }

    fun Sequence<Card>.calcPoints(): Sequence<Long> {
        return sequence {
            forEach { card ->
                val wonCount = card.got.count { g -> card.winning.contains(g) }
                if (wonCount != 0) {
                    yield(1L shl (wonCount - 1))
                }
            }
        }
    }

    fun Sequence<Card>.calcCards(): Sequence<Int> {
        val cardsCount = mutableMapOf<Int, Int>()
        return sequence {
            forEachIndexed { originalCardId, card ->
                val wonCount = card.got.count { g -> card.winning.contains(g) }
                repeat(cardsCount.getOrDefault(originalCardId, 0) + 1) {
                    yield(originalCardId)
                    if (wonCount != 0) {
                        for (wonCardId in ((originalCardId + 1)..(originalCardId + wonCount))) {
                            cardsCount
                                .compute(wonCardId) { _, count -> if (count == null) 1 else count + 1 }
                        }
                    }

                }
            }
        }
    }

    fun part1(input: List<String>): Long {
        return input.parse().calcPoints().sum()
    }

    fun part2(input: List<String>): Long {
        val won = input.parse().calcCards().groupingBy { it }.eachCount()
        return won.values.sum().toLong()
    }

    val testInput = readInput("Day04_test")
    var testResult = part1(testInput)
    check(testResult == 13L)
    testResult = part2(testInput)
    check(testResult == 30L)

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}