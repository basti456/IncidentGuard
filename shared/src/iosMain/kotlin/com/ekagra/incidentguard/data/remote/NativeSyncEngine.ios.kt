package com.ekagra.incidentguard.data.remote

import com.ekagra.incidentguard.domain.model.Incident
import com.ekagra.incidentguard.domain.util.Resource

actual class NativeSyncEngine {
    actual suspend fun syncIncidentToFirestore(incident: Incident): Resource<Unit> {
        val provider = NativeSyncRegistry.provider ?: return Resource.Success(Unit)
        return try {
            val success = provider.syncIncidentToFirestore(incident)
            if (success) Resource.Success(Unit) else Resource.Error("Sync failed on iOS")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Firestore sync failed on iOS")
        }
    }

    actual suspend fun uploadPhotoToStorage(
        localImagePath: String,
        remoteFileName: String
    ): Resource<String> {
        return Resource.Success(localImagePath)
    }

    actual suspend fun fetchRemoteIncidents(): Resource<List<Incident>> {
        val provider = NativeSyncRegistry.provider ?: return Resource.Success(emptyList())
        return try {
            val incidents = provider.fetchRemoteIncidents()
            Resource.Success(incidents)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch remote incidents on iOS")
        }
    }
}
