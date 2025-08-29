package com.fstengineering.leasilycrm.customerSegments.composables

import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R

@Composable
fun ConfirmDeleteCustomerSegmentDialog(
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        title = {
            Text(
                text = stringResource(R.string.customer_segments_delete_dialog_headline),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        text = {
            Text(
                text = stringResource(R.string.customer_segments_delete_dialog_supporting_text),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        confirmButton = {
            TextButton(onClick = onDelete) {
                Text(
                    text = stringResource(id = R.string.delete_action_button_text),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(
                    text = stringResource(id = R.string.cancel_action_button_text),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
        onDismissRequest = {},
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier
            .width(450.dp)
    )
}
