package com.fstengineering.leasilycrm.atendimentoRegistration.presentation

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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.composables.AtendimentoPreferredNeighborhoodRegistrationDialog
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.composables.AtendimentoRegistrationFormSectionStep1
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.composables.AtendimentoRegistrationTopAppBar
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.AtendimentoDraftFormUiState
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.fields.AtendimentoStep1Field
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.viewModels.AtendimentoRegistrationStep1ViewModel
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.collectWithLifecycle
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtendimentoRegistrationStep1Screen(
    parentViewModel: AtendimentoRegistrationViewModel,
    stepViewModel: AtendimentoRegistrationStep1ViewModel,
    onLeaveRegistration: () -> Unit,
    onNext: () -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val surfaceColor = MaterialTheme.colorScheme.surface

    val draftForm by parentViewModel.draftFormUiState.collectAsStateWithLifecycle()
    val loadedDraftForm = draftForm as? AtendimentoDraftFormUiState.Loaded
    val formErrors by stepViewModel.formErrors.collectAsStateWithLifecycle()

    val neighborhoodForm by stepViewModel.neighborhoodForm.collectAsStateWithLifecycle()
    var neighborhoodFormBeforeEdit by rememberSaveable { mutableStateOf<String?>(null) }

    val neighborhoodFormError by stepViewModel.neighborhoodFormError.collectAsStateWithLifecycle()
    val pastNeighborhoods by parentViewModel.pastNeighborhoods.collectAsStateWithLifecycle()

    val scrollToFieldChannel = remember { Channel<AtendimentoStep1Field>() }
    val scrollToFieldFlow = remember(scrollToFieldChannel) { scrollToFieldChannel.receiveAsFlow() }

    var isRegisteringNewPreferredNeighborhood by rememberSaveable { mutableStateOf(false) }
    var mustShowConfirmLeaveRegistrationDialog by rememberSaveable { mutableStateOf(false) }

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        parentViewModel.init()

        launch {
            parentViewModel.uiEvents.collectWithLifecycle(lifecycle) { uiEvent ->
                when (uiEvent) {
                    is AtendimentoRegistrationViewModel.UiEvent.ShowErrorBottomSheet -> {
                        val errorData = uiEvent.error.toBottomSheetData(
                            DefaultErrorBottomSheetDataPropertiesStrategy,
                        )
                        onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                    }

                    is AtendimentoRegistrationViewModel.UiEvent.Exit -> {
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
            stepViewModel.uiEvents.collectWithLifecycle(lifecycle) { uiEvent ->
                when (uiEvent) {
                    is AtendimentoRegistrationStep1ViewModel.UiEvent.ScrollToField -> {
                        scrollToFieldChannel.trySend(uiEvent.field)
                    }

                    is AtendimentoRegistrationStep1ViewModel.UiEvent.ValidateSuccess -> {
                        onNext()
                    }

                    is AtendimentoRegistrationStep1ViewModel.UiEvent.AtendimentoSearchedNeighborhoodSaveSuccess -> {
                        if (neighborhoodForm == null || loadedDraftForm == null) return@collectWithLifecycle

                        val atendimentoSearchedNeighborhoods =
                            loadedDraftForm.preferredNeighborhoods.toPersistentList()

                        val newPreferredNeighborhoods =
                            if (isRegisteringNewPreferredNeighborhood) {
                                atendimentoSearchedNeighborhoods.add(neighborhoodForm!!)
                            } else {
                                atendimentoSearchedNeighborhoods.mutate {
                                    val i = atendimentoSearchedNeighborhoods
                                        .indexOf(neighborhoodFormBeforeEdit!!)
                                    it[i] = neighborhoodForm!!
                                }
                            }

                        val newForm =
                            loadedDraftForm.copy(preferredNeighborhoods = newPreferredNeighborhoods)
                        parentViewModel.onAtendimentoFormChange(newForm)

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
                AtendimentoRegistrationTopAppBar(
                    clienteName = loadedDraftForm.clienteName,
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
                is AtendimentoDraftFormUiState.Loading -> {
                    FullScreenLoadingIndicator(innerPadding)
                }

                is AtendimentoDraftFormUiState.Loaded -> {
                    AtendimentoRegistrationFormSectionStep1(
                        atendimentoForm = form,
                        onAtendimentoFormChange = { newForm ->
                            parentViewModel.onAtendimentoFormChange(newForm)
                            stepViewModel.onAtendimentoFormChange(newForm, oldForm = form)
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
                            val atendimentoPreferredNeighborhoods =
                                form.preferredNeighborhoods.toPersistentList()

                            val newPreferredNeighborhoods =
                                atendimentoPreferredNeighborhoods.remove(neighborhood)

                            parentViewModel.onAtendimentoFormChange(
                                form.copy(preferredNeighborhoods = newPreferredNeighborhoods)
                            )
                        },
                        formErrors = formErrors.errors,
                        scrollToFieldFlow = scrollToFieldFlow,
                        onNext = {
                            focusManager.clearFocus()

                            stepViewModel.validateAtendimentoStep1(atendimentoForm = form)
                        },
                        modifier = Modifier
                            .padding(innerPadding)
                    )
                }
            }
        }

        if (neighborhoodForm != null && loadedDraftForm != null) {
            AtendimentoPreferredNeighborhoodRegistrationDialog(
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
