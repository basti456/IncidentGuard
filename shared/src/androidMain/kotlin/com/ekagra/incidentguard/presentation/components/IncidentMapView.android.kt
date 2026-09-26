package com.ekagra.incidentguard.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.ekagra.incidentguard.domain.model.Incident
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

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
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), zoom)
    }

    LaunchedEffect(latitude, longitude, zoom) {
        cameraPositionState.position =
            CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), zoom)
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
            onMapClick?.invoke(latLng.latitude, latLng.longitude)
        }
    ) {
        if (incidents.isEmpty()) {
            Marker(
                state = rememberUpdatedMarkerState(
                    position = LatLng(latitude, longitude)
                ),
                title = "Selected Location"
            )
        } else {
            incidents.forEach { incident ->
                Marker(
                    state = rememberUpdatedMarkerState(
                        position = LatLng(incident.latitude, incident.longitude)
                    ),
                    title = incident.title,
                    snippet = "Severity: ${incident.severity.name} | Status: ${incident.status.name}",
                    onClick = {
                        onPinClick?.invoke(incident.id)
                        true
                    }
                )
            }
        }
    }
}
