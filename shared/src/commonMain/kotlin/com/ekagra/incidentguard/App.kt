package com.ekagra.incidentguard

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ekagra.incidentguard.di.appModule
import com.ekagra.incidentguard.presentation.navigation.NavGraph
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
@Preview
fun App() {
    KoinApplication(configuration = koinConfiguration {
        modules(appModule())
    }) {
        MaterialTheme {
            NavGraph()
        }
    }
}
