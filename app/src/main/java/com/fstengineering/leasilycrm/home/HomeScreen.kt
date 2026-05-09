package com.fstengineering.leasilycrm.home

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.dashboard.presentation.DashboardPage
import com.fstengineering.leasilycrm.home.HomeViewModel.UiEvent.BottomSheetError
import com.fstengineering.leasilycrm.home.presentation.composables.HomeBottomNavigationBar
import com.fstengineering.leasilycrm.home.presentation.composables.HomeTopAppBar
import com.fstengineering.leasilycrm.home.presentation.models.BottomNavBarDestination
import com.fstengineering.leasilycrm.leadTenants.presentation.leads.LeadsPage
import com.fstengineering.leasilycrm.leadTenants.presentation.tenants.TenantsPage
import com.fstengineering.leasilycrm.occupiedPeriods.presentation.OccupiedPeriodsPage
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    startedFromStayCheckInCheckOutNotification: Boolean,
    isStayRegistrationSuccess: Boolean,
    onSettingsSelect: () -> Unit,
    onProfileSelect: () -> Unit,
    onPropertiesSelect: () -> Unit,
    onPropertyOwnersSelect: () -> Unit,
    onRegisterLeadTenant: (Long?) -> Unit,
    onViewLeadTenantDetails: (Long) -> Unit,
    onNewAssistance: (Long) -> Unit,
    onNewStayWithLeadTenant: (Long) -> Unit,
    onRegisterStayBlank: (Long?, Boolean) -> Unit,
    onViewStay: (Long, cameFromLeadTenant: Boolean) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val navController = rememberNavController()

    val surfaceColor = MaterialTheme.colorScheme.surface
    val surfaceContainerColor = MaterialTheme.colorScheme.surfaceContainer

    val userProfilePictureUrl by viewModel.userProfilePictureUrl.collectAsStateWithLifecycle()
    val todayCheckInsAndCheckOutsCount by viewModel.todayCheckInsAndCheckoutsCount.collectAsStateWithLifecycle()

    var currentBottomNavDestination: BottomNavBarDestination by rememberSaveable(
        startedFromStayCheckInCheckOutNotification,
    ) {
        val startNavDestination =
            if (startedFromStayCheckInCheckOutNotification) BottomNavBarDestination.Occupation
            else BottomNavBarDestination.Dashboard

        mutableStateOf(startNavDestination)
    }

    var isSearching by remember { mutableStateOf(false) }
    var searchInput by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.init()

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is BottomSheetError -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }
            }
        }
    }

    LaunchedEffect(isStayRegistrationSuccess) {
        if (isStayRegistrationSuccess) {
            currentBottomNavDestination = BottomNavBarDestination.Occupation

            navController.navigate(BottomNavBarDestination.Occupation.navDestination) {
                launchSingleTop = true
                restoreState = true
                popUpTo(navController.graph.startDestinationId) { saveState = true }
            }
        }
    }

    @Suppress("DEPRECATION")
    SideEffect {
        activity.window.statusBarColor = surfaceColor.toArgb()
        activity.window.navigationBarColor = surfaceContainerColor.toArgb()
    }

    BackHandler(enabled = isSearching) {
        isSearching = false
        searchInput = null
    }

    Scaffold(
        topBar = {
            Column {
                HomeTopAppBar(
                    currentBottomNavDestination = currentBottomNavDestination,
                    userProfilePictureUrl = userProfilePictureUrl,
                    hasSearchButton = currentBottomNavDestination in listOf(
                        BottomNavBarDestination.Leads,
                        BottomNavBarDestination.Tenants,
                    ),
                    onBack = {
                        isSearching = false
                        searchInput = null
                    },
                    isSearching = isSearching,
                    onSearchSelect = { isSearching = true },
                    searchInput = searchInput,
                    onSearchInputChange = { searchInput = it },
                    onProfileSelect = onProfileSelect,
                    onSettingsSelect = onSettingsSelect,
                )
                HorizontalDivider()
            }
        },
        bottomBar = {
            val enterBottomNavBarAnim =
                fadeIn() + slideInVertically(initialOffsetY = { fullHeight -> fullHeight })
            val exitBottomNavBarAnim =
                slideOutVertically(targetOffsetY = { fullHeight -> fullHeight }) + fadeOut()

            AnimatedVisibility(
                visible = !isSearching,
                enter = enterBottomNavBarAnim,
                exit = exitBottomNavBarAnim,
            ) {
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
                    todayCheckInsAndCheckOutsCount = todayCheckInsAndCheckOutsCount,
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.Home(startedFromStayCheckInCheckOutNotification),
            modifier = Modifier
                .padding(innerPadding)
        ) {
            navigation<Destinations.Home>(
                startDestination = if (startedFromStayCheckInCheckOutNotification) {
                    Destinations.Home.OccupiedPeriods
                } else Destinations.Home.Dashboard,
            ) {
                composable<Destinations.Home.Dashboard> {
                    DashboardPage(
                        viewModel = koinViewModel(),
                        onPropertiesSelect = onPropertiesSelect,
                        onPropertyOwnersSelect = onPropertyOwnersSelect,
                    )
                }
                composable<Destinations.Home.Leads> {
                    LeadsPage(
                        viewModel = koinViewModel(),
                        isSearchingLead = isSearching,
                        searchInput = searchInput,
                        onRegisterLead = onRegisterLeadTenant,
                        onViewLeadDetails = onViewLeadTenantDetails,
                        onNewAssistance = onNewAssistance,
                        onNewStay = onNewStayWithLeadTenant,
                        onViewStay = { stayId -> onViewStay(stayId, true) },
                        onUiEvent = onUiEvent,
                    )
                }
                composable<Destinations.Home.Tenants> {
                    TenantsPage(
                        viewModel = koinViewModel(),
                        isSearchingTenant = isSearching,
                        searchInput = searchInput,
                        onRegisterTenant = onRegisterLeadTenant,
                        onViewTenantDetails = onViewLeadTenantDetails,
                        onNewAssistance = onNewAssistance,
                        onNewStay = onNewStayWithLeadTenant,
                        onViewStay = { stayId -> onViewStay(stayId, true) },
                        onUiEvent = onUiEvent,
                    )
                }
                composable<Destinations.Home.OccupiedPeriods> {
                    OccupiedPeriodsPage(
                        viewModel = koinViewModel(),
                        onRegisterOccupiedPeriod = { stayId, isUnavailablePeriod ->
                            onRegisterStayBlank(stayId, isUnavailablePeriod)
                        },
                        onViewOccupiedPeriodDetails = { stayId -> onViewStay(stayId, false) },
                        onUiEvent = onUiEvent,
                    )
                }
            }
        }
    }
}
