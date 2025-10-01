package com.fstengineering.leasilycrm.assistanceRegistration

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.assistanceRegistration.AssistanceRegistrationViewModel.UiEvents.AssistanceCreateSuccess
import com.fstengineering.leasilycrm.assistanceRegistration.AssistanceRegistrationViewModel.UiEvents.AssistanceEditSuccess
import com.fstengineering.leasilycrm.assistanceRegistration.AssistanceRegistrationViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.assistanceRegistration.AssistanceRegistrationViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.assistanceRegistration.composables.AssistanceInteractionRegistrationBottomsheet
import com.fstengineering.leasilycrm.assistanceRegistration.composables.AssistanceRegistrationFormSectionStep3
import com.fstengineering.leasilycrm.assistanceRegistration.composables.AssistanceRegistrationTopAppBar
import com.fstengineering.leasilycrm.assistanceRegistration.composables.ConfirmDeleteAssistanceInteractionDialog
import com.fstengineering.leasilycrm.assistanceRegistration.viewModels.AssistanceRegistrationStep3ViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.viewModels.AssistanceRegistrationStep3ViewModel.UiEvents.InteractionCreateSuccess
import com.fstengineering.leasilycrm.assistanceRegistration.viewModels.AssistanceRegistrationStep3ViewModel.UiEvents.InteractionEditSuccess
import com.fstengineering.leasilycrm.core.domain.models.AssistanceInteraction
import com.fstengineering.leasilycrm.core.presentation.composables.AssistanceInteractionDetailsBottomsheet
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.ListState
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
    onSignOut: () -> Unit,
    showSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val sheetState = rememberModalBottomSheetState()

    val surfaceColor = MaterialTheme.colorScheme.surface

    val assistanceForm by parentViewModel.assistanceForm.collectAsStateWithLifecycle()
    val leadTenantName by parentViewModel.leadTenantName.collectAsStateWithLifecycle()

    val isLoading by parentViewModel.isLoading.collectAsStateWithLifecycle()

    val interactionForm by stepViewModel.interactionForm.collectAsStateWithLifecycle()

    val formErrors by stepViewModel.formErrors.collectAsStateWithLifecycle()

    var interactionToView by rememberSaveable { mutableStateOf<AssistanceInteraction?>(null) }
    var interactionToDelete by rememberSaveable { mutableStateOf<AssistanceInteraction?>(null) }

    var mustShowConfirmLeaveRegistrationDialog by rememberSaveable { mutableStateOf(false) }

    val listStatus = remember(isLoading, assistanceForm.interactions) {
        when {
            isLoading -> ListState.Loading
            assistanceForm.interactions.isEmpty() -> ListState.Empty
            else -> ListState.Idle
        }
    }

    LaunchedEffect(Unit) {
        launch {
            parentViewModel.uiEvents.collect { uiEvent ->
                when (uiEvent) {
                    is DataSourceError -> {
                        showSnackbar(context.getString(uiEvent.messageId))
                    }

                    is RedirectToSignIn -> {
                        onSignOut()
                    }

                    is AssistanceCreateSuccess -> {
                        onLeaveRegistration()
                        showSnackbar(context.getString(R.string.snackbar_create_assistance_succeeded))
                    }

                    is AssistanceEditSuccess -> {
                        onLeaveRegistration()
                        showSnackbar(context.getString(uiEvent.messageId))
                    }
                }
            }
        }

        launch {
            stepViewModel.uiEvents.collect { uiEvent ->
                when (uiEvent) {
                    is InteractionCreateSuccess -> {
                        val newInterations = assistanceForm.interactions
                            .toPersistentList()
                            .add(interactionForm!!)

                        parentViewModel.onAssistanceFormChange(
                            form = assistanceForm.copy(interactions = newInterations),
                        )
                        stepViewModel.onInteractionFormChange(form = null)
                        stepViewModel.onDismissInteraction()

                        val message =
                            context.getString(R.string.snackbar_create_assistance_interaction_succeeded)
                        showSnackbar(message)
                    }

                    is InteractionEditSuccess -> {
                        val newInterations = assistanceForm.interactions
                            .toPersistentList()
                            .mutate { list ->
                                val i = list.indexOf(stepViewModel.interactionFormBeforeEdit)
                                list[i] = interactionForm!!
                            }

                        parentViewModel.onAssistanceFormChange(
                            form = assistanceForm.copy(interactions = newInterations),
                        )
                        stepViewModel.onInteractionFormChange(form = null)
                        stepViewModel.onDismissInteraction()

                        showSnackbar(context.getString(uiEvent.messageId))
                    }
                }
            }
        }
    }

    SideEffect {
        activity.window.statusBarColor = surfaceColor.toArgb()
        activity.window.navigationBarColor = surfaceColor.toArgb()
    }

    BackHandler {
        mustShowConfirmLeaveRegistrationDialog = true
    }

    Scaffold(
        topBar = {
            AssistanceRegistrationTopAppBar(
                leadTenantName = leadTenantName,
                isEditing = parentViewModel.assistanceId != null,
                onBack = { mustShowConfirmLeaveRegistrationDialog = true },
            )
        },
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { focusManager.clearFocus() }
            }
    ) { innerPadding ->
        AssistanceRegistrationFormSectionStep3(
            listStatus = listStatus,
            assistanceForm = assistanceForm,
            onAddInteraction = {
                stepViewModel.onInteractionFormChange(form = AssistanceInteraction())
            },
            onViewInteractionDetails = { interactionToView = it },
            onEditInteraction = {
                stepViewModel.onInteractionFormChange(form = it)
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
    if (interactionForm != null) {
        AssistanceInteractionRegistrationBottomsheet(
            sheetState = sheetState,
            interactionForm = interactionForm!!,
            onInterationFormChange = stepViewModel::onInteractionFormChange,
            formErrors = formErrors.errors,
            onDismissRequest = {
                stepViewModel.onInteractionFormChange(form = null)
                stepViewModel.onDismissInteraction()
            },
            onSave = {
                stepViewModel.clearErrors()

                stepViewModel.validateInteractionForm(assistanceForm = assistanceForm)
            }
        )
    }
    if (interactionToView != null) {
        AssistanceInteractionDetailsBottomsheet(
            sheetState = sheetState,
            interaction = interactionToView!!,
            onDismissRequest = { interactionToView = null },
        )
    }
    if (interactionToDelete != null) {
        ConfirmDeleteAssistanceInteractionDialog(
            onConfirmDelete = {
                val newInteractions = assistanceForm.interactions
                    .toPersistentList()
                    .mutate { list ->
                        if (interactionToDelete?.id == null) {
                            list.remove(interactionToDelete)
                        } else {
                            val i = list.indexOf(interactionToDelete)
                            list[i] = list[i].copy(isDeleted = true)
                        }
                    }

                parentViewModel.onAssistanceFormChange(
                    form = assistanceForm.copy(interactions = newInteractions),
                )

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
