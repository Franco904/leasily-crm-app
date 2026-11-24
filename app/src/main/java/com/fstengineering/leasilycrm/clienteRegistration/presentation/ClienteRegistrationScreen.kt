package com.fstengineering.leasilycrm.clienteRegistration.presentation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.clienteRegistration.presentation.ClienteRegistrationViewModel.UiEvent.CloseScreen
import com.fstengineering.leasilycrm.clienteRegistration.presentation.ClienteRegistrationViewModel.UiEvent.CloseScreenWithSnackbar
import com.fstengineering.leasilycrm.clienteRegistration.presentation.ClienteRegistrationViewModel.UiEvent.ScrollToField
import com.fstengineering.leasilycrm.clienteRegistration.presentation.ClienteRegistrationViewModel.UiEvent.ShowConfirmLeaveDialog
import com.fstengineering.leasilycrm.clienteRegistration.presentation.ClienteRegistrationViewModel.UiEvent.ShowErrorBottomSheet
import com.fstengineering.leasilycrm.clienteRegistration.presentation.composables.ClienteRegistrationFormSection
import com.fstengineering.leasilycrm.clienteRegistration.presentation.composables.ClienteRegistrationTopAppBar
import com.fstengineering.leasilycrm.clienteRegistration.presentation.models.ClienteDraftFormUiState
import com.fstengineering.leasilycrm.clienteRegistration.presentation.models.ClienteField
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.utils.collectWithLifecycle
import com.fstengineering.leasilycrm.core.presentation.utils.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteRegistrationScreen(
    viewModel: ClienteRegistrationViewModel,
    onBack: () -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val surfaceColor = MaterialTheme.colorScheme.surface

    val draftForm by viewModel.draftFormUiState.collectAsStateWithLifecycle()
    val formErrors by viewModel.formErrors.collectAsStateWithLifecycle()

    val pastTags by viewModel.pastTags.collectAsStateWithLifecycle()
    val pastCities by viewModel.pastCities.collectAsStateWithLifecycle()
    val pastContactOrigins by viewModel.pastContactOrigins.collectAsStateWithLifecycle()

    val scrollToFieldChannel = remember { Channel<ClienteField>() }
    val scrollToFieldFlow = remember(scrollToFieldChannel) { scrollToFieldChannel.receiveAsFlow() }

    var mustShowConfirmLeaveRegistrationDialog by rememberSaveable { mutableStateOf(false) }

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
                val loadedForm = (draftForm as? ClienteDraftFormUiState.Loaded)

                ClienteRegistrationTopAppBar(
                    isEditing = viewModel.isEditing,
                    isLead = loadedForm?.isLead == true,
                    onBack = viewModel::onExit,
                    onSaveCliente = viewModel::saveCliente,
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
            is ClienteDraftFormUiState.Loading -> {
                FullScreenLoadingIndicator(innerPadding)
            }

            is ClienteDraftFormUiState.Loaded -> {
                ClienteRegistrationFormSection(
                    clienteForm = draftForm as ClienteDraftFormUiState.Loaded,
                    onClienteFormChange = viewModel::onClienteFormChange,
                    pastTags = pastTags,
                    pastCities = pastCities,
                    pastContactOrigins = pastContactOrigins,
                    formErrors = formErrors.errors,
                    onLastTextFieldDone = { focusManager.clearFocus() },
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
