package com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.FullScreenLoadingIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.SystemBarsEffect
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeleteStayDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmRegisterStayCheckOutDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.NoAppToShowFileContentDialog
import com.fstengineering.leasilycrm.core.presentation.models.UiEvent
import com.fstengineering.leasilycrm.core.presentation.models.strategies.DefaultErrorBottomSheetDataPropertiesStrategy
import com.fstengineering.leasilycrm.core.presentation.utils.openFileWithChooser
import com.fstengineering.leasilycrm.core.presentation.utils.openPlayStoreApp
import com.fstengineering.leasilycrm.core.presentation.utils.rememberNow
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toBottomSheetData
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.OccupiedPeriodDetailsViewModel.UiEvents.BottomSheetError
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.OccupiedPeriodDetailsViewModel.UiEvents.NavigateUp
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.OccupiedPeriodDetailsViewModel.UiEvents.OccupiedPeriodDeleteSuccess
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.OccupiedPeriodDetailsViewModel.UiEvents.RegisterCheckInSuccess
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.OccupiedPeriodDetailsViewModel.UiEvents.RegisterCheckOutSuccess
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.composables.StayDetailsFloatingActionButton
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.composables.StayDetailsSection
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.composables.StayDetailsTopAppBar
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.composables.UnavailablePeriodDetailsSection
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.models.OccupiedPeriodDetailsUiState

@Composable
fun OccupiedPeriodDetailsScreen(
    occupiedPeriodId: Long,
    cameFromProperty: Boolean,
    cameFromTenant: Boolean,
    viewModel: OccupiedPeriodDetailsViewModel,
    onEdit: (Long, Long?, Long?, Boolean) -> Unit,
    onViewPropertyDashboard: (Long) -> Unit,
    onViewTenantDetails: (Long) -> Unit,
    onBack: () -> Unit,
    onUiEvent: (UiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loaded = uiState as? OccupiedPeriodDetailsUiState.Loaded

    val now by rememberNow()

    val canRegisterCheckIn = loaded != null
            && !loaded.isCheckInDone
            && now.isAfter(loaded.checkInDateTime.toStartOfDay())
            && !loaded.isUnavailablePeriod

    val canRegisterCheckOut = loaded != null
            && !loaded.isCheckOutDone
            && now.isAfter(loaded.checkOutDateTime.toStartOfDay())
            && !loaded.isUnavailablePeriod

    var mustShowConfirmDeleteDialog by remember { mutableStateOf(false) }
    var mustShowConfirmRegisterStayCheckOutDialog by rememberSaveable { mutableStateOf(false) }
    var mustShowNoAppToShowFileContentDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init(stayId = occupiedPeriodId)

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is BottomSheetError -> {
                    val errorData = uiEvent.error.toBottomSheetData(
                        DefaultErrorBottomSheetDataPropertiesStrategy,
                    )
                    onUiEvent(UiEvent.ShowErrorBottomSheet(errorData))
                }

                is OccupiedPeriodDeleteSuccess -> {
                    val messageId =
                        if (loaded?.isUnavailablePeriod == true) R.string.delete_unavailable_period_success_message
                        else R.string.delete_stay_success_message

                    onUiEvent(UiEvent.ShowSnackBar(messageId))
                }

                RegisterCheckInSuccess -> {
                    onUiEvent(
                        UiEvent.ShowActionSnackBar(
                            R.string.register_check_in_stay_success_message,
                            R.string.undo_button_text,
                            viewModel::undoRegisterStayCheckIn,
                        )
                    )
                }

                RegisterCheckOutSuccess -> {
                    onUiEvent(
                        UiEvent.ShowActionSnackBar(
                            R.string.register_check_out_stay_success_message,
                            R.string.undo_button_text,
                            viewModel::undoRegisterStayCheckOut,
                        )
                    )
                }

                NavigateUp -> {
                    onBack()
                }
            }
        }
    }

    SystemBarsEffect(
        statusBarColor = MaterialTheme.colorScheme.surface,
        navigationBarColor = MaterialTheme.colorScheme.surface,
    )

    Scaffold(
        topBar = {
            Column {
                StayDetailsTopAppBar(
                    isUnavailablePeriod = loaded?.isUnavailablePeriod ?: false,
                    isCheckOutDone = loaded?.isCheckOutDone ?: false,
                    onBack = onBack,
                    onEdit = {
                        if (loaded != null) {
                            onEdit(
                                loaded.id,
                                loaded.property.id,
                                loaded.tenantId,
                                loaded.isUnavailablePeriod,
                            )
                        }
                    },
                    onDelete = { mustShowConfirmDeleteDialog = true },
                )
                HorizontalDivider()
            }
        },
        floatingActionButton = {
            if (canRegisterCheckIn || canRegisterCheckOut) {
                StayDetailsFloatingActionButton(
                    canRegisterCheckIn = canRegisterCheckIn,
                    onClick = {
                        when {
                            canRegisterCheckIn -> viewModel.registerStayCheckIn()
                            viewModel.mustNotShowCheckOutDialog -> viewModel.registerStayCheckOut()
                            else -> mustShowConfirmRegisterStayCheckOutDialog = true
                        }
                    },
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        when (uiState) {
            is OccupiedPeriodDetailsUiState.Loading -> {
                FullScreenLoadingIndicator(innerPadding)
            }

            is OccupiedPeriodDetailsUiState.Loaded -> {
                val loaded = uiState as OccupiedPeriodDetailsUiState.Loaded

                if (loaded.isUnavailablePeriod) {
                    UnavailablePeriodDetailsSection(
                        unavailablePeriod = loaded,
                        cameFromProperty = cameFromProperty,
                        onViewPropertyDashboard = { onViewPropertyDashboard(loaded.property.id) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp)
                    )
                } else {
                    StayDetailsSection(
                        stay = loaded,
                        onOpenStayFile = { file ->
                            context.openFileWithChooser(
                                uriString = file.uri.toString(),
                                onActivityNotFound = { mustShowNoAppToShowFileContentDialog = true }
                            )
                        },
                        cameFromProperty = cameFromProperty,
                        cameFromTenant = cameFromTenant,
                        onViewPropertyDashboard = { onViewPropertyDashboard(loaded.property.id) },
                        onViewTenantDetails = { onViewTenantDetails(loaded.tenantId!!) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }

        if (mustShowConfirmDeleteDialog) {
            ConfirmDeleteStayDialog(
                isUnavailablePeriod = loaded?.isUnavailablePeriod ?: false,
                onConfirmDelete = {
                    viewModel.deletePeriod()
                    mustShowConfirmDeleteDialog = false
                },
                onCancel = { mustShowConfirmDeleteDialog = false },
            )
        }

        if (mustShowConfirmRegisterStayCheckOutDialog) {
            ConfirmRegisterStayCheckOutDialog(
                onConfirmCheckOut = { mustNotShowCheckOutDialogAnymore ->
                    viewModel.registerStayCheckOut(
                        mustNotShowCheckOutDialogAnymore = mustNotShowCheckOutDialogAnymore,
                    )
                    mustShowConfirmRegisterStayCheckOutDialog = false
                },
                onCancel = { mustShowConfirmRegisterStayCheckOutDialog = false },
            )
        }

        if (mustShowNoAppToShowFileContentDialog) {
            NoAppToShowFileContentDialog(
                onOpenPlayStore = { context.openPlayStoreApp() },
                onClose = { mustShowNoAppToShowFileContentDialog = false }
            )
        }
    }
}
