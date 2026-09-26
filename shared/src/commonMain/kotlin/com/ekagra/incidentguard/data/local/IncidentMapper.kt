package com.ekagra.incidentguard.data.local

import com.ekagra.incidentguard.db.IncidentEntity
import com.ekagra.incidentguard.domain.model.Incident
import com.ekagra.incidentguard.domain.model.Severity
import com.ekagra.incidentguard.domain.model.Status
import com.ekagra.incidentguard.domain.model.WeatherInfo

// Database Entity -> Domain Model
fun IncidentEntity.toDomainModel(): Incident {
    return Incident(
        id = id,
        title = title,
        description = description,
        severity = try { Severity.valueOf(severity) } catch (e: Exception) { Severity.LOW },
        status = try { Status.valueOf(status) } catch (e: Exception) { Status.OPEN },
        latitude = latitude,
        longitude = longitude,
        weather = WeatherInfo(
            temperatureCelsius = temperature,
            condition = weatherCondition
        ),
        localImagePath = localImagePath,
        remoteImageUrl = remoteImageUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isSynced = isSynced == 1L
    )
}

// Domain Model -> Database Entity
fun Incident.toEntity(): IncidentEntity {
    return IncidentEntity(
        id = id,
        title = title,
        description = description,
        severity = severity.name,
        status = status.name,
        latitude = latitude,
        longitude = longitude,
        temperature = weather.temperatureCelsius,
        weatherCondition = weather.condition,
        localImagePath = localImagePath,
        remoteImageUrl = remoteImageUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isSynced = if (isSynced) 1L else 0L
    )
}