package com.ekagra.incidentguard.di

import com.ekagra.incidentguard.domain.usecase.CreateIncidentUseCase
import com.ekagra.incidentguard.domain.usecase.GetIncidentUseCase
import com.ekagra.incidentguard.domain.usecase.SyncIncidentsUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetIncidentUseCase(get()) }
    factory { CreateIncidentUseCase(get()) }
    factory { SyncIncidentsUseCase(get()) }
}