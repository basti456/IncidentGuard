package com.ekagra.incidentguard.data.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

actual class NetworkObserver {
    actual val isConnected: Flow<Boolean> = flowOf(true)
}