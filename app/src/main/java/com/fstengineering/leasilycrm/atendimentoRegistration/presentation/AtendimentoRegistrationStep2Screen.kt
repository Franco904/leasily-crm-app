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
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.composables.AtendimentoRegistrationFormSectionStep2
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.composables.AtendimentoRegistrationTopAppBar
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.AtendimentoDraftFormUiState
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.AtendimentoRegistrationStep2ListState
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.viewModels.AtendimentoRegistrationStep2ViewModel
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.utils.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.collectWithLifecycle
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtendimentoRegistrationStep2Screen(
    parentViewModel: AtendimentoRegistrationViewModel,
    stepViewModel: AtendimentoRegistrationStep2ViewModel,
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
    val loadedDraftForm = draftForm as? AtendimentoDraftFormUiState.Loaded

    val searchInput by stepViewModel.searchInput.collectAsStateWithLifecycle()
    val selectableImoveis by stepViewModel.imoveis.collectAsStateWithLifecycle()

    var isAddingImovelEnviado by rememberSaveable { mutableStateOf(false) }
    var isSearchingImovelToSend by rememberSaveable { mutableStateOf(false) }
    var mustShowConfirmLeaveRegistrationDialog by rememberSaveable { mutableStateOf(false) }

    val listStatus = remember(
        isAddingImovelEnviado,
        selectableImoveis,
    ) {
        when {
            !isAddingImovelEnviado -> AtendimentoRegistrationStep2ListState.ShowingSelectedImoveisEnviados
            selectableImoveis.isEmpty() -> AtendimentoRegistrationStep2ListState.Empty
            else -> AtendimentoRegistrationStep2ListState.AddingImoveisEnviados
        }
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        stepViewModel.init()

        launch {
            parentViewModel.uiEvents.collectWithLifecycle(lifecycle) { uiEvent ->
                when (uiEvent) {
                    is AtendimentoRegistrationViewModel.UiEvent.ShowErrorBottomSheet -> {
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
            stepViewModel.uiEvents.collectWithLifecycle(lifecycle) { uiEvent ->
                when (uiEvent) {
                    is AtendimentoRegistrationStep2ViewModel.UiEvent.ShowErrorBottomSheet -> {
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
            isAddingImovelEnviado -> isAddingImovelEnviado = false
            else -> mustShowConfirmLeaveRegistrationDialog = true
        }
    }

    Scaffold(
        topBar = {
            Column {
                if (loadedDraftForm == null) return@Scaffold

                AtendimentoRegistrationTopAppBar(
                    clienteName = loadedDraftForm.clienteName,
                    isAddingImovelEnviado = isAddingImovelEnviado,
                    mustShowSearchImovelAction = isAddingImovelEnviado
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

                            isAddingImovelEnviado -> isAddingImovelEnviado = false
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
                is AtendimentoDraftFormUiState.Loading -> {
                    FullScreenLoadingIndicator(innerPadding)
                }

                is AtendimentoDraftFormUiState.Loaded -> {
                    AtendimentoRegistrationFormSectionStep2(
                        listStatus = listStatus,
                        atendimentoForm = form,
                        selectableImoveis = selectableImoveis,
                        onAddImovelEnviado = { isAddingImovelEnviado = true },
                        isSearchingImovelToSend = isSearchingImovelToSend,
                        onBack = onBack,
                        onCancelImovelSelection = { isAddingImovelEnviado = false },
                        onNext = {
                            focusManager.clearFocus()
                            onNext()
                        },
                        onSelectImoveis = { selectedImoveisEnviados ->
                            val newForm = form.copy(imoveisEnviados = selectedImoveisEnviados)
                            parentViewModel.onAtendimentoFormChange(newForm)

                            isAddingImovelEnviado = false
                        },
                        modifier = Modifier
                            .padding(innerPadding)
                    )
                }
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
