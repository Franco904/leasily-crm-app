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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables.AssistanceRegistrationFormSectionStep1
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables.AssistanceRegistrationTopAppBar
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables.AssistancePreferredNeighborhoodRegistrationDialog
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceDraftFormUiState
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.fields.AssistanceStep1Field
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels.AssistanceRegistrationStep1ViewModel
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistanceRegistrationStep1Screen(
    assistanceId: Long?,
    leadTenantId: Long,
    parentViewModel: AssistanceRegistrationViewModel,
    stepViewModel: AssistanceRegistrationStep1ViewModel,
    onLeaveRegistration: () -> Unit,
    onNext: () -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val surfaceColor = MaterialTheme.colorScheme.surface

    val draftForm by parentViewModel.draftForm.collectAsStateWithLifecycle()
    val loadedDraftForm = draftForm as? AssistanceDraftFormUiState.Loaded
    val formErrors by stepViewModel.formErrors.collectAsStateWithLifecycle()

    val neighborhoodForm by stepViewModel.neighborhoodForm.collectAsStateWithLifecycle()
    var neighborhoodFormBeforeEdit by rememberSaveable { mutableStateOf<String?>(null) }

    val neighborhoodFormError by stepViewModel.neighborhoodFormError.collectAsStateWithLifecycle()
    val pastNeighborhoods by parentViewModel.pastNeighborhoods.collectAsStateWithLifecycle()

    val scrollToFieldChannel = remember { Channel<AssistanceStep1Field>() }
    val scrollToFieldFlow = remember(scrollToFieldChannel) { scrollToFieldChannel.receiveAsFlow() }

    var isRegisteringNewPreferredNeighborhood by rememberSaveable { mutableStateOf(false) }
    var mustShowConfirmLeaveRegistrationDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        parentViewModel.init(
            leadTenantId = leadTenantId,
            assistanceId = assistanceId,
        )

        launch {
            parentViewModel.uiEvents.collect { uiEvent ->
                when (uiEvent) {
                    is AssistanceRegistrationViewModel.UiEvent.BottomSheetError -> {
                        val errorData = uiEvent.error.toBottomSheetData(
                            DefaultErrorBottomSheetDataPropertiesStrategy,
                        )
                        onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                    }

                    is AssistanceRegistrationViewModel.UiEvent.Exit -> {
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
                    is AssistanceRegistrationStep1ViewModel.UiEvent.ScrollToField -> {
                        scrollToFieldChannel.trySend(uiEvent.field)
                    }

                    is AssistanceRegistrationStep1ViewModel.UiEvent.ValidateSuccess -> {
                        onNext()
                    }

                    is AssistanceRegistrationStep1ViewModel.UiEvent.AssistanceSearchedNeighborhoodSaveSuccess -> {
                        if (neighborhoodForm == null || loadedDraftForm == null) return@collect

                        val assistanceSearchedNeighborhoods =
                            loadedDraftForm.preferredNeighborhoods.toPersistentList()

                        val newPreferredNeighborhoods =
                            if (isRegisteringNewPreferredNeighborhood) {
                                assistanceSearchedNeighborhoods.add(neighborhoodForm!!)
                            } else {
                                assistanceSearchedNeighborhoods.mutate {
                                    val i = assistanceSearchedNeighborhoods
                                        .indexOf(neighborhoodFormBeforeEdit!!)
                                    it[i] = neighborhoodForm!!
                                }
                            }

                        val newForm =
                            loadedDraftForm.copy(preferredNeighborhoods = newPreferredNeighborhoods)
                        parentViewModel.onAssistanceFormChange(newForm)

                        stepViewModel.onNeighborhoodChange(neighborhood = null)
                        isRegisteringNewPreferredNeighborhood = false
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
            if (loadedDraftForm == null) return@Scaffold
            Column {
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
                    AssistanceRegistrationFormSectionStep1(
                        assistanceForm = form,
                        onAssistanceFormChange = { newForm ->
                            parentViewModel.onAssistanceFormChange(newForm)
                            stepViewModel.onAssistanceFormChange(newForm, oldForm = form)
                        },
                        onAddPreferredNeighborhood = {
                            stepViewModel.onNeighborhoodChange(neighborhood = null)
                            isRegisteringNewPreferredNeighborhood = true
                        },
                        onEditPreferredNeighborhood = { neighborhood ->
                            stepViewModel.onNeighborhoodChange(neighborhood = neighborhood)
                            neighborhoodFormBeforeEdit = neighborhood
                        },
                        onDeletePreferredNeighborhood = { neighborhood ->
                            val assistancePreferredNeighborhoods =
                                form.preferredNeighborhoods.toPersistentList()

                            val newPreferredNeighborhoods =
                                assistancePreferredNeighborhoods.remove(neighborhood)

                            parentViewModel.onAssistanceFormChange(
                                form.copy(preferredNeighborhoods = newPreferredNeighborhoods)
                            )
                        },
                        formErrors = formErrors.errors,
                        scrollToFieldFlow = scrollToFieldFlow,
                        onNext = {
                            focusManager.clearFocus()

                            stepViewModel.validateAssistanceStep1(assistanceForm = form)
                        },
                        modifier = Modifier
                            .padding(innerPadding)
                    )
                }
            }
        }

        if (neighborhoodForm != null && loadedDraftForm != null) {
            AssistancePreferredNeighborhoodRegistrationDialog(
                neighborhood = neighborhoodForm!!,
                onNeighborhoodChange = stepViewModel::onNeighborhoodChange,
                pastNeighborhoods = pastNeighborhoods,
                error = neighborhoodFormError,
                isEditing = neighborhoodForm in loadedDraftForm.preferredNeighborhoods,
                onSave = stepViewModel::savePreferredNeighborhood,
                onCancel = {
                    stepViewModel.clearPreferredNeighborhoodErrors()
                    stepViewModel.onNeighborhoodChange(neighborhood = null)
                },
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
