package com.ekagra.incidentguard.presentation.incident_list

import com.ekagra.incidentguard.domain.model.Incident

sealed interface IncidentListUiState {
    object Loading : IncidentListUiState
    data class Success(
        val incidents: List<Incident> = emptyList(),
        val userLatitude:Double? = null,
        val userLongitude:Double? = null,
        val isSyncing: Boolean = false
    ) : IncidentListUiState
    data class Error(val message: String) : IncidentListUiState
}