package com.ekagra.incidentguard.presentation.components

import androidx.compose.runtime.Composable

interface PhotoPickerLauncher {
    fun launch()
}

@Composable
expect fun rememberPhotoPickerLauncher(
    onPhotoPicked: (localPath: String) -> Unit
): PhotoPickerLauncher