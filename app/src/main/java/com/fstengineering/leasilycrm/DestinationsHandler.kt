package com.fstengineering.leasilycrm

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.fstengineering.leasilycrm.home.HomeScreen
import com.fstengineering.leasilycrm.home.HomeViewModel
import com.fstengineering.leasilycrm.profile.ProfileScreen
import com.fstengineering.leasilycrm.profile.ProfileViewModel
import com.fstengineering.leasilycrm.root.RootScreen
import com.fstengineering.leasilycrm.root.RootViewModel
import com.fstengineering.leasilycrm.signin.SignInScreen
import com.fstengineering.leasilycrm.signin.SignInViewModel
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
                viewModel = koinViewModel<RootViewModel>(),
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
                viewModel = koinViewModel<SignInViewModel>(),
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
                viewModel = koinViewModel<HomeViewModel>(),
                onProfileSelect = {
                    navController.navigate(Destinations.Profile) {
                        val startDestination =
                            navController.graph.startDestinationRoute ?: return@navigate

                        popUpTo(startDestination) { inclusive = true }
                    }
                },
                onPropertiesSelect = {},
                onPropertyOwnersSelect = {},
                onCustomerSegmentsSelect = {},
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
                viewModel = koinViewModel<ProfileViewModel>(),
                credentialManager = koinInject(),
                onBack = {
                    navController.navigateUp()
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
    }
}
