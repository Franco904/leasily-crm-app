package com.fstengineering.leasilycrm.stayRegistration.presentation

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.valueObjects.toDecimalOrNull
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.PropertiesEmptySection
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmLeaveRegistrationDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.home.tenants.composables.TenantsEmptySection
import com.fstengineering.leasilycrm.stayRegistration.presentation.composables.StayPropertySelectionList
import com.fstengineering.leasilycrm.stayRegistration.presentation.composables.StayRegistrationFormSection
import com.fstengineering.leasilycrm.stayRegistration.presentation.composables.StayRegistrationTopAppBar
import com.fstengineering.leasilycrm.stayRegistration.presentation.composables.StayTenantSelectionList
import com.fstengineering.leasilycrm.stayRegistration.presentation.composables.UnavailablePeriodRegistrationFormSection
import com.fstengineering.leasilycrm.stayRegistration.presentation.models.StayDraftFormUiState
import com.fstengineering.leasilycrm.stayRegistration.presentation.models.StayField
import com.fstengineering.leasilycrm.stayRegistration.presentation.models.StayRegistrationListState
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

    val draftForm by viewModel.draftFormUiState.collectAsStateWithLifecycle()
    val ownedStayForm = (draftForm as? StayDraftFormUiState.Loaded.OwnedStay)
    val unavailablePeriodForm = (draftForm as? StayDraftFormUiState.Loaded.UnavailablePeriod)

    val formErrors by viewModel.formErrors.collectAsStateWithLifecycle()

    val propertySearchInput by viewModel.propertySearchInput.collectAsStateWithLifecycle()
    val properties by viewModel.properties.collectAsStateWithLifecycle()

    val tenantSearchInput by viewModel.tenantSearchInput.collectAsStateWithLifecycle()
    val tenants by viewModel.tenants.collectAsStateWithLifecycle()

    var isSearching by rememberSaveable { mutableStateOf(false) }

    val scrollToFieldChannel = remember { Channel<StayField>() }
    val scrollToFieldFlow = remember(scrollToFieldChannel) { scrollToFieldChannel.receiveAsFlow() }

    var listStatus by rememberSaveable { mutableStateOf(StayRegistrationListState.ShowingMainForm) }

    var mustShowConfirmLeaveRegistrationDialog by remember { mutableStateOf(false) }

    var totalPriceInput by rememberSaveable(ownedStayForm?.totalPrice) {
        mutableStateOf(ownedStayForm?.totalPrice?.toString() ?: "")
    }
    var commissionInput by rememberSaveable(ownedStayForm?.commission) {
        mutableStateOf(ownedStayForm?.commission?.toString() ?: "")
    }

    val openDocumentFile = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { contentUri ->
            if (contentUri == null) return@rememberLauncherForActivityResult

            context.contentResolver.takePersistableUriPermission(
                contentUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )

            viewModel.onStayFilePick(
                contentUriString = contentUri.toString(),
                defaultFileName = context.getString(R.string.unknown_text),
            )
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
                is StayRegistrationViewModel.UiEvent.BottomSheetError -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is StayRegistrationViewModel.UiEvent.ScrollToField -> {
                    scrollToFieldChannel.trySend(uiEvent.field)
                }

                is StayRegistrationViewModel.UiEvent.StayCreateSuccess -> {
                    onBack(true)

                    val messageId =
                        if (isUnavailablePeriod) R.string.create_unavailable_period_success_message
                        else R.string.create_stay_success_message
                    onUiEvent(UiEvent.ShowSnackBar(messageId))
                }

                is StayRegistrationViewModel.UiEvent.StayEditSuccess -> {
                    onBack(true)

                    onUiEvent(UiEvent.ShowSnackBar(uiEvent.messageId))
                }

                is StayRegistrationViewModel.UiEvent.Exit -> {
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
            listStatus == StayRegistrationListState.ShowingMainForm -> {
                viewModel.onExit()
            }

            isSearching -> {
                isSearching = false
                viewModel.onPropertySearchInputChange(searchInput = null)
                viewModel.onTenantSearchInputChange(searchInput = null)
            }

            else -> listStatus =
                StayRegistrationListState.ShowingMainForm
        }
    }

    Scaffold(
        topBar = {
            val isSelectingProperty =
                listStatus == StayRegistrationListState.SelectingProperty
            val isSelectingTenant =
                listStatus == StayRegistrationListState.SelectingTenant

            Column {
                StayRegistrationTopAppBar(
                    isEditing = stayId != null,
                    onSaveStay = {
                        val form = ownedStayForm ?: return@StayRegistrationTopAppBar

                        viewModel.onOwnedStayFormChange(
                            form.copy(
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
                            listStatus == StayRegistrationListState.ShowingMainForm -> {
                                viewModel.onExit()
                            }

                            isSearching -> {
                                isSearching = false
                                viewModel.onPropertySearchInputChange(searchInput = null)
                                viewModel.onTenantSearchInputChange(searchInput = null)
                            }

                            else -> listStatus =
                                StayRegistrationListState.ShowingMainForm
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
        when (draftForm) {
            is StayDraftFormUiState.Loading -> {
                FullScreenLoadingIndicator(innerPadding)
            }

            else -> {
                AnimatedContent(
                    targetState = listStatus,
                ) { status ->
                    when (status) {
                        StayRegistrationListState.ShowingMainForm -> {
                            if (isUnavailablePeriod) {
                                val form = unavailablePeriodForm ?: return@AnimatedContent

                                UnavailablePeriodRegistrationFormSection(
                                    form = form,
                                    onFormChange = viewModel::onUnavailablePeriodFormChange,
                                    stayFormErrors = formErrors.errors,
                                    onSelectProperty = {
                                        listStatus =
                                            StayRegistrationListState.SelectingProperty
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
                                val form = ownedStayForm ?: return@AnimatedContent

                                StayRegistrationFormSection(
                                    form = form,
                                    onFormChange = viewModel::onOwnedStayFormChange,
                                    totalPriceInput = totalPriceInput,
                                    onTotalPriceChange = { totalPriceInput = it },
                                    commissionInput = commissionInput,
                                    onCommissionChange = {
                                        commissionInput = it
                                        viewModel.clearCommissionError()
                                    },
                                    stayFormErrors = formErrors.errors,
                                    onSelectProperty = { isEnabled ->
                                        if (isEnabled) {
                                            listStatus =
                                                StayRegistrationListState.SelectingProperty
                                        } else {
                                            onUiEvent(UiEvent.ShowSnackBar(R.string.register_stay_check_in_already_done_warning_message))
                                        }
                                    },
                                    onSelectTenant = { isEnabled ->
                                        if (isEnabled) {
                                            listStatus =
                                                StayRegistrationListState.SelectingTenant
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
                                        val form = ownedStayForm ?: return@StayPropertySelectionList
                                        viewModel.onOwnedStayFormChange(
                                            form.copy(
                                                property = property,
                                                checkInDateTime = null,
                                                checkOutDateTime = null,
                                            )
                                        )

                                        listStatus =
                                            StayRegistrationListState.ShowingMainForm
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
                                        val form = ownedStayForm ?: return@StayTenantSelectionList
                                        viewModel.onOwnedStayFormChange(form.copy(tenant = tenant))

                                        listStatus =
                                            StayRegistrationListState.ShowingMainForm
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
            }
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
