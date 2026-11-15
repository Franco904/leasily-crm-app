package com.fstengineering.leasilycrm

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.AssistanceRegistrationStep1Screen
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.AssistanceRegistrationStep2Screen
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.AssistanceRegistrationStep3Screen
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.home.HomeScreen
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.LeadTenantDetailsScreen
import com.fstengineering.leasilycrm.leadTenantRegistration.presentation.LeadTenantRegistrationScreen
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.OccupiedPeriodDetailsScreen
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.OccupiedPeriodRegistrationScreen
import com.fstengineering.leasilycrm.profile.presentation.ProfileScreen
import com.fstengineering.leasilycrm.imoveis.presentation.ImoveisScreen
import com.fstengineering.leasilycrm.imovelDashboard.presentation.ImovelDashboardScreen
import com.fstengineering.leasilycrm.imovelDetails.presentation.ImovelDetailsScreen
import com.fstengineering.leasilycrm.imovelOccupation.presentation.ImovelOccupationScreen
import com.fstengineering.leasilycrm.proprietarioDetails.presentation.ProprietarioDetailsScreen
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.ProprietarioRegistrationScreen
import com.fstengineering.leasilycrm.proprietarios.presentation.ProprietariosScreen
import com.fstengineering.leasilycrm.imovelRegistration.presentation.ImovelRegistrationStep1Screen
import com.fstengineering.leasilycrm.imovelRegistration.presentation.ImovelRegistrationStep2Screen
import com.fstengineering.leasilycrm.imovelRegistration.presentation.ImovelRegistrationStep3Screen
import com.fstengineering.leasilycrm.imovelRegistration.presentation.ImovelRegistrationStep4Screen
import com.fstengineering.leasilycrm.root.RootScreen
import com.fstengineering.leasilycrm.settings.SettingsScreen
import com.fstengineering.leasilycrm.signin.presentation.SignInScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

private const val STAY_REGISTRATION_SUCCESS_KEY = "stay_registration_success"

fun NavGraphBuilder.destinations(
    navController: NavController,
    startedFromStayNotificationPopUp: Boolean,
    onSignIn: () -> Unit,
    onUiEvent: (UiEvent) -> Unit,
) {
    composable<Destinations.Root> {
        RootScreen(
            viewModel = koinViewModel(),
            startedFromStayNotificationPopUp = startedFromStayNotificationPopUp,
            onNavigateToFirstDestination = { firstDestination ->
                navController.navigate(firstDestination) {
                    val startDestination =
                        navController.graph.startDestinationRoute ?: return@navigate

                    popUpTo(startDestination) { inclusive = true }
                }
            },
        )
    }

    composable<Destinations.SignIn> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.SignIn>()

        SignInScreen(
            hasSessionExpired = args.hasSessionExpired,
            viewModel = koinViewModel(),
            credentialManager = koinInject(),
            onSignIn = {
                onSignIn()

                val destination = Destinations.Home(startedFromStayNotificationPopUp = false)
                navController.navigate(destination) {
                    val startDestination =
                        navController.graph.startDestinationRoute ?: return@navigate

                    popUpTo(startDestination) { inclusive = true }
                }
            },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.Home> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.Home>()
        val savedStateHandle = navController.currentBackStackEntry
            ?.savedStateHandle

        var isStayRegistrationSuccess by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            savedStateHandle
                ?.getLiveData<Boolean>(STAY_REGISTRATION_SUCCESS_KEY)
                ?.observeForever { success ->
                    isStayRegistrationSuccess = success
                    savedStateHandle.remove<String>(STAY_REGISTRATION_SUCCESS_KEY)
                }
        }

        HomeScreen(
            viewModel = koinViewModel(),
            startedFromStayCheckInCheckOutNotification = args.startedFromStayNotificationPopUp,
            isStayRegistrationSuccess = isStayRegistrationSuccess,
            onSettingsSelect = {
                navController.navigateAndPopAllTo(Destinations.Settings)
            },
            onProfileSelect = {
                navController.navigateAndPopAllTo(Destinations.Profile)
            },
            onImoveisSelect = {
                navController.navigate(Destinations.Imoveis)
            },
            onProprietariosSelect = {
                navController.navigate(Destinations.Proprietarios)
            },
            onRegisterLeadTenant = { leadTenantId ->
                navController.navigate(Destinations.LeadTenantRegistration(leadTenantId))
            },
            onViewLeadTenantDetails = { leadTenantId ->
                navController.navigate(Destinations.LeadTenantDetails(leadTenantId))
            },
            onNewAssistance = { leadTenantId ->
                val destination = Destinations.AssistanceRegistration(
                    assistanceId = null,
                    leadTenantId = leadTenantId,
                )
                navController.navigate(destination)
            },
            onNewStayWithLeadTenant = { leadTenantId ->
                val destination = Destinations.OccupiedPeriodRegistration(
                    occupiedPeriodId = null,
                    imovelId = null,
                    leadTenantId = leadTenantId,
                    isUnavailablePeriod = false,
                )

                navController.navigate(destination)
            },
            onRegisterStayBlank = { stayId, isUnavailablePeriod ->
                val destination = Destinations.OccupiedPeriodRegistration(
                    occupiedPeriodId = stayId,
                    imovelId = null,
                    leadTenantId = null,
                    isUnavailablePeriod = isUnavailablePeriod,
                )

                navController.navigate(destination)
            },
            onViewStay = { stayId, cameFromLeadTenant ->
                val destination = Destinations.OccupiedPeriodDetails(
                    occupiedPeriodId = stayId,
                    cameFromImovel = false,
                    cameFromTenant = cameFromLeadTenant,
                )

                navController.navigate(destination)
            },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.Settings> {
        SettingsScreen(
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.Profile> {
        ProfileScreen(
            viewModel = koinViewModel(),
            credentialManager = koinInject(),
            onBack = { navController.navigateUp() },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.Imoveis> {
        ImoveisScreen(
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onRegisterImovel = { imovelId ->
                navController.navigate(Destinations.ImovelRegistration(imovelId))
            },
            onViewImovelDashboard = { imovelId ->
                navController.navigate(Destinations.ImovelDashboard(imovelId))
            },
            onViewImovelDetails = { imovelId ->
                navController.navigate(Destinations.ImovelDetails(imovelId))
            },
            onNewStay = { imovelId, isUnavailablePeriod ->
                val destination = Destinations.OccupiedPeriodRegistration(
                    occupiedPeriodId = null,
                    imovelId = imovelId,
                    leadTenantId = null,
                    isUnavailablePeriod = isUnavailablePeriod,
                )

                navController.navigate(destination)
            },
            onUiEvent = onUiEvent,
        )
    }

    navigation<Destinations.ImovelRegistration>(
        startDestination = Destinations.ImovelRegistration.ImovelRegistrationStep1,
    ) {
        composable<Destinations.ImovelRegistration.ImovelRegistrationStep1> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Destinations.ImovelRegistration>()
            }

            val args = parentEntry.toRoute<Destinations.ImovelRegistration>()

            ImovelRegistrationStep1Screen(
                imovelId = args.imovelId,
                parentViewModel = koinViewModel(viewModelStoreOwner = parentEntry),
                stepViewModel = koinViewModel(),
                onRegisterNewProprietario = {
                    navController.navigate(Destinations.ProprietarioRegistration(null))
                },
                onLeaveRegistration = {
                    navController
                        .popBackStack<Destinations.ImovelRegistration>(inclusive = true)
                },
                onNext = {
                    val nextRoute = Destinations.ImovelRegistration.ImovelRegistrationStep2
                    navController.navigate(route = nextRoute)
                },
                onUiEvent = onUiEvent,
            )
        }

        composable<Destinations.ImovelRegistration.ImovelRegistrationStep2> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Destinations.ImovelRegistration>()
            }

            ImovelRegistrationStep2Screen(
                parentViewModel = koinViewModel(viewModelStoreOwner = parentEntry),
                stepViewModel = koinViewModel(),
                onLeaveRegistration = {
                    navController
                        .popBackStack<Destinations.ImovelRegistration>(inclusive = true)
                },
                onBack = { navController.navigateUp() },
                onNext = {
                    val nextRoute = Destinations.ImovelRegistration.ImovelRegistrationStep3
                    navController.navigate(route = nextRoute)
                },
                onUiEvent = onUiEvent,
            )
        }

        composable<Destinations.ImovelRegistration.ImovelRegistrationStep3> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Destinations.ImovelRegistration>()
            }

            ImovelRegistrationStep3Screen(
                parentViewModel = koinViewModel(viewModelStoreOwner = parentEntry),
                stepViewModel = koinViewModel(),
                onLeaveRegistration = {
                    navController
                        .popBackStack<Destinations.ImovelRegistration>(inclusive = true)
                },
                onBack = { navController.navigateUp() },
                onNext = {
                    val nextRoute = Destinations.ImovelRegistration.ImovelRegistrationStep4
                    navController.navigate(route = nextRoute)
                },
                onUiEvent = onUiEvent,
            )
        }

        composable<Destinations.ImovelRegistration.ImovelRegistrationStep4> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Destinations.ImovelRegistration>()
            }

            ImovelRegistrationStep4Screen(
                parentViewModel = koinViewModel(viewModelStoreOwner = parentEntry),
                onLeaveRegistration = {
                    navController
                        .popBackStack<Destinations.ImovelRegistration>(inclusive = true)
                },
                onBack = { navController.navigateUp() },
                onUiEvent = onUiEvent,
            )
        }
    }

    composable<Destinations.ImovelDashboard> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.ImovelDashboard>()

        ImovelDashboardScreen(
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onEditImovel = {
                navController.navigate(Destinations.ImovelRegistration(args.imovelId))
            },
            onViewImovelDetails = {
                navController.navigate(Destinations.ImovelDetails(args.imovelId))
            },
            onViewProprietarioDetails = { proprietarioId ->
                navController.navigate(Destinations.ProprietarioDetails(proprietarioId))
            },
            onViewAllImovelStays = {
                navController.navigate(Destinations.ImovelOccupation(args.imovelId))
            },
            onNewStay = { isUnavailablePeriod ->
                val destination = Destinations.OccupiedPeriodRegistration(
                    occupiedPeriodId = null,
                    imovelId = args.imovelId,
                    leadTenantId = null,
                    isUnavailablePeriod = isUnavailablePeriod,
                )

                navController.navigate(destination)
            },
            onViewStay = { stayId ->
                val destination = Destinations.OccupiedPeriodDetails(
                    occupiedPeriodId = stayId,
                    cameFromImovel = true,
                    cameFromTenant = false,
                )

                navController.navigate(destination)
            },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.ImovelDetails> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.ImovelDetails>()

        ImovelDetailsScreen(
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onEditImovel = {
                navController.navigate(
                    Destinations.ImovelRegistration(args.imovelId),
                )
            },
            onNewStay = { isUnavailablePeriod ->
                val destination = Destinations.OccupiedPeriodRegistration(
                    occupiedPeriodId = null,
                    imovelId = args.imovelId,
                    leadTenantId = null,
                    isUnavailablePeriod = isUnavailablePeriod,
                )

                navController.navigate(destination)
            },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.ImovelOccupation> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.ImovelOccupation>()

        ImovelOccupationScreen(
            imovelId = args.imovelId,
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onNewStay = { isUnavailablePeriod ->
                val destination = Destinations.OccupiedPeriodRegistration(
                    occupiedPeriodId = null,
                    imovelId = args.imovelId,
                    leadTenantId = null,
                    isUnavailablePeriod = isUnavailablePeriod,
                )

                navController.navigate(destination)
            },
            onViewStay = { stayId ->
                val destination = Destinations.OccupiedPeriodDetails(
                    occupiedPeriodId = stayId,
                    cameFromImovel = true,
                    cameFromTenant = false,
                )

                navController.navigate(destination)
            },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.Proprietarios> {
        ProprietariosScreen(
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onRegisterProprietario = { proprietarioId ->
                navController.navigate(Destinations.ProprietarioRegistration(proprietarioId))
            },
            onViewProprietarioDetails = { proprietarioId ->
                navController.navigate(Destinations.ProprietarioDetails(proprietarioId))
            },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.ProprietarioRegistration> {
        ProprietarioRegistrationScreen(
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.ProprietarioDetails> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.ProprietarioDetails>()

        ProprietarioDetailsScreen(
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onEditProprietario = {
                navController.navigate(Destinations.ProprietarioRegistration(args.proprietarioId))
            },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.LeadTenantRegistration> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.LeadTenantRegistration>()

        LeadTenantRegistrationScreen(
            leadTenantId = args.leadTenantId,
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.LeadTenantDetails> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.LeadTenantDetails>()

        LeadTenantDetailsScreen(
            leadTenantId = args.leadTenantId,
            viewModel = koinViewModel(),
            onEditLeadTenant = {
                navController.navigate(
                    Destinations.LeadTenantRegistration(leadTenantId = args.leadTenantId),
                )
            },
            onRegisterAssistance = { assistanceId ->
                val destination = Destinations.AssistanceRegistration(
                    assistanceId = assistanceId,
                    leadTenantId = args.leadTenantId,
                )
                navController.navigate(destination)
            },
            onNewStay = {
                val destination = Destinations.OccupiedPeriodRegistration(
                    occupiedPeriodId = null,
                    imovelId = null,
                    leadTenantId = args.leadTenantId,
                    isUnavailablePeriod = false,
                )

                navController.navigate(destination)
            },
            onViewStay = { stayId ->
                val destination = Destinations.OccupiedPeriodDetails(
                    occupiedPeriodId = stayId,
                    cameFromImovel = false,
                    cameFromTenant = true,
                )

                navController.navigate(destination)
            },
            onViewSentImovelDetails = { imovelId ->
                navController.navigate(Destinations.ImovelDashboard(imovelId))
            },
            onBack = { navController.navigateUp() },
            onUiEvent = onUiEvent,
        )
    }

    navigation<Destinations.AssistanceRegistration>(
        startDestination = Destinations.AssistanceRegistration.AssistanceRegistrationStep1,
    ) {
        composable<Destinations.AssistanceRegistration.AssistanceRegistrationStep1> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Destinations.AssistanceRegistration>()
            }

            val args = parentEntry.toRoute<Destinations.AssistanceRegistration>()

            AssistanceRegistrationStep1Screen(
                assistanceId = args.assistanceId,
                leadTenantId = args.leadTenantId,
                parentViewModel = koinViewModel(viewModelStoreOwner = parentEntry),
                stepViewModel = koinViewModel(),
                onLeaveRegistration = {
                    navController
                        .popBackStack<Destinations.AssistanceRegistration>(inclusive = true)
                },
                onNext = {
                    val nextRoute =
                        Destinations.AssistanceRegistration.AssistanceRegistrationStep2
                    navController.navigate(route = nextRoute)
                },
                onUiEvent = onUiEvent,
            )
        }

        composable<Destinations.AssistanceRegistration.AssistanceRegistrationStep2> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Destinations.AssistanceRegistration>()
            }

            AssistanceRegistrationStep2Screen(
                parentViewModel = koinViewModel(viewModelStoreOwner = parentEntry),
                stepViewModel = koinViewModel(),
                onLeaveRegistration = {
                    navController
                        .popBackStack<Destinations.AssistanceRegistration>(inclusive = true)
                },
                onBack = { navController.navigateUp() },
                onNext = {
                    val nextRoute =
                        Destinations.AssistanceRegistration.AssistanceRegistrationStep3
                    navController.navigate(route = nextRoute)
                },
                onUiEvent = onUiEvent,
            )
        }

        composable<Destinations.AssistanceRegistration.AssistanceRegistrationStep3> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Destinations.AssistanceRegistration>()
            }

            AssistanceRegistrationStep3Screen(
                parentViewModel = koinViewModel(viewModelStoreOwner = parentEntry),
                stepViewModel = koinViewModel(),
                onLeaveRegistration = {
                    navController
                        .popBackStack<Destinations.AssistanceRegistration>(inclusive = true)
                },
                onBack = { navController.navigateUp() },
                onUiEvent = onUiEvent,
            )
        }
    }

    composable<Destinations.OccupiedPeriodRegistration> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.OccupiedPeriodRegistration>()
        val previousBackStackEntry = navController.previousBackStackEntry

        OccupiedPeriodRegistrationScreen(
            stayId = args.occupiedPeriodId,
            imovelId = args.imovelId,
            tenantId = args.leadTenantId,
            isUnavailablePeriod = args.isUnavailablePeriod,
            viewModel = koinViewModel(),
            onBack = { success ->
                previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(STAY_REGISTRATION_SUCCESS_KEY, success)

                navController.navigateUp()
            },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.OccupiedPeriodDetails> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.OccupiedPeriodDetails>()

        OccupiedPeriodDetailsScreen(
            occupiedPeriodId = args.occupiedPeriodId,
            cameFromImovel = args.cameFromImovel,
            cameFromTenant = args.cameFromTenant,
            viewModel = koinViewModel(),
            onEdit = { stayId, imovelId, tenantId, isUnavailablePeriod ->
                val destination = Destinations.OccupiedPeriodRegistration(
                    occupiedPeriodId = stayId,
                    imovelId = imovelId,
                    leadTenantId = tenantId,
                    isUnavailablePeriod = isUnavailablePeriod,
                )

                navController.navigate(destination)
            },
            onViewImovelDashboard = { imovelId ->
                navController.navigate(Destinations.ImovelDashboard(imovelId))
            },
            onViewTenantDetails = { tenantId ->
                navController.navigate(Destinations.LeadTenantDetails(tenantId))
            },
            onBack = { navController.navigateUp() },
            onUiEvent = onUiEvent,
        )
    }
}

private fun NavController.navigateAndPopAllTo(destination: Any) {
    navigate(destination) {
        val startDestination = graph.startDestinationRoute ?: return@navigate
        popUpTo(startDestination) { inclusive = true }

        launchSingleTop = true
    }
}
