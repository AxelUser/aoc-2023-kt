private enum class Type(val rank: Int) {
    HighCard(1),
    OnePair(2),
    TwoPairs(3),
    ThreeKind(4),
    FullHouse(5),
    FourKind(6),
    FiveKind(7)
}

fun main() {
    data class Input(val hand: String, val type: Type, val bid: Long)

    fun String.countSizes(useJokers: Boolean): IntArray {
        val sizes = IntArray(6) { 0 }
        val groups = groupingBy { it }.eachCount().toMutableMap()
        val jokers = groups['J']
        if (useJokers && jokers != null && jokers < 5) {
            groups.remove('J')
            val maxKey = groups.maxByOrNull { (_, v) -> v }?.key ?: error("no max in $this")
            groups[maxKey] = groups[maxKey]!! + jokers
        }
        for ((_, size) in groups) {
            sizes[size]++
        }
        return sizes
    }


    fun IntArray.getTypeRank(): Type {
        return when {
            this[5] == 1 -> Type.FiveKind
            this[4] == 1 -> Type.FourKind
            this[3] == 1 && this[2] == 1 -> Type.FullHouse
            this[3] == 1 && this[1] == 2 -> Type.ThreeKind
            this[2] == 2 -> Type.TwoPairs
            this[2] == 1 && this[1] == 3 -> Type.OnePair
            else -> Type.HighCard
        }
    }

    fun List<String>.parse(useJokers: Boolean = false): List<Input> {
        return map { line ->
            line.split(" ").let { (hand, bidStr) ->
                Input(hand, hand.countSizes(useJokers).getTypeRank(), bidStr.toLong())
            }
        }
    }

    fun getCardsComparator(cards: String): Comparator<Input> {
        val lettersStrength = cards.reversed().mapIndexed {index, c -> c to index }.toMap()
        return Comparator { (hand1, _), (hand2, _) ->
            for (i in 0 .. 4) {
                lettersStrength[hand1[i]]!!.compareTo(lettersStrength[hand2[i]]!!).takeIf { it != 0 }?.let { return@Comparator it }
            }

            return@Comparator 0
        }
    }

    val typeComparator = Comparator<Input> { (_, type1), (_, type2) ->
        type1.rank.compareTo(type2.rank)
    }

    fun part1(input: List<String>): Long {
        val sorted = input.parse().sortedWith(typeComparator.thenComparing(getCardsComparator("AKQJT98765432")))
        return sorted.foldIndexed(0L) { index, acc, item -> acc + (index + 1) * item.bid }
    }

    fun part2(input: List<String>): Long {
        val sorted = input.parse(true).sortedWith(typeComparator.thenComparing(getCardsComparator("AKQT98765432J")))
        return sorted.foldIndexed(0L) { index, acc, item -> acc + (index + 1) * item.bid }
    }

    val testInput = readInput("Day07_test")
    var testResult = part1(testInput)
    check(testResult == 6440L)
    testResult = part2(testInput)
    check(testResult == 5905L)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}