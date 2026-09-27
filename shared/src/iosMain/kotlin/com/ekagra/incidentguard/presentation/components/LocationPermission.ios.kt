package com.ekagra.incidentguard.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
actual fun RequestLocationPermissionEffect(
    onPermissionGranted: () -> Unit
) {
    LaunchedEffect(Unit) {
        onPermissionGranted()
    }
}
