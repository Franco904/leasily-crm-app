package com.fstengineering.leasilycrm

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.fstengineering.leasilycrm.customerSegments.CustomerSegmentsScreen
import com.fstengineering.leasilycrm.home.HomeScreen
import com.fstengineering.leasilycrm.profile.ProfileScreen
import com.fstengineering.leasilycrm.propertyOwnerDetails.PropertyOwnerDetailsScreen
import com.fstengineering.leasilycrm.propertyOwnerRegistration.PropertyOwnerRegistrationScreen
import com.fstengineering.leasilycrm.propertyOwners.PropertyOwnersScreen
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
                    navController.navigate(Destinations.Profile) {
                        val startDestination =
                            navController.graph.startDestinationRoute ?: return@navigate

                        popUpTo(startDestination) { inclusive = true }
                    }
                },
                onPropertiesSelect = {},
                onPropertyOwnersSelect = {
                    navController.navigate(Destinations.PropertyOwners)
                },
                onCustomerSegmentsSelect = {
                    navController.navigate(Destinations.CustomerSegments)
                },
                onSignOut = {
                    onSignOut()

                    navController.navigate(Destinations.SignIn) {
                        val startDestination =
                            navController.graph.startDestinationRoute ?: return@navigate

                        popUpTo(startDestination) { inclusive = true }
                    }
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

                    navController.navigate(Destinations.SignIn) {
                        val startDestination =
                            navController.graph.startDestinationRoute ?: return@navigate

                        popUpTo(startDestination) { inclusive = true }
                    }
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

                    navController.navigate(Destinations.SignIn) {
                        val startDestination =
                            navController.graph.startDestinationRoute ?: return@navigate

                        popUpTo(startDestination) { inclusive = true }
                    }
                },
                showSnackbar = { message -> showSnackbar(message) },
            )
        }

        composable<Destinations.PropertyOwners> {
            PropertyOwnersScreen(
                viewModel = koinViewModel(),
                onBack = { navController.navigateUp() },
                onSignOut = {
                    onSignOut()

                    navController.navigate(Destinations.SignIn) {
                        val startDestination =
                            navController.graph.startDestinationRoute ?: return@navigate

                        popUpTo(startDestination) { inclusive = true }
                    }
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

                    navController.navigate(Destinations.SignIn) {
                        val startDestination =
                            navController.graph.startDestinationRoute ?: return@navigate

                        popUpTo(startDestination) { inclusive = true }
                    }
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

                    navController.navigate(Destinations.SignIn) {
                        val startDestination =
                            navController.graph.startDestinationRoute ?: return@navigate

                        popUpTo(startDestination) { inclusive = true }
                    }
                },
                onEditPropertyOwner = { propertyOwnerId ->
                    navController.navigate(Destinations.PropertyOwnerRegistration(propertyOwnerId))
                },
                showSnackbar = { message -> showSnackbar(message) },
            )
        }
    }
}
