package com.fstengineering.leasilycrm.core.presentation.composables.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import com.fstengineering.leasilycrm.core.presentation.composables.AppCheckbox

@Composable
fun AppTitleAndMessageDialog(
    title: String,
    message: String,
    confirmButtonText: String,
    dismissButtonText: String,
    onConfirmButtonClick: () -> Unit,
    onDismissButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    hasDontShowAnymoreCheckBox: Boolean = false,
    isDontShowAnymoreChecked: Boolean = false,
    onDontShowDialogAnymore: (Boolean) -> Unit = { _ -> },
) {
    AlertDialog(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        text = {
            Column {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (hasDontShowAnymoreCheckBox) {
                    Spacer(modifier = Modifier.height(8.dp))
                    AppCheckbox(
                        label = stringResource(R.string.dont_show_dialog_anymore_in_the_future_title),
                        labelStyle = MaterialTheme.typography.bodySmall,
                        isChecked = isDontShowAnymoreChecked,
                        onCheckedChange = onDontShowDialogAnymore,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmButtonClick) {
                Text(
                    text = confirmButtonText,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissButtonClick) {
                Text(
                    text = dismissButtonText,
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
