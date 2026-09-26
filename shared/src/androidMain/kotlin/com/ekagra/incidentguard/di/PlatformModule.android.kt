package com.ekagra.incidentguard.di

import com.ekagra.incidentguard.data.local.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module =
    module {
        single { DatabaseDriverFactory(get()) }
    }
