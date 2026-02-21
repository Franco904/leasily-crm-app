package com.fstengineering.leasilycrm.profile.presentation.composables

import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun EditCreciNumberDialog(
    initialCreciNumber: String?,
    onCreciNumberChange: () -> Unit,
    creciNumberError: Int?,
    onCreciNumberFieldFocusLoss: () -> Unit,
    onEdit: (String?) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val creciNumberFieldFocusRequester = remember { FocusRequester() }

    var creciNumberInput by remember(initialCreciNumber) {
        mutableStateOf(initialCreciNumber ?: "")
    }

    LaunchedEffect(Unit) {
        delay(250.milliseconds)
        creciNumberFieldFocusRequester.requestFocus()
    }

    AlertDialog(
        title = {
            Text(
                text = stringResource(R.string.profile_edit_user_creci_dialog_headline),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            FormTextField(
                label = stringResource(R.string.profile_edit_user_dialog_creci_field_placeholder),
                value = creciNumberInput,
                onValueChange = { creciNumberInput = it; onCreciNumberChange() },
                isInvalid = creciNumberError != null,
                supportingText = creciNumberError?.let { stringResource(creciNumberError) },
                maxLength = 8,
                keyboardType = KeyboardType.Number,
                onDone = { onEdit(creciNumberInput.takeIf { !it.isBlank() }) },
                fieldFocusRequester = creciNumberFieldFocusRequester,
                onFocusLost = onCreciNumberFieldFocusLoss,
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onEdit(creciNumberInput.takeIf { !it.isBlank() }) },
            ) {
                Text(
                    text = stringResource(R.string.edit_action_button_text),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onCancel() }) {
                Text(
                    text = stringResource(R.string.cancel_action_button_text),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        onDismissRequest = {},
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier
            .width(450.dp)
    )
}
