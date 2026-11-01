package com.fstengineering.leasilycrm.assistanceRegistration.presentation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables.AssistanceInteractionRegistrationBottomSheet
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables.AssistanceRegistrationFormSectionStep3
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables.AssistanceRegistrationTopAppBar
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables.ConfirmDeleteAssistanceInteractionDialog
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceDraftFormUiState
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceInteractionDraftFormUiState
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels.AssistanceRegistrationStep3ViewModel
import com.fstengineering.leasilycrm.core.presentation.composables.AssistanceInteractionDetailsBottomSheet
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.SystemBarsEffect
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistanceRegistrationStep3Screen(
    parentViewModel: AssistanceRegistrationViewModel,
    stepViewModel: AssistanceRegistrationStep3ViewModel,
    onLeaveRegistration: () -> Unit,
    onBack: () -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val draftForm by parentViewModel.draftFormUiState.collectAsStateWithLifecycle()
    val loadedDraftForm = draftForm as? AssistanceDraftFormUiState.Loaded

    val interactionForm by stepViewModel.interactionForm.collectAsStateWithLifecycle()
    val loadedInteractionForm = interactionForm as? AssistanceInteractionDraftFormUiState.Loaded

    val formErrors by stepViewModel.formErrors.collectAsStateWithLifecycle()

    var interactionToView by rememberSaveable {
        mutableStateOf<AssistanceInteractionDraftFormUiState.Loaded?>(null)
    }
    var interactionToDelete by rememberSaveable {
        mutableStateOf<AssistanceInteractionDraftFormUiState.Loaded?>(null)
    }

    var mustShowConfirmLeaveRegistrationDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        launch {
            parentViewModel.uiEvents.collect { uiEvent ->
                when (uiEvent) {
                    is AssistanceRegistrationViewModel.UiEvent.BottomSheetError -> {
                        val errorData = uiEvent.error.toBottomSheetData(
                            DefaultErrorBottomSheetDataPropertiesStrategy,
                        )
                        onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                    }

                    is AssistanceRegistrationViewModel.UiEvent.AssistanceCreateSuccess -> {
                        onLeaveRegistration()
                        onUiEvent(UiEvent.ShowSnackBar(R.string.create_assistance_success_message))
                    }

                    is AssistanceRegistrationViewModel.UiEvent.AssistanceEditSuccess -> {
                        onLeaveRegistration()
                        onUiEvent(UiEvent.ShowSnackBar(uiEvent.messageId))
                    }

                    is AssistanceRegistrationViewModel.UiEvent.ConvertOldTenantToLead -> {
                        onUiEvent(UiEvent.ShowSnackBar(R.string.convert_tenant_to_lead_success_message))
                    }

                    is AssistanceRegistrationViewModel.UiEvent.Exit -> {
                        if (uiEvent.needConfirmation) mustShowConfirmLeaveRegistrationDialog = true
                        else {
                            mustShowConfirmLeaveRegistrationDialog = false
                            onLeaveRegistration()
                        }
                    }
                }
            }
        }

        launch {
            stepViewModel.uiEvents.collect { uiEvent ->
                when (uiEvent) {
                    is AssistanceRegistrationStep3ViewModel.UiEvent.InteractionCreateSuccess -> {
                        if (loadedInteractionForm == null) return@collect
                        if (loadedDraftForm?.interactions == null) return@collect

                        val newInteractions = loadedDraftForm.interactions
                            .toPersistentList()
                            .add(loadedInteractionForm)

                        parentViewModel.onAssistanceFormChange(loadedDraftForm.copy(interactions = newInteractions))
                        stepViewModel.clearInteractionForm()

                        onUiEvent(UiEvent.ShowSnackBar(R.string.create_assistance_interaction_success_message))
                    }

                    is AssistanceRegistrationStep3ViewModel.UiEvent.InteractionEditSuccess -> {
                        if (loadedInteractionForm == null) return@collect
                        if (loadedDraftForm?.interactions == null) return@collect

                        val newInteractions = loadedDraftForm.interactions
                            .toPersistentList()
                            .mutate { list ->
                                val i = list.indexOf(stepViewModel.interactionFormBeforeEdit)
                                list[i] = loadedInteractionForm
                            }

                        parentViewModel.onAssistanceFormChange(loadedDraftForm.copy(interactions = newInteractions))
                        stepViewModel.clearInteractionForm()

                        onUiEvent(UiEvent.ShowSnackBar(uiEvent.messageId))
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
                if (loadedDraftForm == null) return@Scaffold

                AssistanceRegistrationTopAppBar(
                    leadTenantName = loadedDraftForm.leadTenantName,
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
        AnimatedContent(targetState = draftForm) { form ->
            when (form) {
                is AssistanceDraftFormUiState.Loading -> {
                    FullScreenLoadingIndicator(innerPadding)
                }

                is AssistanceDraftFormUiState.Loaded -> {
                    AssistanceRegistrationFormSectionStep3(
                        assistanceForm = form,
                        onAddInteraction = stepViewModel::onOpenInteractionForm,
                        onViewInteractionDetails = { interactionToView = it },
                        onEditInteraction = {
                            stepViewModel.onInteractionFormChange(newForm = it)
                            stepViewModel.onEditInteraction(interaction = it)
                        },
                        onDeleteInteraction = { interactionToDelete = it },
                        onBack = onBack,
                        onSave = {
                            focusManager.clearFocus()
                            parentViewModel.saveAssistance()
                        },
                        modifier = Modifier
                            .padding(innerPadding),
                    )
                }
            }
        }

        if (loadedInteractionForm != null && loadedDraftForm != null) {
            AssistanceInteractionRegistrationBottomSheet(
                sheetState = sheetState,
                interactionForm = loadedInteractionForm,
                onInteractionFormChange = stepViewModel::onInteractionFormChange,
                formErrors = formErrors.errors,
                onDismissRequest = stepViewModel::clearInteractionForm,
                onSave = { stepViewModel.validateInteractionForm(loadedDraftForm) }
            )
        }

        if (interactionToView != null) {
            AssistanceInteractionDetailsBottomSheet(
                sheetState = sheetState,
                interaction = interactionToView!!.toBottomSheetUiState(),
                onDismissRequest = { interactionToView = null },
            )
        }

        if (interactionToDelete != null && loadedDraftForm != null) {
            ConfirmDeleteAssistanceInteractionDialog(
                onConfirmDelete = {
                    val newInteractions = loadedDraftForm.interactions
                        .toPersistentList()
                        .mutate { interactions -> interactions.remove(interactionToDelete) }

                    parentViewModel.onAssistanceFormChange(loadedDraftForm.copy(interactions = newInteractions))
                    interactionToDelete = null
                },
                onCancel = { interactionToDelete = null },
            )
        }

        if (mustShowConfirmLeaveRegistrationDialog) {
            ConfirmLeaveRegistrationDialog(
                isEditing = parentViewModel.assistanceId != null,
                onConfirmLeave = {
                    mustShowConfirmLeaveRegistrationDialog = false
                    onLeaveRegistration()
                },
                onCancel = { mustShowConfirmLeaveRegistrationDialog = false }
            )
        }
    }
}
