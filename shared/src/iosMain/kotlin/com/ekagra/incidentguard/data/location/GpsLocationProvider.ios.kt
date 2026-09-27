package com.ekagra.incidentguard.data.location

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.delay
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
private class LocationDelegate : NSObject(), CLLocationManagerDelegateProtocol {
    override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: CLAuthorizationStatus) {
        if (didChangeAuthorizationStatus == kCLAuthorizationStatusAuthorizedWhenInUse || didChangeAuthorizationStatus == kCLAuthorizationStatusAuthorizedAlways) {
            manager.startUpdatingLocation()
        }
    }
}

actual class GpsLocationProvider {
    private val locationManager = CLLocationManager()
    private val delegate = LocationDelegate()

    init {
        locationManager.delegate = delegate
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()
    }

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun getCurrentLocation(): MapLocation? {
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()

        for (attempt in 0..20) {
            val status = locationManager.authorizationStatus()
            if (status == kCLAuthorizationStatusAuthorizedWhenInUse || status == kCLAuthorizationStatusAuthorizedAlways) {
                val location = locationManager.location
                if (location != null) {
                    val lat = location.coordinate.useContents { latitude }
                    val lon = location.coordinate.useContents { longitude }
                    return MapLocation(lat, lon)
                }
            }
            delay(300)
        }

        return MapLocation(22.4962, 70.1814)
    }
}
