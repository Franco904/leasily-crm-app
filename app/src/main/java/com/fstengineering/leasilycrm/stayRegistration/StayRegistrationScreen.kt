package com.fstengineering.leasilycrm.stayRegistration

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.models.toDecimalOrNull
import com.fstengineering.leasilycrm.core.domain.models.StayTransfer
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.PropertiesEmptySection
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.home.tenants.composables.TenantsEmptySection
import com.fstengineering.leasilycrm.stayRegistration.StayRegistrationViewModel.UiEvents.Error
import com.fstengineering.leasilycrm.stayRegistration.StayRegistrationViewModel.UiEvents.Exit
import com.fstengineering.leasilycrm.stayRegistration.StayRegistrationViewModel.UiEvents.ScrollToField
import com.fstengineering.leasilycrm.stayRegistration.StayRegistrationViewModel.UiEvents.StayCreateSuccess
import com.fstengineering.leasilycrm.stayRegistration.StayRegistrationViewModel.UiEvents.StayEditSuccess
import com.fstengineering.leasilycrm.stayRegistration.StayRegistrationViewModel.UiEvents.TransferCreateSuccess
import com.fstengineering.leasilycrm.stayRegistration.StayRegistrationViewModel.UiEvents.TransferEditSuccess
import com.fstengineering.leasilycrm.stayRegistration.composables.ConfirmDeleteStayTransferDialog
import com.fstengineering.leasilycrm.stayRegistration.composables.StayPropertySelectionList
import com.fstengineering.leasilycrm.stayRegistration.composables.StayRegistrationFormSection
import com.fstengineering.leasilycrm.stayRegistration.composables.StayRegistrationTopAppBar
import com.fstengineering.leasilycrm.stayRegistration.composables.StayTenantSelectionList
import com.fstengineering.leasilycrm.stayRegistration.composables.UnavailablePeriodRegistrationFormSection
import com.fstengineering.leasilycrm.stayRegistration.models.StayField
import com.fstengineering.leasilycrm.stayRegistration.models.StayRegistrationListState
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StayRegistrationScreen(
    stayId: Long?,
    propertyId: Long?,
    tenantId: Long?,
    isUnavailablePeriod: Boolean,
    viewModel: StayRegistrationViewModel,
    onBack: (success: Boolean) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!
    val focusManager = LocalFocusManager.current

    val surfaceColor = MaterialTheme.colorScheme.surface

    val stayForm by viewModel.stayForm.collectAsStateWithLifecycle()
    val stayFormErrors by viewModel.stayFormErrors.collectAsStateWithLifecycle()

    val transferForm by viewModel.transferForm.collectAsStateWithLifecycle()
    val transferFormErrors by viewModel.transferFormErrors.collectAsStateWithLifecycle()
    var transferToDelete by rememberSaveable { mutableStateOf<StayTransfer?>(null) }

    val propertySearchInput by viewModel.propertySearchInput.collectAsStateWithLifecycle()
    val properties by viewModel.properties.collectAsStateWithLifecycle()

    val tenantSearchInput by viewModel.tenantSearchInput.collectAsStateWithLifecycle()
    val tenants by viewModel.tenants.collectAsStateWithLifecycle()

    var isSearching by rememberSaveable { mutableStateOf(false) }

    val scrollToFieldChannel = remember { Channel<StayField>() }
    val scrollToFieldFlow = remember(scrollToFieldChannel) { scrollToFieldChannel.receiveAsFlow() }

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var listStatus by rememberSaveable { mutableStateOf(StayRegistrationListState.ShowingMainForm) }

    var mustShowConfirmLeaveRegistrationDialog by remember { mutableStateOf(false) }

    var totalPriceInput by rememberSaveable(stayForm.totalPrice) {
        mutableStateOf(stayForm.totalPrice?.toString() ?: "")
    }
    var commissionInput by rememberSaveable(stayForm.commission) {
        mutableStateOf(stayForm.commission?.toString() ?: "")
    }

    val openDocumentFile = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { contentUri ->
            if (contentUri == null) return@rememberLauncherForActivityResult

            context.contentResolver.takePersistableUriPermission(
                contentUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )

            viewModel.onStayFilePick(contentUriString = contentUri.toString())
        }
    )

    LaunchedEffect(Unit) {
        viewModel.init(
            stayId = stayId,
            propertyId = propertyId,
            tenantId = tenantId,
            isUnavailablePeriod = isUnavailablePeriod,
        )

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

                is StayCreateSuccess -> {
                    onBack(true)

                    val messageId =
                        if (isUnavailablePeriod) R.string.create_unavailable_period_success_message
                        else R.string.create_stay_success_message
                    onUiEvent(UiEvent.ShowSnackBar(messageId))
                }

                is StayEditSuccess -> {
                    onBack(true)

                    onUiEvent(UiEvent.ShowSnackBar(uiEvent.messageId))
                }

                is TransferCreateSuccess -> {
                    onUiEvent(UiEvent.ShowSnackBar(R.string.create_stay_transfer_success_message))
                }

                is TransferEditSuccess -> {
                    onUiEvent(UiEvent.ShowSnackBar(uiEvent.messageId))
                }

                is Exit -> {
                    if (uiEvent.needConfirmation) mustShowConfirmLeaveRegistrationDialog = true
                    else {
                        mustShowConfirmLeaveRegistrationDialog = false
                        onBack(false)
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
            isLoading -> Unit
            listStatus == StayRegistrationListState.ShowingMainForm -> {
                viewModel.onExit()
            }

            isSearching -> {
                isSearching = false
                viewModel.onPropertySearchInputChange(searchInput = null)
                viewModel.onTenantSearchInputChange(searchInput = null)
            }

            else -> listStatus = StayRegistrationListState.ShowingMainForm
        }
    }

    Scaffold(
        topBar = {
            val isSelectingProperty = listStatus == StayRegistrationListState.SelectingProperty
            val isSelectingTenant = listStatus == StayRegistrationListState.SelectingTenant

            StayRegistrationTopAppBar(
                isEditing = stayId != null,
                onSaveStay = {
                    viewModel.onStayFormChange(
                        stayForm.copy(
                            totalPrice = totalPriceInput.replace(',', '.').toDecimalOrNull(),
                            commission = commissionInput.replace(',', '.').toDecimalOrNull(),
                        )
                    )

                    viewModel.saveStay()
                },
                isUnavailablePeriod = isUnavailablePeriod,
                isSelectingProperty = isSelectingProperty,
                isSelectingTenant = isSelectingTenant,
                mustShowSearchAction = when {
                    isSearching -> false
                    isSelectingProperty -> properties.isNotEmpty()
                    isSelectingTenant -> tenants.isNotEmpty()
                    else -> false
                },
                isSearching = isSearching,
                onSearch = { isSearching = true },
                searchInput = when {
                    !isSearching -> null
                    isSelectingProperty -> propertySearchInput
                    isSelectingTenant -> tenantSearchInput
                    else -> null
                },
                onSearchInputChange = when {
                    isSelectingProperty -> viewModel::onPropertySearchInputChange
                    isSelectingTenant -> viewModel::onTenantSearchInputChange
                    else -> {
                        {}
                    }
                },
                onBack = {
                    when {
                        isLoading -> Unit
                        listStatus == StayRegistrationListState.ShowingMainForm -> {
                            viewModel.onExit()
                        }

                        isSearching -> {
                            isSearching = false
                            viewModel.onPropertySearchInputChange(searchInput = null)
                            viewModel.onTenantSearchInputChange(searchInput = null)
                        }

                        else -> listStatus = StayRegistrationListState.ShowingMainForm
                    }
                },
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
            AnimatedContent(
                targetState = listStatus,
            ) { status ->
                when (status) {
                    StayRegistrationListState.ShowingMainForm -> {
                        if (isUnavailablePeriod) {
                            UnavailablePeriodRegistrationFormSection(
                                stayForm = stayForm,
                                onStayFormChange = viewModel::onStayFormChange,
                                stayFormErrors = stayFormErrors.errors,
                                getPropertyAlreadySelectedDatesForStay = viewModel::getPropertyAlreadySelectedDatesForStay,
                                onSelectProperty = {
                                    listStatus = StayRegistrationListState.SelectingProperty
                                },
                                onPeriodDisabledByPropertyUnselected = {
                                    onUiEvent(UiEvent.ShowSnackBar(R.string.register_first_select_property_warning_message))
                                },
                                scrollToFieldFlow = scrollToFieldFlow,
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .padding(horizontal = 24.dp)
                            )
                        } else {
                            StayRegistrationFormSection(
                                stayForm = stayForm,
                                onStayFormChange = viewModel::onStayFormChange,
                                totalPriceInput = totalPriceInput,
                                onTotalPriceChange = { totalPriceInput = it },
                                commissionInput = commissionInput,
                                onCommissionChange = {
                                    commissionInput = it
                                    viewModel.clearCommissionError()
                                },
                                stayFormErrors = stayFormErrors.errors,
                                getPropertyAlreadySelectedDatesForStay = viewModel::getPropertyAlreadySelectedDatesForStay,
                                transferForm = transferForm,
                                onTransferFormChange = viewModel::onStayTransferFormChange,
                                transferFormErrors = transferFormErrors.errors,
                                onSelectProperty = { isEnabled ->
                                    if (isEnabled) {
                                        listStatus = StayRegistrationListState.SelectingProperty
                                    } else {
                                        onUiEvent(UiEvent.ShowSnackBar(R.string.register_stay_check_in_already_done_warning_message))
                                    }
                                },
                                onSelectTenant = { isEnabled ->
                                    if (isEnabled) {
                                        listStatus = StayRegistrationListState.SelectingTenant
                                    } else {
                                        onUiEvent(UiEvent.ShowSnackBar(R.string.register_stay_check_in_already_done_warning_message))
                                    }
                                },
                                onPeriodDisabledByPropertyUnselected = {
                                    onUiEvent(UiEvent.ShowSnackBar(R.string.register_first_select_property_warning_message))
                                },
                                onPeriodDisabledByCheckInDone = {
                                    onUiEvent(UiEvent.ShowSnackBar(R.string.register_stay_check_in_already_done_warning_message))
                                },
                                onLastTextFieldDone = { focusManager.clearFocus() },
                                onAddTransfer = {
                                    viewModel.onStayTransferFormChange(form = StayTransfer())
                                },
                                onEditTransfer = { transfer ->
                                    viewModel.onStayTransferFormChange(form = transfer)
                                    viewModel.onEditTransfer(transfer = transfer)
                                },
                                onDeleteTransfer = { transfer ->
                                    transferToDelete = transfer
                                },
                                onSaveTransfer = {
                                    viewModel.saveStayTransfer()
                                },
                                onDismissTransfer = {
                                    viewModel.onStayTransferFormChange(form = null)
                                    viewModel.onDismissTransfer()
                                },
                                onAddStayFile = {
                                    val acceptedMimeTypes = arrayOf("application/*", "text/*")
                                    openDocumentFile.launch(acceptedMimeTypes)
                                },
                                scrollToFieldFlow = scrollToFieldFlow,
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .padding(horizontal = 24.dp),
                            )
                        }
                    }

                    StayRegistrationListState.SelectingProperty -> {
                        if (properties.isEmpty()) {
                            PropertiesEmptySection(isSearchingProperty = isSearching)
                        } else {
                            StayPropertySelectionList(
                                properties = properties,
                                onPropertySelect = { property ->
                                    viewModel.onStayFormChange(
                                        stayForm.copy(
                                            property = property,
                                            checkInDateTime = null,
                                            checkOutDateTime = null,
                                        )
                                    )

                                    listStatus = StayRegistrationListState.ShowingMainForm
                                    isSearching = false
                                    viewModel.onPropertySearchInputChange(searchInput = null)
                                },
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .padding(horizontal = 24.dp),
                            )
                        }
                    }

                    StayRegistrationListState.SelectingTenant -> {
                        if (tenants.isEmpty()) {
                            TenantsEmptySection(
                                isSearchingTenant = isSearching,
                                isSelectingTenant = true,
                            )
                        } else {
                            StayTenantSelectionList(
                                tenants = tenants,
                                onTenantSelect = { tenant ->
                                    viewModel.onStayFormChange(stayForm.copy(tenant = tenant))

                                    listStatus = StayRegistrationListState.ShowingMainForm
                                    isSearching = false
                                    viewModel.onTenantSearchInputChange(searchInput = null)
                                },
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .padding(horizontal = 24.dp),
                            )
                        }
                    }
                }
            }
            if (transferToDelete != null) {
                ConfirmDeleteStayTransferDialog(
                    onConfirmDelete = {
                        val newTransfers = stayForm.transfers
                            .toPersistentList()
                            .mutate { list ->
                                if (transferToDelete?.id == null) {
                                    list.remove(transferToDelete)
                                } else {
                                    val i = list.indexOf(transferToDelete)
                                    list[i] = list[i].copy(isDeleted = true)
                                }
                            }

                        viewModel.onStayFormChange(
                            form = stayForm.copy(transfers = newTransfers),
                        )

                        transferToDelete = null
                    },
                    onCancel = { transferToDelete = null },
                )
            }
            if (mustShowConfirmLeaveRegistrationDialog) {
                ConfirmLeaveRegistrationDialog(
                    isEditing = stayId != null,
                    onConfirmLeave = {
                        mustShowConfirmLeaveRegistrationDialog = false
                        onBack(false)
                    },
                    onCancel = { mustShowConfirmLeaveRegistrationDialog = false }
                )
            }
        }
    }
}
