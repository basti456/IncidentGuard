package com.ekagra.incidentguard.domain.usecase

import com.ekagra.incidentguard.domain.model.Severity
import com.ekagra.incidentguard.domain.repository.IncidentRepository
import com.ekagra.incidentguard.domain.util.Resource

class CreateIncidentUseCase(
    private val repository: IncidentRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        severity: Severity,
        latitude: Double,
        longitude: Double,
        localImagePath: String? = null
    ): Resource<Unit> {
        // Business Validation Rules
        if (title.isBlank()) {
            return Resource.Error("Incident title cannot be empty.")
        }
        if (description.isBlank()) {
            return Resource.Error("Incident description cannot be empty.")
        }
        if (latitude < -90.0 || latitude > 90.0 || longitude < -180.0 || longitude > 180.0) {
            return Resource.Error("Invalid GPS coordinates selected.")
        }

        return repository.createIncident(
            title = title.trim(),
            description = description.trim(),
            severity = severity,
            latitude = latitude,
            longitude = longitude,
            localImagePath = localImagePath
        )
    }
}