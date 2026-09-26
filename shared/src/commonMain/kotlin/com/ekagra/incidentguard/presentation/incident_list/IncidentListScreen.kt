package com.ekagra.incidentguard.presentation.incident_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ekagra.incidentguard.domain.model.Incident
import com.ekagra.incidentguard.domain.model.Severity
import com.ekagra.incidentguard.presentation.components.IncidentMapView
import com.ekagra.incidentguard.presentation.components.RequestLocationPermissionEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentListScreen(
    viewModel: IncidentListViewModel,
    onCreateIncidentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    RequestLocationPermissionEffect {
        viewModel.detectUserLocation()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("IncidentGuard Field Reports") },
                actions = {
                    IconButton(onClick = { viewModel.syncIncidents() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync Incidents"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateIncidentClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Incident"
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is IncidentListUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is IncidentListUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is IncidentListUiState.Success -> {
                    val incidents = state.incidents
                    val centerLat = state.userLatitude ?: incidents.firstOrNull()?.latitude ?: 37.7749
                    val centerLon = state.userLongitude ?: incidents.firstOrNull()?.longitude ?: -122.4194
                    Column(modifier = Modifier.fillMaxSize()) {
                        // 1. Top Section: Interactive Map View
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        ) {
                            IncidentMapView(
                                latitude = centerLat,
                                longitude = centerLon,
                                incidents = incidents
                            )
                        }

                        if (incidents.isEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "📍 No Field Incidents Reported Yet",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Tap the + button below to drop a pin and file your first inspection report.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            // 2. Bottom Section: Scrollable Incident Cards
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(incidents, key = { it.id }) { incident ->
                                    IncidentItemCard(incident = incident)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IncidentItemCard(
    incident: Incident,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = incident.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                SeverityBadge(severity = incident.severity)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = incident.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Live Weather Metadata
                incident.weather.temperatureCelsius?.let { temp ->
                    Text(
                        text = "🌡️ $temp°C (${incident.weather.condition ?: "Clear"})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Sync Status Badge
                SyncStatusBadge(isSynced = incident.isSynced)
            }
        }
    }
}

@Composable
fun SeverityBadge(severity: Severity) {
    val (bgColor, textColor) = when (severity) {
        Severity.CRITICAL -> Color(0xFFFFCDD2) to Color(0xFFB71C1C)
        Severity.HIGH -> Color(0xFFFFE0B2) to Color(0xFFE65100)
        Severity.MEDIUM -> Color(0xFFFFF9C4) to Color(0xFFF57F17)
        Severity.LOW -> Color(0xFFC8E6C9) to Color(0xFF1B5E20)
    }

    Box(
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = severity.name,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SyncStatusBadge(isSynced: Boolean) {
    val (text, color) = if (isSynced) {
        "Synced" to Color(0xFF2E7D32)
    } else {
        "Local Queue" to Color(0xFFD84315)
    }

    Text(
        text = "• $text",
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.Medium
    )
}