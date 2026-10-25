package com.fstengineering.leasilycrm.proprietarioDetails.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.proprietarios.presentation.composables.ConfirmDeleteProprietarioDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.NoAppToShowFileContentDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.openFileWithChooser
import com.fstengineering.leasilycrm.core.presentation.utils.openPlayStoreApp
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.proprietarioDetails.presentation.composables.ProprietarioDetailsSection
import com.fstengineering.leasilycrm.proprietarioDetails.presentation.composables.ProprietarioDetailsTopAppBar
import com.fstengineering.leasilycrm.proprietarioDetails.presentation.models.ProprietarioUiState

@Composable
fun ProprietarioDetailsScreen(
    proprietarioId: Long,
    viewModel: ProprietarioDetailsViewModel,
    onBack: () -> Unit,
    onEditProprietario: (Long) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!

    val surfaceColor = MaterialTheme.colorScheme.surface

    val proprietario by viewModel.proprietario.collectAsStateWithLifecycle()

    var mustShowConfirmDeleteDialog by remember { mutableStateOf(false) }
    var mustShowNoAppToShowFileContentDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init(proprietarioId = proprietarioId)

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is ProprietarioDetailsViewModel.UiEvent.BottomSheetError -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is ProprietarioDetailsViewModel.UiEvent.ProprietarioDeleteSuccess -> {
                    onUiEvent(UiEvent.ShowSnackBar(R.string.delete_property_owner_success_message))
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    SideEffect {
        activity.window.statusBarColor = surfaceColor.toArgb()
        activity.window.navigationBarColor = surfaceColor.toArgb()
    }

    Scaffold(
        topBar = {
            Column {
                ProprietarioDetailsTopAppBar(
                    onBack = onBack,
                    onEditProprietario = { onEditProprietario(proprietarioId) },
                    onDeleteProprietario = { mustShowConfirmDeleteDialog = true },
                )
                HorizontalDivider()
            }
        },
        modifier = modifier
    ) { innerPadding ->
        when (proprietario) {
            is ProprietarioUiState.Loading -> {
                FullScreenLoadingIndicator(innerPadding)
            }

            is ProprietarioUiState.Loaded -> {
                ProprietarioDetailsSection(
                    proprietario = proprietario as ProprietarioUiState.Loaded,
                    onOpenProprietarioFile = { file ->
                        context.openFileWithChooser(
                            uriString = file.uriString,
                            onActivityNotFound = { mustShowNoAppToShowFileContentDialog = true }
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                )
            }
        }

        if (mustShowConfirmDeleteDialog) {
            ConfirmDeleteProprietarioDialog(
                onConfirmDelete = {
                    viewModel.deleteProprietario(proprietarioId)

                    mustShowConfirmDeleteDialog = false
                    onBack()
                },
                onCancel = { mustShowConfirmDeleteDialog = false },
            )
        }

        if (mustShowNoAppToShowFileContentDialog) {
            NoAppToShowFileContentDialog(
                onOpenPlayStore = { context.openPlayStoreApp() },
                onClose = { mustShowNoAppToShowFileContentDialog = false }
            )
        }
    }
}
