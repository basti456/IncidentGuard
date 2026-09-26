package com.ekagra.incidentguard.presentation.components

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.FileOutputStream

@Composable
actual fun rememberPhotoPickerLauncher(
    onPhotoPicked: (localPath: String) -> Unit
): PhotoPickerLauncher {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val localPath = copyUriToCache(context, it)
            if (localPath != null) {
                onPhotoPicked(localPath)
            }
        }
    }

    return remember(launcher) {
        object : PhotoPickerLauncher {
            override fun launch() {
                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }
}

private fun copyUriToCache(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val cacheFile = File(context.cacheDir, "inspection_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(cacheFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        cacheFile.absolutePath
    } catch (e: Exception) {
        null
    }
}