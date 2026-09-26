package com.ekagra.incidentguard.presentation.incident_create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekagra.incidentguard.data.location.GpsLocationProvider
import com.ekagra.incidentguard.domain.model.Severity
import com.ekagra.incidentguard.domain.usecase.CreateIncidentUseCase
import com.ekagra.incidentguard.domain.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateIncidentViewModel(
    private val createIncidentUseCase: CreateIncidentUseCase,
    private val gpsLocationProvider: GpsLocationProvider
) :
    ViewModel() {
    private val _uiState = MutableStateFlow(CreateIncidentUiState())
    val uiState: StateFlow<CreateIncidentUiState> = _uiState.asStateFlow()

    fun onTitleChanged(newTitle: String) {
        _uiState.update { it.copy(title = newTitle, errorMessage = null) }
    }

    fun onDescriptionChanged(newDescription: String) {
        _uiState.update { it.copy(description = newDescription, errorMessage = null) }
    }

    fun onSeverityChanged(newSeverity: Severity) {
        _uiState.update { it.copy(severity = newSeverity, errorMessage = null) }
    }

    fun onLocationSelected(latitude: Double, longitude: Double) {
        _uiState.update { it.copy(latitude = latitude, longitude = longitude, errorMessage = null) }
    }

    fun onImageSelected(localImagePath: String) {
        _uiState.update { it.copy(localImagePath = localImagePath, errorMessage = null) }
    }

    init {
        detectCurrentLocation()
    }

    fun detectCurrentLocation() {
        viewModelScope.launch {
            val location = gpsLocationProvider.getCurrentLocation()
            if (location != null) {
                onLocationSelected(location.latitude, location.longitude)
            }
        }
    }

    fun submitIncident() {
        val currentState = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val result = createIncidentUseCase(
                title = currentState.title,
                description = currentState.description,
                severity = currentState.severity,
                latitude = currentState.latitude,
                longitude = currentState.longitude,
                localImagePath = currentState.localImagePath
            )
            when (result) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isSubmitting = false, isSuccess = true) }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = result.message ?: "Failed to create incident"
                        )
                    }
                }

                is Resource.Loading -> {
                    _uiState.update { it.copy(isSubmitting = true) }
                }
            }
        }
    }

}
