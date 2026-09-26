package com.ekagra.incidentguard.di

import com.ekagra.incidentguard.data.local.DatabaseDriverFactory
import com.ekagra.incidentguard.data.local.IncidentLocalDataSource
import com.ekagra.incidentguard.data.local.IncidentLocalDataSourceImpl
import com.ekagra.incidentguard.db.IncidentDatabase
import org.koin.dsl.module

val databaseModule = module {
    single {
        val driverFactory: DatabaseDriverFactory = get()
        IncidentDatabase(driverFactory.createDriver())
    }
    single { get<IncidentDatabase>().incidentEntityQueries }
    single<IncidentLocalDataSource> { IncidentLocalDataSourceImpl(get()) }
}