fun main() {
    data class Race(val duration: Long, val distance: Long)

    data class BoostConfig(val charge: Long, val speed: Long, val distance: Long)

    fun List<String>.parse(): List<Race> {
        val regex = Regex("\\d+")
        return map { line ->
            regex.findAll(line).map { m -> m.value.toLong() }
        }.let { (time, dist) -> time.zip(dist).map { (time, dist) -> Race(time, dist) } }.toList()
    }

    fun getConfigs(timeLimit: Long): Sequence<BoostConfig> {
        return sequence {
            for (boostTime in 0L..timeLimit) {
                yield(BoostConfig(
                    charge = boostTime,
                    speed = boostTime,
                    distance = boostTime * (timeLimit - boostTime)
                ))
            }
        }
    }

    fun part1(input: List<Race>): Long {
        return input.map { race -> getConfigs(race.duration).count { (_, _, dist) -> race.distance < dist }.toLong()}.reduce { acc, i -> acc * i }
    }

    fun List<Race>.merge(): Race {
        val mergedTime = map { race -> race.duration.toString() }.reduce { acc, s -> acc + s }.toLong()
        val mergedDistance = map { race -> race.distance.toString() }.reduce { acc, s -> acc + s }.toLong()
        return Race(mergedTime, mergedDistance)
    }

    fun part2(input: List<Race>): Long {
        val race = input.merge()
        return getConfigs(race.duration).count { (_, _, dist) -> race.distance < dist }.toLong()
    }

    val testInput = readInput("Day06_test").parse()
    var testResult = part1(testInput)
    check(testResult == 288L)
    testResult = part2(testInput)
    check(testResult == 71503L)

    val input = readInput("Day06").parse()
    part1(input).println()
    part2(input).println()
}