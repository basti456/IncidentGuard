package com.ekagra.incidentguard.data.repository

import com.ekagra.incidentguard.data.local.IncidentLocalDataSource
import com.ekagra.incidentguard.data.remote.NativeSyncEngine
import com.ekagra.incidentguard.data.remote.WeatherRemoteDataSource
import com.ekagra.incidentguard.domain.model.Incident
import com.ekagra.incidentguard.domain.model.Severity
import com.ekagra.incidentguard.domain.model.Status
import com.ekagra.incidentguard.domain.model.WeatherInfo
import com.ekagra.incidentguard.domain.repository.IncidentRepository
import com.ekagra.incidentguard.domain.util.Resource
import com.ekagra.incidentguard.util.currentTimeMillis
import com.ekagra.incidentguard.util.generateUUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class IncidentRepositoryImpl(
    private val localDataSource: IncidentLocalDataSource,
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
    private val syncEngine: NativeSyncEngine,
    private val externalScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : IncidentRepository {

    override fun getAllIncidents(): Flow<List<Incident>> {
        return localDataSource.getAllIncidents()
    }

    override fun getIncidentById(id: String): Flow<Incident?> {
        return localDataSource.getIncidentById(id)
    }

    override suspend fun createIncident(
        title: String,
        description: String,
        severity: Severity,
        latitude: Double,
        longitude: Double,
        localImagePath: String?
    ): Resource<Unit> {
        // Step 1: Fetch weather for coordinates via Open-Meteo API
        val weatherInfo = when (val result = weatherRemoteDataSource.getWeather(latitude, longitude)) {
            is Resource.Success -> result.data ?: WeatherInfo()
            else -> WeatherInfo()
        }

        // Step 2: Construct domain Incident model
        val incident = Incident(
            id = generateUUID(),
            title = title,
            description = description,
            severity = severity,
            status = Status.OPEN,
            latitude = latitude,
            longitude = longitude,
            weather = weatherInfo,
            localImagePath = localImagePath,
            createdAt = currentTimeMillis(),
            updatedAt = currentTimeMillis(),
            isSynced = false
        )

        // Step 3: Write IMMEDIATELY to local SQLDelight DB (Offline-First Ground Truth!)
        localDataSource.insertIncident(incident)

        // Step 4: Non-blocking background sync
        externalScope.launch {
            syncPendingIncidents()
        }

        return Resource.Success(Unit)
    }

    override suspend fun syncPendingIncidents(): Resource<Unit> {
        // Step 1: PUSH local unsynced records -> Cloud Firestore
        val unsyncedIncidents = localDataSource.getUnsyncedIncidents()
        for (incident in unsyncedIncidents) {
            var remoteImageUrl = incident.remoteImageUrl

            if (incident.localImagePath != null && remoteImageUrl == null) {
                when (val photoResult = syncEngine.uploadPhotoToStorage(incident.localImagePath, "${incident.id}.jpg")) {
                    is Resource.Success -> {
                        remoteImageUrl = photoResult.data
                    }
                    else -> {}
                }
            }

            val updatedIncident = incident.copy(remoteImageUrl = remoteImageUrl)
            when (syncEngine.syncIncidentToFirestore(updatedIncident)) {
                is Resource.Success -> {
                    localDataSource.updateSyncStatus(
                        id = incident.id,
                        isSynced = true,
                        remoteImageUrl = remoteImageUrl,
                        updatedAt = currentTimeMillis()
                    )
                }
                else -> {}
            }
        }

        // Step 2: PULL remote Firestore documents -> Local SQLDelight DB
        when (val remoteResult = syncEngine.fetchRemoteIncidents()) {
            is Resource.Success -> {
                val remoteIncidents = remoteResult.data ?: emptyList()
                for (remoteIncident in remoteIncidents) {
                    localDataSource.insertIncident(remoteIncident.copy(isSynced = true))
                }
            }
            else -> {}
        }

        return Resource.Success(Unit)
    }
}
