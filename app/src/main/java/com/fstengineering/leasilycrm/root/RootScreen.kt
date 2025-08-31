package com.fstengineering.leasilycrm.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.root.RootViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.root.RootViewModel.UiEvents.SignInState

@Composable
fun RootScreen(
    viewModel: RootViewModel,
    modifier: Modifier = Modifier,
    onNavigateToFirstDestination: (Any) -> Unit = { _ -> },
    showSnackbar: (String, Boolean) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.init()

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is DataSourceError -> {
                    showSnackbar(context.getString(uiEvent.messageId), false)

                    onNavigateToFirstDestination(Destinations.SignIn)
                }

                is SignInState -> {
                    val firstDestination = when {
                        uiEvent.isUserSignedIn -> Destinations.Home
                        else -> Destinations.SignIn
                    }

                    onNavigateToFirstDestination(firstDestination)
                }
            }
        }
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
