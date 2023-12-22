fun main() {
    data class Block(val x: IntRange, val y: IntRange, val z: IntRange) {
        fun height(): Int = z.count()
    }

    fun List<String>.parse(): List<Block> {
        return asSequence().flatMap { line -> line.split('~') }
            .flatMap { coors -> coors.split(',').map { it.toInt() } }
            .chunked(3).chunked(2)
            .map { (left, right) ->
                Block(left[0]..right[0], left[1]..right[1], left[2]..right[2])
            }.sortedBy { it.z.first }.toList()
    }

    class ConnectedBlock(
        val id: Int,
        val supportedBy: MutableList<ConnectedBlock> = mutableListOf(),
        val supports: MutableList<ConnectedBlock> = mutableListOf()
    )

    class Height(val value: Int, val owner: ConnectedBlock)

    //TODO: create a graph starting from the floor, each node has list of blocks it supports and what nodes support it, then we can count all
    // nodes that don't support anything or has multiple nodes that support it

    fun List<Block>.simulate(): ConnectedBlock {
        // block to list of blocks that it supports
        val supports = mutableMapOf<Int, Set<Int>>()
        val maxX = maxOf { it.x.last }
        val maxY = maxOf { it.y.last }
        val root = ConnectedBlock(-1)
        val heightMap = Array(maxY + 1) { Array(maxX + 1) { Height(0, root) } }

        fun intersection(block: Block, func: (Height) -> Height) {
            for (y in block.y) {
                for (x in block.x) {
                    heightMap[y][x] = func(heightMap[y][x])
                }
            }
        }

        forEachIndexed { id, block ->
            var maxHeight = 0
            val currentBlock = ConnectedBlock(id)
            // blocks that support current block
            val blockSupport = mutableSetOf<ConnectedBlock>()
            intersection(block) { h ->
                if (h.value >= maxHeight) {
                    if (h.value > maxHeight) {
                        maxHeight = h.value
                        blockSupport.clear()
                    }
                    blockSupport += h.owner
                }
                h
            }

            currentBlock.supportedBy.addAll(blockSupport)
            blockSupport.forEach { sb -> sb.supports += currentBlock }

            intersection(block) {
                Height(maxHeight + block.height(), currentBlock)
            }
        }

        return root
    }

    fun ConnectedBlock.bfs(): Sequence<ConnectedBlock> {
        val queue = ArrayDeque(listOf(this))
        val visited = mutableSetOf<Int>()
        return sequence {
            while (queue.isNotEmpty()) {
                val block = queue.removeFirst()
                if (block.id in visited) continue
                visited += block.id
                yield(block)
                block.supports.forEach { next ->
                    if (next.id !in visited) {
                        queue += next
                    }
                }
            }
        }
    }

    fun Map<Point2D, Int>.printProjection(maxHeight: Int, maxWidth: Int) {
        for (h in maxHeight downTo 1) {
            print("|")
            for (w in 0..maxWidth) {
                val id = this[Point2D(w.toLong(), h.toLong())]
                when {
                    id == -1 -> print("?")
                    id != null -> print('A' + id)
                    else -> print(' ')
                }
            }
            println("| $h")
        }
        kotlin.io.println()
    }

    fun List<Block>.print() {
        val maxX = maxOf { it.x.last }
        val maxY = maxOf { it.y.last }
        val maxZ = maxOf { it.z.last }

        val pointsX = mutableMapOf<Point2D, Int>()
        val pointsY = mutableMapOf<Point2D, Int>()
        forEachIndexed { id, b ->
            for (z in b.z) {
                for (x in b.x) {
                    pointsX.compute(
                        Point2D(
                            x.toLong(),
                            z.toLong()
                        )
                    ) { _, old -> if (old == null || old == id) id else -1 }
                }
                for (y in b.y) {
                    pointsY.compute(
                        Point2D(
                            y.toLong(),
                            z.toLong()
                        )
                    ) { _, old -> if (old == null || old == id) id else -1 }
                }
            }
        }

        println("X")
        pointsX.printProjection(maxZ, maxX)

        println("Y")
        pointsY.printProjection(maxZ, maxY)
    }

    fun part1(input: List<Block>): Long {
        val graph = input.simulate()
        val canRemove =
            graph.bfs().filter { it.supports.isEmpty() || it.supports.all { s -> s.supportedBy.size >= 2 } }.toList()
        return canRemove.count().toLong()
    }

    fun part2(input: List<Block>): Long {
        return 0L
    }

    val testInput = readInput("Day22_test").parse()
    //testInput.print()
    var testResult = part1(testInput)
    check(testResult == 5L)
    testResult = part2(testInput)
    check(testResult == 0L)

    val input = readInput("Day22").parse()
    part1(input).println()
    part2(input).println()
}