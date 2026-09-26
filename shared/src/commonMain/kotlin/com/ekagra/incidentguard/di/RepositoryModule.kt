package com.ekagra.incidentguard.di

import com.ekagra.incidentguard.data.repository.IncidentRepositoryImpl
import com.ekagra.incidentguard.domain.repository.IncidentRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<IncidentRepository> {
        IncidentRepositoryImpl(
            localDataSource = get(),
            weatherRemoteDataSource = get(),
            syncEngine = get()
        )
    }
}
