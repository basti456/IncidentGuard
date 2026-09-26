package com.ekagra.incidentguard.di

fun appModule() = listOf(
    platformModule,
    databaseModule,
    networkModule,
    repositoryModule,
    useCaseModule,
    viewModelModule
)
