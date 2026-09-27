package com.ekagra.incidentguard.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import com.ekagra.incidentguard.domain.model.Incident
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIView

object NativeMapConfig {
    var provider: ((
        latitude: Double,
        longitude: Double,
        zoom: Float,
        incidents: List<Incident>,
        onMapClick: ((latitude: Double, longitude: Double) -> Unit)?
    ) -> UIView)? = null

    var updater: ((
        mapView: UIView,
        latitude: Double,
        longitude: Double,
        zoom: Float,
        incidents: List<Incident>
    ) -> Unit)? = null
}

@OptIn(ExperimentalForeignApi::class)
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
    val provider = NativeMapConfig.provider
    val updater = NativeMapConfig.updater

    if (provider != null) {
        UIKitView(
            factory = {
                provider(latitude, longitude, zoom, incidents, onMapClick)
            },
            update = { mapView ->
                updater?.invoke(mapView, latitude, longitude, zoom, incidents)
            },
            modifier = modifier.fillMaxSize(),
            properties = UIKitInteropProperties(
                isInteractive = true,
                isNativeAccessibilityEnabled = true
            )
        )
    } else {
        Box(modifier = modifier.fillMaxSize())
    }
}
