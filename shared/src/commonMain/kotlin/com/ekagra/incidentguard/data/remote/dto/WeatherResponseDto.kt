package com.ekagra.incidentguard.data.remote.dto

import com.ekagra.incidentguard.domain.model.WeatherInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenMeteoResponseDto(
    @SerialName("current_weather")
    val currentWeather: CurrentWeatherDto? = null
)

@Serializable
data class CurrentWeatherDto(
    @SerialName("temperature")
    val temperature: Double? = null,
    @SerialName("weathercode")
    val weatherCode: Int? = null
)

fun OpenMeteoResponseDto.toDomainModel(): WeatherInfo {
    val current = this.currentWeather
    return WeatherInfo(
        temperatureCelsius = current?.temperature,
        condition = mapWeatherCodeToCondition(current?.weatherCode)
    )
}

fun mapWeatherCodeToCondition(code: Int?): String {
    return when (code) {
        0 -> "Clear Sky"
        1, 2, 3 -> "Partly Cloudy"
        45, 48 -> "Foggy"
        51, 53, 55, 56, 57 -> "Drizzle"
        61, 63, 65, 66, 67 -> "Rain"
        71, 73, 75, 77 -> "Snow"
        80, 81, 82 -> "Rain Showers"
        95, 96, 99 -> "Thunderstorm"
        else -> "Unknown"
    }
}