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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.composables.AtendimentoRegistrationFormSectionStep3
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.composables.AtendimentoRegistrationTopAppBar
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.composables.ConfirmDeleteInteracaoDialog
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.composables.InteracaoRegistrationBottomSheet
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.AtendimentoDraftFormUiState
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.InteracaoDraftFormUiState
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.viewModels.AtendimentoRegistrationStep3ViewModel
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.InteracaoDetailsBottomSheet
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.utils.collectWithLifecycle
import com.fstengineering.leasilycrm.core.presentation.utils.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtendimentoRegistrationStep3Screen(
    parentViewModel: AtendimentoRegistrationViewModel,
    stepViewModel: AtendimentoRegistrationStep3ViewModel,
    onLeaveRegistration: () -> Unit,
    onBack: () -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val surfaceColor = MaterialTheme.colorScheme.surface

    val draftForm by parentViewModel.draftFormUiState.collectAsStateWithLifecycle()
    val loadedDraftForm = draftForm as? AtendimentoDraftFormUiState.Loaded

    val interacaoForm by stepViewModel.interacaoForm.collectAsStateWithLifecycle()
    val loadedInteracaoForm = interacaoForm as? InteracaoDraftFormUiState.Loaded

    val formErrors by stepViewModel.formErrors.collectAsStateWithLifecycle()

    var interacaoToView by rememberSaveable {
        mutableStateOf<InteracaoDraftFormUiState.Loaded?>(null)
    }
    var interacaoToDelete by rememberSaveable {
        mutableStateOf<InteracaoDraftFormUiState.Loaded?>(null)
    }

    var mustShowConfirmLeaveRegistrationDialog by rememberSaveable { mutableStateOf(false) }

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        launch {
            parentViewModel.uiEvents.collectWithLifecycle(lifecycle) { uiEvent ->
                when (uiEvent) {
                    is AtendimentoRegistrationViewModel.UiEvent.ShowErrorBottomSheet -> {
                        val errorData = uiEvent.error.toBottomSheetData(
                            DefaultErrorBottomSheetDataPropertiesStrategy,
                        )
                        onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                    }

                    is AtendimentoRegistrationViewModel.UiEvent.CloseScreenWithSnackbar -> {
                        onLeaveRegistration()
                        onUiEvent(UiEvent.ShowSnackBar(uiEvent.messageId))
                    }

                    is AtendimentoRegistrationViewModel.UiEvent.Exit -> {
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
            stepViewModel.uiEvents.collectWithLifecycle(lifecycle) { uiEvent ->
                when (uiEvent) {
                    is AtendimentoRegistrationStep3ViewModel.UiEvent.FinishInteracaoCreate -> {
                        if (loadedInteracaoForm == null) return@collectWithLifecycle
                        if (loadedDraftForm?.interacoes == null) return@collectWithLifecycle

                        val newInteracoes = loadedDraftForm.interacoes
                            .toPersistentList()
                            .add(loadedInteracaoForm)

                        parentViewModel.onAtendimentoFormChange(loadedDraftForm.copy(interacoes = newInteracoes))
                        stepViewModel.clearInteracaoForm()

                        onUiEvent(UiEvent.ShowSnackBar(R.string.create_assistance_interaction_success_message))
                    }

                    is AtendimentoRegistrationStep3ViewModel.UiEvent.FinishInteracaoEdit -> {
                        if (loadedInteracaoForm == null) return@collectWithLifecycle
                        if (loadedDraftForm?.interacoes == null) return@collectWithLifecycle

                        val newInteracoes = loadedDraftForm.interacoes
                            .toPersistentList()
                            .mutate { list ->
                                val i = list.indexOf(stepViewModel.interacaoFormBeforeEdit)
                                list[i] = loadedInteracaoForm
                            }

                        parentViewModel.onAtendimentoFormChange(loadedDraftForm.copy(interacoes = newInteracoes))
                        stepViewModel.clearInteracaoForm()

                        onUiEvent(UiEvent.ShowSnackBar(uiEvent.messageId))
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
                if (loadedDraftForm == null) return@Scaffold

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
                    AtendimentoRegistrationFormSectionStep3(
                        atendimentoForm = form,
                        onAddInteracao = stepViewModel::onOpenInteracaoForm,
                        onViewInteracaoDetails = { interacaoToView = it },
                        onEditInteracao = {
                            stepViewModel.onInteracaoFormChange(newForm = it)
                            stepViewModel.onEditInteracao(interacao = it)
                        },
                        onDeleteInteracao = { interacaoToDelete = it },
                        onBack = onBack,
                        onSave = {
                            focusManager.clearFocus()
                            parentViewModel.saveAtendimento()
                        },
                        modifier = Modifier
                            .padding(innerPadding)
                    )
                }
            }
        }

        if (loadedInteracaoForm != null && loadedDraftForm != null) {
            InteracaoRegistrationBottomSheet(
                sheetState = sheetState,
                interacaoForm = loadedInteracaoForm,
                onInteracaoFormChange = stepViewModel::onInteracaoFormChange,
                formErrors = formErrors.errors,
                onDismissRequest = stepViewModel::clearInteracaoForm,
                onSave = { stepViewModel.validateInteracaoForm(loadedDraftForm) }
            )
        }

        if (interacaoToView != null) {
            InteracaoDetailsBottomSheet(
                sheetState = sheetState,
                interacao = interacaoToView!!.toBottomSheetUiState(),
                onDismissRequest = { interacaoToView = null },
            )
        }

        if (interacaoToDelete != null && loadedDraftForm != null) {
            ConfirmDeleteInteracaoDialog(
                onConfirmDelete = {
                    val newInteracoes = loadedDraftForm.interacoes
                        .toPersistentList()
                        .mutate { interacoes -> interacoes.remove(interacaoToDelete) }

                    parentViewModel.onAtendimentoFormChange(loadedDraftForm.copy(interacoes = newInteracoes))
                    interacaoToDelete = null
                },
                onCancel = { interacaoToDelete = null },
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
