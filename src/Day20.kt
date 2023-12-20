import Day20Pulse.*

private enum class Day20Pulse {
    Low,
    High
}

private interface Day20Module {
    val id: String
    fun signal(pulse: Day20Pulse, input: String? = null): Day20Pulse?
}

private typealias Day20Input = Map<String, Pair<Day20Module, List<String>>>

fun main() {

    class FlipFlopModule(override val id: String) : Day20Module {
        private var active = false

        override fun signal(pulse: Day20Pulse, input: String?): Day20Pulse? {
            return when (pulse) {
                Low -> if (active) {
                    active = false
                    Low
                } else {
                    active = true
                    High
                }

                High -> null
            }
        }
    }

    class ConjunctionModule(override val id: String, inputs: List<String>) : Day20Module {
        val state = inputs.associateWithTo(mutableMapOf()) { Low }

        override fun signal(pulse: Day20Pulse, input: String?): Day20Pulse {
            checkNotNull(input) { "input should be not null" }
            state[input] = pulse
            return if (state.values.all { it == High }) Low else High
        }
    }

    class BroadcastModule(override val id: String) : Day20Module {
        override fun signal(pulse: Day20Pulse, input: String?): Day20Pulse {
            return pulse
        }

    }

    fun List<String>.parse(): Day20Input {
        val inputs = mutableMapOf<String, MutableList<String>>()
        val outputs = mutableMapOf<String, List<String>>()

        forEach { line ->
            line.split("->").map { it.trim() }.let { (nodeWithPrefix, outStr) ->
                val outputsWithoutPrefix = outStr.split(", ")
                outputs[nodeWithPrefix] = outputsWithoutPrefix
                outputsWithoutPrefix.forEach { outputNoPrefix ->
                    inputs.computeIfAbsent(outputNoPrefix) { _ -> mutableListOf() }
                        .add(nodeWithPrefix.trimStart('%', '&'))
                }
            }
        }

        return outputs.map { (nodeWithPrefix, outputsWithoutPrefix) ->
            val module = when {
                nodeWithPrefix == "broadcaster" -> BroadcastModule(nodeWithPrefix)
                nodeWithPrefix[0] == '%' -> FlipFlopModule(nodeWithPrefix.substring(1))
                nodeWithPrefix[0] == '&' -> ConjunctionModule(
                    nodeWithPrefix.substring(1),
                    inputs[nodeWithPrefix.substring(1)]!!
                )

                else -> error("unknown module $nodeWithPrefix")
            }

            nodeWithPrefix.trimStart('%', '&') to (module to outputsWithoutPrefix)
        }.toMap()
    }

    data class State(val input: String?, val signal: Day20Pulse, val module: String)

    fun Day20Input.pulse(input: String, signal: Day20Pulse, exitOnModuleSignal: (String, Day20Pulse) -> Boolean): Pair<Long, Long> {
        val queue = ArrayDeque(buildList {
            add(State(null, signal, input))
        })

        var lowSignals = 0L
        var highSignals = 0L
        while (queue.isNotEmpty()) {
            val (i, s, m) = queue.removeFirst()
            if (s == Low) lowSignals++ else highSignals++
            val (module, outputs) = this@pulse[m] ?: continue
            module.signal(s, i)?.also { pulse ->
                if (exitOnModuleSignal(m, pulse)) return 0L to 0L;
                outputs.forEach { out ->
                    queue.add(State(m, pulse, out))
                }
            }
        }

        return lowSignals to highSignals
    }

    fun part1(input: List<String>): Long {
        val config = input.parse()
        var sumLow = 0L
        var sumHigh = 0L
        repeat(1000) {
            val (l, h) = config.pulse("broadcaster", Low) { _, _ -> false }
            sumLow += l
            sumHigh += h
        }
        return sumLow * sumHigh
    }

    fun part2(input: List<String>): Long {
        val config = input.parse()
        val rxInput = config.values.single { "rx" in it.second }.first.id
        val rxInputTriggers = config.values.filter { (_, outputs) -> rxInput in outputs }.map { it.first.id }
        val rxInputTriggersHighs = rxInputTriggers.associateWith { mutableListOf<Long>() }
        var run = true
        var pushes = 0L
        while (run) {
            pushes++
            config.pulse("broadcaster", Low) { m, s ->
                if (m in rxInputTriggers && s == High) {
                    rxInputTriggersHighs[m]!! += pushes
                    if (rxInputTriggersHighs.values.all { it.size >= 2 }) {
                        run = false
                        return@pulse true
                    }
                }
                false
            }
        }
        return rxInputTriggersHighs.values.map { (a, b) -> b - a }.reduce(::lcm)
    }

    val testInput = readInput("Day20_test")
    val testResult = part1(testInput)
    check(testResult == 32000000L)

    val input = readInput("Day20")
    part1(input).println()
    part2(input).println()
}