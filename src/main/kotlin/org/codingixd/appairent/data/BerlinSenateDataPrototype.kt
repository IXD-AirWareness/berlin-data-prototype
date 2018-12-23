//package org.codingixd.appairent.data
//
//import com.github.kittinunf.fuel.httpGet
//import com.github.kittinunf.result.Result
//import java.io.File
//import java.lang.RuntimeException
//import java.nio.file.Files
//import java.nio.file.Paths
//import java.time.LocalDate
//import java.time.LocalDateTime
//import java.time.Month
//import java.time.format.DateTimeFormatter
//import java.util.*
//
//
//fun main(args: Array<String>) {
//    saveHistoricalData()
//}
//
//enum class MeasurementType {
//    all, traffic, background, suburb
//}
//
//
//enum class Period(val param: String, val csvName: String) {
//    hourly("1h", "Stundenwerte"),
//    daily("24h", "Tageswerte"),
//    monthly("1m", "Monatswerte"),
//    yearly("1y", "Jahreswerte"),
////    daily_glide("24hg", "")
//}
//
////fun String.toPeriod() = Period.values().find { it.csvName == this } ?: throw RuntimeException("Could not parse '$this' to Period.")
//
//enum class PollutantType(val csvName: String) {
//    PM10("Feinstaub (PM10)"),
//    NO("Stickstoffmonoxid"),
//    NO2("Stickstoffdioxid"),
//    NOX("Stickoxide"),
//    O3("Ozon"),
//    CHB("Benzol"),
//    CO("Kohlenstoffmonoxid"),
//    SO2("Schwefeldioxid"),
//    CHT("Toluol")
//}
//
//enum class Unit(val csvName: String) {
//    MicrogramPerCubicMeter("µg/m³"),
//    MilligramPerCubicMeter("mg/m³")
//}
//
//fun String.toUnit() =
//    Unit.values().find { it.csvName == this } ?: throw RuntimeException("Could not parse '$this' to Unit")
//
//fun String.toPollutant(): PollutantType {
//    return PollutantType.values().find { it.csvName == this }
//        ?: throw RuntimeException("Could not parse '$this' to PollutantType.")
//}
//
//fun saveHistoricalData() {
//    val saveToFile = true
//
//    val period = Period.hourly
//
//    for (year in 2012..2012) {
//        PollutantType.values().forEach { pollutantType ->
//            MeasurementType.values().forEach { type ->
//
//                val startDate = LocalDate.of(year, Month.OCTOBER, 1)
//                val startHour = 0
//
//                val endDate = LocalDate.of(year, Month.OCTOBER, 1)
//                val endHour = 10
//
//                val pollutionData = parsePollutionCSV(
//                    getPollutionCSV(
//                        pollutantType,
//                        type,
//                        period,
//                        startDate,
//                        startHour,
//                        endDate,
//                        endHour,
//                        saveToFile
//                    ), type
//                )
//                println(pollutionData)
//            }
//        }
//    }
//}
//
//fun fetchNewestData() {
//
//    val now = LocalDateTime.now()
//    val period = Period.hourly
//
//    val date = now.toLocalDate()
//    val startHour = now.hour - 1
//
//    val endHour = now.hour
//    PollutantType.values().forEach { pollutantType ->
//        MeasurementType.values().forEach { type ->
//
//            val pollutionData = parsePollutionCSV(
//                getPollutionCSV(
//                    pollutantType,
//                    type,
//                    period,
//                    date,
//                    startHour,
//                    date,
//                    endHour,
//                    false
//                ), type
//            )
//            println(pollutionData)
//        }
//    }
//}
//
//private fun parsePollutionCSV(csv: String, type: MeasurementType): Data {
//    val lines = csv.lineSequence()
//
//    val stations = lines.elementAt(0).split(";").drop(1).map {
//        it.split(" ").let { stationStr ->
//            Station(
//                stationStr[0],
//                stationStr.drop(1).joinToString(separator = " "),
//                idToLocation(stationStr[0])
//            )
//        }
//    }
//
//    if (stations.isEmpty()) {
//        return Data.Empty()
//    }
//
//    val pollutantType = lines.elementAt(1).split(";")[1].toPollutant()
//
//    val unit = lines.elementAt(2).split(";")[1].toUnit()
//
//    val timedValues = linkedMapOf<LocalDateTime, List<Double>>()
//    lines.drop(5).forEach { line ->
//        line.split(";").let {
//            val time = LocalDateTime.parse(it[0], DateTimeFormatter.ofPattern("dd.MM.uuuu mm:kk"))
//            val valueList = it.drop(1).map(String::toDouble)
//
//            timedValues[time] = valueList
//        }
//    }
//
//    return Data.PollutantCSVFormat(pollutantType, unit, type, stations, timedValues)
//}
//
//fun idToLocation(id: String): GeoLocation {
//    return when (id) {
//        "010" -> GeoLocation(52.543041, 13.349326)
//        "018" -> GeoLocation(52.485814, 13.348775)
//        "027" -> GeoLocation(52.398406, 13.368103)
//        "032" -> GeoLocation(52.473192, 13.225144)
//        "042" -> GeoLocation(52.489439, 13.430856)
//        "077" -> GeoLocation(52.644167, 13.483056)
//        "085" -> GeoLocation(52.447697, 13.647050)
//        "088" -> GeoLocation(52.510200, 13.388529)
//        "115" -> GeoLocation(52.506600, 13.332972)
//        "117" -> GeoLocation(52.463611, 13.318250)
//        "124" -> GeoLocation(52.438056, 13.387500)
//        "143" -> GeoLocation(52.467511, 13.441650)
//        "145" -> GeoLocation(52.653269, 13.296081)
//        "171" -> GeoLocation(52.513606, 13.418833)
//        "174" -> GeoLocation(52.514072, 13.469931)
//        "220" -> GeoLocation(52.481669, 13.433967)
//        "282" -> GeoLocation(52.485296, 13.529504)
//        else -> throw RuntimeException("Error while finding location of station. Station with id $id not found.")
//    }
//}
//
//open class Data {
//    data class PollutantCSVFormat(
//        val pollutantType: PollutantType,
//        val unit: Unit,
//        val type: MeasurementType,
//        val stationList: List<Station>,
//        val timePollutantPair: LinkedHashMap<LocalDateTime, List<Double>>
//    ) : Data()
//
//    class Failure(val error: Exception) : Data()
//    class Empty : Data()
//}
//
//
//data class Station(val id: String, val name: String, val location: GeoLocation)
//
//data class GeoLocation(val lat: Double, val lng: Double)
//
//private fun getPollutionCSV(
//    pollutantType: PollutantType,
//    type: MeasurementType,
//    period: Period,
//    startDate: LocalDate,
//    startHour: Int,
//    endDate: LocalDate,
//    endHour: Int,
//    saveToFile: Boolean
//): String {
//
//    val formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu")
//
//    val query =
//        "https://luftdaten.berlin.de/core/${pollutantType.name.toLowerCase(Locale.ROOT)}.csv?stationgroup=${type.name}&period=${period.param}" +
//                "&timespan=custom&start%5Bdate%5D=${startDate.format(formatter)}&start%5Bhour%5D=$startHour" +
//                "&end%5Bdate%5D=${endDate.format(formatter)}&end%5Bhour%5D=$endHour"
//    println("QUERY: $query")
//
//    val (request, response, result) = query.httpGet().responseString()
//
//    when (result) {
//        is Result.Failure -> error(response)
//        is Result.Success -> {
//            if (saveToFile) {
//
//                val path =
//                    Paths.get("./data/${startDate.year}/${pollutantType}_${type}_${period}_$startDate-${startHour}_$endDate-$endHour.csv")
//                if (!Files.exists(path)) {
//                    println("Creating File: ./data/${startDate.year}/${pollutantType}_${type}_${period}_$startDate-${startHour}_$endDate-$endHour.csv")
//                    File(path.toUri()).parentFile.mkdirs()
//                    Files.createFile(path)
//                    val file = path.toFile()
//                    file.writeText(result.value)
//                }
//            }
//
//            return result.value
//        }
//    }
//}
