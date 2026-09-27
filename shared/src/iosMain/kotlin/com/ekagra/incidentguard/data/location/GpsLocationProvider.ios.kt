package com.ekagra.incidentguard.data.location

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse

actual class GpsLocationProvider {
    private val locationManager = CLLocationManager()

    init {
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()
    }

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun getCurrentLocation(): MapLocation? {
        val status = locationManager.authorizationStatus()
        if (status == kCLAuthorizationStatusAuthorizedWhenInUse || status == kCLAuthorizationStatusAuthorizedAlways) {
            locationManager.startUpdatingLocation()
            val location = locationManager.location
            if (location != null) {
                val lat = location.coordinate.useContents { latitude }
                val lon = location.coordinate.useContents { longitude }
                return MapLocation(lat, lon)
            }
        } else {
            locationManager.requestWhenInUseAuthorization()
            locationManager.startUpdatingLocation()
        }
        return MapLocation(22.4962, 70.1814)
    }
}
