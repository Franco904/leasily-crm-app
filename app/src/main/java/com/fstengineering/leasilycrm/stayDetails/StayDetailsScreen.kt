package com.fstengineering.leasilycrm.stayDetails

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.presentation.composables.AppCircularProgressIndicator
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmDeleteStayDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.ConfirmRegisterStayCheckOutDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.NoAppToShowFileContentDialog
import com.fstengineering.leasilycrm.core.presentation.utils.openFileWithChooser
import com.fstengineering.leasilycrm.core.presentation.utils.openPlayStoreApp
import com.fstengineering.leasilycrm.core.presentation.utils.rememberNow
import com.fstengineering.leasilycrm.stayDetails.StayDetailsViewModel.UiEvents.DataSourceError
import com.fstengineering.leasilycrm.stayDetails.StayDetailsViewModel.UiEvents.NavigateUp
import com.fstengineering.leasilycrm.stayDetails.StayDetailsViewModel.UiEvents.RedirectToSignIn
import com.fstengineering.leasilycrm.stayDetails.StayDetailsViewModel.UiEvents.RegisterCheckInSuccess
import com.fstengineering.leasilycrm.stayDetails.StayDetailsViewModel.UiEvents.RegisterCheckOutSuccess
import com.fstengineering.leasilycrm.stayDetails.StayDetailsViewModel.UiEvents.StayDeleteSuccess
import com.fstengineering.leasilycrm.stayDetails.composables.StayDetailsFloatingActionButton
import com.fstengineering.leasilycrm.stayDetails.composables.StayDetailsSection
import com.fstengineering.leasilycrm.stayDetails.composables.StayDetailsTopAppBar
import com.fstengineering.leasilycrm.stayDetails.composables.UnavailablePeriodDetailsSection

@Composable
fun StayDetailsScreen(
    stayId: Long,
    cameFromProperty: Boolean,
    cameFromTenant: Boolean,
    viewModel: StayDetailsViewModel,
    onEdit: (Stay) -> Unit,
    onViewPropertyDashboard: (Long) -> Unit,
    onViewTenantDetails: (Long) -> Unit,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    showSnackbar: (String) -> Unit,
    showActionSnackbar: (String, String, () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    stayPeriodDateCircleColor: Color? = null,
) {
    val context = LocalContext.current
    val activity = LocalActivity.current!!

    val surfaceColor = MaterialTheme.colorScheme.surface

    val stay by viewModel.stay.collectAsStateWithLifecycle()
    val stayStructuredDates by viewModel.stayStructuredDates.collectAsStateWithLifecycle()

    val now by rememberNow()

    val checkInDateTimeStartOfDay = stay.checkInDateTime
        ?.withHour(0)
        ?.withMinute(0)
        ?: now

    val checkOutDateTimeStartOfDay = stay.checkOutDateTime
        ?.withHour(0)
        ?.withMinute(0)
        ?: now

    val canRegisterCheckIn =
        !stay.isCheckInDone && now > checkInDateTimeStartOfDay && !stay.isUnavailablePeriod
    val canRegisterCheckOut =
        !stay.isCheckOutDone && now > checkOutDateTimeStartOfDay && !stay.isUnavailablePeriod

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var mustShowConfirmDeleteDialog by remember { mutableStateOf(false) }
    var mustShowConfirmRegisterStayCheckOutDialog by rememberSaveable { mutableStateOf(false) }
    var mustShowNoAppToShowFileContentDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.init(stayId = stayId)

        viewModel.uiEvents.collect { uiEvent ->
            when (uiEvent) {
                is DataSourceError -> {
                    showSnackbar(context.getString(uiEvent.messageId))
                }

                is RedirectToSignIn -> {
                    onSignOut()
                }

                is StayDeleteSuccess -> {
                    showSnackbar(context.getString(R.string.snackbar_delete_stay_succeeded))
                }

                RegisterCheckInSuccess -> {
                    showActionSnackbar(
                        context.getString(R.string.snackbar_register_check_in_stay_succeeded),
                        context.getString(R.string.undo_button_text),
                        viewModel::undoRegisterStayCheckIn,
                    )
                }

                RegisterCheckOutSuccess -> {
                    showActionSnackbar(
                        context.getString(R.string.snackbar_register_check_out_stay_succeeded),
                        context.getString(R.string.undo_button_text),
                        viewModel::undoRegisterStayCheckOut,
                    )
                }

                NavigateUp -> {
                    onBack()
                }
            }
        }
    }

    SideEffect {
        activity.window.statusBarColor = surfaceColor.toArgb()
        activity.window.navigationBarColor = surfaceColor.toArgb()
    }

    Scaffold(
        topBar = {
            StayDetailsTopAppBar(
                isUnavailablePeriod = stay.isUnavailablePeriod,
                onBack = onBack,
                onEdit = { onEdit(stay) },
                onDelete = { mustShowConfirmDeleteDialog = true },
            )
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
        when {
            isLoading -> {
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
            }

            stay.isUnavailablePeriod -> {
                UnavailablePeriodDetailsSection(
                    stay = stay,
                    stayStructuredDates = stayStructuredDates,
                    cameFromProperty = cameFromProperty,
                    onViewPropertyDashboard = { onViewPropertyDashboard(stay.property?.id!!) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                )
            }

            else -> {
                StayDetailsSection(
                    stay = stay,
                    stayStructuredDates = stayStructuredDates,
                    stayPeriodDateCircleColor = stayPeriodDateCircleColor,
                    onOpenStayFile = { file ->
                        context.openFileWithChooser(
                            uriString = file.uriString,
                            onActivityNotFound = { mustShowNoAppToShowFileContentDialog = true }
                        )
                    },
                    cameFromProperty = cameFromProperty,
                    cameFromTenant = cameFromTenant,
                    onViewPropertyDashboard = { onViewPropertyDashboard(stay.property?.id!!) },
                    onViewTenantDetails = { onViewTenantDetails(stay.tenant?.id!!) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                )
            }
        }
        if (mustShowConfirmDeleteDialog) {
            ConfirmDeleteStayDialog(
                onConfirmDelete = {
                    viewModel.deleteStay()

                    mustShowConfirmDeleteDialog = false
                    onBack()
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
