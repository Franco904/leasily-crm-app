package com.fstengineering.leasilycrm.core.presentation.composables.dialogs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
    initialDisplayedMonthMillis: Long,
    selectableDates: SelectableDates,
    onDateRangeSelected: (Pair<Long, Long>) -> Unit,
    onDismiss: () -> Unit,
    isDateSelectionEmpty: Boolean,
    modifier: Modifier = Modifier,
) {
    val dateRangePickerState = rememberDateRangePickerState(
        initialDisplayedMonthMillis = initialDisplayedMonthMillis,
        selectableDates = selectableDates,
    )

    LaunchedEffect(isDateSelectionEmpty) {
        if (isDateSelectionEmpty) {
            dateRangePickerState.setSelection(
                startDateMillis = null,
                endDateMillis = null,
            )
        }
    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (dateRangePickerState.selectedStartDateMillis == null
                        || dateRangePickerState.selectedEndDateMillis == null
                    ) {
                        return@TextButton
                    }

                    val selectedDates = Pair(
                        dateRangePickerState.selectedStartDateMillis!!,
                        dateRangePickerState.selectedEndDateMillis!!,
                    )

                    onDateRangeSelected(selectedDates)
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.date_picker_dialog_primary_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.date_picker_dialog_secondary_button))
            }
        },
        modifier = modifier
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = stringResource(R.string.date_range_picker_dialog_title),
                    modifier = Modifier
                        .padding(16.dp)
                )
            },
            headline = {
                DateRangePickerDefaults.DateRangePickerHeadline(
                    selectedStartDateMillis = dateRangePickerState.selectedStartDateMillis,
                    selectedEndDateMillis = dateRangePickerState.selectedEndDateMillis,
                    displayMode = dateRangePickerState.displayMode,
                    dateFormatter = DatePickerDefaults.dateFormatter(),
                    modifier = Modifier
                        .padding(6.dp)
                        .scale(0.9f)
                )
            },
            showModeToggle = true,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}
