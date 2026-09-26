package com.ekagra.incidentguard.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.ekagra.incidentguard.db.IncidentDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = IncidentDatabase.Schema,
            name = "incident_guard.db"
        )
    }
}
