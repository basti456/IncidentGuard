package com.ekagra.incidentguard.data.remote

import com.ekagra.incidentguard.domain.model.WeatherInfo
import com.ekagra.incidentguard.domain.util.Resource

interface WeatherRemoteDataSource {
    suspend fun getWeather(latitude: Double, longitude: Double): Resource<WeatherInfo>
}