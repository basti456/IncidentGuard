package com.ekagra.incidentguard.domain.model

data class Incident(
    val id: String,
    val title: String,
    val description: String,
    val severity: Severity,
    val status: Status,
    val latitude: Double,
    val longitude: Double,
    val weather: WeatherInfo = WeatherInfo(),
    val localImagePath: String? = null,
    val remoteImageUrl: String? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val isSynced: Boolean = false
)