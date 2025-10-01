package com.fstengineering.leasilycrm.stayRegistration.composables.fields

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.text.isDigitsOnly
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField
import java.math.BigDecimal

@Composable
fun StayTotalPriceFormField(
    totalPrice: BigDecimal?,
    onTotalPriceChange: (BigDecimal?) -> Unit,
    error: Int?,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FormTextField(
        label = stringResource(R.string.stay_registration_total_price_label),
        value = totalPrice?.toString() ?: "",
        onValueChange = { totalPriceInput ->
            if (!totalPriceInput.isDigitsOnly()) return@FormTextField
            onTotalPriceChange(totalPriceInput.toBigDecimalOrNull())
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
        keyboardType = KeyboardType.Number,
        onDone = { onDone() },
        modifier = modifier
    )
}
