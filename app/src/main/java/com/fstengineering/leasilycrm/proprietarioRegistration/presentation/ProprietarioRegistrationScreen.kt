package com.fstengineering.leasilycrm.proprietarioRegistration.presentation

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.collectWithLifecycle
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.ProprietarioRegistrationViewModel.UiEvent.CloseScreen
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.ProprietarioRegistrationViewModel.UiEvent.CloseScreenWithSnackbar
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.ProprietarioRegistrationViewModel.UiEvent.ScrollToField
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.ProprietarioRegistrationViewModel.UiEvent.ShowConfirmLeaveDialog
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.ProprietarioRegistrationViewModel.UiEvent.ShowErrorBottomSheet
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.composables.ProprietarioRegistrationFormSection
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.composables.ProprietarioRegistrationTopAppBar
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioDraftFormUiState
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioField
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProprietarioRegistrationScreen(
    viewModel: ProprietarioRegistrationViewModel,
    onBack: () -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val surfaceColor = MaterialTheme.colorScheme.surface

    val draftForm by viewModel.draftFormUiState.collectAsStateWithLifecycle()
    val formErrors by viewModel.formErrors.collectAsStateWithLifecycle()

    val scrollToFieldChannel = remember { Channel<ProprietarioField>() }
    val scrollToFieldFlow = remember(scrollToFieldChannel) { scrollToFieldChannel.receiveAsFlow() }

    var mustShowConfirmLeaveRegistrationDialog by remember { mutableStateOf(false) }

    val openDocumentFile = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { contentUri ->
            if (contentUri == null) return@rememberLauncherForActivityResult

            context.contentResolver.takePersistableUriPermission(
                contentUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )

            viewModel.onProprietarioFilePick(
                contentUriString = contentUri.toString(),
                defaultFileName = context.getString(R.string.unknown_text),
            )
        }
    )

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        viewModel.init()
    }

    LaunchedEffect(viewModel, lifecycle) {
        viewModel.uiEvents.collectWithLifecycle(lifecycle) { uiEvent ->
            when (uiEvent) {
                is ShowErrorBottomSheet -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is ScrollToField -> {
                    scrollToFieldChannel.trySend(uiEvent.field)
                }

                is CloseScreenWithSnackbar -> {
                    onBack()

                    onUiEvent(UiEvent.ShowSnackBar(uiEvent.messageId))
                }

                is ShowConfirmLeaveDialog -> {
                    mustShowConfirmLeaveRegistrationDialog = true
                }

                is CloseScreen -> {
                    mustShowConfirmLeaveRegistrationDialog = false
                    onBack()
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    SideEffect {
        activity.window.statusBarColor = surfaceColor.toArgb()
        activity.window.navigationBarColor = surfaceColor.toArgb()
    }

    BackHandler {
        viewModel.onExit()
    }

    Scaffold(
        topBar = {
            Column {
                ProprietarioRegistrationTopAppBar(
                    isEditing = viewModel.isEditing,
                    onBack = viewModel::onExit,
                    onSaveProprietario = viewModel::saveProprietario,
                )
                HorizontalDivider()
            }
        },
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            }
    ) { innerPadding ->
        when (draftForm) {
            is ProprietarioDraftFormUiState.Loading -> {
                FullScreenLoadingIndicator(innerPadding)
            }

            is ProprietarioDraftFormUiState.Loaded -> {
                ProprietarioRegistrationFormSection(
                    proprietarioForm = draftForm as ProprietarioDraftFormUiState.Loaded,
                    onProprietarioFormChange = viewModel::onProprietarioFormChange,
                    formErrors = formErrors.errors,
                    onLastTextFieldDone = { focusManager.clearFocus() },
                    onAddProprietarioFile = {
                        val acceptedMimeTypes = arrayOf("application/*", "text/*")
                        openDocumentFile.launch(acceptedMimeTypes)
                    },
                    scrollToFieldFlow = scrollToFieldFlow,
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp)
                )
            }
        }

        if (mustShowConfirmLeaveRegistrationDialog) {
            ConfirmLeaveRegistrationDialog(
                isEditing = viewModel.isEditing,
                onConfirmLeave = {
                    mustShowConfirmLeaveRegistrationDialog = false
                    onBack()
                },
                onCancel = { mustShowConfirmLeaveRegistrationDialog = false }
            )
        }
    }
}
