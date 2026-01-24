package com.fstengineering.leasilycrm.assistanceRegistration

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.assistanceRegistration.AssistanceRegistrationViewModel.UiEvents.Error
import com.fstengineering.leasilycrm.assistanceRegistration.AssistanceRegistrationViewModel.UiEvents.Exit
import com.fstengineering.leasilycrm.assistanceRegistration.composables.AssistanceRegistrationFormSectionStep1
import com.fstengineering.leasilycrm.assistanceRegistration.composables.AssistanceRegistrationTopAppBar
import com.fstengineering.leasilycrm.assistanceRegistration.composables.AssistanceSearchedNeighborhoodRegistrationDialog
import com.fstengineering.leasilycrm.assistanceRegistration.models.fields.AssistanceNeighborhoodField
import com.fstengineering.leasilycrm.assistanceRegistration.models.fields.AssistanceStep1Field
import com.fstengineering.leasilycrm.assistanceRegistration.viewModels.AssistanceRegistrationStep1ViewModel
import com.fstengineering.leasilycrm.assistanceRegistration.viewModels.AssistanceRegistrationStep1ViewModel.UiEvents.AssistanceSearchedNeighborhoodSaveSuccess
import com.fstengineering.leasilycrm.assistanceRegistration.viewModels.AssistanceRegistrationStep1ViewModel.UiEvents.ScrollToField
import com.fstengineering.leasilycrm.assistanceRegistration.viewModels.AssistanceRegistrationStep1ViewModel.UiEvents.ValidateSuccess
import com.fstengineering.leasilycrm.core.domain.models.AssistanceSearchedNeighborhood
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
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

    val assistanceForm by parentViewModel.assistanceForm.collectAsStateWithLifecycle()
    val formErrors by stepViewModel.formErrors.collectAsStateWithLifecycle()

    val searchedNeighborhoodForm by stepViewModel.searchedNeighborhoodForm.collectAsStateWithLifecycle()
    var searchedNeighborhoodFormBeforeEdit by rememberSaveable {
        mutableStateOf<AssistanceSearchedNeighborhood?>(null)
    }

    val searchedNeighborhoodErrors by stepViewModel.searchedNeighborhoodErrors.collectAsStateWithLifecycle()
    val pastNeighborhoods by parentViewModel.pastNeighborhoods.collectAsStateWithLifecycle()

    val seaProximityTypes by parentViewModel.seaProximityTypes.collectAsStateWithLifecycle()
    val assistancePropertyCharacteristics by parentViewModel.assistancePropertyCharacteristics
        .collectAsStateWithLifecycle()

    val scrollToFieldChannel = remember { Channel<AssistanceStep1Field>() }
    val scrollToFieldFlow = remember(scrollToFieldChannel) { scrollToFieldChannel.receiveAsFlow() }

    val isLoading by parentViewModel.isLoading.collectAsStateWithLifecycle()

    var isRegisteringNewSearchedNeighborhood by rememberSaveable { mutableStateOf(false) }
    var mustShowConfirmLeaveRegistrationDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        parentViewModel.init(
            assistanceId = assistanceId,
            leadTenantId = leadTenantId,
        )

        launch {
            parentViewModel.uiEvents.collect { uiEvent ->
                when (uiEvent) {
                    is Error -> {
                        val errorData = uiEvent.error.toBottomSheetData(
                            DefaultErrorBottomSheetDataPropertiesStrategy,
                        )
                        onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                    }

                    is Exit -> {
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
                    is ScrollToField -> {
                        scrollToFieldChannel.trySend(uiEvent.field)
                    }

                    is ValidateSuccess -> {
                        onNext()
                    }

                    is AssistanceSearchedNeighborhoodSaveSuccess -> {
                        if (searchedNeighborhoodForm == null) return@collect

                        val assistanceSearchedNeighborhoods =
                            assistanceForm.searchedNeighborhoods.toPersistentList()

                        val newAssistanceSearchedNeighborhoods =
                            if (isRegisteringNewSearchedNeighborhood) {
                                assistanceSearchedNeighborhoods.add(searchedNeighborhoodForm!!)
                            } else {
                                assistanceSearchedNeighborhoods.mutate {
                                    val i = assistanceSearchedNeighborhoods
                                        .indexOf(searchedNeighborhoodFormBeforeEdit!!)
                                    it[i] = searchedNeighborhoodForm!!
                                }
                            }

                        parentViewModel.onAssistanceFormChange(
                            assistanceForm.copy(searchedNeighborhoods = newAssistanceSearchedNeighborhoods)
                        )

                        stepViewModel.onSearchedNeighborhoodFormChange(form = null)
                        isRegisteringNewSearchedNeighborhood = false
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
                AssistanceRegistrationTopAppBar(
                    leadTenantName = assistanceForm.tenant?.name ?: "",
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
        AnimatedContent(
            targetState = isLoading,
        ) { isLoading ->
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
                AssistanceRegistrationFormSectionStep1(
                    assistanceForm = assistanceForm,
                    onAssistanceFormChange = { newAssistance ->
                        parentViewModel.onAssistanceFormChange(newAssistance)
                        stepViewModel.onAssistanceFormChange(newAssistance, assistanceForm)
                    },
                    onAddSearchedNeighborhood = {
                        stepViewModel.onSearchedNeighborhoodFormChange(form = AssistanceSearchedNeighborhood())
                        isRegisteringNewSearchedNeighborhood = true
                    },
                    onEditSearchedNeighborhood = { searchedNeighborhood ->
                        stepViewModel.onSearchedNeighborhoodFormChange(form = searchedNeighborhood)
                        searchedNeighborhoodFormBeforeEdit = searchedNeighborhood
                    },
                    onDeleteSearchedNeighborhood = { searchedNeighborhood ->
                        val assistanceSearchedNeighborhoods =
                            assistanceForm.searchedNeighborhoods.toPersistentList()

                        val newAssistanceSearchedNeighborhoods =
                            if (searchedNeighborhood.id == null) {
                                assistanceSearchedNeighborhoods.remove(searchedNeighborhood)
                            } else {
                                assistanceSearchedNeighborhoods.mutate {
                                    val i = assistanceSearchedNeighborhoods
                                        .indexOf(searchedNeighborhood)
                                    it[i] = searchedNeighborhood.copy(isDeleted = true)
                                }
                            }

                        parentViewModel.onAssistanceFormChange(
                            assistanceForm.copy(searchedNeighborhoods = newAssistanceSearchedNeighborhoods)
                        )
                    },
                    seaProximityTypes = seaProximityTypes,
                    assistancePropertyCharacteristics = assistancePropertyCharacteristics,
                    formErrors = formErrors.errors,
                    scrollToFieldFlow = scrollToFieldFlow,
                    onNext = {
                        focusManager.clearFocus()

                        stepViewModel.validateAssistanceStep1(
                            assistanceForm = assistanceForm,
                        )
                    },
                    modifier = Modifier
                        .padding(innerPadding)
                )
            }
        }
        if (searchedNeighborhoodForm != null) {
            AssistanceSearchedNeighborhoodRegistrationDialog(
                searchedNeighborhoodForm = searchedNeighborhoodForm!!,
                onSearchedNeighborhoodFormChange = stepViewModel::onSearchedNeighborhoodFormChange,
                pastNeighborhoods = pastNeighborhoods,
                error = searchedNeighborhoodErrors.errors[AssistanceNeighborhoodField.Neighborhood],
                isEditing = searchedNeighborhoodForm!!.id != null,
                onSave = stepViewModel::saveSearchedNeighborhood,
                onCancel = {
                    stepViewModel.clearSearchedNeighborhoodErrors()
                    stepViewModel.onSearchedNeighborhoodFormChange(form = null)
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
