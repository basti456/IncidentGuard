package com.ekagra.incidentguard.di

import com.ekagra.incidentguard.data.local.DatabaseDriverFactory
import com.ekagra.incidentguard.data.location.GpsLocationProvider
import com.ekagra.incidentguard.data.remote.NetworkObserver
import org.koin.core.module.Module
import org.koin.dsl.module
import kotlin.math.sin

actual val platformModule: Module =
    module {
        single { DatabaseDriverFactory(get()) }
        single { NetworkObserver(get()) }
        single { GpsLocationProvider(get()) }
    }
