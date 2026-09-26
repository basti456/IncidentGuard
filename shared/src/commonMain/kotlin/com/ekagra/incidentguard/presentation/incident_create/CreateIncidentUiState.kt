package com.ekagra.incidentguard.presentation.incident_create

import com.ekagra.incidentguard.domain.model.Severity

data class CreateIncidentUiState(
    val title: String = "",
    val description: String = "",
    val severity: Severity = Severity.MEDIUM,
    val latitude: Double = 37.7749, // Default SF center or GPS location
    val longitude: Double = -122.4194,
    val localImagePath: String? = null,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
