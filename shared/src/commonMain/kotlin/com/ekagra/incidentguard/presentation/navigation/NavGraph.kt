package com.ekagra.incidentguard.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ekagra.incidentguard.presentation.incident_create.CreateIncidentScreen
import com.ekagra.incidentguard.presentation.incident_create.CreateIncidentViewModel
import com.ekagra.incidentguard.presentation.incident_list.IncidentListScreen
import com.ekagra.incidentguard.presentation.incident_list.IncidentListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoute.IncidentList,
        modifier = modifier
    ) {
        // 1. Incident List Screen
        composable<ScreenRoute.IncidentList> {
            val listViewModel: IncidentListViewModel = koinViewModel()
            IncidentListScreen(
                viewModel = listViewModel,
                onCreateIncidentClick = {
                    navController.navigate(ScreenRoute.CreateIncident)
                }
            )
        }

        // 2. Create Incident Screen
        composable<ScreenRoute.CreateIncident> {
            val createViewModel: CreateIncidentViewModel = koinViewModel()
            CreateIncidentScreen(
                viewModel = createViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onIncidentCreated = {
                    navController.popBackStack()
                }
            )
        }
    }
}