package com.fstengineering.leasilycrm.propertyRegistration.presentation.composables.fields

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
fun PriceAmountFormField(
    priceAmount: String,
    onPriceAmountChange: (String) -> Unit,
    error: Int?,
    priceAmountFieldFocusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    FormTextField(
        label = stringResource(R.string.property_registration_price_amount_label),
        value = priceAmount,
        onValueChange = { priceAmountInput ->
            if (!priceAmountInput.all { it.isDigit() || it == ',' || it == '.' }) return@FormTextField
            if (priceAmountInput.count { it == ',' || it == '.' } > 1) return@FormTextField

            onPriceAmountChange(priceAmountInput)
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
        fieldFocusRequester = priceAmountFieldFocusRequester,
        modifier = modifier
    )
}
