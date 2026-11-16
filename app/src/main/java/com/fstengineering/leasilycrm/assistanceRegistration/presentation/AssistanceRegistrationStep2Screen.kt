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
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables.AssistanceRegistrationFormSectionStep2
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.composables.AssistanceRegistrationTopAppBar
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceDraftFormUiState
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceRegistrationStep2ListState
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels.AssistanceRegistrationStep2ViewModel
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistanceRegistrationStep2Screen(
    parentViewModel: AssistanceRegistrationViewModel,
    stepViewModel: AssistanceRegistrationStep2ViewModel,
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
    val loadedDraftForm = draftForm as? AssistanceDraftFormUiState.Loaded

    val searchInput by stepViewModel.searchInput.collectAsStateWithLifecycle()
    val selectableImoveis by stepViewModel.imoveis.collectAsStateWithLifecycle()

    var isAddingSentImovel by rememberSaveable { mutableStateOf(false) }
    var isSearchingImovelToSend by rememberSaveable { mutableStateOf(false) }
    var mustShowConfirmLeaveRegistrationDialog by rememberSaveable { mutableStateOf(false) }

    val listStatus = remember(
        isAddingSentImovel,
        selectableImoveis,
    ) {
        when {
            !isAddingSentImovel -> AssistanceRegistrationStep2ListState.ShowingSelectedSentImoveis
            selectableImoveis.isEmpty() -> AssistanceRegistrationStep2ListState.Empty
            else -> AssistanceRegistrationStep2ListState.AddingSentImoveis
        }
    }

    LaunchedEffect(Unit) {
        stepViewModel.init()

        launch {
            parentViewModel.uiEvents.collect { uiEvent ->
                when (uiEvent) {
                    is AssistanceRegistrationViewModel.UiEvent.BottomSheetError -> {
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
                    is AssistanceRegistrationStep2ViewModel.UiEvent.BottomSheetError -> {
                        val errorData = uiEvent.error.toBottomSheetData(
                            DefaultErrorBottomSheetDataPropertiesStrategy,
                        )
                        onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
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
        when {
            isSearchingImovelToSend -> isSearchingImovelToSend = false
            isAddingSentImovel -> isAddingSentImovel = false
            else -> mustShowConfirmLeaveRegistrationDialog = true
        }
    }

    Scaffold(
        topBar = {
            Column {
                if (loadedDraftForm == null) return@Scaffold

                AssistanceRegistrationTopAppBar(
                    clienteName = loadedDraftForm.clienteName,
                    isAddingSentImovel = isAddingSentImovel,
                    mustShowSearchImovelAction = isAddingSentImovel
                            && selectableImoveis.isNotEmpty()
                            && !isSearchingImovelToSend,
                    isSearchingImovelToSend = isSearchingImovelToSend,
                    onSearchImovelToSend = { isSearchingImovelToSend = true },
                    searchInput = searchInput,
                    onSearchInputChange = stepViewModel::onSearchInputChange,
                    onBack = {
                        when {
                            isSearchingImovelToSend -> {
                                isSearchingImovelToSend = false
                                stepViewModel.onSearchInputChange(searchInput = null)
                            }

                            isAddingSentImovel -> isAddingSentImovel = false
                            else -> mustShowConfirmLeaveRegistrationDialog = true
                        }
                    },
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
                    AssistanceRegistrationFormSectionStep2(
                        listStatus = listStatus,
                        assistanceForm = form,
                        selectableImoveis = selectableImoveis,
                        onAddSentImovel = { isAddingSentImovel = true },
                        isSearchingImovelToSend = isSearchingImovelToSend,
                        onBack = onBack,
                        onCancelImovelSelection = { isAddingSentImovel = false },
                        onNext = {
                            focusManager.clearFocus()
                            onNext()
                        },
                        onSelectImoveis = { selectedSentImoveis ->
                            val newForm = form.copy(sentImoveis = selectedSentImoveis)
                            parentViewModel.onAssistanceFormChange(newForm)

                            isAddingSentImovel = false
                        },
                        modifier = Modifier
                            .padding(innerPadding)
                    )
                }
            }
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
