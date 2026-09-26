package com.ekagra.incidentguard.presentation.components

import androidx.compose.runtime.Composable

@Composable
expect fun RequestLocationPermissionEffect(
    onPermissionGranted: () -> Unit,
)