package com.ekagra.incidentguard.data.local


import com.ekagra.incidentguard.domain.model.Incident
import kotlinx.coroutines.flow.Flow

interface IncidentLocalDataSource {
    fun getAllIncidents(): Flow<List<Incident>>
    fun getIncidentById(id: String): Flow<Incident?>
    suspend fun getUnsyncedIncidents(): List<Incident>
    suspend fun insertIncident(incident: Incident)
    suspend fun updateSyncStatus(id: String, isSynced: Boolean, remoteImageUrl: String?, updatedAt: Long)
    suspend fun deleteIncident(id: String)
}