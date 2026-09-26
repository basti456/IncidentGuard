package com.ekagra.incidentguard.presentation.incident_create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ekagra.incidentguard.domain.model.Severity
import com.ekagra.incidentguard.presentation.components.IncidentMapView
import com.ekagra.incidentguard.presentation.components.RequestLocationPermissionEffect
import com.ekagra.incidentguard.presentation.components.rememberPhotoPickerLauncher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateIncidentScreen(
    viewModel: CreateIncidentViewModel,
    onBackClick: () -> Unit,
    onIncidentCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onIncidentCreated()
        }
    }
    RequestLocationPermissionEffect {
        viewModel.detectCurrentLocation()
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }
    val photoPicker = rememberPhotoPickerLauncher { localPath ->
        viewModel.onImageSelected(localPath)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("File Incident Report") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedButton(
                onClick = { photoPicker.launch() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Attach Photo"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (uiState.localImagePath != null) "Photo Attached ✓" else "Attach Inspection Photo"
                )
            }
            // 1. Title Input
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.onTitleChanged(it) },
                label = { Text("Incident Title *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 2. Description Input
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.onDescriptionChanged(it) },
                label = { Text("Detailed Description *") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            // 3. Severity Selector
            Text(
                text = "Severity Level",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Severity.entries.forEach { severity ->
                    FilterChip(
                        selected = uiState.severity == severity,
                        onClick = { viewModel.onSeverityChanged(severity) },
                        label = { Text(severity.name) }
                    )
                }
            }

            // 4. Location Selector Map
            Text(
                text = "Tap Map to Select Location",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                IncidentMapView(
                    latitude = uiState.latitude,
                    longitude = uiState.longitude,
                    onMapClick = { lat, lon ->
                        viewModel.onLocationSelected(lat, lon)
                    }
                )
            }

            Text(
                text = "Selected Coordinates: ${uiState.latitude.toString().take(8)}, ${uiState.longitude.toString().take(9)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 5. Submit Button
            Button(
                onClick = { viewModel.submitIncident() },
                enabled = !uiState.isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(
                        text = "Submit Report",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}