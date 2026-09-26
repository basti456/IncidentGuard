package com.ekagra.incidentguard.data.location

data class MapLocation(val latitude: Double, val longitude: Double)

expect class GpsLocationProvider {
    suspend fun getCurrentLocation(): MapLocation?
}