package com.fstengineering.leasilycrm.leadTenantRegistration

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.enums.TenantStatus
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.AppComboBottomsheet
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.leadTenantRegistration.LeadTenantRegistrationViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.leadTenantRegistration.LeadTenantRegistrationViewModel.UiEvents.LeadCreateSuccess
import com.fstengineering.leasilycrm.leadTenantRegistration.LeadTenantRegistrationViewModel.UiEvents.LeadTenantEditSuccess
import com.fstengineering.leasilycrm.leadTenantRegistration.LeadTenantRegistrationViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.leadTenantRegistration.LeadTenantRegistrationViewModel.UiEvents.ScrollToField
import com.fstengineering.leasilycrm.leadTenantRegistration.composables.LeadTenantRegistrationFormSection
import com.fstengineering.leasilycrm.leadTenantRegistration.composables.LeadTenantRegistrationTopAppBar
import com.fstengineering.leasilycrm.leadTenantRegistration.models.LeadTenantComboType
import com.fstengineering.leasilycrm.leadTenantRegistration.models.LeadTenantField
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadTenantRegistrationScreen(
    leadTenantId: Long?,
    viewModel: LeadTenantRegistrationViewModel,
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

    val leadTenantForm by viewModel.leadTenantForm.collectAsStateWithLifecycle()
    val formErrors by viewModel.formErrors.collectAsStateWithLifecycle()

    val tenantSegments by viewModel.tenantSegments.collectAsStateWithLifecycle()
    val pastCities by viewModel.pastCities.collectAsStateWithLifecycle()
    val tenantContactOriginTypes by viewModel.tenantContactOriginTypes.collectAsStateWithLifecycle()

    val scrollToFieldFlow = Channel<LeadTenantField>()

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var mustShowConfirmLeaveRegistrationDialog by remember { mutableStateOf(false) }
    var showingComboBottomsheetType by remember { mutableStateOf<LeadTenantComboType?>(null) }

    LaunchedEffect(Unit) {
        viewModel.init(leadTenantId = leadTenantId)

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is DataSourceError -> {
                    showSnackbar(context.getString(uiEvent.messageId))
                }

                is RedirectToSignIn -> {
                    onSignOut()
                }

                is ScrollToField -> {
                    scrollToFieldFlow.send(uiEvent.field)
                }

                is LeadCreateSuccess -> {
                    onBack()

                    showSnackbar(context.getString(R.string.snackbar_create_lead_succeeded))
                }

                is LeadTenantEditSuccess -> {
                    onBack()

                    showSnackbar(context.getString(uiEvent.messageId))
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
            LeadTenantRegistrationTopAppBar(
                isEditing = leadTenantId != null,
                isLead = leadTenantForm.status == TenantStatus.Lead.code,
                onBack = { mustShowConfirmLeaveRegistrationDialog = true },
                onSaveLeadTenant = viewModel::saveLeadTenant,
            )
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
                pastCities = pastCities,
                formErrors = formErrors.errors,
                onCustomerSegmentFieldTap = {
                    showingComboBottomsheetType = LeadTenantComboType.TenantSegment
                },
                onTenantContactOriginFieldTap = {
                    showingComboBottomsheetType = LeadTenantComboType.TenantContactOriginType
                },
                onLastTextFieldDone = { focusManager.clearFocus() },
                scrollToFieldFlow = scrollToFieldFlow.receiveAsFlow(),
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
            )
        }
        if (showingComboBottomsheetType != null) {
            AppComboBottomsheet(
                sheetState = sheetState,
                options = when (showingComboBottomsheetType) {
                    LeadTenantComboType.TenantSegment -> {
                        tenantSegments.map { it.name ?: "" }.toPersistentList()
                    }

                    LeadTenantComboType.TenantContactOriginType -> {
                        tenantContactOriginTypes.map { it.name ?: "" }.toPersistentList()
                    }

                    else -> persistentListOf()
                },
                onOptionSelect = { optionName ->
                    when (showingComboBottomsheetType) {
                        LeadTenantComboType.TenantSegment -> {
                            val selectedCustomerSegment =
                                tenantSegments.find { it.name == optionName }

                            viewModel.onLeadTenantFormChange(
                                form = leadTenantForm.copy(customerSegment = selectedCustomerSegment)
                            )
                        }

                        LeadTenantComboType.TenantContactOriginType -> {
                            val selectedTenantContactOriginType =
                                tenantContactOriginTypes.find { it.name == optionName }

                            viewModel.onLeadTenantFormChange(
                                form = leadTenantForm.copy(tenantContactOriginType = selectedTenantContactOriginType)
                            )
                        }

                        else -> {}
                    }

                    focusManager.clearFocus()
                },
                onDismissRequest = {
                    showingComboBottomsheetType = null
                    focusManager.clearFocus()
                },
                emptyOptionsIcon = Icons.Default.BookmarkBorder,
                emptyOptionsText = stringResource(R.string.lead_tenant_registration_customer_segment_empty_text),
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
