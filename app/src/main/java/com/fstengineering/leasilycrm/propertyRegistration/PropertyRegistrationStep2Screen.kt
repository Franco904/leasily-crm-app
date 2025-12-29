package com.fstengineering.leasilycrm.propertyRegistration

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.propertyRegistration.PropertyRegistrationViewModel.UiEvents.Error
import com.fstengineering.leasilycrm.propertyRegistration.composables.PropertyRegistrationFormSectionStep2
import com.fstengineering.leasilycrm.propertyRegistration.composables.PropertyRegistrationTopAppBar
import com.fstengineering.leasilycrm.propertyRegistration.models.fields.PropertyStep2Field
import com.fstengineering.leasilycrm.propertyRegistration.viewModels.PropertyRegistrationStep2ViewModel
import com.fstengineering.leasilycrm.propertyRegistration.viewModels.PropertyRegistrationStep2ViewModel.UiEvents.ScrollToField
import com.fstengineering.leasilycrm.propertyRegistration.viewModels.PropertyRegistrationStep2ViewModel.UiEvents.ValidateSuccess
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

    val propertyForm by parentViewModel.propertyForm.collectAsStateWithLifecycle()
    val formErrors by stepViewModel.formErrors.collectAsStateWithLifecycle()

    val seaProximityTypes by parentViewModel.seaProximityTypes.collectAsStateWithLifecycle()
    val propertyCharacteristics by parentViewModel.propertyCharacteristics.collectAsStateWithLifecycle()

    val scrollToFieldFlow = Channel<PropertyStep2Field>()

    val isLoading by parentViewModel.isLoading.collectAsStateWithLifecycle()

    var mustShowConfirmLeaveRegistrationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        launch {
            parentViewModel.uiEvents.collect { uiEvent ->
                when (uiEvent) {
                    is Error -> {
                        val errorData = uiEvent.error.toBottomSheetData(
                            DefaultErrorBottomSheetDataPropertiesStrategy,
                        )
                        onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                    }

                    else -> {}
                }
            }
        }

        launch {
            stepViewModel.uiEvents.collect { uiEvent ->
                when (uiEvent) {
                    is ScrollToField -> {
                        scrollToFieldFlow.trySend(uiEvent.field)
                    }

                    is ValidateSuccess -> {
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
        mustShowConfirmLeaveRegistrationDialog = true
    }

    Scaffold(
        topBar = {
            PropertyRegistrationTopAppBar(
                isEditing = parentViewModel.propertyId != null,
                onBack = { mustShowConfirmLeaveRegistrationDialog = true },
            )
        },
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            }
    ) { innerPadding ->
        if (isLoading) {
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
        } else {
            PropertyRegistrationFormSectionStep2(
                propertyForm = propertyForm,
                onPropertyFormChange = { newProperty ->
                    parentViewModel.onPropertyFormChange(newProperty)
                    stepViewModel.onPropertyFormChange(newProperty, propertyForm)
                },
                seaProximityTypes = seaProximityTypes,
                propertyCharacteristics = propertyCharacteristics,
                formErrors = formErrors.errors,
                scrollToFieldFlow = scrollToFieldFlow.receiveAsFlow(),
                onBack = onBack,
                onNext = {
                    focusManager.clearFocus()

                    stepViewModel.validatePropertyStep2(
                        propertyForm = propertyForm,
                    )
                },
                modifier = Modifier
                    .padding(innerPadding)
            )
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
