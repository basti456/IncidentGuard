package com.ekagra.incidentguard.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ekagra.incidentguard.domain.model.Incident

@Composable
expect fun IncidentMapView(
    modifier: Modifier = Modifier,
    latitude: Double,
    longitude: Double,
    zoom: Float = 12f,
    incidents: List<Incident> = emptyList(),
    onMapClick: ((latitude: Double, longitude: Double) -> Unit)? = null,
    onPinClick: ((incidentId: String) -> Unit)? = null

)