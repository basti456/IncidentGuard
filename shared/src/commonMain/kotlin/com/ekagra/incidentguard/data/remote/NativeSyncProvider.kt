package com.ekagra.incidentguard.data.remote

import com.ekagra.incidentguard.domain.model.Incident

interface NativeSyncProvider {
    suspend fun syncIncidentToFirestore(incident: Incident): Boolean
    suspend fun fetchRemoteIncidents(): List<Incident>
}

object NativeSyncRegistry {
    var provider: NativeSyncProvider? = null
}
