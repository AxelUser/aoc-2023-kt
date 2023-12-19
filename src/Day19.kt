import Day19Operation.*

private interface Day19Rule {
    fun check(data: Map<String, Long>): Boolean

    val dest: String
}

private enum class Day19Operation(val char: Char) {
    LT('<'),
    GT('>'),
}

fun main() {
    class ValueRule(val variable: String, val op: Day19Operation, val value: Long, override val dest: String) :
        Day19Rule {
        override fun check(data: Map<String, Long>): Boolean {
            return when (op) {
                LT -> data[variable]!! < value
                GT -> data[variable]!! > value
            }
        }
    }

    class TerminalRule(override val dest: String) : Day19Rule {
        override fun check(data: Map<String, Long>): Boolean = true
    }

    fun List<String>.parseWorkflow(): Map<String, List<Day19Rule>> {
        val ruleRegex = Regex("((?<var>\\w+)(?<op>[><])(?<val>\\d+):(?<dest>\\w+))")

        return associate { line ->
            val fcb = line.indexOf('{')
            val wfName = line.substring(0 until fcb)
            val rulesStr = line.substring(fcb).trim('{', '}')
                .split(',')

            val rules = mutableListOf<Day19Rule>()
            rulesStr.mapNotNull { r ->
                ruleRegex.matchEntire(r)?.let { m ->
                    ValueRule(
                        variable = m.groups["var"]!!.value,
                        op = m.groups["op"]!!.value[0].let { s -> Day19Operation.entries.single { it.char == s } },
                        value = m.groups["val"]!!.value.toLong(),
                        dest = m.groups["dest"]!!.value,
                    )
                }
            }.forEach { rules += it }

            rules += TerminalRule(rulesStr.last())

            wfName to rules
        }
    }

    fun List<String>.parseData(): List<Map<String, Long>> {
        return map { line ->
            line.trim('{', '}').split(',')
                .associate { v -> v.split('=').let { (l, r) -> l to r.toLong() } }
        }
    }

    fun String.parse(): Pair<Map<String, List<Day19Rule>>, List<Map<String, Long>>> {
        return split(System.lineSeparator() + System.lineSeparator()).let { (a, b) ->
            a.lines().parseWorkflow() to b.lines().parseData()
        }
    }

    fun Map<String, List<Day19Rule>>.check(data: Map<String, Long>): Boolean {
        var wf = "in"
        while (true) {
            wf = this[wf]!!.first { it.check(data) }.dest
            when (wf) {
                "R" -> return false
                "A" -> return true
            }
        }
    }

    fun part1(input: String): Long {
        val (workflows, data) = input.parse()
        return data.filter { d -> workflows.check(d) }.sumOf { d -> d.values.sum() }
    }

    fun Map<String, List<Day19Rule>>.getRanges(
        workflow: String,
        initRange: Map<String, LongRange>
    ): List<Map<String, LongRange>> {
        val currentRange = initRange.toMutableMap()
        if (workflow == "R") return emptyList()
        if (workflow == "A") return listOf(currentRange.toMap())

        val ranges = mutableListOf<Map<String, LongRange>>()
        for (rule in this[workflow]!!) {
            when (rule) {
                is TerminalRule -> {
                    ranges.addAll(getRanges(rule.dest, currentRange.toMap()))
                }

                is ValueRule -> {
                    val (positive, negative) = when (rule.op) {
                        LT -> {
                            val currentVarRange = currentRange[rule.variable]!!
                            val positive = currentVarRange.first..<rule.value
                            val negative = rule.value..currentVarRange.last
                            positive to negative
                        }

                        GT -> {
                            val currentVarRange = currentRange[rule.variable]!!
                            val positive = rule.value + 1..currentVarRange.last
                            val negative = currentVarRange.first..rule.value
                            positive to negative
                        }
                    }

                    ranges.addAll(getRanges(rule.dest, buildMap {
                        putAll(currentRange)
                        put(rule.variable, positive)
                    }))
                    currentRange[rule.variable] = negative
                }
            }
        }

        return ranges
    }

    fun part2(input: String): Long {
        val (workflows, _) = input.parse()
        val initRange = mapOf(
            "x" to 1L..4000,
            "m" to 1L..4000,
            "a" to 1L..4000,
            "s" to 1L..4000,
        )
        return workflows.getRanges("in", initRange).sumOf { ranges ->
            ranges.values.map { r -> r.count().toLong() }
                .reduce { acc, i -> acc * i }
        }
    }

    val testInput = readText("Day19_test")
    var testResult = part1(testInput)
    check(testResult == 19114L)
    testResult = part2(testInput)
    check(testResult == 167409079868000L)

    val input = readText("Day19")
    part1(input).println()
    part2(input).println()
}