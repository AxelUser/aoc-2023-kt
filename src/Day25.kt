fun main() {
    fun List<String>.parse(): Map<String, MutableSet<String>> {
        val graph = mutableMapOf<String, MutableSet<String>>()
        val regex = Regex("\\w{3}")
        forEach { line ->
            val nodes = regex.findAll(line).map { m -> m.value }.toList()
            graph.computeIfAbsent(nodes[0]) { mutableSetOf() }.addAll(nodes.drop(1))
            nodes.drop(1).forEach { connected ->
                graph.computeIfAbsent(connected) { mutableSetOf() }.add(nodes[0])
            }
        }
        return graph
    }

    fun Map<String, Set<String>>.bfs(from: String, to: String? = null, edgeStats: MutableMap<Pair<String, String>, Long>? = null): Long {
        val queue = ArrayDeque(listOf(from))
        val visited = mutableSetOf<String>()
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (current in visited) continue
            visited += current
            if (current == to) {
                return visited.size.toLong()
            }

            getValue(current).filter { it !in visited }.forEach { connection ->
                queue += connection
                val edge = minOf(current, connection) to maxOf(current, connection)
                edgeStats?.compute(edge) { _, old, -> if (old == null) 1 else old + 1 }
            }
        }
        return visited.size.toLong()
    }

    fun part1(input: Map<String, MutableSet<String>>): Long {
        val edgeStats = mutableMapOf<Pair<String, String>, Long>()
        repeat(5000) {
            val random = input.keys.shuffled()
            val node1 = random[0]
            val node2 = random[4]
            input.bfs(node1, node2, edgeStats)
        }
        val top3 = edgeStats.asSequence().sortedByDescending { it.value }.take(5).map { it.key }.toList()
        top3.forEach { (from, to) ->
            input[from]!!.remove(to)
            input[to]!!.remove(from)
        }
        val count1 = input.bfs(top3[0].first)
        val count2 = input.bfs(top3[0].second)

        return count1 * count2
    }

    fun part2(input: List<String>): Long {
        return 0L
    }

    val testInput = readInput("Day25_test").parse()
    val testResult = part1(testInput)
    //check(testResult == 0L)

    val input = readInput("Day25").parse()
    part1(input).println()
}