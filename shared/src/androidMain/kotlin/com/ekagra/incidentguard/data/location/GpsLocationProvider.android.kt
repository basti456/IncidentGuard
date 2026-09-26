package com.ekagra.incidentguard.data.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

actual class GpsLocationProvider(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    actual suspend fun getCurrentLocation(): MapLocation? {
        return try {
            val location = fusedLocationClient.lastLocation.await()
            if (location != null) {
                MapLocation(location.latitude, location.longitude)
            } else {
                null
            }
        } catch (e: SecurityException) {
            null
        }
    }


}