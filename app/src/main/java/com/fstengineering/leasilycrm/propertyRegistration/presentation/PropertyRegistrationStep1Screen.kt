package com.fstengineering.leasilycrm.propertyRegistration.presentation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppComboBottomSheet
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.SystemBarsEffect
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.PropertyRegistrationFormSectionStep1
import com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.PropertyRegistrationTopAppBar
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyDraftFormUiState
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.fields.PropertyStep1Field
import com.fstengineering.leasilycrm.propertyRegistration.presentation.viewModels.PropertyRegistrationStep1ViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyRegistrationStep1Screen(
    propertyId: Long?,
    parentViewModel: PropertyRegistrationViewModel,
    stepViewModel: PropertyRegistrationStep1ViewModel,
    onRegisterNewProprietario: () -> Unit,
    onLeaveRegistration: () -> Unit,
    onNext: () -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val sheetState = rememberModalBottomSheetState()

    val draftForm by parentViewModel.draftFormUiState.collectAsStateWithLifecycle()
    val loadedForm = draftForm as? PropertyDraftFormUiState.Loaded
    val formErrors by stepViewModel.formErrors.collectAsStateWithLifecycle()

    val proprietarios by parentViewModel.proprietarios.collectAsStateWithLifecycle()
    val pastNeighborhoods by parentViewModel.pastNeighborhoods.collectAsStateWithLifecycle()

    val scrollToFieldChannel = remember { Channel<PropertyStep1Field>() }
    val scrollToFieldFlow = remember(scrollToFieldChannel) { scrollToFieldChannel.receiveAsFlow() }

    var mustShowConfirmLeaveRegistrationDialog by remember { mutableStateOf(false) }
    var mustShowProprietariosComboBottomsheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        parentViewModel.init(propertyId = propertyId)

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
                    is PropertyRegistrationStep1ViewModel.UiEvent.ScrollToField -> {
                        scrollToFieldChannel.trySend(uiEvent.field)
                    }

                    is PropertyRegistrationStep1ViewModel.UiEvent.ValidateSuccess -> {
                        onNext()
                    }
                }
            }
        }
    }

    SystemBarsEffect(
        statusBarColor = MaterialTheme.colorScheme.surface,
        navigationBarColor = MaterialTheme.colorScheme.surface,
    )

    BackHandler {
        parentViewModel.onExit()
    }

    Scaffold(
        topBar = {
            Column {
                PropertyRegistrationTopAppBar(
                    isEditing = propertyId != null,
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

                PropertyRegistrationFormSectionStep1(
                    form = currentForm,
                    onFormChange = { newForm ->
                        parentViewModel.onPropertyFormChange(newForm)
                        stepViewModel.onPropertyFormChange(currentForm, newForm)
                    },
                    pastNeighborhoods = pastNeighborhoods,
                    formErrors = formErrors.errors,
                    onSelectProprietario = { mustShowProprietariosComboBottomsheet = true },
                    onLastTextFieldDone = { focusManager.clearFocus() },
                    scrollToFieldFlow = scrollToFieldFlow,
                    onNext = {
                        focusManager.clearFocus()
                        stepViewModel.validatePropertyStep1(currentForm)
                    },
                    modifier = Modifier
                        .padding(innerPadding)
                )
            }
        }

        if (mustShowProprietariosComboBottomsheet) {
            AppComboBottomSheet(
                sheetState = sheetState,
                options = proprietarios.map { it.name }.toPersistentList(),
                onOptionSelect = { proprietarioName ->
                    loadedForm ?: return@AppComboBottomSheet

                    val selectedProprietario =
                        proprietarios.find { it.name == proprietarioName }
                    val newForm = loadedForm.copy(owner = selectedProprietario)

                    parentViewModel.onPropertyFormChange(form = newForm)
                    stepViewModel.onPropertyFormChange(loadedForm, newForm)

                    focusManager.clearFocus()
                },
                onDismissRequest = {
                    mustShowProprietariosComboBottomsheet = false
                    focusManager.clearFocus()
                },
                emptyOptionsIcon = Icons.Outlined.Person,
                emptyOptionsText = stringResource(R.string.property_registration_property_owner_empty_text),
                emptyAction = {
                    TextButton(onRegisterNewProprietario) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.add_action_button_text),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            )
        }

        if (mustShowConfirmLeaveRegistrationDialog) {
            ConfirmLeaveRegistrationDialog(
                isEditing = propertyId != null,
                onConfirmLeave = {
                    mustShowConfirmLeaveRegistrationDialog = false
                    onLeaveRegistration()
                },
                onCancel = { mustShowConfirmLeaveRegistrationDialog = false }
            )
        }
    }
}
