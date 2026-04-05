package com.fstengineering.leasilycrm.stayRegistration.presentation.composables

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.helpers.toDateTime
import com.fstengineering.leasilycrm.core.presentation.composables.centerVisibleLazyItemByKey
import com.fstengineering.leasilycrm.core.presentation.composables.dialogs.DateRangePickerDialog
import com.fstengineering.leasilycrm.core.presentation.utils.getMonthTimestamp
import com.fstengineering.leasilycrm.core.presentation.utils.getSelectableDates
import com.fstengineering.leasilycrm.stayRegistration.presentation.composables.fields.StayCheckInTimeFormField
import com.fstengineering.leasilycrm.stayRegistration.presentation.composables.fields.StayCheckOutTimeFormField
import com.fstengineering.leasilycrm.stayRegistration.presentation.composables.fields.StayPeriodFormField
import com.fstengineering.leasilycrm.stayRegistration.presentation.composables.fields.StayPropertyFormField
import com.fstengineering.leasilycrm.stayRegistration.presentation.composables.fields.StaysObservationsFormField
import com.fstengineering.leasilycrm.stayRegistration.presentation.models.StayDraftFormUiState
import com.fstengineering.leasilycrm.stayRegistration.presentation.models.StayField
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnavailablePeriodRegistrationFormSection(
    form: StayDraftFormUiState.Loaded.UnavailablePeriod,
    onFormChange: (StayDraftFormUiState.Loaded.UnavailablePeriod) -> Unit,
    stayFormErrors: ImmutableMap<StayField, Int?>,
    onSelectProperty: () -> Unit,
    onPeriodDisabledByPropertyUnselected: () -> Unit,
    scrollToFieldFlow: Flow<StayField>,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    val bringIntoViewRequesters = remember {
        StayField.entries.associateWith { BringIntoViewRequester() }
    }

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
        item(key = StayField.Property) {
            Box(
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequesters.getValue(StayField.Property))
            ) {
                StayPropertyFormField(
                    stayProperty = form.property,
                    onSelectStayProperty = onSelectProperty,
                    isEnabled = true,
                    error = stayFormErrors[StayField.Property],
                )
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            Text(
                text = stringResource(R.string.stay_registration_period_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
        item(key = StayField.Period) {
            Box(
                modifier = Modifier
                    .bringIntoViewRequester(bringIntoViewRequesters.getValue(StayField.Period))
            ) {
                val isEnabled = form.property != null

                StayPeriodFormField(
                    checkInDateTime = form.checkInDateTime,
                    checkOutDateTime = form.checkOutDateTime,
                    error = stayFormErrors[StayField.Period],
                    isEnabled = isEnabled,
                    onTap = {
                        if (isEnabled) {
                            mustShowStayDateRangePickerDialog = true
                            return@StayPeriodFormField
                        }

                        coroutineScope.launch {
                            centerVisibleLazyItemByKey(
                                lazyListState = lazyListState,
                                key = StayField.Property,
                                fallbackIndex = StayField.Property.scrollIndex,
                            )

                            onPeriodDisabledByPropertyUnselected()
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
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .bringIntoViewRequester(bringIntoViewRequesters.getValue(StayField.CheckInTime))
                ) {
                    StayCheckInTimeFormField(
                        checkInHour = 0,
                        checkInMinute = 0,
                        isUnavailablePeriod = true,
                        error = stayFormErrors[StayField.CheckInTime],
                        onTap = { mustShowStayCheckInTimePickerDialog = true },
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .bringIntoViewRequester(bringIntoViewRequesters.getValue(StayField.CheckOutTime))
                ) {
                    StayCheckOutTimeFormField(
                        checkOutHour = 0,
                        checkOutMinute = 0,
                        isUnavailablePeriod = true,
                        error = stayFormErrors[StayField.CheckOutTime],
                        onTap = { mustShowStayCheckOutTimePickerDialog = true },
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item { HorizontalDivider() }
        item { Spacer(modifier = Modifier.height(24.dp)) }
        item {
            StaysObservationsFormField(
                value = form.observations,
                onValueChange = { onFormChange(form.copy(observations = it)) },
                isUnavailablePeriod = true,
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

//    if (mustShowStayCheckInTimePickerDialog) {
//        TimePickerDialog(
//            title = stringResource(R.string.stay_registration_start_time_picker_dialog_title),
//            onTimeSelect = {
//                onFormChange(
//                    form.copy(checkInHourMinute = Pair(it.hour, it.minute))
//                )
//            },
//            onDismiss = { mustShowStayCheckInTimePickerDialog = false },
//        )
//    }
//
//    if (mustShowStayCheckOutTimePickerDialog) {
//        TimePickerDialog(
//            title = stringResource(R.string.stay_registration_end_time_picker_dialog_title),
//            onTimeSelect = {
//                onFormChange(
//                    form.copy(checkOutHourMinute = Pair(it.hour, it.minute))
//                )
//            },
//            onDismiss = { mustShowStayCheckOutTimePickerDialog = false },
//        )
//    }
}
