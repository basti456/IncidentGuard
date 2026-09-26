package com.ekagra.incidentguard.data.remote

import com.ekagra.incidentguard.domain.model.Incident
import com.ekagra.incidentguard.domain.util.Resource

actual class NativeSyncEngine {
    actual suspend fun syncIncidentToFirestore(incident: Incident): Resource<Unit> {
        return Resource.Success(Unit)
    }

    actual suspend fun uploadPhotoToStorage(
        localImagePath: String,
        remoteFileName: String
    ): Resource<String> {
        return Resource.Success("https://storage.googleapis.com/placeholder/$remoteFileName")
    }
}
