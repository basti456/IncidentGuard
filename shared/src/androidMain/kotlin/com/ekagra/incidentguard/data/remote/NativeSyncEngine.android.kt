package com.ekagra.incidentguard.data.remote

import com.ekagra.incidentguard.domain.model.Incident
import com.ekagra.incidentguard.domain.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import java.io.IOException

actual class NativeSyncEngine {
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    // Firebase Storage disabled for free plan usage
    // private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    actual suspend fun syncIncidentToFirestore(incident: Incident): Resource<Unit> {
        return try {
            val data = mapOf(
                "id" to incident.id,
                "title" to incident.title,
                "description" to incident.description,
                "severity" to incident.severity.name,
                "status" to incident.status.name,
                "latitude" to incident.latitude,
                "longitude" to incident.longitude,
                "temperature" to incident.weather.temperatureCelsius,
                "weatherCondition" to incident.weather.condition,
                "localImagePath" to incident.localImagePath,
                "remoteImageUrl" to incident.remoteImageUrl,
                "createdAt" to incident.createdAt,
                "updatedAt" to incident.updatedAt
            )
            firestore.collection("incidents")
                .document(incident.id)
                .set(data)
                .await()
            Resource.Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Resource.Error("Firestore sync failed (${e.code}): ${e.message}")
        } catch (e: IOException) {
            Resource.Error("Network error during Firestore sync: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Unexpected error syncing to Firestore: ${e.message}")
        }
    }

    actual suspend fun uploadPhotoToStorage(
        localImagePath: String,
        remoteFileName: String
    ): Resource<String> {
        // Firebase Storage disabled for free tier.
        // Photos remain stored locally on device storage.
        return Resource.Success(localImagePath)
    }
}
