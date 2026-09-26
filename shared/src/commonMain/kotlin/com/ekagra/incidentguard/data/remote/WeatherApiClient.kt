package com.ekagra.incidentguard.data.remote

import com.ekagra.incidentguard.data.remote.dto.OpenMeteoResponseDto
import com.ekagra.incidentguard.data.remote.dto.toDomainModel
import com.ekagra.incidentguard.domain.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.io.IOException
import com.ekagra.incidentguard.domain.model.WeatherInfo

class WeatherApiClient(
    private val httpClient: HttpClient
) : WeatherRemoteDataSource {

    override suspend fun getWeather(latitude: Double, longitude: Double): Resource<WeatherInfo> {
        return try {
            val dto: OpenMeteoResponseDto = httpClient.get("v1/forecast") {
                parameter("latitude", latitude)
                parameter("longitude", longitude)
                parameter("current_weather", true)
            }.body()

            Resource.Success(dto.toDomainModel())
        } catch (e: ClientRequestException) {
            Resource.Error("Client HTTP Error (${e.response.status.value}): ${e.message}")
        } catch (e: ServerResponseException) {
            Resource.Error("Server Error (${e.response.status.value}). Please try again later.")
        } catch (e: IOException) {
            Resource.Error("Network error: Please check your internet connection.")
        } catch (e: Exception) {
            Resource.Error("Unexpected error occurred: ${e.message ?: "Unknown error"}")
        }
    }
}