package com.fstengineering.leasilycrm.stayRegistration.composables.fields

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField

@Composable
fun StayTransferAmountFormField(
    transferAmount: String,
    onTransferAmountChange: (String) -> Unit,
    error: Int?,
    fieldFocusRequester: FocusRequester,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FormTextField(
        label = stringResource(R.string.stay_registration_transfers_amount_label),
        value = transferAmount,
        onValueChange = { transferAmountInput ->
            if (!transferAmountInput.all { it.isDigit() || it == ',' || it == '.' }) return@FormTextField
            if (transferAmountInput.count { it == ',' || it == '.' } > 1) return@FormTextField

            onTransferAmountChange(transferAmountInput)
        },
        isInvalid = error != null,
        prefix = {
            Text(
                text = "R$ ",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        supportingText = stringResource(error ?: R.string.inline_required),
        keyboardType = KeyboardType.Decimal,
        fieldFocusRequester = fieldFocusRequester,
        onDone = { onDone() },
        modifier = modifier
    )
}
