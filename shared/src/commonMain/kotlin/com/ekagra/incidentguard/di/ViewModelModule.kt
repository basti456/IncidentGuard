package com.ekagra.incidentguard.di

import com.ekagra.incidentguard.presentation.incident_create.CreateIncidentViewModel
import com.ekagra.incidentguard.presentation.incident_list.IncidentListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { IncidentListViewModel(get(), get()) }
    viewModel { CreateIncidentViewModel(get()) }
}