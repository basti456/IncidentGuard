package com.ekagra.incidentguard.domain.repository

import com.ekagra.incidentguard.domain.model.Incident
import com.ekagra.incidentguard.domain.model.Severity
import com.ekagra.incidentguard.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface IncidentRepository {
    fun getAllIncidents(): Flow<List<Incident>>
    fun getIncidentById(id: String): Flow<Incident?>
    suspend fun createIncident(
        title: String,
        description: String,
        severity: Severity,
        latitude: Double,
        longitude: Double,
        localImagePath: String?
    ): Resource<Unit>
    suspend fun syncPendingIncidents(): Resource<Unit>
}