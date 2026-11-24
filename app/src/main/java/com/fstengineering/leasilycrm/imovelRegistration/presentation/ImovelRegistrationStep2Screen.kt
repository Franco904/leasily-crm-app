package com.fstengineering.leasilycrm.imovelRegistration.presentation

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.utils.collectWithLifecycle
import com.fstengineering.leasilycrm.core.presentation.utils.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.imovelRegistration.presentation.composables.ImovelRegistrationFormSectionStep2
import com.fstengineering.leasilycrm.imovelRegistration.presentation.composables.ImovelRegistrationTopAppBar
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ImovelDraftFormUiState
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.fields.ImovelStep2Field
import com.fstengineering.leasilycrm.imovelRegistration.presentation.viewModels.ImovelRegistrationStep2ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImovelRegistrationStep2Screen(
    parentViewModel: ImovelRegistrationViewModel,
    stepViewModel: ImovelRegistrationStep2ViewModel,
    onLeaveRegistration: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val surfaceColor = MaterialTheme.colorScheme.surface

    val draftForm by parentViewModel.draftFormUiState.collectAsStateWithLifecycle()
    val formErrors by stepViewModel.formErrors.collectAsStateWithLifecycle()

    val scrollToFieldChannel = remember { Channel<ImovelStep2Field>() }
    val scrollToFieldFlow = remember(scrollToFieldChannel) { scrollToFieldChannel.receiveAsFlow() }

    var mustShowConfirmLeaveRegistrationDialog by remember { mutableStateOf(false) }

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        launch {
            parentViewModel.uiEvents.collectWithLifecycle(lifecycle) { uiEvent ->
                when (uiEvent) {
                    is ImovelRegistrationViewModel.UiEvent.ShowErrorBottomSheet -> {
                        val errorData = uiEvent.error.toBottomSheetData(
                            DefaultErrorBottomSheetDataPropertiesStrategy,
                        )
                        onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                    }

                    is ImovelRegistrationViewModel.UiEvent.Exit -> {
                        if (uiEvent.needConfirmation) mustShowConfirmLeaveRegistrationDialog = true
                        else {
                            mustShowConfirmLeaveRegistrationDialog = false
                            onLeaveRegistration()
                        }
                    }

                    else -> {}
                }
            }
        }

        launch {
            stepViewModel.uiEvents.collect { uiEvent ->
                when (uiEvent) {
                    is ImovelRegistrationStep2ViewModel.UiEvent.ScrollToField -> {
                        scrollToFieldChannel.trySend(uiEvent.field)
                    }

                    is ImovelRegistrationStep2ViewModel.UiEvent.ValidateSuccess -> {
                        onNext()
                    }
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
        parentViewModel.onExit()
    }

    Scaffold(
        topBar = {
            Column {
                ImovelRegistrationTopAppBar(
                    isEditing = parentViewModel.isEditing,
                    onBack = parentViewModel::onExit,
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
            is ImovelDraftFormUiState.Loading -> {
                FullScreenLoadingIndicator(innerPadding)
            }

            is ImovelDraftFormUiState.Loaded -> {
                val currentForm = draftForm as ImovelDraftFormUiState.Loaded

                ImovelRegistrationFormSectionStep2(
                    form = currentForm,
                    onFormChange = { newForm ->
                        parentViewModel.onImovelFormChange(newForm)
                        stepViewModel.onImovelFormChange(currentForm, newForm)
                    },
                    formErrors = formErrors.errors,
                    scrollToFieldFlow = scrollToFieldFlow,
                    onBack = onBack,
                    onNext = {
                        focusManager.clearFocus()
                        stepViewModel.validateImovelStep2(currentForm)
                    },
                    modifier = Modifier
                        .padding(innerPadding)
                )
            }
        }

        if (mustShowConfirmLeaveRegistrationDialog) {
            ConfirmLeaveRegistrationDialog(
                isEditing = parentViewModel.isEditing,
                onConfirmLeave = {
                    mustShowConfirmLeaveRegistrationDialog = false
                    onLeaveRegistration()
                },
                onCancel = { mustShowConfirmLeaveRegistrationDialog = false }
            )
        }
    }
}
