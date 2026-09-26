package com.ekagra.incidentguard.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.ekagra.incidentguard.db.IncidentEntityQueries
import com.ekagra.incidentguard.domain.model.Incident
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class IncidentLocalDataSourceImpl(
    private val queries: IncidentEntityQueries,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : IncidentLocalDataSource {

    override fun getAllIncidents(): Flow<List<Incident>> {
        return queries.selectAllIncidents()
            .asFlow()
            .mapToList(dispatcher)
            .map { entities ->
                entities.map { entity -> entity.toDomainModel() }
            }
    }

    override fun getIncidentById(id: String): Flow<Incident?> {
        return queries.selectIncidentById(id)
            .asFlow()
            .mapToOneOrNull(dispatcher)
            .map { entity -> entity?.toDomainModel() }
    }

    override suspend fun getUnsyncedIncidents(): List<Incident> {
        return withContext(dispatcher) {
            queries.selectUnsyncedIncidents()
                .executeAsList()
                .map { entity -> entity.toDomainModel() }
        }
    }

    override suspend fun insertIncident(incident: Incident) {
        withContext(dispatcher) {
            val entity = incident.toEntity()
            queries.insertOrReplaceIncident(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                severity = entity.severity,
                status = entity.status,
                latitude = entity.latitude,
                longitude = entity.longitude,
                temperature = entity.temperature,
                weatherCondition = entity.weatherCondition,
                localImagePath = entity.localImagePath,
                remoteImageUrl = entity.remoteImageUrl,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                isSynced = entity.isSynced
            )
        }
    }

    override suspend fun updateSyncStatus(
        id: String,
        isSynced: Boolean,
        remoteImageUrl: String?,
        updatedAt: Long
    ) {
        withContext(dispatcher) {
            queries.updateSyncStatus(
                isSynced = if (isSynced) 1L else 0L,
                remoteImageUrl = remoteImageUrl,
                updatedAt = updatedAt,
                id = id
            )
        }
    }

    override suspend fun deleteIncident(id: String) {
        withContext(dispatcher) {
            queries.deleteIncidentById(id)
        }
    }
}