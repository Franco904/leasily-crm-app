package com.fstengineering.leasilycrm.stayRegistration.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.models.StayTransfer
import com.fstengineering.leasilycrm.core.presentation.composables.AppCheckbox
import com.fstengineering.leasilycrm.core.presentation.composables.CountAnswerCounter
import com.fstengineering.leasilycrm.core.presentation.composables.centerVisibleLazyItemByKey
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.DateRangePickerDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.TimePickerDialog
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthTimestamp
import com.fstengineering.leasilycrm.core.presentation.utils.getSelectableDates
import com.fstengineering.leasilycrm.core.presentation.utils.timestampToLocalDateTime
import com.fstengineering.leasilycrm.stayRegistration.composables.fields.StayCheckInTimeFormField
import com.fstengineering.leasilycrm.stayRegistration.composables.fields.StayCheckOutTimeFormField
import com.fstengineering.leasilycrm.stayRegistration.composables.fields.StayCommissionFormField
import com.fstengineering.leasilycrm.stayRegistration.composables.fields.StayPeriodFormField
import com.fstengineering.leasilycrm.stayRegistration.composables.fields.StayPropertyFormField
import com.fstengineering.leasilycrm.stayRegistration.composables.fields.StayTenantFormField
import com.fstengineering.leasilycrm.stayRegistration.composables.fields.StayTenantVehicleColorFormField
import com.fstengineering.leasilycrm.stayRegistration.composables.fields.StayTenantVehicleLicensePlateFormField
import com.fstengineering.leasilycrm.stayRegistration.composables.fields.StayTenantVehicleMakeFormField
import com.fstengineering.leasilycrm.stayRegistration.composables.fields.StayTotalPriceFormField
import com.fstengineering.leasilycrm.stayRegistration.composables.fields.StayTransfersFormField
import com.fstengineering.leasilycrm.stayRegistration.composables.fields.StaysObservationsFormField
import com.fstengineering.leasilycrm.stayRegistration.composables.fields.stayDocumentsFormField
import com.fstengineering.leasilycrm.stayRegistration.models.StayField
import com.fstengineering.leasilycrm.stayRegistration.models.StayTransferField
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StayRegistrationFormSection(
    stayForm: Stay,
    onStayFormChange: (Stay) -> Unit,
    totalPriceInput: String,
    onTotalPriceChange: (String) -> Unit,
    commissionInput: String,
    onCommissionChange: (String) -> Unit,
    stayFormErrors: ImmutableMap<StayField, Int?>,
    transferForm: StayTransfer?,
    onTransferFormChange: (StayTransfer) -> Unit,
    transferFormErrors: ImmutableMap<StayTransferField, Int?>,
    getPropertyAlreadySelectedDatesForStay: (ImmutableList<Stay>) -> ImmutableSet<LocalDate>,
    onSelectProperty: (isEnabled: Boolean) -> Unit,
    onSelectTenant: (isEnabled: Boolean) -> Unit,
    onPeriodDisabledByPropertyUnselected: () -> Unit,
    onPeriodDisabledByCheckInDone: () -> Unit,
    onLastTextFieldDone: () -> Unit,
    onAddTransfer: () -> Unit,
    onEditTransfer: (StayTransfer) -> Unit,
    onDeleteTransfer: (StayTransfer) -> Unit,
    onSaveTransfer: () -> Unit,
    onDismissTransfer: () -> Unit,
    onAddStayFile: () -> Unit,
    scrollToFieldFlow: Flow<StayField>,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    val bringIntoViewRequesters = remember {
        StayField.entries.associateWith { BringIntoViewRequester() }
    }

    val vehicleColorFieldFocusRequester = remember { FocusRequester() }
    val vehicleLicensePlateFieldFocusRequester = remember { FocusRequester() }
    val commissionFieldFocusRequester = remember { FocusRequester() }

    val initialDateSelection = remember {
        val calendar = Calendar.getInstance()

        Triple(
            calendar.get(Calendar.DAY_OF_MONTH),
            (calendar.get(Calendar.MONTH) + 1),
            calendar.get(Calendar.YEAR)
        )
    }

    val propertyAlreadySelectedDatesForStay = remember(stayForm.property) {
        getPropertyAlreadySelectedDatesForStay(stayForm.property?.stays ?: persistentListOf())
    }

    val stayNotDeletedTransfers = remember(stayForm.transfers) {
        stayForm.transfers
            .filter { !it.isDeleted }
            .sortedByDescending { it.transferDate }
            .toPersistentList()
    }

    val stayNotDeletedFiles = remember(stayForm.files) {
        stayForm.files.filter { !it.isDeleted }.toPersistentList()
    }

    var mustShowStayDateRangePickerDialog by rememberSaveable { mutableStateOf(false) }
    var mustShowStayCheckInTimePickerDialog by rememberSaveable { mutableStateOf(false) }
    var mustShowStayCheckOutTimePickerDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scrollToFieldFlow.collect { fieldToScroll ->
            bringIntoViewRequesters[fieldToScroll]?.bringIntoView()

            centerVisibleLazyItemByKey(
                lazyListState = lazyListState,
                key = fieldToScroll,
                fallbackIndex = fieldToScroll.scrollIndex,
            )
        }
    }

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(bottom = 240.dp),
        modifier = modifier
    ) {
        item(key = StayField.Property) {
            Box(
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequesters.getValue(StayField.Property))
            ) {
                val isEnabled = !stayForm.isCheckInDone

                StayPropertyFormField(
                    stayProperty = stayForm.property,
                    onSelectStayProperty = { onSelectProperty(isEnabled) },
                    isEnabled = isEnabled,
                    error = stayFormErrors[StayField.Property],
                )
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item(key = StayField.Tenant) {
            Box(
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequesters.getValue(StayField.Tenant))
            ) {
                val isEnabled = !stayForm.isCheckInDone

                StayTenantFormField(
                    stayTenant = stayForm.tenant,
                    onSelectStayTenant = { onSelectTenant(isEnabled) },
                    isEnabled = isEnabled,
                    error = stayFormErrors[StayField.Tenant],
                )
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StayTenantVehicleMakeFormField(
                    value = stayForm.vehicleMake,
                    onValueChange = { onStayFormChange(stayForm.copy(vehicleMake = it)) },
                    onDone = { vehicleColorFieldFocusRequester.requestFocus() },
                    modifier = Modifier
                        .weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StayTenantVehicleColorFormField(
                    value = stayForm.vehicleColor,
                    onValueChange = { onStayFormChange(stayForm.copy(vehicleColor = it)) },
                    onDone = { vehicleLicensePlateFieldFocusRequester.requestFocus() },
                    fieldFocusRequester = vehicleColorFieldFocusRequester,
                    modifier = Modifier
                        .weight(1f)
                )
            }
        }
        item { Spacer(modifier = Modifier.height(28.dp)) }
        item {
            StayTenantVehicleLicensePlateFormField(
                value = stayForm.vehicleLicensePlate,
                onValueChange = { onStayFormChange(stayForm.copy(vehicleLicensePlate = it)) },
                onDone = { onLastTextFieldDone() },
                fieldFocusRequester = vehicleLicensePlateFieldFocusRequester,
            )
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            Text(
                text = stringResource(R.string.stay_registration_stay_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item {
            CountAnswerCounter(
                label = stringResource(R.string.stay_registration_qt_guests_label),
                count = stayForm.qtGuests ?: 1,
                onCountChange = { onStayFormChange(stayForm.copy(qtGuests = it)) },
            )
        }
        item { Spacer(modifier = Modifier.height(28.dp)) }
        item(key = StayField.Period) {
            Box(
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequesters.getValue(StayField.Period))
            ) {
                val isEnabled = stayForm.property != null && !stayForm.isCheckInDone

                StayPeriodFormField(
                    checkInDateTime = stayForm.checkInDateTime,
                    checkOutDateTime = stayForm.checkOutDateTime,
                    error = stayFormErrors[StayField.Period],
                    isEnabled = isEnabled,
                    onTap = {
                        when {
                            isEnabled -> mustShowStayDateRangePickerDialog = true
                            stayForm.property == null -> {
                                coroutineScope.launch {
                                    centerVisibleLazyItemByKey(
                                        lazyListState = lazyListState,
                                        key = StayField.Property,
                                        fallbackIndex = StayField.Property.scrollIndex,
                                    )

                                    onPeriodDisabledByPropertyUnselected()
                                }
                            }

                            stayForm.isCheckInDone -> onPeriodDisabledByCheckInDone()
                        }
                    },
                )
            }
        }
        item { Spacer(modifier = Modifier.height(28.dp)) }
        item(key = StayField.CheckInTime) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .bringIntoViewRequester(bringIntoViewRequesters.getValue(StayField.CheckInTime))
                    ) {
                        StayCheckInTimeFormField(
                            checkInHour = stayForm.checkInHourMinute.first,
                            checkInMinute = stayForm.checkInHourMinute.second,
                            error = stayFormErrors[StayField.CheckInTime],
                            onTap = { mustShowStayCheckInTimePickerDialog = true },
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    AppCheckbox(
                        label = stringResource(R.string.stay_registration_is_auto_check_in_label),
                        isChecked = stayForm.isAutoCheckIn,
                        onCheckedChange = { newIsAutoCheckIn ->
                            onStayFormChange(stayForm.copy(isAutoCheckIn = newIsAutoCheckIn))
                        },
                        isEnabled = stayForm.checkInHourMinute.first != null,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .bringIntoViewRequester(bringIntoViewRequesters.getValue(StayField.CheckOutTime))
                    ) {
                        StayCheckOutTimeFormField(
                            checkOutHour = stayForm.checkOutHourMinute.first,
                            checkOutMinute = stayForm.checkOutHourMinute.second,
                            error = stayFormErrors[StayField.CheckOutTime],
                            onTap = { mustShowStayCheckOutTimePickerDialog = true },
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    AppCheckbox(
                        label = stringResource(R.string.stay_registration_is_auto_check_out_label),
                        isChecked = stayForm.isAutoCheckOut,
                        onCheckedChange = { newIsAutoCheckOut ->
                            onStayFormChange(stayForm.copy(isAutoCheckOut = newIsAutoCheckOut))
                        },
                        isEnabled = stayForm.checkOutHourMinute.first != null,
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            StayTotalPriceFormField(
                totalPrice = totalPriceInput,
                onTotalPriceChange = onTotalPriceChange,
                onDone = { commissionFieldFocusRequester.requestFocus() },
            )
        }
        item { Spacer(modifier = Modifier.height(28.dp)) }
        item(key = StayField.Commission) {
            Box(
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequesters.getValue(StayField.Commission))
            ) {
                StayCommissionFormField(
                    commission = commissionInput,
                    onCommissionChange = onCommissionChange,
                    error = stayFormErrors[StayField.Commission],
                    fieldFocusRequester = commissionFieldFocusRequester,
                    onDone = { onLastTextFieldDone() },
                )
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            StayTransfersFormField(
                stayNotDeletedTransfers = stayNotDeletedTransfers,
                onAddTransfer = onAddTransfer,
                onEditTransfer = onEditTransfer,
                onDeleteTransfer = onDeleteTransfer,
            )
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        stayDocumentsFormField(
            stayForm = stayForm,
            onStayFormChange = onStayFormChange,
            stayNotDeletedFiles = stayNotDeletedFiles,
            onAddStayFile = onAddStayFile,
        )
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            StaysObservationsFormField(
                value = stayForm.observations,
                onValueChange = { onStayFormChange(stayForm.copy(observations = it)) },
            )
        }
    }
    if (mustShowStayDateRangePickerDialog) {
        val (initialDayOfMonth, initialMonth, initialYear) = initialDateSelection

        DateRangePickerDialog(
            initialDisplayedMonthMillis = getMonthTimestamp(
                month = initialMonth,
                year = initialYear,
            ),
            selectableDates = getSelectableDates(
                initialDayOfMonth = initialDayOfMonth,
                initialMonth = initialMonth,
                initialYear = initialYear,
                alreadySelectedDates = propertyAlreadySelectedDatesForStay,
            ),
            onDateRangeSelected = { (startDateTimestamp, endDateTimestamp) ->
                onStayFormChange(
                    stayForm.copy(
                        checkInDateTime = startDateTimestamp.timestampToLocalDateTime(),
                        checkOutDateTime = endDateTimestamp.timestampToLocalDateTime(),
                    )
                )

                mustShowStayDateRangePickerDialog = false
            },
            isDateSelectionEmpty = stayForm.checkInDateTime == null,
            onDismiss = { mustShowStayDateRangePickerDialog = false },
        )
    }
    if (mustShowStayCheckInTimePickerDialog) {
        TimePickerDialog(
            title = stringResource(R.string.stay_registration_check_in_time_picker_dialog_title),
            onTimeSelect = {
                onStayFormChange(
                    stayForm.copy(checkInHourMinute = Pair(it.hour, it.minute))
                )
            },
            onDismiss = { mustShowStayCheckInTimePickerDialog = false },
        )
    }
    if (mustShowStayCheckOutTimePickerDialog) {
        TimePickerDialog(
            title = stringResource(R.string.stay_registration_check_out_time_picker_dialog_title),
            onTimeSelect = {
                onStayFormChange(
                    stayForm.copy(checkOutHourMinute = Pair(it.hour, it.minute))
                )
            },
            onDismiss = { mustShowStayCheckOutTimePickerDialog = false },
        )
    }
    if (transferForm != null) {
        StayTransferRegistrationBottomsheet(
            sheetState = sheetState,
            transferForm = transferForm,
            onTransferFormChange = { onTransferFormChange(it) },
            formErrors = transferFormErrors,
            onSave = onSaveTransfer,
            onDismissRequest = onDismissTransfer,
        )
    }
}
