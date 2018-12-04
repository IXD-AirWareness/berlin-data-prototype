package org.codingixd.appairent.data

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import java.nio.file.Files
import java.nio.file.Paths

enum class Type {
    all, traffic, background, suburb
}

enum class Period(val param: String) {
    hour("1h"),
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

    val startDate = "04.12.2018"
    val startHour = "10"

    val endDate = "04.12.2018"
    val endHour = "11"

    val type = Type.all.name
    val period = Period.hour.param
    val pollutant = Pollutant.cht.name

    val query = "https://luftdaten.berlin.de/core/$pollutant.csv?stationgroup=$type&period=$period&timespan=custom&start%5Bdate%5D=$startDate&start%5Bhour%5D=$startHour&end%5Bdate%5D=$endDate&end%5Bhour%5D=$endHour"

    val (request, response, result) = query.httpGet().responseString()

    when (result) {
        is Result.Failure -> { error(response) }
        is Result.Success -> {
            println(result.value)

            if (saveToFile) {
                val path = Paths.get("./${pollutant}_${type}_${period}_$startDate-${startHour}_$endDate-$endHour.csv")
                if (!Files.exists(path)) {
                    Files.createFile(path)
                    val file = path.toFile()
                    file.writeText(result.value)
                }
            }
        }
    }


}


