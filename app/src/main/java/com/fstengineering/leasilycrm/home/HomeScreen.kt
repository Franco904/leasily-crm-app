package com.fstengineering.leasilycrm.home

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.home.HomeViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.home.HomeViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.home.composables.HomeBottomNavigationBar
import com.fstengineering.leasilycrm.home.composables.HomeTopBar
import com.fstengineering.leasilycrm.home.dashboard.DashboardPage
import com.fstengineering.leasilycrm.home.leads.LeadsPage
import com.fstengineering.leasilycrm.home.models.BottomNavBarDestination
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    onProfileSelect: () -> Unit = {},
    onPropertiesSelect: () -> Unit = {},
    onPropertyOwnersSelect: () -> Unit = {},
    onCustomerSegmentsSelect: () -> Unit = {},
    onRegisterLead: (Long?) -> Unit,
    onSignOut: () -> Unit = {},
    showSnackbar: (String) -> Unit = { _ -> },
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!

    val navController = rememberNavController()

    val surfaceColor = MaterialTheme.colorScheme.surface
    val surfaceContainerColor = MaterialTheme.colorScheme.surfaceContainer

    val userProfilePictureUrl by viewModel.userProfilePictureUrl.collectAsStateWithLifecycle()

    var currentBottomNavDestination: BottomNavBarDestination by rememberSaveable {
        mutableStateOf(BottomNavBarDestination.Dashboard)
    }

    LaunchedEffect(Unit) {
        viewModel.init()

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is DataSourceError -> {
                    showSnackbar(context.getString(uiEvent.messageId))
                }

                is RedirectToSignIn -> {
                    onSignOut()
                }
            }
        }
    }

    SideEffect {
        activity.window.statusBarColor = surfaceColor.toArgb()
        activity.window.navigationBarColor = surfaceContainerColor.toArgb()
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                currentBottomNavDestination = currentBottomNavDestination,
                userProfilePictureUrl = userProfilePictureUrl,
                hasSearchButton = currentBottomNavDestination in listOf(
                    BottomNavBarDestination.Leads,
                    BottomNavBarDestination.Tenants,
                ),
                onSearchSelect = {}, // TODO: Implement search leads and tenants use cases
                onProfileSelect = onProfileSelect,
            )
        },
        bottomBar = {
            HomeBottomNavigationBar(
                currentDestination = currentBottomNavDestination,
                onDestinationSelect = { destination ->
                    currentBottomNavDestination = destination

                    navController.navigate(destination.navDestination) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                    }
                },
                upcomingStaysCount = 2, // TODO: Get real value from view model when implementing Stays use cases
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.Home,
            modifier = Modifier
                .padding(innerPadding)
        ) {
            navigation<Destinations.Home>(startDestination = Destinations.Home.Dashboard) {
                composable<Destinations.Home.Dashboard> {
                    DashboardPage(
                        onPropertiesSelect = onPropertiesSelect,
                        onPropertyOwnersSelect = onPropertyOwnersSelect,
                        onCustomerSegmentsSelect = onCustomerSegmentsSelect,
                    )
                }
                composable<Destinations.Home.Leads> {
                    LeadsPage(
                        viewModel = koinViewModel(),
                        onRegisterLead = onRegisterLead
                    )
                }
                composable<Destinations.Home.Tenants> {
                    Box(modifier = Modifier)
                }
                composable<Destinations.Home.Stays> {
                    Box(modifier = Modifier)
                }
            }
        }
    }
}
