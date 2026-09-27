package com.ekagra.incidentguard.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import com.ekagra.incidentguard.domain.model.Incident
import platform.UIKit.UIView

object NativeMapRegistry {
    var provider: NativeMapProvider? = null
}

@Composable
actual fun IncidentMapView(
    modifier: Modifier,
    latitude: Double,
    longitude: Double,
    zoom: Float,
    incidents: List<Incident>,
    onMapClick: ((latitude: Double, longitude: Double) -> Unit)?,
    onPinClick: ((incidentId: String) -> Unit)?
) {
    val provider = NativeMapRegistry.provider

    if (provider != null) {
        UIKitView(
            factory = {
                provider.createMapView(latitude, longitude, zoom, incidents) { lat, lon ->
                    onMapClick?.invoke(lat, lon)
                } as UIView
            },
            update = { mapView ->
                provider.updateMapView(mapView, latitude, longitude, zoom, incidents)
            },
            modifier = modifier.fillMaxSize()
        )
    } else {
        Box(modifier = modifier.fillMaxSize())
    }
}
