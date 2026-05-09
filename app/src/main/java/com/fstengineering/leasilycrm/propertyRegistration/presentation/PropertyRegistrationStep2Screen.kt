package com.fstengineering.leasilycrm.propertyRegistration.presentation

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.PropertyRegistrationFormSectionStep2
import com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.PropertyRegistrationTopAppBar
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyDraftFormUiState
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.fields.PropertyStep2Field
import com.fstengineering.leasilycrm.propertyRegistration.presentation.viewModels.PropertyRegistrationStep2ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyRegistrationStep2Screen(
    parentViewModel: PropertyRegistrationViewModel,
    stepViewModel: PropertyRegistrationStep2ViewModel,
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

    val scrollToFieldChannel = remember { Channel<PropertyStep2Field>() }
    val scrollToFieldFlow = remember(scrollToFieldChannel) { scrollToFieldChannel.receiveAsFlow() }

    var mustShowConfirmLeaveRegistrationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        launch {
            parentViewModel.uiEvents.collect { uiEvent ->
                when (uiEvent) {
                    is PropertyRegistrationViewModel.UiEvent.BottomSheetError -> {
                        val errorData = uiEvent.error.toBottomSheetData(
                            DefaultErrorBottomSheetDataPropertiesStrategy,
                        )
                        onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                    }

                    is PropertyRegistrationViewModel.UiEvent.Exit -> {
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
                    is PropertyRegistrationStep2ViewModel.UiEvent.ScrollToField -> {
                        scrollToFieldChannel.trySend(uiEvent.field)
                    }

                    is PropertyRegistrationStep2ViewModel.UiEvent.ValidateSuccess -> {
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
                PropertyRegistrationTopAppBar(
                    isEditing = parentViewModel.propertyId != null,
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
            is PropertyDraftFormUiState.Loading -> {
                FullScreenLoadingIndicator(innerPadding)
            }

            is PropertyDraftFormUiState.Loaded -> {
                val currentForm = draftForm as PropertyDraftFormUiState.Loaded

                PropertyRegistrationFormSectionStep2(
                    form = currentForm,
                    onFormChange = { newForm ->
                        parentViewModel.onPropertyFormChange(newForm)
                        stepViewModel.onPropertyFormChange(currentForm, newForm)
                    },
                    formErrors = formErrors.errors,
                    scrollToFieldFlow = scrollToFieldFlow,
                    onBack = onBack,
                    onNext = {
                        focusManager.clearFocus()
                        stepViewModel.validatePropertyStep2(currentForm)
                    },
                    modifier = Modifier
                        .padding(innerPadding)
                )
            }
        }

        if (mustShowConfirmLeaveRegistrationDialog) {
            ConfirmLeaveRegistrationDialog(
                isEditing = parentViewModel.propertyId != null,
                onConfirmLeave = {
                    mustShowConfirmLeaveRegistrationDialog = false
                    onLeaveRegistration()
                },
                onCancel = { mustShowConfirmLeaveRegistrationDialog = false }
            )
        }
    }
}
