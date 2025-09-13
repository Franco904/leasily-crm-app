package com.fstengineering.leasilycrm

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.fstengineering.leasilycrm.customerSegments.CustomerSegmentsScreen
import com.fstengineering.leasilycrm.home.HomeScreen
import com.fstengineering.leasilycrm.profile.ProfileScreen
import com.fstengineering.leasilycrm.properties.PropertiesScreen
import com.fstengineering.leasilycrm.propertyDashboard.PropertyDashboardScreen
import com.fstengineering.leasilycrm.propertyOwnerDetails.PropertyOwnerDetailsScreen
import com.fstengineering.leasilycrm.propertyOwnerRegistration.PropertyOwnerRegistrationScreen
import com.fstengineering.leasilycrm.propertyOwners.PropertyOwnersScreen
import com.fstengineering.leasilycrm.propertyRegistration.PropertyRegistrationStep1Screen
import com.fstengineering.leasilycrm.propertyRegistration.PropertyRegistrationStep2Screen
import com.fstengineering.leasilycrm.propertyRegistration.PropertyRegistrationStep3Screen
import com.fstengineering.leasilycrm.propertyRegistration.PropertyRegistrationStep4Screen
import com.fstengineering.leasilycrm.root.RootScreen
import com.fstengineering.leasilycrm.signin.SignInScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

object DestinationsHandler {
    fun NavGraphBuilder.destinations(
        navController: NavController,
        onSignInSuccess: () -> Unit,
        onSignOut: () -> Unit,
        showSnackbar: (String) -> Unit,
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
                showSnackbar = { message -> showSnackbar(message) },
            )
        }

        composable<Destinations.Home> {
            HomeScreen(
                viewModel = koinViewModel(),
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
                onSignOut = {
                    onSignOut()
                    navController.navigateAndPopAllTo(Destinations.SignIn)
                },
                showSnackbar = { message -> showSnackbar(message) },
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
                showSnackbar = { message -> showSnackbar(message) },
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
                showSnackbar = { message -> showSnackbar(message) },
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
                onViewPropertyDetails = { propertyId -> },
                onSignOut = {
                    onSignOut()
                    navController.navigateAndPopAllTo(Destinations.SignIn)
                },
                showSnackbar = { message -> showSnackbar(message) },
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
                    showSnackbar = { message -> showSnackbar(message) },
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
                    showSnackbar = { message -> showSnackbar(message) },
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
                    showSnackbar = { message -> showSnackbar(message) },
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
                    showSnackbar = { message -> showSnackbar(message) },
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
                onEditProperty = { propertyId ->
                    navController.navigate(Destinations.PropertyRegistration(propertyId))
                },
                onViewPropertyDetails = {},
                onViewPropertyOwnerDetails = { propertyOwnerId ->
                    navController.navigate(Destinations.PropertyOwnerDetails(propertyOwnerId))
                },
                onSeeNextPropertyStays = {},
                onNewPropertyStay = {},
                showSnackbar = { message -> showSnackbar(message) },
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
                showSnackbar = { message -> showSnackbar(message) },
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
                showSnackbar = { message -> showSnackbar(message) },
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
                showSnackbar = { message -> showSnackbar(message) },
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
