package com.ekagra.incidentguard.data.remote

import kotlinx.coroutines.flow.Flow

expect class NetworkObserver{
    val isConnected: Flow<Boolean>
}