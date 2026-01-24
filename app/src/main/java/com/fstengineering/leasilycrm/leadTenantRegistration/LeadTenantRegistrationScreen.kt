package com.fstengineering.leasilycrm.leadTenantRegistration

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
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
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.leadTenantRegistration.LeadTenantRegistrationViewModel.UiEvents.Error
import com.fstengineering.leasilycrm.leadTenantRegistration.LeadTenantRegistrationViewModel.UiEvents.Exit
import com.fstengineering.leasilycrm.leadTenantRegistration.LeadTenantRegistrationViewModel.UiEvents.LeadCreateSuccess
import com.fstengineering.leasilycrm.leadTenantRegistration.LeadTenantRegistrationViewModel.UiEvents.LeadTenantEditSuccess
import com.fstengineering.leasilycrm.leadTenantRegistration.LeadTenantRegistrationViewModel.UiEvents.ScrollToField
import com.fstengineering.leasilycrm.leadTenantRegistration.composables.LeadTenantRegistrationFormSection
import com.fstengineering.leasilycrm.leadTenantRegistration.composables.LeadTenantRegistrationTopAppBar
import com.fstengineering.leasilycrm.leadTenantRegistration.models.LeadTenantField
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

    val leadTenantForm by viewModel.leadTenantForm.collectAsStateWithLifecycle()
    val formErrors by viewModel.formErrors.collectAsStateWithLifecycle()

    val pastTags by viewModel.pastTags.collectAsStateWithLifecycle()
    val pastCities by viewModel.pastCities.collectAsStateWithLifecycle()

    // TODO: Excluir tabela e pegar nomes direto de Tenants
    val pastContactOriginTypes by viewModel.pastContactOriginTypes.collectAsStateWithLifecycle()

    val scrollToFieldChannel = remember { Channel<LeadTenantField>() }
    val scrollToFieldFlow = remember(scrollToFieldChannel) { scrollToFieldChannel.receiveAsFlow() }

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var mustShowConfirmLeaveRegistrationDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init(leadTenantId = leadTenantId)

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is Error -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is ScrollToField -> {
                    scrollToFieldChannel.trySend(uiEvent.field)
                }

                is LeadCreateSuccess -> {
                    onBack()

                    onUiEvent(UiEvent.ShowSnackBar(R.string.create_lead_success_message))
                }

                is LeadTenantEditSuccess -> {
                    onBack()

                    onUiEvent(UiEvent.ShowSnackBar(uiEvent.messageId))
                }

                is Exit -> {
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
                LeadTenantRegistrationTopAppBar(
                    isEditing = leadTenantId != null,
                    isLead = leadTenantForm.isLead,
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
            LeadTenantRegistrationFormSection(
                leadTenantForm = leadTenantForm,
                onLeadTenantFormChange = viewModel::onLeadTenantFormChange,
                pastTags = pastTags,
                pastCities = pastCities,
                formErrors = formErrors.errors,
                onLastTextFieldDone = { focusManager.clearFocus() },
                scrollToFieldFlow = scrollToFieldFlow,
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
            )
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
