package com.fstengineering.leasilycrm.settings

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.SystemBarsEffect
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.collectWithLifecycle
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.settings.models.SettingsUiState

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current!!

    val settings by viewModel.settingsUiState.collectAsStateWithLifecycle()

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    LaunchedEffect(viewModel, lifecycle) {
        viewModel.uiEvents.collectWithLifecycle(lifecycle) { uiEvent ->
            when (uiEvent) {
                is SettingsViewModel.UiEvent.Error -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }
            }
        }
    }

    SystemBarsEffect(
        statusBarColor = MaterialTheme.colorScheme.surface,
        navigationBarColor = MaterialTheme.colorScheme.surface,
    )

    Scaffold(
        topBar = {
            Column {
                _root_ide_package_.com.fstengineering.leasilycrm.settings.composables.SettingsScreenTopBar(
                    onBack = onBack
                )
                HorizontalDivider()
            }
        },
        modifier = modifier
    ) { innerPadding ->
        when (settings) {
            SettingsUiState.Loading -> FullScreenLoadingIndicator(innerPadding)
            is SettingsUiState.Loaded -> {
                _root_ide_package_.com.fstengineering.leasilycrm.settings.composables.SettingsSection(
                    settings = settings as SettingsUiState.Loaded,
                    onIsTodayCheckInCheckOutNotificationEnabledChange = { isEnabled ->
                        viewModel.onIsTodayCheckInCheckOutNotificationEnabledChange(isEnabled)
                    },
                    onNotificationPermissionDenied = {
                        onUiEvent(UiEvent.ShowSnackBar(R.string.enable_notifications_warning_message))
                    },
                    modifier = Modifier
                        .padding(innerPadding)
                )
            }
        }
    }
}
