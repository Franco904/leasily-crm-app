package com.fstengineering.leasilycrm.stayRegistration.presentation.composables.fields

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.presentation.composables.textFields.FormTextField

@Composable
fun StayTotalPriceFormField(
    totalPrice: String,
    onTotalPriceChange: (String) -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FormTextField(
        label = stringResource(R.string.stay_registration_total_price_label),
        value = totalPrice,
        onValueChange = { totalPriceInput ->
            if (!totalPriceInput.all { it.isDigit() || it == ',' || it == '.' }) return@FormTextField
            if (totalPriceInput.count { it == ',' || it == '.' } > 1) return@FormTextField

            onTotalPriceChange(totalPriceInput)
        },
        prefix = {
            Text(
                text = "R$ ",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        keyboardType = KeyboardType.Decimal,
        onDone = { onDone() },
        modifier = modifier
    )
}
