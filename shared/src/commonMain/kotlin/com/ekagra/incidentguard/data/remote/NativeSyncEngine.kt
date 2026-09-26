package com.ekagra.incidentguard.data.remote

import com.ekagra.incidentguard.domain.model.Incident
import com.ekagra.incidentguard.domain.util.Resource

expect class NativeSyncEngine() {

    suspend fun syncIncidentToFirestore(incident: Incident): Resource<Unit>
    suspend fun uploadPhotoToStorage(
        localImagePath: String,
        remoteFileName: String
    ): Resource<String>
}
