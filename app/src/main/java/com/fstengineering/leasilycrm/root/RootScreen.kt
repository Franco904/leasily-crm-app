package com.fstengineering.leasilycrm.root

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
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
import androidx.core.content.ContextCompat
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.root.RootViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.root.RootViewModel.UiEvents.SignInState
import kotlinx.coroutines.launch

@Composable
fun RootScreen(
    viewModel: RootViewModel,
    startedFromStayNotificationPopUp: Boolean,
    modifier: Modifier = Modifier,
    onNavigateToFirstDestination: (Any) -> Unit = { _ -> },
    showSnackbar: (String, Boolean) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!

    val surfaceColor = MaterialTheme.colorScheme.surface

    LaunchedEffect(Unit) {
        viewModel.init()

        launch {

        }
        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is DataSourceError -> {
                    showSnackbar(context.getString(uiEvent.messageId), false)

                    onNavigateToFirstDestination(Destinations.SignIn)
                }

                is SignInState -> {
                    val firstDestination = when {
                        uiEvent.isUserSignedIn -> {
                            Destinations.Home(startedFromStayNotificationPopUp)
                        }

                        else -> Destinations.SignIn
                    }

                    onNavigateToFirstDestination(firstDestination)
                }
            }
        }

        val isAndroid13OrHigher = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        val hasNotificationPermission = isAndroid13OrHigher && ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasNotificationPermission) viewModel.uncheckAppNotificationSettings()
    }

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
