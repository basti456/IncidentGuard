package com.ekagra.incidentguard.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.ekagra.incidentguard.db.IncidentDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = IncidentDatabase.Schema,
            context = context,
            name = "incident_guard.db"
        )
    }
}
