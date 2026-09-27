package com.ekagra.incidentguard.data.remote

import com.ekagra.incidentguard.domain.model.Incident
import com.ekagra.incidentguard.domain.util.Resource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object NativeSyncConfig {
    var syncHandler: ((incident: Incident, completion: (Boolean, String?) -> Unit) -> Unit)? = null
}

actual class NativeSyncEngine {
    actual suspend fun syncIncidentToFirestore(incident: Incident): Resource<Unit> {
        val handler = NativeSyncConfig.syncHandler ?: return Resource.Success(Unit)

        return suspendCancellableCoroutine { continuation ->
            handler(incident) { success, errorMessage ->
                if (success) {
                    continuation.resume(Resource.Success(Unit))
                } else {
                    continuation.resume(Resource.Error(errorMessage ?: "Firestore sync failed on iOS"))
                }
            }
        }
    }

    actual suspend fun uploadPhotoToStorage(
        localImagePath: String,
        remoteFileName: String
    ): Resource<String> {
        return Resource.Success(localImagePath)
    }
}
