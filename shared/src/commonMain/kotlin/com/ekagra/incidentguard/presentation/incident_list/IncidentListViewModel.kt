package com.ekagra.incidentguard.presentation.incident_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekagra.incidentguard.data.location.GpsLocationProvider
import com.ekagra.incidentguard.data.remote.NetworkObserver
import com.ekagra.incidentguard.domain.usecase.GetIncidentUseCase
import com.ekagra.incidentguard.domain.usecase.SyncIncidentsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class IncidentListViewModel(
    getIncidentUseCase: GetIncidentUseCase,
    private val syncIncidentsUseCase: SyncIncidentsUseCase,
    private val networkObserver: NetworkObserver,
    private val gpsLocationProvider: GpsLocationProvider
) : ViewModel() {

    private val _userLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val uiState: StateFlow<IncidentListUiState> = combine(
        getIncidentUseCase(),
        _userLocation
    ) { incidents, userLoc ->
        IncidentListUiState.Success(
            incidents = incidents,
            userLatitude = userLoc?.first,
            userLongitude = userLoc?.second
        ) as IncidentListUiState
    }.catch { error ->
        emit(IncidentListUiState.Error(error.message ?: "Failed to load incidents"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = IncidentListUiState.Loading
    )

    init {
        viewModelScope.launch {
            networkObserver.isConnected.collect { isConnected ->
                if (isConnected) {
                    syncIncidents()
                }
            }
        }
        detectUserLocation()
    }


    fun syncIncidents() {
        viewModelScope.launch {
            syncIncidentsUseCase()
        }
    }

    fun detectUserLocation() {
        viewModelScope.launch {
            val location = gpsLocationProvider.getCurrentLocation()
            if (location != null) {
                _userLocation.value = location.latitude to location.longitude
            }
        }
    }
}