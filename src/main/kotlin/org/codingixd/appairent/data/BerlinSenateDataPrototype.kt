package org.codingixd.appairent.data

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter

enum class Type {
    all, traffic, background, suburb
}

enum class Period(val param: String) {
    hourly("1h"),
    daily("24h"),
    monthly("1m"),
    yearly("1y"),
    daily_glide("24hg")
}

enum class Pollutant {
    pm10, no, no2, nox, o3, chb, co, so2, cht
}


fun main(args: Array<String>) {
    val saveToFile = true

    val mapper = jacksonObjectMapper()



    val period = Period.hourly

    for (year in 2012..2012) {
        Pollutant.values().forEach { pollutant ->
            Type.values().forEach { type ->

                val startDate = LocalDate.of(year, Month.OCTOBER, 1)
                val startHour = 0

                val endDate = LocalDate.of(year, Month.DECEMBER, 31)
                val endHour = 23

                getPollutionCSV(pollutant, type, period, startDate, startHour, endDate, endHour, saveToFile)
            }
        }
    }


}

private fun getPollutionCSV(
    pollutant: Pollutant,
    type: Type,
    period: Period,
    startDate: LocalDate,
    startHour: Int,
    endDate: LocalDate,
    endHour: Int,
    saveToFile: Boolean
): String {

    val formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu")

    val query =
        "https://luftdaten.berlin.de/core/${pollutant.name}.csv?stationgroup=${type.name}&period=${period.param}" +
                "&timespan=custom&start%5Bdate%5D=${startDate.format(formatter)}&start%5Bhour%5D=$startHour" +
                "&end%5Bdate%5D=${endDate.format(formatter)}&end%5Bhour%5D=$endHour"
    println("QUERY: $query")

    val (request, response, result) = query.httpGet().responseString()

    when (result) {
        is Result.Failure -> {
            error(response)
        }
        is Result.Success -> {
//            println(result.value)

            if (saveToFile) {

                val path = Paths.get("./data/${startDate.year}/${pollutant}_${type}_${period}_$startDate-${startHour}_$endDate-$endHour.csv")
                if (!Files.exists(path)) {
                    println("Creating File: ./data/${startDate.year}/${pollutant}_${type}_${period}_$startDate-${startHour}_$endDate-$endHour.csv")
                    File(path.toUri()).parentFile.mkdirs()
                    Files.createFile(path)
                    val file = path.toFile()
                    file.writeText(result.value)
                }
            }

            return result.value
        }
    }
}


// end is exclusive to the interval
fun daysInInterval(start: LocalDate, end: LocalDate): Sequence<LocalDate> = generateSequence(start) { d ->
    d.plusDays(1).takeIf { it < end }
}

