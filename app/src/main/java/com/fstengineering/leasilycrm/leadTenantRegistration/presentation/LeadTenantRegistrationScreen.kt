package com.fstengineering.leasilycrm.leadTenantRegistration.presentation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.leadTenantRegistration.presentation.composables.LeadTenantRegistrationFormSection
import com.fstengineering.leasilycrm.leadTenantRegistration.presentation.composables.LeadTenantRegistrationTopAppBar
import com.fstengineering.leasilycrm.leadTenantRegistration.presentation.models.LeadTenantDraftFormUiState
import com.fstengineering.leasilycrm.leadTenantRegistration.presentation.models.LeadTenantField
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadTenantRegistrationScreen(
    leadTenantId: Long?,
    viewModel: LeadTenantRegistrationViewModel,
    onBack: () -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val surfaceColor = MaterialTheme.colorScheme.surface

    val draftForm by viewModel.draftFormUiState.collectAsStateWithLifecycle()
    val formErrors by viewModel.formErrors.collectAsStateWithLifecycle()

    val pastTags by viewModel.pastTags.collectAsStateWithLifecycle()
    val pastCities by viewModel.pastCities.collectAsStateWithLifecycle()
    val pastContactOrigins by viewModel.pastContactOrigins.collectAsStateWithLifecycle()

    val scrollToFieldChannel = remember { Channel<LeadTenantField>() }
    val scrollToFieldFlow = remember(scrollToFieldChannel) { scrollToFieldChannel.receiveAsFlow() }

    var mustShowConfirmLeaveRegistrationDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init(leadTenantId = leadTenantId)

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is LeadTenantRegistrationViewModel.UiEvent.BottomSheetError -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is LeadTenantRegistrationViewModel.UiEvent.ScrollToField -> {
                    scrollToFieldChannel.trySend(uiEvent.field)
                }

                is LeadTenantRegistrationViewModel.UiEvent.LeadCreateSuccess -> {
                    onBack()

                    onUiEvent(UiEvent.ShowSnackBar(R.string.create_lead_success_message))
                }

                is LeadTenantRegistrationViewModel.UiEvent.LeadTenantEditSuccess -> {
                    onBack()

                    onUiEvent(UiEvent.ShowSnackBar(uiEvent.messageId))
                }

                is LeadTenantRegistrationViewModel.UiEvent.Exit -> {
                    if (uiEvent.needConfirmation) mustShowConfirmLeaveRegistrationDialog = true
                    else {
                        mustShowConfirmLeaveRegistrationDialog = false
                        onBack()
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
        viewModel.onExit()
    }

    Scaffold(
        topBar = {
            Column {
                val loadedForm = (draftForm as? LeadTenantDraftFormUiState.Loaded)

                LeadTenantRegistrationTopAppBar(
                    isEditing = leadTenantId != null,
                    isLead = loadedForm?.isLead == true,
                    onBack = viewModel::onExit,
                    onSaveLeadTenant = viewModel::saveLeadTenant,
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
            is LeadTenantDraftFormUiState.Loading -> {
                FullScreenLoadingIndicator(innerPadding)
            }

            is LeadTenantDraftFormUiState.Loaded -> {
                LeadTenantRegistrationFormSection(
                    leadTenantForm = draftForm as LeadTenantDraftFormUiState.Loaded,
                    onLeadTenantFormChange = viewModel::onLeadTenantFormChange,
                    pastTags = pastTags,
                    pastCities = pastCities,
                    pastContactOrigins = pastContactOrigins,
                    formErrors = formErrors.errors,
                    onLastTextFieldDone = { focusManager.clearFocus() },
                    scrollToFieldFlow = scrollToFieldFlow,
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp)
                )
            }
        }

        if (mustShowConfirmLeaveRegistrationDialog) {
            ConfirmLeaveRegistrationDialog(
                isEditing = leadTenantId != null,
                onConfirmLeave = {
                    mustShowConfirmLeaveRegistrationDialog = false
                    onBack()
                },
                onCancel = { mustShowConfirmLeaveRegistrationDialog = false }
            )
        }
    }
}
