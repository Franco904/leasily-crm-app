package com.fstengineering.leasilycrm.core.presentation.composables.dialogs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.utils.timestampToLocalDate
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialDisplayedMonthMillis: Long,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    selectableDates: SelectableDates? = null,
) {
    val datePickerState = rememberDatePickerState(
        initialDisplayedMonthMillis = initialDisplayedMonthMillis,
        selectableDates = selectableDates ?: DatePickerDefaults.AllDates,
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (datePickerState.selectedDateMillis == null) {
                        return@TextButton
                    }

                    val selectedDate = datePickerState.selectedDateMillis!!.timestampToLocalDate()

                    onDateSelected(selectedDate)
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
        DatePicker(
            state = datePickerState,
            title = {
                Text(
                    text = stringResource(R.string.date_picker_dialog_title),
                    modifier = Modifier
                        .padding(16.dp)
                )
            },
            headline = {
                DatePickerDefaults.DatePickerHeadline(
                    selectedDateMillis = datePickerState.selectedDateMillis,
                    displayMode = datePickerState.displayMode,
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
