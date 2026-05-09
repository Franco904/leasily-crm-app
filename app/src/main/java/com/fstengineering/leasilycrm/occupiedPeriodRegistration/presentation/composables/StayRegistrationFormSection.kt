package com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.composables

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
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.toDateTime
import com.fstengineering.leasilycrm.core.presentation.composables.AppCheckbox
import com.fstengineering.leasilycrm.core.presentation.composables.CountAnswerCounter
import com.fstengineering.leasilycrm.core.presentation.composables.centerVisibleLazyItemByKey
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.DateRangePickerDialog
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.TimePickerDialog
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthTimestamp
import com.fstengineering.leasilycrm.core.presentation.utils.getSelectableDates
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.composables.fields.StayCheckInTimeFormField
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.composables.fields.StayCheckOutTimeFormField
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.composables.fields.StayCommissionFormField
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.composables.fields.StayPeriodFormField
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.composables.fields.StayPropertyFormField
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.composables.fields.StayTenantFormField
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.composables.fields.StayTenantVehicleColorFormField
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.composables.fields.StayTenantVehicleLicensePlateFormField
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.composables.fields.StayTenantVehicleMakeFormField
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.composables.fields.StayTotalPriceFormField
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.composables.fields.StaysObservationsFormField
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.composables.fields.stayDocumentsFormField
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.models.OccupiedPeriodField
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.models.OccupiedPeriodFormUiState
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StayRegistrationFormSection(
    form: OccupiedPeriodFormUiState.Loaded.Stay,
    onFormChange: (OccupiedPeriodFormUiState.Loaded.Stay) -> Unit,
    totalPriceInput: String,
    onTotalPriceChange: (String) -> Unit,
    commissionInput: String,
    onCommissionChange: (String) -> Unit,
    stayFormErrors: ImmutableMap<OccupiedPeriodField, Int?>,
    onSelectProperty: (isEnabled: Boolean) -> Unit,
    onSelectTenant: (isEnabled: Boolean) -> Unit,
    onPeriodDisabledByPropertyUnselected: () -> Unit,
    onPeriodDisabledByCheckInDone: () -> Unit,
    onLastTextFieldDone: () -> Unit,
    onAddStayFile: () -> Unit,
    scrollToFieldFlow: Flow<OccupiedPeriodField>,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val bringIntoViewRequesters = remember {
        OccupiedPeriodField.entries.associateWith { BringIntoViewRequester() }
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
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item(key = OccupiedPeriodField.Property) {
            Box(
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequesters.getValue(OccupiedPeriodField.Property))
            ) {
                val isEnabled = form.checkInDateTime?.toDate()?.isBefore(AppDate.now()) ?: true

                StayPropertyFormField(
                    stayProperty = form.property,
                    onSelectStayProperty = { onSelectProperty(isEnabled) },
                    isEnabled = isEnabled,
                    error = stayFormErrors[OccupiedPeriodField.Property],
                )
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item(key = OccupiedPeriodField.Tenant) {
            Box(
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequesters.getValue(OccupiedPeriodField.Tenant))
            ) {
                val isEnabled = form.checkInDateTime?.toDate()?.isBefore(AppDate.now()) ?: true

                StayTenantFormField(
                    stayTenant = form.tenant,
                    onSelectStayTenant = { onSelectTenant(isEnabled) },
                    isEnabled = isEnabled,
                    error = stayFormErrors[OccupiedPeriodField.Tenant],
                )
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StayTenantVehicleMakeFormField(
                    value = form.vehicleMake,
                    onValueChange = { onFormChange(form.copy(vehicleMake = it)) },
                    onDone = { vehicleColorFieldFocusRequester.requestFocus() },
                    modifier = Modifier
                        .weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StayTenantVehicleColorFormField(
                    value = form.vehicleColor,
                    onValueChange = { onFormChange(form.copy(vehicleColor = it)) },
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
                value = form.vehicleLicensePlate,
                onValueChange = { onFormChange(form.copy(vehicleLicensePlate = it)) },
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
                count = form.guests,
                onCountChange = { onFormChange(form.copy(guests = it)) },
            )
        }
        item { Spacer(modifier = Modifier.height(28.dp)) }
        item(key = OccupiedPeriodField.Period) {
            Box(
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequesters.getValue(OccupiedPeriodField.Period))
            ) {
                val isPastCheckIn = form.checkInDateTime?.toDate()?.isBefore(AppDate.now()) ?: true
                val isEnabled = form.property != null && !isPastCheckIn

                StayPeriodFormField(
                    checkInDateTime = form.checkInDateTime,
                    checkOutDateTime = form.checkOutDateTime,
                    error = stayFormErrors[OccupiedPeriodField.Period],
                    isEnabled = isEnabled,
                    onTap = {
                        when {
                            isEnabled -> mustShowStayDateRangePickerDialog = true
                            form.property == null -> {
                                coroutineScope.launch {
                                    centerVisibleLazyItemByKey(
                                        lazyListState = lazyListState,
                                        key = OccupiedPeriodField.Property,
                                        fallbackIndex = OccupiedPeriodField.Property.scrollIndex,
                                    )

                                    onPeriodDisabledByPropertyUnselected()
                                }
                            }

                            isPastCheckIn -> onPeriodDisabledByCheckInDone()
                        }
                    },
                )
            }
        }
        item { Spacer(modifier = Modifier.height(28.dp)) }
        item(key = OccupiedPeriodField.CheckInTime) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .bringIntoViewRequester(
                                bringIntoViewRequesters.getValue(
                                    OccupiedPeriodField.CheckInTime
                                )
                            )
                    ) {
                        StayCheckInTimeFormField(
                            checkInHour = form.checkInTime.first,
                            checkInMinute = form.checkInTime.second,
                            error = stayFormErrors[OccupiedPeriodField.CheckInTime],
                            onTap = { mustShowStayCheckInTimePickerDialog = true },
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    AppCheckbox(
                        label = stringResource(R.string.stay_registration_is_auto_check_in_label),
                        isChecked = form.isAutoCheckIn,
                        onCheckedChange = { newIsAutoCheckIn ->
                            onFormChange(form.copy(isAutoCheckIn = newIsAutoCheckIn))
                        },
                        isEnabled = form.checkInTime.first != null,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .bringIntoViewRequester(
                                bringIntoViewRequesters.getValue(
                                    OccupiedPeriodField.CheckOutTime
                                )
                            )
                    ) {
                        StayCheckOutTimeFormField(
                            checkOutHour = form.checkOutTime.first,
                            checkOutMinute = form.checkOutTime.second,
                            error = stayFormErrors[OccupiedPeriodField.CheckOutTime],
                            onTap = { mustShowStayCheckOutTimePickerDialog = true },
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    AppCheckbox(
                        label = stringResource(R.string.stay_registration_is_auto_check_out_label),
                        isChecked = form.isAutoCheckOut,
                        onCheckedChange = { newIsAutoCheckOut ->
                            onFormChange(form.copy(isAutoCheckOut = newIsAutoCheckOut))
                        },
                        isEnabled = form.checkOutTime.first != null,
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
        item(key = OccupiedPeriodField.Commission) {
            Box(
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequesters.getValue(OccupiedPeriodField.Commission))
            ) {
                StayCommissionFormField(
                    commission = commissionInput,
                    onCommissionChange = onCommissionChange,
                    error = stayFormErrors[OccupiedPeriodField.Commission],
                    fieldFocusRequester = commissionFieldFocusRequester,
                    onDone = { onLastTextFieldDone() },
                )
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        stayDocumentsFormField(
            form = form,
            onFormChange = onFormChange,
            stayFiles = form.files.toPersistentList(),
            onAddStayFile = onAddStayFile,
        )
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            StaysObservationsFormField(
                value = form.observations,
                onValueChange = { onFormChange(form.copy(observations = it)) },
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
                fixedNonSelectableDates = form.property?.occupiedDates ?: persistentSetOf(),
            ),
            onDateRangeSelected = { (startDateTimestamp, endDateTimestamp) ->
                onFormChange(
                    form.copy(
                        checkInDateTime = startDateTimestamp.toDateTime(),
                        checkOutDateTime = endDateTimestamp.toDateTime(),
                    )
                )

                mustShowStayDateRangePickerDialog = false
            },
            isDateSelectionEmpty = form.checkInDateTime == null,
            onDismiss = { mustShowStayDateRangePickerDialog = false },
        )
    }
    if (mustShowStayCheckInTimePickerDialog) {
        TimePickerDialog(
            title = stringResource(R.string.stay_registration_check_in_time_picker_dialog_title),
            onTimeSelect = {
                onFormChange(
                    form.copy(checkInTime = Pair(it.hour, it.minute))
                )
            },
            onDismiss = { mustShowStayCheckInTimePickerDialog = false },
        )
    }
    if (mustShowStayCheckOutTimePickerDialog) {
        TimePickerDialog(
            title = stringResource(R.string.stay_registration_check_out_time_picker_dialog_title),
            onTimeSelect = {
                onFormChange(
                    form.copy(checkOutTime = Pair(it.hour, it.minute))
                )
            },
            onDismiss = { mustShowStayCheckOutTimePickerDialog = false },
        )
    }
}
