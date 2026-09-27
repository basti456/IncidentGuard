package com.ekagra.incidentguard.presentation.components

import com.ekagra.incidentguard.domain.model.Incident

interface NativeMapProvider {
    fun createMapView(
        latitude: Double,
        longitude: Double,
        zoom: Float,
        incidents: List<Incident>,
        onMapClick: (latitude: Double, longitude: Double) -> Unit
    ): Any // Any, because commonMain can't reference UIView directly

    fun updateMapView(
        mapView: Any,
        latitude: Double,
        longitude: Double,
        zoom: Float,
        incidents: List<Incident>
    )
}