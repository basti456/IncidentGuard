package com.ekagra.incidentguard.domain.usecase

import com.ekagra.incidentguard.domain.repository.IncidentRepository
import com.ekagra.incidentguard.domain.util.Resource

class SyncIncidentsUseCase(
    private val repository: IncidentRepository
) {
    suspend operator fun invoke(): Resource<Unit> {
        return repository.syncPendingIncidents()
    }
}