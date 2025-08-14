package com.fstengineering.leasilycrm.home

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.home.HomeViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.home.composables.HomeScreenTopBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    onProfileSelect: () -> Unit,
    showSnackbar: (String) -> Unit = { _ -> },
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!

    val surfaceColor = MaterialTheme.colorScheme.surface

    val userProfilePictureUrl by viewModel.userProfilePictureUrl.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.init()

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is DataSourceError -> {
                    showSnackbar(context.getString(uiEvent.messageId))
                }
            }
        }
    }

    SideEffect {
        activity.window.statusBarColor = surfaceColor.toArgb()
    }

    Scaffold(
        topBar = {
            HomeScreenTopBar(
                userProfilePictureUrl = userProfilePictureUrl,
                onProfileSelect = onProfileSelect
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}
