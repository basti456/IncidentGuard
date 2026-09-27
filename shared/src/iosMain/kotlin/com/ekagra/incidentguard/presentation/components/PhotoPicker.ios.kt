package com.ekagra.incidentguard.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberPhotoPickerLauncher(
    onPhotoPicked: (localPath: String) -> Unit
): PhotoPickerLauncher {
    return remember {
        object : PhotoPickerLauncher {
            override fun launch() {
                // iOS Photo Picker Launcher Stub
            }
        }
    }
}
