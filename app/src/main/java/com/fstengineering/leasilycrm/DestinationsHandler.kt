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
import com.fstengineering.leasilycrm.assistanceRegistration.AssistanceRegistrationStep1Screen
import com.fstengineering.leasilycrm.assistanceRegistration.AssistanceRegistrationStep2Screen
import com.fstengineering.leasilycrm.assistanceRegistration.AssistanceRegistrationStep3Screen
import com.fstengineering.leasilycrm.customerSegments.CustomerSegmentsScreen
import com.fstengineering.leasilycrm.home.HomeScreen
import com.fstengineering.leasilycrm.leadTenantDetails.LeadTenantDetailsScreen
import com.fstengineering.leasilycrm.leadTenantRegistration.LeadTenantRegistrationScreen
import com.fstengineering.leasilycrm.profile.ProfileScreen
import com.fstengineering.leasilycrm.properties.PropertiesScreen
import com.fstengineering.leasilycrm.propertyDashboard.PropertyDashboardScreen
import com.fstengineering.leasilycrm.propertyDetails.PropertyDetailsScreen
import com.fstengineering.leasilycrm.propertyOwnerDetails.PropertyOwnerDetailsScreen
import com.fstengineering.leasilycrm.propertyOwnerRegistration.PropertyOwnerRegistrationScreen
import com.fstengineering.leasilycrm.propertyOwners.PropertyOwnersScreen
import com.fstengineering.leasilycrm.propertyRegistration.PropertyRegistrationStep1Screen
import com.fstengineering.leasilycrm.propertyRegistration.PropertyRegistrationStep2Screen
import com.fstengineering.leasilycrm.propertyRegistration.PropertyRegistrationStep3Screen
import com.fstengineering.leasilycrm.propertyRegistration.PropertyRegistrationStep4Screen
import com.fstengineering.leasilycrm.root.RootScreen
import com.fstengineering.leasilycrm.signin.SignInScreen
import com.fstengineering.leasilycrm.stayDetails.StayDetailsScreen
import com.fstengineering.leasilycrm.stayRegistration.StayRegistrationScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

object DestinationsHandler {
    private const val STAY_REGISTRATION_SUCCESS_KEY = "stay_registration_success"

    fun NavGraphBuilder.destinations(
        navController: NavController,
        onSignInSuccess: () -> Unit,
        onSignOut: () -> Unit,
        showSnackbar: (String) -> Unit,
        showActionSnackbar: (String, String, () -> Unit) -> Unit,
    ) {
        composable<Destinations.Root> {
            RootScreen(
                viewModel = koinViewModel(),
                onNavigateToFirstDestination = { firstDestination ->
                    navController.navigate(firstDestination) {
                        val startDestination =
                            navController.graph.startDestinationRoute ?: return@navigate

                        popUpTo(startDestination) { inclusive = true }
                    }
                },
            )
        }

        composable<Destinations.SignIn> {
            SignInScreen(
                viewModel = koinViewModel(),
                credentialManager = koinInject(),
                onSignInSuccess = {
                    onSignInSuccess()

                    navController.navigate(Destinations.Home) {
                        val startDestination =
                            navController.graph.startDestinationRoute ?: return@navigate

                        popUpTo(startDestination) { inclusive = true }
                    }
                },
                showSnackbar = showSnackbar,
            )
        }

        composable<Destinations.Home> {
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
                isStayRegistrationSuccess = isStayRegistrationSuccess,
                onProfileSelect = {
                    navController.navigateAndPopAllTo(Destinations.Profile)
                },
                onPropertiesSelect = {
                    navController.navigate(Destinations.Properties)
                },
                onPropertyOwnersSelect = {
                    navController.navigate(Destinations.PropertyOwners)
                },
                onCustomerSegmentsSelect = {
                    navController.navigate(Destinations.CustomerSegments)
                },
                onRegisterLeadTenant = { leadTenantId ->
                    navController.navigate(Destinations.LeadTenantRegistration(leadTenantId))
                },
                onViewLeadTenantDetails = { leadTenantId ->
                    navController.navigate(Destinations.LeadTenantDetails(leadTenantId))
                },
                onRegisterAssistance = { leadTenantId, assistanceId ->
                    val destination = Destinations.AssistanceRegistration(
                        assistanceId = assistanceId,
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
                onSignOut = {
                    onSignOut()
                    navController.navigateAndPopAllTo(Destinations.SignIn)
                },
                showSnackbar = showSnackbar,
                showActionSnackbar = showActionSnackbar,
            )
        }

        composable<Destinations.Profile> {
            ProfileScreen(
                viewModel = koinViewModel(),
                credentialManager = koinInject(),
                onBack = { navController.navigateUp() },
                onSignOut = {
                    onSignOut()
                    navController.navigateAndPopAllTo(Destinations.SignIn)
                },
                showSnackbar = showSnackbar,
            )
        }

        composable<Destinations.CustomerSegments> {
            CustomerSegmentsScreen(
                viewModel = koinViewModel(),
                onBack = { navController.navigateUp() },
                onSignOut = {
                    onSignOut()
                    navController.navigateAndPopAllTo(Destinations.SignIn)
                },
                showSnackbar = showSnackbar,
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
                onSignOut = {
                    onSignOut()
                    navController.navigateAndPopAllTo(Destinations.SignIn)
                },
                showSnackbar = showSnackbar,
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
                    onSignOut = {
                        onSignOut()
                        navController.navigateAndPopAllTo(Destinations.SignIn)
                    },
                    showSnackbar = showSnackbar,
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
                    onSignOut = {
                        onSignOut()
                        navController.navigateAndPopAllTo(Destinations.SignIn)
                    },
                    showSnackbar = showSnackbar,
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
                    onSignOut = {
                        onSignOut()
                        navController.navigateAndPopAllTo(Destinations.SignIn)
                    },
                    showSnackbar = showSnackbar,
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
                    onSignOut = {
                        onSignOut()
                        navController.navigateAndPopAllTo(Destinations.SignIn)
                    },
                    showSnackbar = showSnackbar,
                )
            }
        }

        composable<Destinations.PropertyDashboard> { backStackEntry ->
            val args = backStackEntry.toRoute<Destinations.PropertyDashboard>()

            PropertyDashboardScreen(
                propertyId = args.propertyId,
                viewModel = koinViewModel(),
                onBack = { navController.navigateUp() },
                onSignOut = {
                    onSignOut()
                    navController.navigateAndPopAllTo(Destinations.SignIn)
                },
                onEditProperty = {
                    navController.navigate(Destinations.PropertyRegistration(args.propertyId))
                },
                onViewPropertyDetails = {
                    navController.navigate(Destinations.PropertyDetails(args.propertyId))
                },
                onViewPropertyOwnerDetails = { propertyOwnerId ->
                    navController.navigate(Destinations.PropertyOwnerDetails(propertyOwnerId))
                },
                onViewNextStays = {},
                onNewStay = { isUnavailablePeriod ->
                    val destination = Destinations.StayRegistration(
                        stayId = null,
                        propertyId = args.propertyId,
                        leadTenantId = null,
                        isUnavailablePeriod = isUnavailablePeriod,
                    )

                    navController.navigate(destination)
                },
                showSnackbar = showSnackbar,
            )
        }

        composable<Destinations.PropertyDetails> { backStackEntry ->
            val args = backStackEntry.toRoute<Destinations.PropertyDashboard>()

            PropertyDetailsScreen(
                propertyId = args.propertyId,
                viewModel = koinViewModel(),
                onBack = { navController.navigateUp() },
                onSignOut = {
                    onSignOut()
                    navController.navigateAndPopAllTo(Destinations.SignIn)
                },
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
                showSnackbar = showSnackbar,
            )
        }

        composable<Destinations.PropertyOwners> {
            PropertyOwnersScreen(
                viewModel = koinViewModel(),
                onBack = { navController.navigateUp() },
                onSignOut = {
                    onSignOut()
                    navController.navigateAndPopAllTo(Destinations.SignIn)
                },
                onRegisterPropertyOwner = { propertyOwnerId ->
                    navController.navigate(Destinations.PropertyOwnerRegistration(propertyOwnerId))
                },
                onViewPropertyOwnerDetails = { propertyOwnerId ->
                    navController.navigate(Destinations.PropertyOwnerDetails(propertyOwnerId))
                },
                showSnackbar = showSnackbar,
            )
        }

        composable<Destinations.PropertyOwnerRegistration> { backStackEntry ->
            val args = backStackEntry.toRoute<Destinations.PropertyOwnerRegistration>()

            PropertyOwnerRegistrationScreen(
                propertyOwnerId = args.propertyOwnerId,
                viewModel = koinViewModel(),
                onBack = { navController.navigateUp() },
                onSignOut = {
                    onSignOut()
                    navController.navigateAndPopAllTo(Destinations.SignIn)
                },
                showSnackbar = showSnackbar,
            )
        }

        composable<Destinations.PropertyOwnerDetails> { backStackEntry ->
            val args = backStackEntry.toRoute<Destinations.PropertyOwnerDetails>()

            PropertyOwnerDetailsScreen(
                propertyOwnerId = args.propertyOwnerId,
                viewModel = koinViewModel(),
                onBack = { navController.navigateUp() },
                onSignOut = {
                    onSignOut()
                    navController.navigateAndPopAllTo(Destinations.SignIn)
                },
                onEditPropertyOwner = { propertyOwnerId ->
                    navController.navigate(Destinations.PropertyOwnerRegistration(propertyOwnerId))
                },
                showSnackbar = showSnackbar,
            )
        }

        composable<Destinations.LeadTenantRegistration> { backStackEntry ->
            val args = backStackEntry.toRoute<Destinations.LeadTenantRegistration>()

            LeadTenantRegistrationScreen(
                leadTenantId = args.leadTenantId,
                viewModel = koinViewModel(),
                onBack = { navController.navigateUp() },
                onSignOut = {
                    onSignOut()
                    navController.navigateAndPopAllTo(Destinations.SignIn)
                },
                showSnackbar = showSnackbar,
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
                    navController.navigate(Destinations.PropertyDetails(propertyId))
                },
                onBack = { navController.navigateUp() },
                onSignOut = {
                    onSignOut()
                    navController.navigateAndPopAllTo(Destinations.SignIn)
                },
                showSnackbar = showSnackbar,
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
                    onSignOut = {
                        onSignOut()
                        navController.navigateAndPopAllTo(Destinations.SignIn)
                    },
                    showSnackbar = showSnackbar,
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
                    onSignOut = {
                        onSignOut()
                        navController.navigateAndPopAllTo(Destinations.SignIn)
                    },
                    showSnackbar = showSnackbar,
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
                    onSignOut = {
                        onSignOut()
                        navController.navigateAndPopAllTo(Destinations.SignIn)
                    },
                    showSnackbar = showSnackbar,
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
                onSignOut = {
                    onSignOut()
                    navController.navigateAndPopAllTo(Destinations.SignIn)
                },
                showSnackbar = showSnackbar,
            )
        }

        composable<Destinations.StayDetails> { backStackEntry ->
            val args = backStackEntry.toRoute<Destinations.StayDetails>()

            StayDetailsScreen(
                stayId = args.stayId,
                cameFromProperty = args.cameFromProperty,
                cameFromTenant = args.cameFromTenant,
                viewModel = koinViewModel(),
                onEdit = { stay ->
                    val destination = Destinations.StayRegistration(
                        stayId = stay.id,
                        propertyId = stay.property?.id,
                        leadTenantId = stay.tenant?.id,
                        isUnavailablePeriod = stay.isUnavailablePeriod,
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
                onSignOut = {
                    onSignOut()
                    navController.navigateAndPopAllTo(Destinations.SignIn)
                },
                showSnackbar = showSnackbar,
                showActionSnackbar = showActionSnackbar,
            )
        }
    }

    private fun NavController.navigateAndPopAllTo(
        destination: Any,
    ) {
        navigate(destination) {
            val startDestination = graph.startDestinationRoute ?: return@navigate
            popUpTo(startDestination) { inclusive = true }

            launchSingleTop = true
        }
    }
}
