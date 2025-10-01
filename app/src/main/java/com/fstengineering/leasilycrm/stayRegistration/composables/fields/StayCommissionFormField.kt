package com.fstengineering.leasilycrm.stayRegistration.composables.fields

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.text.isDigitsOnly
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField
import java.math.BigDecimal

@Composable
fun StayCommissionFormField(
    commission: BigDecimal?,
    onCommissionChange: (BigDecimal?) -> Unit,
    fieldFocusRequester: FocusRequester,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FormTextField(
        label = stringResource(R.string.stay_registration_commission_label),
        value = commission?.toString() ?: "",
        onValueChange = { commissionInput ->
            if (!commissionInput.isDigitsOnly()) return@FormTextField
            onCommissionChange(commissionInput.toBigDecimalOrNull())
        },
        prefix = {
            Text(
                text = "R$ ",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        keyboardType = KeyboardType.Number,
        fieldFocusRequester = fieldFocusRequester,
        onDone = { onDone() },
        modifier = modifier
    )
}
