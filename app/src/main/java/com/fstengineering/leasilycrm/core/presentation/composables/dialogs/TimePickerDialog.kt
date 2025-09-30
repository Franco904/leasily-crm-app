package com.fstengineering.leasilycrm.core.presentation.composables.dialogs

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onTimeSelect: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val timeNow = remember { LocalTime.now() }

    val timePickerState = rememberTimePickerState(
        initialHour = timeNow.hour,
        initialMinute = timeNow.minute,
    )

    AlertDialog(
        title = {
            Text(
                text = stringResource(R.string.time_picker_dialog_title),
                modifier = Modifier
                    .padding(16.dp)
            )
        },
        text = {
            TimePicker(
                state = timePickerState,
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedTime = LocalTime.now()
                        .withHour(timePickerState.hour)
                        .withMinute(timePickerState.minute)

                    onTimeSelect(selectedTime)
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.date_picker_dialog_primary_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel_action_button_text),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
        onDismissRequest = { onDismiss() },
        shape = MaterialTheme.shapes.extraLarge,
    )
}
