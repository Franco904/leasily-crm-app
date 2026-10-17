package com.fstengineering.leasilycrm.root

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.BuildConfig
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.infra.utils.hasNotificationPermission
import com.fstengineering.leasilycrm.root.RootViewModel.UiEvents.SignInState
import kotlinx.coroutines.launch

@Composable
fun RootScreen(
    viewModel: RootViewModel,
    startedFromStayNotificationPopUp: Boolean,
    onNavigateToFirstDestination: (Any) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!

    val surfaceColor = MaterialTheme.colorScheme.surface

    LaunchedEffect(Unit) {
        viewModel.init()

        launch {
            viewModel.uiEvents.collect { uiEvent ->
                when (uiEvent) {
                    is SignInState -> {
                        val firstDestination = when {
                            uiEvent.isSessionAlive || BuildConfig.SKIP_AUTH -> {
                                Destinations.Home(startedFromStayNotificationPopUp)
                            }

                            else -> Destinations.SignIn(hasSessionExpired = false)
                        }

                        onNavigateToFirstDestination(firstDestination)
                    }
                }
            }
        }

        if (!context.hasNotificationPermission()) {
            viewModel.uncheckAppNotificationSettings()
        }
    }

    @Suppress("DEPRECATION")
    SideEffect {
        activity.window.statusBarColor = surfaceColor.toArgb()
        activity.window.navigationBarColor = surfaceColor.toArgb()
    }

    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AppCircularProgressIndicator(
                modifier = Modifier
                    .size(64.dp)
            )
        }
    }
}
