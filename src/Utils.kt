import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText
import kotlin.math.absoluteValue

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

fun readText(name: String) = Path("src/$name.txt").readText()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
        .toString(16)
        .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

data class Point2D(val x: Long, val y: Long) {
    fun isInBounds(maxX: Int, maxY: Int): Boolean {
        if (x !in 0..maxX || y !in 0..maxY) return false
        return true
    }

    operator fun plus(other: Point2D): Point2D {
        return Point2D(x + other.x, y + other.y)
    }

    operator fun unaryMinus(): Point2D {
        return Point2D(-x, -y)
    }

    operator fun times(times: Long): Point2D {
        return Point2D(x * times, y * times)
    }

    fun l1(other: Point2D): Long {
        return (x - other.x).absoluteValue + (y - other.y).absoluteValue
    }
}

enum class Direction4(val offset: Point2D) {
    Up(Point2D(0, -1)),
    Right(Point2D(1, 0)),
    Down(Point2D(0, 1)),
    Left(Point2D(-1, 0)),
}

fun List<Point2D>.shoelaceArea(): Long {
    fun Point2D.shoelace(other: Point2D): Long {
        return (y + other.y) * (x - other.x)
    }

    return this.plus(first()).windowed(2).sumOf { (a, b) -> a.shoelace(b) }.absoluteValue
}

fun List<Point2D>.printMap() {
    val maxX = maxOf { it.x }
    val maxY = maxOf { it.y }
    val set = toSet()
    for (y in 0..maxY) {
        for (x in 0..maxX) {
            if (set.contains(Point2D(x, y))) {
                print('#')
            } else print('.')
        }
        kotlin.io.println()
    }
}

fun <T> dijkstraSearch(
    startingPoints: List<T>,
    neighborProducer: (T) -> List<T>,
    costFunction: (T) -> Long,
): Map<T, Long> {
    data class State(val node: T, val distance: Long)

    val bestCosts = mutableMapOf<T, Long>()
    val boundary = PriorityQueue<State>(compareBy { it.distance })

    for (start in startingPoints) boundary += State(start, 0)

    while (boundary.isNotEmpty()) {
        val (currentNode, currentCost) = boundary.poll()
        if (currentNode in bestCosts) continue

        bestCosts[currentNode] = currentCost

        for (nextNode in neighborProducer(currentNode)) {
            if (nextNode !in bestCosts) {
                boundary += State(nextNode, currentCost + costFunction(nextNode))
            }
        }
    }

    return bestCosts
}

