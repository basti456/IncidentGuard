package com.ekagra.incidentguard.domain.usecase

import com.ekagra.incidentguard.domain.model.Incident
import com.ekagra.incidentguard.domain.repository.IncidentRepository
import kotlinx.coroutines.flow.Flow

class GetIncidentUseCase(private val repository: IncidentRepository) {
    operator fun invoke(): Flow<List<Incident>> {
        return repository.getAllIncidents()
    }
}