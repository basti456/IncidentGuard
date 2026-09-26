package com.ekagra.incidentguard.presentation.incident_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekagra.incidentguard.domain.usecase.GetIncidentUseCase
import com.ekagra.incidentguard.domain.usecase.SyncIncidentsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class IncidentListViewModel(
    getIncidentUseCase: GetIncidentUseCase,
    private val syncIncidentsUseCase: SyncIncidentsUseCase
) : ViewModel() {
    val uiState: StateFlow<IncidentListUiState> = getIncidentUseCase().map { incidents ->
        IncidentListUiState.Success(incidents = incidents) as IncidentListUiState
    }.catch { error ->
        emit(IncidentListUiState.Error(error.message ?: "Failed to load incidents"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = IncidentListUiState.Loading
    )

    fun syncIncidents() {
        viewModelScope.launch {
            syncIncidentsUseCase
        }
    }
}