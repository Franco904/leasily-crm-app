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
import com.fstengineering.leasilycrm.leadTenantDetails.LeadTenantDetailsScreen
import com.fstengineering.leasilycrm.leadTenantRegistration.presentation.LeadTenantRegistrationScreen
import com.fstengineering.leasilycrm.profile.presentation.ProfileScreen
import com.fstengineering.leasilycrm.properties.PropertiesScreen
import com.fstengineering.leasilycrm.propertyDashboard.presentation.PropertyDashboardScreen
import com.fstengineering.leasilycrm.propertyDetails.presentation.PropertyDetailsScreen
import com.fstengineering.leasilycrm.propertyOwnerDetails.presentation.PropertyOwnerDetailsScreen
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.PropertyOwnerRegistrationScreen
import com.fstengineering.leasilycrm.propertyOwners.presentation.PropertyOwnersScreen
import com.fstengineering.leasilycrm.propertyRegistration.presentation.PropertyRegistrationStep1Screen
import com.fstengineering.leasilycrm.propertyRegistration.presentation.PropertyRegistrationStep2Screen
import com.fstengineering.leasilycrm.propertyRegistration.presentation.PropertyRegistrationStep3Screen
import com.fstengineering.leasilycrm.propertyRegistration.presentation.PropertyRegistrationStep4Screen
import com.fstengineering.leasilycrm.propertyStays.presentation.PropertyStaysScreen
import com.fstengineering.leasilycrm.root.RootScreen
import com.fstengineering.leasilycrm.settings.SettingsScreen
import com.fstengineering.leasilycrm.signin.presentation.SignInScreen
import com.fstengineering.leasilycrm.stayDetails.StayDetailsScreen
import com.fstengineering.leasilycrm.stayRegistration.presentation.StayRegistrationScreen
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
            startedFromStayNotificationPopUp = args.startedFromStayNotificationPopUp,
            isStayRegistrationSuccess = isStayRegistrationSuccess,
            onSettingsSelect = {
                navController.navigateAndPopAllTo(Destinations.Settings)
            },
            onProfileSelect = {
                navController.navigateAndPopAllTo(Destinations.Profile)
            },
            onPropertiesSelect = {
                navController.navigate(Destinations.Properties)
            },
            onPropertyOwnersSelect = {
                navController.navigate(Destinations.PropertyOwners)
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
                val destination = Destinations.StayRegistration(
                    stayId = null,
                    propertyId = null,
                    leadTenantId = leadTenantId,
                    isUnavailablePeriod = false,
                )

                navController.navigate(destination)
            },
            onRegisterStayBlank = { stayId, isUnavailablePeriod ->
                val destination = Destinations.StayRegistration(
                    stayId = stayId,
                    propertyId = null,
                    leadTenantId = null,
                    isUnavailablePeriod = isUnavailablePeriod,
                )

                navController.navigate(destination)
            },
            onViewStay = { stayId, cameFromLeadTenant ->
                val destination = Destinations.StayDetails(
                    stayId = stayId,
                    cameFromProperty = false,
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

    composable<Destinations.Properties> {
        PropertiesScreen(
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onRegisterProperty = { propertyId ->
                navController.navigate(Destinations.PropertyRegistration(propertyId))
            },
            onViewPropertyDashboard = { propertyId ->
                navController.navigate(Destinations.PropertyDashboard(propertyId))
            },
            onViewPropertyDetails = { propertyId ->
                navController.navigate(Destinations.PropertyDetails(propertyId))
            },
            onNewStay = { propertyId, isUnavailablePeriod ->
                val destination = Destinations.StayRegistration(
                    stayId = null,
                    propertyId = propertyId,
                    leadTenantId = null,
                    isUnavailablePeriod = isUnavailablePeriod,
                )

                navController.navigate(destination)
            },
            onUiEvent = onUiEvent,
        )
    }

    navigation<Destinations.PropertyRegistration>(
        startDestination = Destinations.PropertyRegistration.PropertyRegistrationStep1,
    ) {
        composable<Destinations.PropertyRegistration.PropertyRegistrationStep1> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Destinations.PropertyRegistration>()
            }

            val args = parentEntry.toRoute<Destinations.PropertyRegistration>()

            PropertyRegistrationStep1Screen(
                propertyId = args.propertyId,
                parentViewModel = koinViewModel(viewModelStoreOwner = parentEntry),
                stepViewModel = koinViewModel(),
                onRegisterNewPropertyOwner = {
                    navController.navigate(Destinations.PropertyOwnerRegistration(null))
                },
                onLeaveRegistration = {
                    navController
                        .popBackStack<Destinations.PropertyRegistration>(inclusive = true)
                },
                onNext = {
                    val nextRoute = Destinations.PropertyRegistration.PropertyRegistrationStep2
                    navController.navigate(route = nextRoute)
                },
                onUiEvent = onUiEvent,
            )
        }

        composable<Destinations.PropertyRegistration.PropertyRegistrationStep2> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Destinations.PropertyRegistration>()
            }

            PropertyRegistrationStep2Screen(
                parentViewModel = koinViewModel(viewModelStoreOwner = parentEntry),
                stepViewModel = koinViewModel(),
                onLeaveRegistration = {
                    navController
                        .popBackStack<Destinations.PropertyRegistration>(inclusive = true)
                },
                onBack = { navController.navigateUp() },
                onNext = {
                    val nextRoute = Destinations.PropertyRegistration.PropertyRegistrationStep3
                    navController.navigate(route = nextRoute)
                },
                onUiEvent = onUiEvent,
            )
        }

        composable<Destinations.PropertyRegistration.PropertyRegistrationStep3> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Destinations.PropertyRegistration>()
            }

            PropertyRegistrationStep3Screen(
                parentViewModel = koinViewModel(viewModelStoreOwner = parentEntry),
                stepViewModel = koinViewModel(),
                onLeaveRegistration = {
                    navController
                        .popBackStack<Destinations.PropertyRegistration>(inclusive = true)
                },
                onBack = { navController.navigateUp() },
                onNext = {
                    val nextRoute = Destinations.PropertyRegistration.PropertyRegistrationStep4
                    navController.navigate(route = nextRoute)
                },
                onUiEvent = onUiEvent,
            )
        }

        composable<Destinations.PropertyRegistration.PropertyRegistrationStep4> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Destinations.PropertyRegistration>()
            }

            PropertyRegistrationStep4Screen(
                parentViewModel = koinViewModel(viewModelStoreOwner = parentEntry),
                onLeaveRegistration = {
                    navController
                        .popBackStack<Destinations.PropertyRegistration>(inclusive = true)
                },
                onBack = { navController.navigateUp() },
                onUiEvent = onUiEvent,
            )
        }
    }

    composable<Destinations.PropertyDashboard> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.PropertyDashboard>()

        PropertyDashboardScreen(
            propertyId = args.propertyId,
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onEditProperty = {
                navController.navigate(Destinations.PropertyRegistration(args.propertyId))
            },
            onViewPropertyDetails = {
                navController.navigate(Destinations.PropertyDetails(args.propertyId))
            },
            onViewPropertyOwnerDetails = { propertyOwnerId ->
                navController.navigate(Destinations.PropertyOwnerDetails(propertyOwnerId))
            },
            onViewAllPropertyStays = {
                navController.navigate(Destinations.PropertyStays(args.propertyId))
            },
            onNewStay = { isUnavailablePeriod ->
                val destination = Destinations.StayRegistration(
                    stayId = null,
                    propertyId = args.propertyId,
                    leadTenantId = null,
                    isUnavailablePeriod = isUnavailablePeriod,
                )

                navController.navigate(destination)
            },
            onViewStay = { stayId ->
                val destination = Destinations.StayDetails(
                    stayId = stayId,
                    cameFromProperty = true,
                    cameFromTenant = false,
                )

                navController.navigate(destination)
            },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.PropertyDetails> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.PropertyDashboard>()

        PropertyDetailsScreen(
            propertyId = args.propertyId,
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onEditProperty = {
                navController.navigate(
                    Destinations.PropertyRegistration(args.propertyId),
                )
            },
            onNewStay = { isUnavailablePeriod ->
                val destination = Destinations.StayRegistration(
                    stayId = null,
                    propertyId = args.propertyId,
                    leadTenantId = null,
                    isUnavailablePeriod = isUnavailablePeriod,
                )

                navController.navigate(destination)
            },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.PropertyStays> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.PropertyStays>()

        PropertyStaysScreen(
            propertyId = args.propertyId,
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onNewStay = { isUnavailablePeriod ->
                val destination = Destinations.StayRegistration(
                    stayId = null,
                    propertyId = args.propertyId,
                    leadTenantId = null,
                    isUnavailablePeriod = isUnavailablePeriod,
                )

                navController.navigate(destination)
            },
            onViewStay = { stayId ->
                val destination = Destinations.StayDetails(
                    stayId = stayId,
                    cameFromProperty = true,
                    cameFromTenant = false,
                )

                navController.navigate(destination)
            },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.PropertyOwners> {
        PropertyOwnersScreen(
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onRegisterPropertyOwner = { propertyOwnerId ->
                navController.navigate(Destinations.PropertyOwnerRegistration(propertyOwnerId))
            },
            onViewPropertyOwnerDetails = { propertyOwnerId ->
                navController.navigate(Destinations.PropertyOwnerDetails(propertyOwnerId))
            },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.PropertyOwnerRegistration> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.PropertyOwnerRegistration>()

        PropertyOwnerRegistrationScreen(
            propertyOwnerId = args.propertyOwnerId,
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onUiEvent = onUiEvent,
        )
    }

    composable<Destinations.PropertyOwnerDetails> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.PropertyOwnerDetails>()

        PropertyOwnerDetailsScreen(
            propertyOwnerId = args.propertyOwnerId,
            viewModel = koinViewModel(),
            onBack = { navController.navigateUp() },
            onEditPropertyOwner = { propertyOwnerId ->
                navController.navigate(Destinations.PropertyOwnerRegistration(propertyOwnerId))
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
                val destination = Destinations.StayRegistration(
                    stayId = null,
                    propertyId = null,
                    leadTenantId = args.leadTenantId,
                    isUnavailablePeriod = false,
                )

                navController.navigate(destination)
            },
            onViewStay = { stayId ->
                val destination = Destinations.StayDetails(
                    stayId = stayId,
                    cameFromProperty = false,
                    cameFromTenant = true,
                )

                navController.navigate(destination)
            },
            onViewSentPropertyDetails = { propertyId ->
                navController.navigate(Destinations.PropertyDashboard(propertyId))
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

    composable<Destinations.StayRegistration> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.StayRegistration>()
        val previousBackStackEntry = navController.previousBackStackEntry

        StayRegistrationScreen(
            stayId = args.stayId,
            propertyId = args.propertyId,
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

    composable<Destinations.StayDetails> { backStackEntry ->
        val args = backStackEntry.toRoute<Destinations.StayDetails>()

        StayDetailsScreen(
            stayId = args.stayId,
            cameFromProperty = args.cameFromProperty,
            cameFromTenant = args.cameFromTenant,
            viewModel = koinViewModel(),
            onEdit = { stayId, propertyId, tenantId, isUnavailablePeriod ->
                val destination = Destinations.StayRegistration(
                    stayId = stayId,
                    propertyId = propertyId,
                    leadTenantId = tenantId,
                    isUnavailablePeriod = isUnavailablePeriod,
                )

                navController.navigate(destination)
            },
            onViewPropertyDashboard = { propertyId ->
                navController.navigate(Destinations.PropertyDashboard(propertyId))
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
