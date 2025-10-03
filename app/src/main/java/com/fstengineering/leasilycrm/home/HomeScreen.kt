package com.fstengineering.leasilycrm.home

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeleteAssistanceDialog
import com.fstengineering.leasilycrm.home.HomeViewModel.UiEvents.CurrentAssistanceDeleteSuccess
import com.fstengineering.leasilycrm.home.HomeViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.home.HomeViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.home.composables.HomeBottomNavigationBar
import com.fstengineering.leasilycrm.home.composables.HomeTopAppBar
import com.fstengineering.leasilycrm.home.dashboard.DashboardPage
import com.fstengineering.leasilycrm.home.leads.LeadsPage
import com.fstengineering.leasilycrm.home.models.BottomNavBarDestination
import com.fstengineering.leasilycrm.home.stays.StaysScreen
import com.fstengineering.leasilycrm.home.tenants.TenantsPage
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    onProfileSelect: () -> Unit = {},
    onPropertiesSelect: () -> Unit = {},
    onPropertyOwnersSelect: () -> Unit = {},
    onCustomerSegmentsSelect: () -> Unit = {},
    onRegisterLeadTenant: (Long?) -> Unit,
    onViewLeadTenantDetails: (Long) -> Unit,
    onRegisterAssistance: (Long, Long?) -> Unit,
    onNewStayWithLeadTenant: (Long) -> Unit,
    onRegisterStayBlank: (Long?, Boolean) -> Unit,
    onViewStay: (Long) -> Unit,
    onSignOut: () -> Unit = {},
    showSnackbar: (String) -> Unit = { _ -> },
    showActionSnackbar: (String, String, () -> Unit) -> Unit = { _, _, _ -> },
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val navController = rememberNavController()

    val surfaceColor = MaterialTheme.colorScheme.surface
    val surfaceContainerColor = MaterialTheme.colorScheme.surfaceContainer

    val userProfilePictureUrl by viewModel.userProfilePictureUrl.collectAsStateWithLifecycle()

    var currentBottomNavDestination: BottomNavBarDestination by rememberSaveable {
        mutableStateOf(BottomNavBarDestination.Dashboard)
    }

    var isSearching by remember { mutableStateOf(false) }
    var searchInput by remember { mutableStateOf<String?>(null) }

    var assistanceToDelete by rememberSaveable { mutableStateOf<Assistance?>(null) }

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

                is CurrentAssistanceDeleteSuccess -> {
                    showSnackbar(context.getString(R.string.snackbar_delete_assistance_succeeded))
                }
            }
        }
    }

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
            )
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
                    upcomingStaysCount = 0, // TODO: Get real value from view model when implementing Stays use cases
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
                        isSearchingLead = isSearching,
                        searchInput = searchInput,
                        onRegisterLead = onRegisterLeadTenant,
                        onViewLeadDetails = onViewLeadTenantDetails,
                        onRegisterAssistance = onRegisterAssistance,
                        onDeleteAssistance = { assistanceToDelete = it },
                        onNewStay = onNewStayWithLeadTenant,
                        onViewStay = onViewStay,
                        onSignOut = onSignOut,
                        showSnackbar = showSnackbar,
                    )
                }
                composable<Destinations.Home.Tenants> {
                    TenantsPage(
                        viewModel = koinViewModel(),
                        isSearchingTenant = isSearching,
                        searchInput = searchInput,
                        onRegisterTenant = onRegisterLeadTenant,
                        onViewTenantDetails = onViewLeadTenantDetails,
                        onRegisterAssistance = onRegisterAssistance,
                        onDeleteAssistance = { assistanceToDelete = it },
                        onNewStay = onNewStayWithLeadTenant,
                        onViewStay = onViewStay,
                        onSignOut = onSignOut,
                        showSnackbar = showSnackbar,
                    )
                }
                composable<Destinations.Home.Stays> {
                    StaysScreen(
                        viewModel = koinViewModel(),
                        onRegisterStayBlank = { stayId, isStayFromOtherRealtor ->
                            onRegisterStayBlank(stayId, isStayFromOtherRealtor)
                        },
                        onViewStayDetails = { stayId -> onViewStay(stayId) },
                        onSignOut = onSignOut,
                        showSnackbar = showSnackbar,
                        showActionSnackbar = showActionSnackbar,
                    )
                }
            }
        }
        if (assistanceToDelete != null) {
            ConfirmDeleteAssistanceDialog(
                onConfirmDelete = {
                    viewModel.deleteCurrentAssistance(
                        currentAssistance = assistanceToDelete!!,
                        leadTenant = assistanceToDelete?.tenant!!,
                    )

                    assistanceToDelete = null
                },
                onCancel = { assistanceToDelete = null },
            )
        }
    }
}
