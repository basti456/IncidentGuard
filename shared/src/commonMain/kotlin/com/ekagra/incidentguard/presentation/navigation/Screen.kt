package com.ekagra.incidentguard.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface ScreenRoute {
    @Serializable
    data object IncidentList : ScreenRoute

    @Serializable
    data object CreateIncident : ScreenRoute

    @Serializable
    data class IncidentDetail(val incidentId: String) : ScreenRoute
}