fun main() {
    fun String.getHashCode(): Int {
        return fold(0) { hash, c ->
            (hash + c.code) * 17 % 256
        }
    }

    fun part1(input: String): Long {
        return input.split(',').sumOf { it.getHashCode().toLong() }
    }

    data class Operation(val label: String, val op: Char, val focalLen: Int?)

    fun String.parseOperations(): List<Operation> {
        val regex = Regex("(?<label>[a-z]+)(?<op>[-=])(?<len>\\d*)")
        return split(',').mapNotNull { op -> regex.matchEntire(op) }.map { m ->
            val label = m.groups["label"]!!.value
            val op = m.groups["op"]!!.value[0]
            val len = m.groups["len"]?.value?.toIntOrNull()
            Operation(label, op, len)
        }
    }

    fun LinkedHashMap<String, Int>.sumFocusingPower(box: Int): Long {
        if (size == 0) return 0L
        var pos = 1
        var sum = 0L

        for ((_, fl) in this) {
            sum += box * pos++ * fl
        }
        return sum
    }

    fun part2(input: String): Long {
        val parsed = input.parseOperations()
        val hashMap = Array<LinkedHashMap<String, Int>>(256) { LinkedHashMap() }
        for (op in parsed) {
            when (op.op) {
                '=' -> hashMap[op.label.getHashCode()].put(op.label, op.focalLen!!)
                '-' -> hashMap[op.label.getHashCode()].remove(op.label)
            }
        }

        return hashMap.mapIndexed { idx, box -> box.sumFocusingPower(idx + 1) }.sum()
    }

    val testInput = readText("Day15_test")
    var testResult = part1(testInput)
    check(testResult == 1320L)
    testResult = part2(testInput)
    check(testResult == 145L)

    val input = readText("Day15")
    part1(input).println()
    part2(input).println()
}