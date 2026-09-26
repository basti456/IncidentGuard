package com.ekagra.incidentguard.di

import com.ekagra.incidentguard.data.remote.ApiConstants
import com.ekagra.incidentguard.data.remote.NativeSyncEngine
import com.ekagra.incidentguard.data.remote.WeatherApiClient
import com.ekagra.incidentguard.data.remote.WeatherRemoteDataSource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single { NativeSyncEngine() }

    single {
        HttpClient {
            // 1. Base URL Config
            defaultRequest {
                url(ApiConstants.OPEN_METEO_BASE_URL)
            }

            // 2. Timeout Configuration
            install(HttpTimeout) {
                requestTimeoutMillis = ApiConstants.NETWORK_TIMEOUT_MILLIS
                connectTimeoutMillis = ApiConstants.NETWORK_TIMEOUT_MILLIS
                socketTimeoutMillis = ApiConstants.NETWORK_TIMEOUT_MILLIS
            }

            // 3. JSON Content Negotiation
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = true
                })
            }
        }
    }

    single<WeatherRemoteDataSource> { WeatherApiClient(get()) }
}
